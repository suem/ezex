package fs;

import fs.protocol.FileInfo;
import fs.protocol.ReceiveFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;

/**
 * Created by suem on 25.12.16.
 */
public class Test {

    public static void main(String[] args) throws IOException {
        ReceiveFiles.Response resp = SendServer.getResponse(new File("/tmp/tree"));
        for (FileInfo fileInfo : resp.getFileInfos()) {
            System.out.println(fileInfo);
        }
    }


}
