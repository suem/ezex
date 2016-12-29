package fs.server;

import fs.protocol.FileInfo;
import fs.protocol.GetFiles;
import fs.protocol.Request;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Logger;

public class GetFilesRequestHandler {

    public static final Logger LOGGER = Logger.getLogger(GetFilesRequestHandler.class.getName());

    public static final long CHUNK_SIZE = 100 * 1024 * 1024; // 100MB

    public static void handleRequest(final SocketChannel socketChannel, final ObjectOutputStream oos, final GetFiles.Request request, final File baseFolder)
            throws IOException{

        LOGGER.info("Handling GetFiles request");

        Path basePath = baseFolder.toPath();

        // create response object
        FileInfo[] fileInfos = Arrays.stream(request.getFileNames())
                        .map(fileName -> new File(baseFolder, fileName))
                        .filter(file -> file.exists() && file.isFile() && file.canRead() && !file.isHidden())
                        .map(file -> new FileInfo(file, basePath)).toArray(FileInfo[]::new);

        GetFiles.Response response = new GetFiles.Response(fileInfos);

        LOGGER.info("Sending list of files");

        // send response to server
        oos.writeObject(response);
        oos.flush();


        // send files to server
        for (FileInfo fileInfo : fileInfos) {
            LOGGER.info("Sending file: " + fileInfo);
            File file = new File(baseFolder, fileInfo.getFileName());
            try(FileChannel sourceChannel = new FileInputStream(file).getChannel()) {
                long toSend = file.length();
                long position = 0;
                while (toSend > 0) {
                    long sentBytes = sourceChannel.transferTo(position, toSend, socketChannel);
                    position += sentBytes;
                    toSend -= sentBytes;
                }
            }
        }

        LOGGER.info("GetFiles request handled.\n");

    }

}
