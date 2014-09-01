/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.representations;

import devicerestmodel.app.MdUtil;
import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.core.Response;

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
    
    private final HashMap<String, Method> baseMethods = new HashMap<>();
    private final HashMap<String, Method> myMethods = new HashMap<>();
    private static final String basehtml
            = MdUtil.convertStreamToString("devicerestmodel/representations/GuiPropertyModel.html");

    public GuiPropertyModel(String name, DevicePropertyNode parent) {
        super(name, parent);
        loadFields();
    }

    public GuiPropertyModel(DevicePropertyNode parent) {
        super(parent);
        loadFields();
    }

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

    public String getBaseContextMethods() {
        StringBuilder sb = new StringBuilder();
        for (String key : baseMethods.keySet()) {
            sb.append("GuiPropertyModel.prototype.")
                    .append(baseMethods.get(key).getName()).append(" = function() {\n");
            sb.append("return this.").append(key).append(";\n}\n");
        }
        return sb.toString();
    }

    public String getContextMethods() {
        StringBuilder sb = new StringBuilder();
        for (String key : myMethods.keySet()) {
            sb.append(this.getClassname());
            sb.append(".prototype.")
                    .append(myMethods.get(key).getName()).append(" = function() {\n");
            sb.append("return this.").append(key).append(";\n}\n");
        }
        return sb.toString();
    }

    public String getContextCtr() {
        StringBuilder sb = new StringBuilder();
        if (myMethods.size() > 0) {
            sb.append("if (context !== undefined) {\n");
            for (String key : myMethods.keySet()) {
                sb.append("\nthis.").append(key).append(" = context.")
                        .append(key).append(" || undefined;");
            }
            sb.append("\n}");
        }
        return sb.toString();
    }

    public String getBaseContextCtr() {
        StringBuilder sb = new StringBuilder();
        for (String key : baseMethods.keySet()) {
            sb.append("\nthis.").append(key).append(" = context.")
                    .append(key).append(" || undefined;");
        }
        return sb.toString();
    }

    private String getChildJavascript(GuiPropertyModel myClass) throws Exception {
        StringBuilder sb = new StringBuilder();
        Class<?> extended = myClass.getClass().getSuperclass();
        sb.append("\nfunction ");
        sb.append(myClass.getClassname());
        sb.append("(context) {\n");
        // call the super constructor
        if (extended.isAssignableFrom(GuiPropertyModel.class)) {
            sb.append(extended.getSimpleName());
            sb.append(".call(this, context);\n");
        }
        sb.append(myClass.getContextCtr());

        // Get a reference to base object
        // can't use this inside functions
        sb.append("\nvar REF = this;\n");

        sb.append(myClass.getJSONFunction());

        sb.append("\n}\n");

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
        sb.append(myClass.getContextMethods());

        return sb.toString();
    }

    private void loadFields() {

        ArrayList<String> baseFields = new ArrayList<>();
        for (Field field : GuiPropertyModel.class.getDeclaredFields()) {
            if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                String f = field.getName().toLowerCase();
                baseFields.add(f);
            }
        }
        for (Method method : GuiPropertyModel.class.getDeclaredMethods()) {
            if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                String m = method.getName();
                if (m.startsWith("get")) {
                    m = m.replaceFirst("get", "").toLowerCase();

                    for (String match : baseFields) {
                        if (m.equals(match)) {
//                                System.out.println("Method is " + method.getName() + " field " + match);
                            baseMethods.put(match, method);
                            break;
                        }
                    }
                }
            }
        }

        Class<?> extern = this.getClass();
        while (!extern.equals(GuiPropertyModel.class)) {
            ArrayList<String> fields = new ArrayList<>();
            for (Field field : extern.getDeclaredFields()) {
                if (!java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                    String f = field.getName().toLowerCase();
                    fields.add(f);
                }
            }
            for (Method method : extern.getDeclaredMethods()) {
                if (!java.lang.reflect.Modifier.isStatic(method.getModifiers())) {
                    String m = method.getName();
                    if (m.startsWith("get")) {
                        m = m.replaceFirst("get", "").toLowerCase();

                        for (String match : fields) {
                            if (m.equals(match)) {
                                System.out.println("Adding method " + method.getName() + " key " + match);
                                myMethods.put(match, method);
                                break;
                            }
                        }
                    }
                }
            }
            extern = extern.getSuperclass();
        }
    }

    public String getContext() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n{d3ref:");
        sb.append(getD3Ref());
        sb.append(",path:\"").append(getPathRef()).append("\"");
        for (String key : baseMethods.keySet()) {
            Method method = baseMethods.get(key);
            try {
                String mymeth = method.invoke(this).toString();
                if (mymeth != null) {
                    sb.append(",");
                    sb.append(key);
                    sb.append(":");

                    if (method.getReturnType().isPrimitive() && !method.getReturnType().equals(String.class)) {
                        sb.append(mymeth);
                    } else {
                        sb.append("\"");
                        sb.append(mymeth);
                        sb.append("\"");
                    }
                }
            } catch (Exception ex) {
            };
        }
        for (String key : myMethods.keySet()) {
            Method method = myMethods.get(key);
            try {
                String mymeth = method.invoke(this).toString();
                if (mymeth != null) {
                    sb.append(",");
                    sb.append(key);
                    sb.append(":");

                    if (method.getReturnType().isPrimitive() && !method.getReturnType().equals(String.class)) {
                        sb.append(mymeth);
                    } else {
                        sb.append("\"");
                        sb.append(mymeth);
                        sb.append("\"");
                    }
                }
            } catch (Exception ex) {
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

    public final String getJavacriptFromPaths() {
        StringBuilder sb = new StringBuilder();
        sb.append("var topGui = new GuiPropertyModel(");
        sb.append(getContext());
        sb.append(").addChild(");
        sb.append(buildJavascriptMemory(this));
        sb.append(");");
        return sb.toString();
    }

    private String buildJavascriptMemory(GuiPropertyModel tree) {
        StringBuilder sb = new StringBuilder();
        sb.append("new ");
        sb.append(tree.getClassname());
        sb.append("(");
        sb.append(tree.getContext());
        sb.append(")");
        for (DevicePropertyNode child : tree.getChildren()) {
            if (child instanceof GuiPropertyModel) {
                GuiPropertyModel cgui = (GuiPropertyModel) child;
                sb.append(".addChild(");
                sb.append(buildJavascriptMemory(cgui));
                sb.append(")");
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

    //XXX This will not work unless at top
    public String getPathRef() {
        StringBuilder sb = new StringBuilder();
        DevicePropertyNode parent = this;

        while (parent instanceof GuiPropertyModel) {
            sb.insert(0, parent.getRootPath());
            parent = parent.getParent();
        }

        return sb.toString();
    }

    //XXX This will not work unless at top
    public String getD3Ref() {
        StringBuilder sb = new StringBuilder();
        DevicePropertyNode parent = this;

        while (parent instanceof GuiPropertyModel) {
            sb.insert(0, ".select(\"div#" + parent.getName() + "\")");
            parent = parent.getParent();
        }
        sb.insert(0, "d3");

        return sb.toString();
    }

    public final String getMyHTML() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<div id=\"");
        sb.append(getName());
        sb.append("\" class=\"");
        sb.append(getClassname());
        sb.append("\"");
        sb.append(" style=\"");
        sb.append(getXposAttr());
        sb.append(getYposAttr());
        sb.append(getHeightAttr());
        sb.append(getWidthAttr());
        sb.append(getZIndexAttr());
        sb.append(getPaddingAttr());
        sb.append(getBackgroundColorAttr());
        sb.append("position: absolute\">");
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
    public Response getJSON() throws Exception {
        return Response.ok().entity(getContext()).build();
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
    public Color getJBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @return the color
     */
    public String getBackgroundColor() {
        return "#" + getInternetColor(backgroundColor);
    }

    /**
     * @param color the color to set
     */
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

}
