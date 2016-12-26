package fs;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import fs.protocol.FileInfo;
import fs.protocol.ReceiveFiles;

public class ReceiveClient {
    public static final long CHUNK_SIZE = 1024 * 1024; // 1MB chunks

    public static void receiveFiles(InetSocketAddress serverAddress, File destinationDir) throws IOException, ClassNotFoundException {

        Log.i("INFO: " ,"Receiving into: " + destinationDir);

        try(SocketChannel inputChannel = SocketChannel.open(serverAddress)) {

            InputStream in = Channels.newInputStream(inputChannel);

            ObjectInputStream ois = new ObjectInputStream(in);

            ReceiveFiles.Response response = (ReceiveFiles.Response) ois.readObject();

            for (FileInfo fileInfo : response.getFileInfos()) {
                Log.i("INFO: ", "Receiving file: " + fileInfo);

                // Check if file exists and if yes, create a new distinct filename
                File file = new File(destinationDir, fileInfo.getFileName());
                if (file.exists()) {
                    System.err.println(file + " already exists, saving under _NEW");
                    file = new File(destinationDir, fileInfo.getFileName() + "_NEW");
                }

                // Create parent folders if they do not exist yet
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();

                // Read files from socket
                try (FileChannel targetChannel = new FileOutputStream(file).getChannel()) {

                    long toSend = fileInfo.getFileSize();
                    long position = 0;
                    while (toSend > 0) {
                        long sentBytes = targetChannel.transferFrom(inputChannel, position, Math.min(CHUNK_SIZE, toSend));
                        position += sentBytes;
                        toSend -= sentBytes;

                    }
                }
            }

        }
        Log.i("INFO: ", "Done");
    }

}
