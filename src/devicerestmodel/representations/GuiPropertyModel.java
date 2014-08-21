/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.representations;

import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.core.Response;
import org.adrianwalker.multilinestring.Multiline;
import org.jdom2.Element;

/**
 *
 * @author root
 */
public abstract class GuiPropertyModel extends DevicePropertyNode implements HtmlInterface, JSONInterface {

    public GuiPropertyModel(String name, PropertyChangeListener listener) {
        super(name, listener);
    }

    public GuiPropertyModel(PropertyChangeListener listener) {
        super(listener);
    }

    /**
     * <!DOCTYPE html>
     * <html>
     * <head>
     * <script src="/resources/devicerestmodel/resources/d3.js"
     * charset="utf-8"></script>
     * <script src="/resources/devicerestmodel/resources/jquery.min.js"
     * charset="utf-8"></script>
     * <script src="/resources/devicerestmodel/resources/loader.js"
     * charset="utf-8"></script>
     * <meta charset="UTF-8">
     * <title>{@link #getName()}</title>
     * </head>
     * <body>
     * <script type="text/javascript"> {@link #getJavacriptFromPaths()}
     * </script> {@link #getMyHTML()}
     * </body>
     * </html>
     *
     */
    @Multiline
    private static String basehtml;

    /**
     *
     */
    @Multiline
    private static String myhtml;

    public String getJavacriptFromPaths() {
        StringBuilder sb = new StringBuilder();
        String[] paths = addPaths();
        for (String mpath : paths) {
            sb.append("$(document).ready(WidgetStarter(\"");
            sb.append(mpath);
            sb.append("\"));\n");
        }

        return sb.toString();
    }

    public String getHTMLDocument() {
        try {
            return substituteVariablesWithMethod(basehtml, this);
        } catch (Exception ex) {
            Logger.getLogger(GuiPropertyModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String getMyHTML() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(substituteVariablesWithMethod(myhtml, this));
        ArrayList<DevicePropertyNode> children = getChildren();
        for (DevicePropertyNode node : children) {
            if (node instanceof GuiPropertyModel) {
                sb.append(((GuiPropertyModel) node).getMyHTML());
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    @Override
    public abstract Response getJSON() throws Exception;

    @Override
    public final Response getHTML() throws Exception {
        return Response.ok().entity(getHTMLDocument()).build();
    }

    public static String substituteVariablesWithMethod(String template, Object object) throws Exception {

        Pattern pattern = Pattern.compile("\\{\\s*\\@link\\s+\\#(.+?)\\(\\)\\s*\\}");
        Matcher matcher = pattern.matcher(template);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            Method match = object.getClass().getMethod(matcher.group(1));
            if (match != null) {
                String replacement = match.invoke(object).toString();
                matcher.appendReplacement(buffer, replacement != null ? Matcher.quoteReplacement(replacement) : "null");
            } else {
                throw new Exception("Match for variable not found " + matcher.group(1));
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

}
