package fs.server;

import fs.protocol.FileTreeNode;
import fs.protocol.GetIndex;
import fs.protocol.Request;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

public class GetIndexRequestHandler {

    public static final Logger LOGGER = Logger.getLogger(GetIndexRequestHandler.class.getName());

    /**
     * Handle GetIndex request
     *
     * create response and send to client
     *
     * @param socketChannel
     * @param baseFile
     * @throws IOException
     */
    public static void handleRequest(final SocketChannel socketChannel, ObjectOutputStream oos, final File baseFolder)
            throws IOException {

        assert baseFolder.canRead();
        assert baseFolder.isDirectory();

        LOGGER.info("Handling GetIndex request");

        // create response object
        GetIndex.Response response = new GetIndex.Response(FileTreeNode.ofFolder(baseFolder));

        // send response to client
        oos.writeObject(response);
        oos.flush();

        LOGGER.info("Done handling GetIndex request");
    }

}
