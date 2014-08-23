package devicerestmodel.app;

import devicerestmodel.logger.LoggerOut;
import devicerestmodel.representations.DevicePropertyNode;
import devicerestmodel.services.ResourceBase;
import devicerestmodel.types.DeviceServerType;
import devicerestmodel.types.SystemConfigType;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.jdom2.Element;

public abstract class App {

    private final int deviceProxyContactInterval = 3000;
    private App instance = null;
    private Server server;
    private final long startTime = System.currentTimeMillis();
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());
    private boolean deviceProxyContacted = false;
    private final SystemConfigType systemConfig = new SystemConfigType();
    private final ArrayList<PathResourceHolder> pathResources = new ArrayList<>();
    private boolean started = false;

    public abstract Element getConfiguration();

    public final void doInit() {
        addStaticResource("devicerestmodel/resources/jquery.min.js", MediaType.APPLICATION_JSON);
        addStaticResource("devicerestmodel/resources/d3.js", MediaType.APPLICATION_JSON);
        addStaticResource("devicerestmodel/resources/loader.js", MediaType.APPLICATION_JSON);
        Element configuration = getConfiguration();
        try {
            System.out.println("Got Config in server: " + MdUtil.element2XmlString(configuration));
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        instance = this;
        try {
            systemConfig.setXml(configuration);
        } catch (Exception ex) {
            System.out.println("Cannot set device configuration.");
            System.exit(-1);
        }

        if (systemConfig.isDeviceServerIpSet()) {
            try {
                startServer();
            } catch (Exception ex) {
                System.out.println("Cannot start HTTP server with specified configuration.");
                System.exit(-1);
            }
        } else {
            System.out.println("No HTTP server configuration detected.");
            System.exit(-1);
        }

        String serverName = systemConfig.getDeviceName();
        LoggerOut.println(serverName + " server started.\n");
        System.out.println("Try accessing " + getBaseURI() + " in the browser.\n");

        if (systemConfig.isDeviceProxyIpSet()) {
            contactDeviceProxy();
        } else {
            System.out.println("No proxy information detected. Proxy will not be contacted.");
        }

    }

    public void addStaticResource(String path, String mediatype) {
        assert (!started);
        PathResourceHolder nPath = new PathResourceHolder(path, mediatype);
        pathResources.add(nPath);
    }

    public void noExit() {
        final Thread thisThread = Thread.currentThread();
        Thread stop = new Thread() {
            @Override
            public void run() {
                synchronized (thisThread) {
                    thisThread.notify();
                }
                try {
                    thisThread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(stop);
        synchronized (thisThread) {
            try {
                thisThread.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public App getInstance() {
        return instance;
    }

    public abstract DevicePropertyNode getRootNode();

    public SystemConfigType getSystemConfig() {
        return systemConfig;
    }
    
    public void startServer() throws IOException {

        System.out.println("!!!!!!!!! Starting " + getBaseURI());
        ResourceBase base = new ResourceBase(getRootNode(), pathResources);

        server = JettyHttpContainerFactory.createServer(getBaseURI(), base);
        started = true;
    }

    public static void stopServer() {
        Timer haltTimer = new Timer();

        haltTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.exit(0);
            }
        }, 0, 3000);
    }

    private static int getPort(int defaultPort) {
        final String port = System.getProperty("jersey.config.test.container.port");
        if (null != port) {
            try {
                return Integer.parseInt(port);
            } catch (NumberFormatException e) {
                System.out.println("Value of jersey.config.test.container.port property"
                        + " is not a valid positive integer [" + port + "]."
                        + " Reverting to default [" + defaultPort + "].");
            }
        }
        return defaultPort;
    }

    public URI getBaseURI() {
        String deviceServerIpAddress = systemConfig.getDeviceServerIpAddress();
        int deviceServerIpPort = systemConfig.getDeviceServerIpPort();
        return UriBuilder.fromUri("http://" + deviceServerIpAddress + "/").port(getPort(deviceServerIpPort)).build();
    }

    private void contactDeviceProxy() {
        DeviceServerType deviceServer = new DeviceServerType();
        deviceServer.setName(systemConfig.getDeviceName());
        deviceServer.setDescription(systemConfig.getDeviceDescription());
        deviceServer.setServerIpAddress(systemConfig.getDeviceServerIpAddress());
        deviceServer.setServerIpPort(systemConfig.getDeviceServerIpPort());
        Element deviceServerElement = deviceServer.getXml();
        String deviceServerXmlString;
        try {
            deviceServerXmlString = MdUtil.element2XmlString(deviceServerElement);
        } catch (IOException ex) {
            deviceServerXmlString = "";
        }
        System.out.println("deviceServerXmlString: " + deviceServerXmlString);

        deviceProxyContacted = false;
        System.out.print("Device, " + systemConfig.getDeviceName() + ", attempting to contact DeviceProxy at " + systemConfig.getDeviceProxyIpAddress() + ":" + systemConfig.getDeviceProxyIpPort() + " ");
        final String deviceProxyIpAddress = systemConfig.getDeviceProxyIpAddress();
        final int deviceProxyIpPort = systemConfig.getDeviceProxyIpPort();
        final String proxyUri = "http://" + deviceProxyIpAddress + ":" + deviceProxyIpPort + "/DeviceProxy1";
        final byte[] sendContent = deviceServerXmlString.getBytes();
//        final PropertyChangeListener proxyContactListener = new PropertyChangeListener() {
//
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                if (evt.getPropertyName().equals("ContactProxy")) {
//                    ServerResponse serverResponse = (ServerResponse) evt.getNewValue();
//                    if (serverResponse.getException() == null) {
//                        deviceProxyContacted = true;
//                        System.out.println("Proxy successfully contacted.");
//                    } else {
//                        System.out.print(".");
//                    }
//                }
//            }
//        };
//
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                while (!deviceProxyContacted) {
//                    Client.sendRequest(Client.RequestType.Put, proxyUri, "text/xml", null, sendContent, null, 1000, "ContactProxy", proxyContactListener);
//                    try {
//                        Thread.sleep(deviceProxyContactInterval);
//                    } catch (InterruptedException ex) {
//                        Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }
//            }
//        }).start();

    }

    public class PathResourceHolder {

        private final String path;
        private final String Mediatype;

        private PathResourceHolder(String path, String mediatype) {
            this.path = path;
            this.Mediatype = mediatype;
        }

        /**
         * @return the path
         */
        public String getPath() {
            return path;
        }

        /**
         * @return the Mediatype
         */
        public String getMediatype() {
            return Mediatype;
        }

    }

}
