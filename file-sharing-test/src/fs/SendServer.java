package fs;

import fs.protocol.FileInfo;
import fs.protocol.GetIndex;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.function.BiConsumer;

public class SendServer {

//    public static final long CHUNK_SIZE = 100 * 1024 * 1024; // 100MB chunks

//    public static void main(String[] args) throws IOException {

//        File base = GuiStuff.chooseFile(JFileChooser.FILES_AND_DIRECTORIES);
//        if (base == null) return;
//
//        System.out.println("Sending from: " + base);
//
//        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
//        serverSocketChannel.bind(new InetSocketAddress(9999));
//
//        GetIndex.Response response = getResponse(base);
//
//        System.out.println("Waiting for connection...");
//        try(SocketChannel targetChannel = serverSocketChannel.accept()) {
//            ObjectOutputStream ous = new ObjectOutputStream(Channels.newOutputStream(targetChannel));
//            ous.writeObject(response);
//            ous.flush();
//
//
//
//            for (FileInfo fileInfo : response.getFileInfos()) {
//                System.out.println("Sending file: " + fileInfo);
//                File file = new File(base, fileInfo.getFileName());
//                try(FileChannel sourceChannel = new FileInputStream(file).getChannel()) {
//                    long toSend = fileInfo.getFileSize();
//                    long position = 0;
//                    while (toSend > 0) {
//                        long sentBytes = sourceChannel.transferTo(position, toSend, targetChannel);
//                        position += sentBytes;
//                        toSend -= sentBytes;
//                    }
//                }
//            }
//
//        }
//
//        System.out.println("done");
//    }
//
//    public static GetIndex.Response getResponse(File base) throws IOException {
//
//        if (base.isFile()) {
//            FileInfo i = new FileInfo(base);
//            return GetIndex.Response.from(new FileInfo(base));
//        }
//
//        Path startingDir = FileSystems.getDefault().getPath(base.getAbsolutePath());
//        LinkedList<FileInfo> fileInfoList = new LinkedList<>();
//
//        FileWalkVisitor visitor = new FileWalkVisitor((path, info) -> {
//            String fileName = path.subpath(startingDir.getNameCount(), path.getNameCount()).toString();
//            long fileSize = info.size();
//            fileInfoList.add(new FileInfo(fileName, fileSize));
//        });
//
//        Files.walkFileTree(startingDir, visitor);
//        FileInfo[] fileInfos = new FileInfo[fileInfoList.size()];
//
//        return new GetIndex.Response(fileInfoList.toArray(fileInfos));
//
//    }
//
//    private static class FileWalkVisitor extends SimpleFileVisitor<Path> {
//
//        private BiConsumer<Path, BasicFileAttributes> fileVisitor;
//
//        public FileWalkVisitor(BiConsumer<Path, BasicFileAttributes> fileVisitor) {
//            this.fileVisitor = fileVisitor;
//        }
//
//        @Override
//        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//            fileVisitor.accept(file, attrs);
//            return FileVisitResult.CONTINUE;
//        }
//
//        @Override
//        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
//            System.err.println("Failed to read file: " + file.getFileName());
//            return FileVisitResult.SKIP_SUBTREE;
//        }
//    }
}
