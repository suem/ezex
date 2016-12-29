package fs.protocol;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Node that models a folder in the filesystem offered by the server.
 */
public class FileTreeNode implements Serializable {

    public static final long serialVersionUID = 0L;

    /**
     * Array of files in this folder.
     */
    private final FileInfo[] files;

    /**
     * Array of subfolders of this folder.
     */
    private final FileTreeNode[] folders;

     /**
     * Name of the folder
     */
    private final String directoryName;

    public FileTreeNode(String directoryName, FileInfo[] files, FileTreeNode[] folders) {
        this.directoryName = directoryName;
        this.files = files;
        this.folders = folders;
    }

    /**
     * Get files
     *
     * @return value of files
     */
    public FileInfo[] getFiles() {
        return files;
    }

    /**
     * Get folders
     *
     * @return value of folders
     */
    public FileTreeNode[] getFolders() {
        return folders;
    }

    /**
     * Get directoryName
     *
     * @return value of directoryName
     */
    public String getDirectoryName() {
        return directoryName;
    }

    /**
     * Converts a folder of the filesystem to a FileTreeNode that contains the corresponding subtree of the folder provided.
     * @param folder folder to be converted
     * @return
     */
    public static FileTreeNode ofFolder(File folder) {
        assert folder.isDirectory();
        assert folder.canRead();

        FileTreeNode ftn = ofFolderRecursive(folder, folder.toPath());
        return new FileTreeNode("/", ftn.getFiles(), ftn.getFolders());
    }

    /**
     * Recursively builds a tree of FileTreeNode entries for given folder
     * @param folder The folder to traverse
     * @param basePath Absolute path to folder where recursion started
     * @return
     */
    private static FileTreeNode ofFolderRecursive(File folder, Path basePath) {
        assert folder.isDirectory();
        assert folder.canRead();

        FileInfo[] files = Arrays.stream(folder.listFiles(f -> f.isFile() && !f.isHidden() && f.canRead()))
                .map(file -> new FileInfo(file, basePath))
                .toArray(FileInfo[]::new);
        FileTreeNode[] folders = Arrays.stream(folder.listFiles(f -> f.isDirectory() && !f.isHidden() && f.canRead()))
                .map(subFolder -> ofFolderRecursive(subFolder, basePath))
                .toArray(FileTreeNode[]::new);
        return new FileTreeNode(folder.getName(), files, folders);
    }

}
