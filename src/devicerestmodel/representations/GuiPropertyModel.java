/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.representations;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.core.Response;
import org.adrianwalker.multilinestring.Multiline;

/**
 *
 * @author root
 */
public abstract class GuiPropertyModel extends DevicePropertyNode implements HtmlInterface, JSONInterface {

    private int xpos = 0;
    private int ypos = 0;
    private int height = 100;
    private int width = 100;
    private int zindex = 1;
    private int padding = 0;
    private Color backgroundColor = null;

    public GuiPropertyModel(String name, DevicePropertyNode parent) {
        super(name, parent);
    }

    public GuiPropertyModel(DevicePropertyNode parent) {
        super(parent);
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
     * <script type="text/javascript">
     * function GuiPropertyModel(context) { if (context !== undefined) {
     * this.myParent = context.parent || undefined; } alert("Made it " +
     * context); GuiPropertyModel.prototype.showAlert = function(){
     * alert(this.myParent); } }
     *
     * {@link #getJavascriptPrototypes()}
     * {@link #getJavacriptFromPaths()} $(document).ready(function(){ var
     * testbase = new GuiPropertyModel(); var testgui = new
     * GUI({parent:testbase,unused:true}); testgui.showAlert();});
     * </script> {@link #getMyHTML()}
     * </body>
     * </html>
     *
     */
    @Multiline
    private static String basehtml;

    /**
     *
     * <div id="{@link #getName()}" class="{@link #getClassname()}"
     * style="{@link #getXposAttr()}{@link #getYposAttr()} {@link
     * #getHeightAttr()}{@link #getWidthAttr()}{@link #getZIndexAttr()} {@link
     * #getPaddingAttr()}{@link #getBackgroundColorAttr()} position: absolute">
     *
     */
    @Multiline
    private static String myhtml;

    public String getClassname() {
        return this.getClass().getSimpleName();
    }

    public String getJavascriptPrototypes() {
        StringBuilder sb = new StringBuilder();
        HashMap<String, GuiPropertyModel> mymap = new HashMap<>();
        findChildren(this, mymap);
        for (GuiPropertyModel classes : mymap.values()) {
            try {
                sb.append(getChildJavascript(classes));
            } catch (Exception ex) {
                Logger.getLogger(GuiPropertyModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return sb.toString();
    }

    private void findChildren(GuiPropertyModel parent, HashMap<String, GuiPropertyModel> mymap) {
        mymap.put(parent.getClassname(), parent);
        for (DevicePropertyNode node : parent.getChildren()) {
            if (node instanceof GuiPropertyModel) {
                findChildren((GuiPropertyModel) node, mymap);
            }
        }
    }

    private String getChildJavascript(GuiPropertyModel myClass) throws Exception {
        StringBuilder sb = new StringBuilder();
        Class<?> extended = myClass.getClass().getSuperclass();
        sb.append("function ");
        sb.append(myClass.getClassname());
        sb.append("(context) {\n");
        if (extended.isAssignableFrom(GuiPropertyModel.class)) {
            sb.append(extended.getSimpleName());
            sb.append(".call(this, context);\n");
        }
        sb.append(myClass.getJSONFunction());

        sb.append("}\n");

        if (extended.isAssignableFrom(GuiPropertyModel.class)) {
            sb.append(myClass.getClassname());
            sb.append(".prototype = Object.create(");
            sb.append(extended.getSimpleName());
            sb.append(".prototype);\n");
            sb.append(myClass.getClassname());
            sb.append(".prototype.constructor = ");
            sb.append(myClass.getClassname());
            sb.append(";\n");
        }
        return sb.toString();
    }

    public String getClassJavacript(GuiPropertyModel myNode) {
        StringBuilder sb = new StringBuilder();
        try {
            if (myNode instanceof GuiPropertyModel) {
                GuiPropertyModel myGui = (GuiPropertyModel) myNode;
                sb.append("var ");
                sb.append(myGui.getName());
                sb.append(" = new function() { \n");
                sb.append(myGui.getJSONFunction());
                sb.append("\n");
                sb.append("this.loadJS = function() { \n");
                sb.append("main()");
                sb.append("};\n");
                sb.append("};\n");
                sb.append("$(document).ready(");
                sb.append(myGui.getName());
                sb.append(".loadJS() \n");
                sb.append(");\n");
            }
//                sb.append("$(document).ready(WidgetStarter(\"");
//                sb.append(mpath);
//                sb.append("\", function() {\n");
//                sb.append("loadJS();\n");
//                sb.append("        }));\n");
        } catch (Exception ex) {
            Logger.getLogger(GuiPropertyModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sb.toString();
    }

    public String getJavacriptFromPaths() {
        StringBuilder sb = new StringBuilder();
        String[] paths = addPaths();
        for (String mpath : paths) {
            try {
                DevicePropertyNode myNode = getPropertyNodeFromPath(mpath);
                if (myNode instanceof GuiPropertyModel) {
                    GuiPropertyModel myGui = (GuiPropertyModel) myNode;
                    sb.append("var ");
                    sb.append(myGui.getName());
                    sb.append(" = new function() { \n");
                    sb.append(myGui.getJSONFunction());
                    sb.append("\n");
                    sb.append("this.loadJS = function() { \n");
                    sb.append("main()");
                    sb.append("};\n");
                    sb.append("};\n");
                    sb.append("$(document).ready(");
                    sb.append(myGui.getName());
                    sb.append(".loadJS() \n");
                    sb.append(");\n");
                }
//                sb.append("$(document).ready(WidgetStarter(\"");
//                sb.append(mpath);
//                sb.append("\", function() {\n");
//                sb.append("loadJS();\n");
//                sb.append("        }));\n");
            } catch (Exception ex) {
                Logger.getLogger(GuiPropertyModel.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    public String getD3Ref() {
        return "d3.select(\"div#" + getName() + "\")";
    }

    public final String getMyHTML() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(substituteVariablesWithMethod(myhtml, this));
        ArrayList<DevicePropertyNode> children = getChildren();
        for (DevicePropertyNode node : children) {
            if (node instanceof GuiPropertyModel) {
                sb.append(((GuiPropertyModel) node).getMyHTML());
                sb.append("\n");
            }
        }
        sb.append("</div>");

        return sb.toString();
    }

    public abstract String getJSONFunction() throws Exception;

    @Override
    public final Response getJSON() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("var ");
        sb.append(getName());
        sb.append(" = new function() { \n");
        sb.append(getJSONFunction());
        sb.append("\n");
        sb.append("this.loadJS = function() { \n");
        sb.append("main()");
        sb.append("};\n");
        sb.append("};\n");
        sb.append("function loadJS() { \n");
        sb.append(getName());
        sb.append(".loadJS() \n");
        sb.append("} \n");
        return Response.ok().entity(sb.toString()).build();
    }

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

    public String getXposAttr() {
        return "left: " + xpos + "px; ";
    }

    /**
     * @return the xpos
     */
    public int getXpos() {
        return xpos;
    }

    /**
     * @param xpos the xpos to set
     */
    public void setXpos(int xpos) {
        this.xpos = xpos;
    }

    public String getYposAttr() {
        return "top: " + ypos + "px; ";
    }

    /**
     * @return the ypos
     */
    public int getYpos() {
        return ypos;
    }

    /**
     * @param ypos the ypos to set
     */
    public void setYpos(int ypos) {
        this.ypos = ypos;
    }

    public String getHeightAttr() {
        if (height != 0) {
            return "height: " + height + "px; ";
        }
        return "";
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    public String getWidthAttr() {
        if (width != 0) {
            return "width: " + width + "px; ";
        }
        return "";
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    public String getZIndexAttr() {
        return "z-index: " + zindex + "; ";
    }

    /**
     * @return the zindex
     */
    public int getZIndex() {
        return zindex;
    }

    /**
     * @param zpos the zindex to set
     */
    public void setZIndex(int zpos) {
        this.zindex = zpos;
    }

    public String getPaddingAttr() {
        if (padding != 0) {
            return "padding: " + padding + "em; ";
        }
        return "";
    }

    /**
     * @return the padding
     */
    public int getPadding() {
        return padding;
    }

    /**
     * @param padding the padding to set
     */
    public void setPadding(int padding) {
        this.padding = padding;
    }

    private String getColorString(int c) {
        String cstring = String.format("%x", c);
        if (cstring.length() < 2) {
            cstring = "0" + cstring;
        }
        return cstring;
    }

    private String getInternetColor(Color c) {

        String cstring = getColorString(c.getRed());
        cstring += getColorString(c.getGreen());
        cstring += getColorString(c.getBlue());

        return cstring;
    }

    public String getBackgroundColorAttr() {
        if (backgroundColor != null) {
            return "background-color: #" + getInternetColor(backgroundColor) + "; ";
        }
        return "";
    }

    /**
     * @return the color
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @param color the color to set
     */
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

}
