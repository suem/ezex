package fs;

import com.sun.imageio.spi.InputStreamImageInputStreamSpi;
import fs.protocol.FileInfo;
import fs.protocol.ReceiveFiles;

import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.stream.IntStream;

public class ReceiveClient {
    public static final long CHUNK_SIZE = 1024 * 1024; // 1MB chunks

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        File destinationDir = GuiStuff.chooseFile(JFileChooser.DIRECTORIES_ONLY);
        if (destinationDir == null) return;

        String addr = args.length == 2 ? args[1] : "localhost";

        System.out.println("Receiving into: " + destinationDir);

        try(SocketChannel inputChannel = SocketChannel.open(new InetSocketAddress(addr, 9999))) {

            InputStream in = Channels.newInputStream(inputChannel);

            ObjectInputStream ois = new ObjectInputStream(in);

            ReceiveFiles.Response response = (ReceiveFiles.Response) ois.readObject();

            for (FileInfo fileInfo : response.getFileInfos()) {
                System.out.println("Receiving file: " + fileInfo);

                File file = new File(destinationDir, fileInfo.getFileName());
                if (file.exists()) {
                    System.err.println(file + " already exists, saving under _NEW");
                    file = new File(destinationDir, fileInfo.getFileName() + "_NEW");
                }

                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();

                try (FileChannel targetChannel = new FileOutputStream(file).getChannel()) {

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
                    System.out.println();
                }
            }

        }

        System.out.println("Done");
    }

}
