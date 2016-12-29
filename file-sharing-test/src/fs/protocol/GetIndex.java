package fs.protocol;

/**
 * GetIndex is a wrapper for GetIndex.Request and GetIndex.Response.
 * A client can request an index of files and folders the server is offering by sending a GetIndex.Request object to the server.
 * The server will respond with the corresponding data wrapped in a GetIndex.Response object.
 */
public class GetIndex {

    public static class Request extends fs.protocol.Request {
        public static final long serialVersionUID = 0L;

        @Override
        public void validate() throws IllegalRequestException { }
    }

    public static class Response extends fs.protocol.Response {

        public static final long serialVersionUID = 0L;

        /**
         * Root of the index of the files offered by the server
         */
        private FileTreeNode fileTreeNode;

        public Response(FileTreeNode fileTreeNode) {
            this.fileTreeNode = fileTreeNode;
        }

        /**
         * Get fileTreeNode
         *
         * @return value of fileTreeNode
         */
        public FileTreeNode getFileTreeNode() {
            return fileTreeNode;
        }
    }

}
