/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.services;

import devicerestmodel.app.App;
import devicerestmodel.htmlhelpers.ServerEventPropertyChange;
import devicerestmodel.htmlhelpers.ServerEventPropertyChangeJSON;
import devicerestmodel.htmlhelpers.ServerEventPropertyChangeXML;
import devicerestmodel.logger.LoggerOut;
import devicerestmodel.logger.RestLogger;
import devicerestmodel.representations.DevicePropertyNode;
import devicerestmodel.representations.HtmlInterface;
import devicerestmodel.representations.JSONInterface;
import devicerestmodel.app.MdUtil;
import java.io.InputStream;
import java.util.ArrayList;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.SseFeature;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.jdom2.Document;
import org.jdom2.Element;

/**
 *
 * @author adam
 */
public final class ResourceBase extends ResourceConfig {

    //private String path; // "helloworld"
//    private String httpMethod; // "GET"
//    private final String baseUri;
    private final DevicePropertyNode top;

    private void addLink(StringBuilder sb, String url) {
        sb.append("<a href=\"").append(url).append("\">");
        sb.append(url);
        sb.append("</a>").append(" <br>");
    }

    public ResourceBase(DevicePropertyNode top, ArrayList<App.PathResourceHolder> staticResources) {
//        this.baseUri = baseUri;
        this.top = top;
        String[] paths = top.addPaths();

        for (String path : paths) {
            try {
                DevicePropertyNode myNode = top.getPropertyNodeFromPath(path);
                if (myNode instanceof JSONInterface) {
                    addGetResourceJSON(path, (JSONInterface) myNode);
                    addPutResourceJSON(path, (JSONInterface) myNode);
                    addEventResourceJSON(path, myNode);
                }
                if (myNode instanceof HtmlInterface) {
                    addGetResourceHtml(path, (HtmlInterface) myNode);
                    addEventResourceHtml(path, myNode);
                }
                addGetResourceXML(path, myNode);
                addPutResourceXML(path, myNode);
                addEventResourceXML(path, myNode);
            } catch (Exception ex) {
            }
        }
        final StringBuilder sb = new StringBuilder();

        sb.append("<html><body>Best Web Page EVER!!!<br><br>");

        addLink(sb, "/resources");
        addLink(sb, "/logging/stdout");
        addLink(sb, "/logging/events");
        for (App.PathResourceHolder holder : staticResources) {
            addLink(sb, "/resources/" + holder.getPath());
            registerStaticResource(holder.getPath(), holder.getMediatype());
        }
        // build parameter content
        for (String parameter : paths) {
            if (!parameter.equals("")) {
                addLink(sb, parameter);
            }
        }

        sb.append("</body></html>");

        // build base content
        final Resource.Builder resourceBuilder = Resource.builder();
        resourceBuilder.path("/resources");

        final ResourceMethod.Builder methodBuilder = resourceBuilder.addMethod("GET");

        methodBuilder.consumes(MediaType.TEXT_XML).handledBy(new Inflector<ContainerRequestContext, String>() {

            @Override
            public String apply(ContainerRequestContext containerRequestContext) {

                return sb.toString();
            }
        });

        final Resource resource = resourceBuilder.build();
        EventServices.setRoot(top);
        registerResources(resource);
        register(MultiPartFeature.class);
        register(SseFeature.class);
        register(RestLogger.class);
        register(EventServices.class);
    }

    public void registerStaticResource(final String path, final String produces) {
        final Resource.Builder resourceBuilder = Resource.builder();
        resourceBuilder.path("/resources/" + path);

        final ResourceMethod.Builder methodBuilder = resourceBuilder.addMethod("GET");
        methodBuilder.produces(produces).handledBy(new Inflector<ContainerRequestContext, Response>() {

            @Override
            public Response apply(ContainerRequestContext containerRequestContext) {
                try {
                    if (containerRequestContext.getMethod().equals("GET")) {
                        System.out.println("Got a GET for " + path);
                        LoggerOut.println("Get a GET for " + path);
                        InputStream in = ClassLoader.getSystemResourceAsStream(path);

                        return Response.ok().type(produces).entity(in).build();
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                } catch (Exception ex) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }

            }
        });

        final Resource resource = resourceBuilder.build();
        registerResources(resource);
    }

