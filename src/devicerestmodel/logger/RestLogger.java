/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.logger;

import devicerestmodel.htmlhelpers.BasicDocument;
import devicerestmodel.htmlhelpers.ServerEventPropertyChange;
import devicerestmodel.htmlhelpers.ServerEventPropertyChangeXML;
import java.beans.PropertyChangeSupport;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseFeature;

/**
 *
 * @author root
 */
@Path("/logging")
public class RestLogger {

    private final static PropertyChangeSupport sseSupport = new PropertyChangeSupport(new Object());

    public static PropertyChangeSupport getSSESupport() {
        return sseSupport;
    }

    @GET
    @Path("/events")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    public EventOutput getServerSentEvents() {
        final ServerEventPropertyChange eventOutput = new ServerEventPropertyChange(sseSupport);
        return eventOutput;
    }

    @GET
    @Path("/stdout")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    @Consumes("event-stream/xml")
    public EventOutput getServerSentEventsXML() {
        final ServerEventPropertyChangeXML eventOutput = new ServerEventPropertyChangeXML(sseSupport);
        return eventOutput;
    }

    @GET
    @Path("/stdout")
    @Produces(SseFeature.SERVER_SENT_EVENTS)
    @Consumes("event-stream/json")
    public EventOutput getServerSentEventsJson() {
        final ServerEventPropertyChangeXML eventOutput = new ServerEventPropertyChangeXML(sseSupport);
        return eventOutput;
    }

    @GET
    @Path("/stdout")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.TEXT_HTML)
    public Response getStdOut() {
        BasicDocument doc = new BasicDocument();
//        if (refresh != null) {
//            doc.setRefresh(0);
//            doc.addContent(LoggerOut.getString());
//        } else {
        doc.addContent(LoggerOut.getStringNoWait());
//        }

        return Response.status(Response.Status.OK).entity(doc.toString()).build();
    }

}
