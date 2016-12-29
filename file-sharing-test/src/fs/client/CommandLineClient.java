package fs.client;

import fs.GuiStuff;
import fs.protocol.*;

import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

public class CommandLineClient {

    public static final long CHUNK_SIZE = 1024 * 1024; // 1MB chunks
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        String addr = args[0];
        int port = Integer.parseInt(args[1]);

        try(SocketChannel inputChannel = SocketChannel.open(new InetSocketAddress(addr, port))) {
            ObjectOutputStream oos = new ObjectOutputStream(Channels.newOutputStream(inputChannel));
            ObjectInputStream ois = new ObjectInputStream(Channels.newInputStream(inputChannel));
            Scanner scanner = new Scanner(System.in);
            FileTreeNode indexNode = null;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if ("GetIndex".equals(line.trim())) {
                    oos.writeObject(new GetIndex.Request());
                    oos.flush();
                    GetIndex.Response response = (GetIndex.Response) ois.readObject();
                    indexNode = response.getFileTreeNode();
                    printIndex(response.getFileTreeNode());
                } else if (line.startsWith("GetFiles")) {
                    if (indexNode == null) {
                        System.out.println("Request index first");
                        continue;
                    }

                    File dest = GuiStuff.chooseFile(JFileChooser.DIRECTORIES_ONLY);
                    GetFiles.Request req = new GetFiles.Request(Arrays.stream(indexNode.getFiles()).map(FileInfo::getFileName).toArray(String[]::new));

                    for (String s : req.getFileNames()) {
                        System.out.println("Requesting: " + s);
                    }

                    oos.writeObject(req);
                    oos.flush();

                    GetFiles.Response resp = (GetFiles.Response) ois.readObject();

                    for (FileInfo fileInfo : resp.getFileInfos()) {
                        System.out.println("Receiving: " + fileInfo);

                        File target = new File(dest, fileInfo.getFileName());
                        target.getParentFile().mkdirs();
                        target.createNewFile();

                        try (FileChannel targetChannel = new FileOutputStream(target).getChannel()) {

                            int progressbar = 0;

                            long toSend = fileInfo.getFileSize();
                            long position = 0;
                            while (toSend > 0) {
                                long sentBytes = targetChannel.transferFrom(inputChannel, position, Math.min(CHUNK_SIZE, toSend));
                                position += sentBytes;
                                toSend -= sentBytes;

                                int percentage = (int) ((float) position / (float)fileInfo.getFileSize() * 100f);
                                int progressbarDiff = percentage - progressbar;
                                IntStream.range(0, progressbarDiff).forEach(i -> System.out.print("="));
                                progressbar += progressbarDiff;

                            }
                            System.out.println(" done");
                        }

                    }




                } else {
                    System.out.println("Unknown command");
                }
            }
        }

    }

    private static void printIndex(FileTreeNode node) {
        System.out.println(node.getDirectoryName());
        for (FileInfo fileInfo : node.getFiles()) {
            System.out.println(fileInfo);
        }
        for (FileTreeNode fileTreeNode : node.getFolders()) {
            printIndex(fileTreeNode);
        }
    }

}