    private void addPutResourceXML(final String path, final DevicePropertyNode element) {

        final Resource.Builder resourceBuilder = Resource.builder();
        resourceBuilder.path(path);

        final ResourceMethod.Builder methodBuilder = resourceBuilder.addMethod("PUT");
        methodBuilder.consumes(MediaType.TEXT_XML).produces(MediaType.TEXT_XML).handledBy(new Inflector<ContainerRequestContext, Response>() {

            @Override
            public Response apply(ContainerRequestContext containerRequestContext) {
                try {
                    if (containerRequestContext.getMethod().equals("PUT")) {
                        LoggerOut.println("Got a PUT for " + path);
                        InputStream is = containerRequestContext.getEntityStream();
                        byte[] bb = new byte[65536];
                        int num = is.read(bb);
                        byte[] data = new byte[num];
                        System.arraycopy(bb, 0, data, 0, num);
                        LoggerOut.println(new String(data));
                        Element myElement = MdUtil.xmlString2Element(new String(data));
                        LoggerOut.println("Setting xml to " + path + " Element " + myElement);

                        element.setXml(myElement);
                        return Response.ok().type(MediaType.TEXT_XML).entity(MdUtil.element2XmlString(element.getXml())).build();
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                } catch (Exception ex) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }
            }
        });

        final Resource resource = resourceBuilder.build();
        registerResources(resource);
    }

    private void addGetResourceXML(final String path, final DevicePropertyNode element) {

        final Resource.Builder resourceBuilder = Resource.builder();
        resourceBuilder.path(path);

        final ResourceMethod.Builder methodBuilder = resourceBuilder.addMethod("GET");
        methodBuilder.produces(MediaType.APPLICATION_XML).handledBy(new Inflector<ContainerRequestContext, Response>() {

            @Override
            public Response apply(ContainerRequestContext containerRequestContext) {
                try {
                    if (containerRequestContext.getMethod().equals("GET")) {
                        System.out.println("Got a GET for " + path);
                        LoggerOut.println("Get a GET for " + path);
                        Document myDocument = new Document();
                        myDocument.setRootElement(element.getXml());
                        return Response.ok().type(MediaType.APPLICATION_XML).entity(MdUtil.document2XmlString(myDocument)).build();
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                } catch (Exception ex) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }

            }
        });

        final Resource resource = resourceBuilder.build();
        registerResources(resource);
    }

    private void addEventResourceXML(final String path, final DevicePropertyNode element) {

        LoggerOut.println("Adding resource " + path + "/events");
        final Resource.Builder resourceBuilder = Resource.builder();
//        resourceBuilder.path(path + "/events");
        resourceBuilder.path(path);

        final ResourceMethod.Builder methodBuilder = resourceBuilder.addMethod("GET");
        methodBuilder.consumes("event-stream/xml").produces(SseFeature.SERVER_SENT_EVENTS).handledBy(new Inflector<ContainerRequestContext, EventOutput>() {
//        methodBuilder.produces(SseFeature.SERVER_SENT_EVENTS).handledBy(new Inflector<ContainerRequestContext, EventOutput>() {

            @Override
            public EventOutput apply(ContainerRequestContext containerRequestContext) {
                LoggerOut.println("Setting up event for " + path + "/events");
                try {
                    if (containerRequestContext.getMethod().equals("GET")) {
                        System.out.println("!!!!!!!!!!!!! Set up event for " + path + "/events");
                        LoggerOut.println("Set up event for " + path + "/events");
                        ServerEventPropertyChangeXML eventOutput = new ServerEventPropertyChangeXML(element.getPropertyChangeSupport());
                        return eventOutput;
                    }
                } catch (Exception ex) {
                    System.out.println("It is not working " + path + "/events");
                }
                return null;
            }
        });

        final Resource resource = resourceBuilder.build();
        registerResources(resource);
    }

    private void addPutResourceJSON(final String path, final JSONInterface element) {

        final Resource.Builder resourceBuilder = Resource.builder();
        resourceBuilder.path(path);

        final ResourceMethod.Builder methodBuilder = resourceBuilder.addMethod("PUT");
        methodBuilder.produces(MediaType.APPLICATION_JSON).consumes(MediaType.APPLICATION_JSON).handledBy(new Inflector<ContainerRequestContext, Response>() {

            @Override
            public Response apply(ContainerRequestContext containerRequestContext) {
                try {
                    if (containerRequestContext.getMethod().equals("PUT")) {
                        LoggerOut.println("Got a PUT for " + path);
                        InputStream is = containerRequestContext.getEntityStream();
                        byte[] bb = new byte[65536];
                        int num = is.read(bb);
                        byte[] data = new byte[num];
                        System.arraycopy(bb, 0, data, 0, num);
                        String jsonString = new String(data);
                        LoggerOut.println("Setting json to " + path + " Element " + jsonString);

                        element.setJSON(jsonString);
                        return Response.ok().type(MediaType.APPLICATION_JSON).entity(element.getJSON()).build();
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                } catch (Exception ex) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }
            }
        });

        final Resource resource = resourceBuilder.build();
        registerResources(resource);
    }

