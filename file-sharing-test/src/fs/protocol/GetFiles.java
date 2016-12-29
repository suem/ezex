package fs.protocol;

import java.util.Arrays;
import java.util.IllegalFormatException;

public class GetFiles {

    public static class Request extends fs.protocol.Request {

        public static final long serialVersionUID = 0L;

        /**
         * Array of file paths that have been requested by client
         */
        private String[] fileNames;

        public Request(String[] fileNames) {
            this.fileNames = new String[fileNames.length];
            for (int i = 0; i < fileNames.length; i++) this.fileNames[i] = fileNames[i];
        }

        /**
         * Get fileNames
         *
         * @return value of fileNames
         */
        public String[] getFileNames() {
            return fileNames;
        }

        @Override
        public void validate() throws IllegalRequestException {
            if (fileNames == null) {
                throw new IllegalRequestException("fileNames is null");
            }

            if (Arrays.stream(fileNames).anyMatch(fileName -> fileName == null || fileName.contains(".."))) {
                throw new IllegalRequestException("fileNames contains value that is null or contains \"..\"");
            }
        }
    }

    public static class Response extends fs.protocol.Response {

        public static final long serialVersionUID = 0L;

        private FileInfo[] fileInfos;

        public static Response from(FileInfo... fileInfos) {
            return new Response(fileInfos);
        }

        public Response(FileInfo[] fileInfos) {
            this.fileInfos = fileInfos;
        }

        /**
         * Get fileInfos
         *
         * @return value of fileInfos
         */
        public FileInfo[] getFileInfos() {
            return fileInfos;
        }
    }

}
