package fs;

import fs.protocol.FileTreeNode;

import java.io.File;
import java.io.IOException;

/**
 * Created by suem on 25.12.16.
 */
public class Test {

    public static void main(String[] args) throws IOException {

        FileTreeNode node = FileTreeNode.ofFolder(new File("/tmp/tree"));

        System.out.println(node);

    }


}
