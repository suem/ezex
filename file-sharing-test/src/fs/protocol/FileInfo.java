package fs.protocol;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;

/**
 * Contains relevant information about a file.
 */
public class FileInfo implements Serializable {

    public static final long serialVersionUID = 0L;

    /**
     * Name and path of the file relative to the servers base directory.
     * e.g. if the server serves all files from "/home/user/documents"
     * then the file "/home/user/documents/bla/test.txt"
     * has fileName = "/bla/test.txt"
     */
    private final String fileName;

    /**
     * Size of file in bytes.
     */
    private final long fileSize;

    /**
     * Unix timestamp of last modification.
     */
    private final long lastModified;

    /**
     * Create new FileInfo form a file and a base path
     * @param file File that the server serves, e.g. "/home/user/documents/bla/test.txt"
     * @param basePath absolute path to folder that the server serves, e.g. "/home/user/documents"
     */
    public FileInfo(File file, Path basePath) {
        Path filePath = file.toPath();
        this.fileName = "/" + filePath.subpath(basePath.getNameCount(), filePath.getNameCount());
        this.lastModified = file.lastModified();
        this.fileSize = file.length();
    }

    /**
     * Get fileName
     *
     * @return value of fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Get fileSize
     *
     * @return value of fileSize
     */
    public long getFileSize() {
        return fileSize;
    }

    @Override
    public String toString() {
        return fileName + "(" + fileSize + ")";
    }
}
