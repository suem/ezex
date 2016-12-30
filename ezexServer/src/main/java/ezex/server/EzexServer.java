package ezex.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ezex.model.FileTreeNode;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class EzexServer extends Server {

    public EzexServer(File baseDirectory, int port, String key) {
        super(port);

        ContextHandler indexContext = new ContextHandler();
        indexContext.setContextPath("/");
        indexContext.setHandler(new IndexHandler(baseDirectory));

        ContextHandler fileTreeContext = new ContextHandler();
        fileTreeContext.setContextPath("/files");
        fileTreeContext.setHandler(new FileTreeHandler(baseDirectory));

        ContextHandlerCollection contextHandlers = new ContextHandlerCollection();
        contextHandlers.setHandlers(new Handler[] {indexContext, fileTreeContext});

        if (key != null) {
            KeyVerificationWrapper keyVerificationWrapper = new KeyVerificationWrapper(key);
            keyVerificationWrapper.setHandler(contextHandlers);
            this.setHandler(keyVerificationWrapper);
        } else {
            this.setHandler(contextHandlers);
        }
    }

    public static void main( String[] args ) throws Exception {
        File baseDirectory = new File(".");
        EzexServer server = new EzexServer(baseDirectory, 8080, null);
        server.start();
        server.dumpStdErr();
        server.join();
    }

}

class KeyVerificationWrapper extends HandlerWrapper {

    private final String key;

    public KeyVerificationWrapper(String key) {
        assert key != null;
        this.key = key;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String requestKey = request.getParameter("key");
        if (isRequestKeyValid(requestKey)) {
            super.handle(target, baseRequest, request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            baseRequest.setHandled(true);
        }
    }

    private boolean isRequestKeyValid(String requestKey) {
        return key.equals(requestKey);
    }

}

class IndexHandler extends AbstractHandler {

    private final File baseDirectory;

    private final Gson gson;

    public IndexHandler(File baseDirectory) {
        assert baseDirectory != null;
        assert baseDirectory.isDirectory();
        assert baseDirectory.canRead();
        this.baseDirectory = baseDirectory;
        this.gson = new GsonBuilder().create();
    }

    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        FileTreeNode baseNode = FileTreeNode.ofFolder(this.baseDirectory);
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(baseNode));
        baseRequest.setHandled(true);
    }

}

class FileTreeHandler extends HandlerList {

    public FileTreeHandler(File baseDirectory) {
        assert baseDirectory != null;
        assert baseDirectory.isDirectory();
        assert baseDirectory.canRead();

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(baseDirectory.getAbsolutePath());
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setDirAllowed(true);

        this.setHandlers(new Handler[] {resourceHandler, new DefaultHandler()});
    }

}