    private void addGetResourceJSON(final String path, final JSONInterface element) {

        final Resource.Builder resourceBuilder = Resource.builder();
        resourceBuilder.path(path);

        final ResourceMethod.Builder methodBuilder = resourceBuilder.addMethod("GET");
        methodBuilder.produces(MediaType.APPLICATION_JSON).handledBy(new Inflector<ContainerRequestContext, Response>() {

            @Override
            public Response apply(ContainerRequestContext containerRequestContext) {
                try {
                    if (containerRequestContext.getMethod().equals("GET")) {
                        System.out.println("Got a json GET for " + path);
                        LoggerOut.println("Get a GET for " + path);
                        return element.getJSON();
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                } catch (Exception ex) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }

            }
        });

        final Resource resource = resourceBuilder.build();
        registerResources(resource);
    }

    private void addEventResourceJSON(final String path, final DevicePropertyNode element) {

        LoggerOut.println("Adding resource " + path);
        final Resource.Builder resourceBuilder = Resource.builder();
        resourceBuilder.path(path);

        final ResourceMethod.Builder methodBuilder = resourceBuilder.addMethod("GET");
        methodBuilder.consumes("event-stream/json").produces(SseFeature.SERVER_SENT_EVENTS).handledBy(new Inflector<ContainerRequestContext, EventOutput>() {

            @Override
            public EventOutput apply(ContainerRequestContext containerRequestContext) {
                LoggerOut.println("Setting up event for " + path + "/events");
                try {
                    if (containerRequestContext.getMethod().equals("GET")) {
                        System.out.println("!!!!!!!!!!!!! Set up event for " + path + "/events");
                        LoggerOut.println("Set up event for " + path + "/events");
                        ServerEventPropertyChangeJSON eventOutput = new ServerEventPropertyChangeJSON(element.getPropertyChangeSupport());
                        return eventOutput;
                    }
                } catch (Exception ex) {
                    System.out.println("It is not working " + path + "/events");
                }
                return null;
            }
        });

        final Resource resource = resourceBuilder.build();
        registerResources(resource);
    }

    private void addGetResourceHtml(final String path, final HtmlInterface element) {

        final Resource.Builder resourceBuilder = Resource.builder();
        resourceBuilder.path(path);

        final ResourceMethod.Builder methodBuilder = resourceBuilder.addMethod("GET");
        methodBuilder.produces(MediaType.TEXT_HTML).handledBy(new Inflector<ContainerRequestContext, Response>() {

            @Override
            public Response apply(ContainerRequestContext containerRequestContext) {
                try {
                    if (containerRequestContext.getMethod().equals("GET")) {
                        System.out.println("Got a GET for " + path);
                        LoggerOut.println("Get a GET for " + path);
                        return element.getHTML();
                    } else {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                } catch (Exception ex) {
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                }

            }
        });

        final Resource resource = resourceBuilder.build();
        registerResources(resource);
    }

    private void addEventResourceHtml(final String path, final DevicePropertyNode element) {

        LoggerOut.println("Adding resource " + path);
        final Resource.Builder resourceBuilder = Resource.builder();
        resourceBuilder.path(path);

        final ResourceMethod.Builder methodBuilder = resourceBuilder.addMethod("GET");
        methodBuilder.consumes("event-stream/html").produces(SseFeature.SERVER_SENT_EVENTS).handledBy(new Inflector<ContainerRequestContext, EventOutput>() {

            @Override
            public EventOutput apply(ContainerRequestContext containerRequestContext) {
                LoggerOut.println("Setting up event for " + path + "/events");
                try {
                    if (containerRequestContext.getMethod().equals("GET")) {
                        System.out.println("!!!!!!!!!!!!! Set up event for " + path + "/events");
                        LoggerOut.println("Set up event for " + path + "/events");
                        ServerEventPropertyChange eventOutput = new ServerEventPropertyChange(element.getPropertyChangeSupport());
                        return eventOutput;
                    }
                } catch (Exception ex) {
                    System.out.println("It is not working " + path + "/events");
                }
                return null;
            }
        });

        final Resource resource = resourceBuilder.build();
        registerResources(resource);
    }

}
