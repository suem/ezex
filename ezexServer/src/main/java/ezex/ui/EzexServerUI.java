package ezex.ui;

import ezex.server.EzexServer;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Optional;

public class EzexServerUI extends Application {

    public static final int port = 8080;

    private Button button = new Button();
    private ImageView imageView = new ImageView();

    private EzexServer server;

    public static void main(String[] args) {
        launch(args);
    }

    public synchronized void startServer(File baseDirectory) {

        if (server != null && server.isRunning()) {
            return;
        }

        try {
            server = new EzexServer(baseDirectory, port, null);
            server.start();
            server.dumpStdErr();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public File chooseDirectory(Stage stage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open directory to share");
        File directory = directoryChooser.showDialog(stage);
        return directory;
    }


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Ezex Server");

        primaryStage.setOnCloseRequest(event -> {
            if (server != null && server.isRunning()) {
                try {
                    server.stop();
                    server.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        button.setText("Start Server");
        button.setOnAction(event -> {
            button.setDisable(true);
            if (server != null && server.isRunning()) {
                try {
                    server.stop();
                    server.join();
                    server = null;
                    imageView.setImage(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                button.setText("Start Server");
            } else {
                File baseDir = chooseDirectory(primaryStage);
                if (baseDir != null) {
                    startServer(baseDir);
                    button.setText("Stop Server");

                    try {
                        String addr = "https://" + getLocalHostLANAddress().getHostAddress() + ":" + port;
                        System.out.println(addr);
                        File qrFile = QRCode.from(addr).withSize(400, 400).file();
                        Image qrImage = new Image(qrFile.toURI().toString());
                        imageView.setImage(qrImage);
                        imageView.setFitHeight(400);
                        imageView.setFitWidth(400);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }


                }
            }
            button.setDisable(false);
        });

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(button);
        borderPane.setCenter(imageView);

        primaryStage.setScene(new Scene(borderPane, 500, 500));
        primaryStage.show();
    }


    /**
     * Returns an <code>InetAddress</code> object encapsulating what is most likely the machine's LAN IP address.
     * <p/>
     * This method is intended for use as a replacement of JDK method <code>InetAddress.getLocalHost</code>, because
     * that method is ambiguous on Linux systems. Linux systems enumerate the loopback network interface the same
     * way as regular LAN network interfaces, but the JDK <code>InetAddress.getLocalHost</code> method does not
     * specify the algorithm used to select the address returned under such circumstances, and will often return the
     * loopback address, which is not valid for network communication. Details
     * <a href="http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037">here</a>.
     * <p/>
     * This method will scan all IP addresses on all network interfaces on the host machine to determine the IP address
     * most likely to be the machine's LAN address. If the machine has multiple IP addresses, this method will prefer
     * a site-local IP address (e.g. 192.168.x.x or 10.10.x.x, usually IPv4) if the machine has one (and will return the
     * first site-local address if the machine has more than one), but if the machine does not hold a site-local
     * address, this method will return simply the first non-loopback address found (IPv4 or IPv6).
     * <p/>
     * If this method cannot find a non-loopback address using this selection algorithm, it will fall back to
     * calling and returning the result of JDK method <code>InetAddress.getLocalHost</code>.
     * <p/>
     *
     * @throws UnknownHostException If the LAN address of the machine cannot be found.
     */
    private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {

                        if (inetAddr.isSiteLocalAddress()) {
                            // Found non-loopback site-local address. Return it immediately...
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // Found non-loopback address, but not necessarily site-local.
                            // Store it as a candidate to be returned if site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                            // only the first. For subsequent iterations, candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other non-loopback address.
                // Server might have a non-site-local address assigned to its NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress;
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost() returns...
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

}
