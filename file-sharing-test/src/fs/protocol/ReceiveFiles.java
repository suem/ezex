package fs.protocol;

import java.io.Serializable;

public class ReceiveFiles {

    public static class Request implements Serializable {

    }

    public static class Response implements Serializable {

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
