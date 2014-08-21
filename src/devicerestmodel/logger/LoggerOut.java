/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.logger;

import devicerestmodel.htmlhelpers.GetFunctions;
import devicerestmodel.app.App;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.UriBuilder;

/**
 *
 * @author root
 */
public class LoggerOut {

    private static final StringBuilder outString = new StringBuilder();
    private static int readLength = 0;
    private static final int totalLength = 100000;
    private static URI printURI = URI.create("");
    private static final ExecutorService threadPool = Executors.newSingleThreadExecutor();
    private final static PropertyChangeSupport sseSupport = RestLogger.getSSESupport();
    

    public static void println(final String line) {
        Runnable printTask = new Runnable() {
            @Override
            public void run() {
                synchronized (outString) {
                    outString.append(line).append("<br>");
                    adjustBufferLength();
                }
                sseSupport.firePropertyChange(printURI + "/logging/events", null, line);
            }
            
        };
        threadPool.submit(printTask);
    }

    public static void print(final String line) {
        Runnable printTask = new Runnable() {
            @Override
            public void run() {
                synchronized (outString) {
                    outString.append(line);
                    adjustBufferLength();
                }
                sseSupport.firePropertyChange(printURI + "/logging/events", null, line);
            }
        };
        threadPool.submit(printTask);
    }

    public static void setPrintURI(URI uri) {
        printURI = uri;
    }

    private static void adjustBufferLength() {
        if (outString.length() > totalLength) {
            outString.delete(0, outString.length() - totalLength);
        }
        readLength = outString.length();
    }

    public static String getStringNoWait() {
        synchronized (outString) {
            String ret = outString.toString();
            adjustBufferLength();
            return ret;
        }
    }

}
