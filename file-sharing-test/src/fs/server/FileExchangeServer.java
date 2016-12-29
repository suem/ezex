package fs.server;

import fs.GuiStuff;
import fs.protocol.BadRequest;
import fs.protocol.GetFiles;
import fs.protocol.GetIndex;
import fs.protocol.Request;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class FileExchangeServer extends Thread {

    public static final Logger LOGGER = Logger.getLogger(FileExchangeServer.class.getName());

    private final File baseFolder;

    private final int port;

    private volatile ServerSocketChannel serverSocketChannel;

    private volatile boolean running = false;

    private ConcurrentHashMap<ClientHandlerThread, ClientHandlerThread> clientThreads = new ConcurrentHashMap<>();

    public FileExchangeServer(File base, int port) {
        this.baseFolder = base;
        this.port = port;
    }

    public synchronized void stopServer() {
        if (running && this.serverSocketChannel != null) {
            LOGGER.info("Telling server to stop");
            running = false;
        } else {
            LOGGER.warning("Tried to stop server that is not running");
        }
    }

    public synchronized void forceStopServer() {
        if (this.serverSocketChannel != null) {
            try {
                LOGGER.info("Forcing server stop");
                this.serverSocketChannel.close();
                LOGGER.info("Server stopped");
            } catch (IOException e) {
                LOGGER.severe(e.getMessage());
                e.printStackTrace();
            }
        } else {
            LOGGER.warning("Tried to stop server that is not running");
        }
    }

    public boolean isRunning() {
        return this.serverSocketChannel != null && running;
    }

    public void run() {
        LOGGER.info("Starting server on port " + this.port);

        try {
            ServerSocketChannel sc = ServerSocketChannel.open();
            sc.bind(new InetSocketAddress(this.port));
            this.serverSocketChannel = sc;
            this.running = true;
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
            return;
        }

        LOGGER.info("Server started, waiting for connections...");

        try {

            while (this.running) {
                SocketChannel channel = this.serverSocketChannel.accept();
                ClientHandlerThread clientHandlerThread = new ClientHandlerThread(channel);
                clientHandlerThread.start();
                this.clientThreads.put(clientHandlerThread, clientHandlerThread);
            }

            for (ClientHandlerThread thread : this.clientThreads.values()) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    LOGGER.severe(e.getMessage());
                    e.printStackTrace();
                }
            }
            this.serverSocketChannel.close();
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }

    }


    private class ClientHandlerThread extends Thread {

        private final SocketChannel socketChannel;

        private ObjectInputStream ois;
        private ObjectOutputStream oos;


        public ClientHandlerThread(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void run() {
            LOGGER.info("Starting client handler " + this.getId());
            try {
                this.ois = new ObjectInputStream(Channels.newInputStream(this.socketChannel));
                this.oos = new ObjectOutputStream(Channels.newOutputStream(this.socketChannel));
                this.requestResponseLoop();
            } catch (IOException e) {
                LOGGER.info("Client closed connection");
            } finally {
                LOGGER.info("Stopping client handler " + this.getId());
                FileExchangeServer.this.clientThreads.remove(this);
                try {
                    this.socketChannel.close();
                } catch (IOException e) {
                    LOGGER.severe(e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        private void requestResponseLoop() throws IOException {
            while (FileExchangeServer.this.running) {
                try {
                    Object requestObject = ois.readObject();
                    if (requestObject instanceof Request) {
                        Request request = (Request) requestObject;
                        request.validate();
                        handleRequest(request);
                    } else {
                        this.badRequest("Unknown Request object");
                    }
                } catch (ClassNotFoundException e) {
                    LOGGER.severe(e.getMessage());
                    e.printStackTrace();
                    this.badRequest("Could not parse Request");
                } catch (Request.IllegalRequestException e) {
                    LOGGER.warning("Illegal request: " + e.getMessage());
                    this.badRequest(e.getMessage());
                }
            }
        }

        private void handleRequest(Request request) throws IOException {
            if (request instanceof GetIndex.Request) {
                GetIndexRequestHandler.handleRequest(this.socketChannel, this.oos, FileExchangeServer.this.baseFolder);
            } else if (request instanceof GetFiles.Request) {
                GetFilesRequestHandler.handleRequest(
                        socketChannel,
                        oos,
                        (GetFiles.Request) request,
                        FileExchangeServer.this.baseFolder
                );
            } else {
                this.badRequest("Unknown Request type");
            }
        }

        private void badRequest(String message) throws IOException {
            oos.writeObject(new BadRequest(message));
            oos.flush();
        }

    }

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        File base = GuiStuff.chooseFile(JFileChooser.DIRECTORIES_ONLY);
        new FileExchangeServer(base, port).start();
    }
}
