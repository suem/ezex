package fs.protocol;

import java.io.File;
import java.io.Serializable;

public class FileInfo implements Serializable {

    private String fileName;

    private long fileSize;

    public FileInfo(File file) {
        this(file.getName(), file.length());
    }

    public FileInfo(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
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
