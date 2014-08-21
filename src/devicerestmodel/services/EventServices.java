/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.services;

import devicerestmodel.htmlhelpers.ServerEventPropertyChangeXML;
import devicerestmodel.representations.DevicePropertyNode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseFeature;

/**
 *
 * @author root
 */
@Path("/")
public class EventServices {

    private static PropertyChangeSupport sseSupport = null;

    public static void setRoot(DevicePropertyNode rootNode) {
        sseSupport = rootNode.getPropertyChangeSupport();
    }

    @GET
    @Path("/events")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput getServerSentEvents() {
        if (sseSupport != null) {
            final ServerEventPropertyChangeXML eventOutput = new ServerEventPropertyChangeXML(sseSupport);
            return eventOutput;
        } else {
            return null;
        }
    }

}
