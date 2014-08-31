/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.representations;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
    private final HashMap<String, Method> myMethods = new HashMap<>();

    public GuiPropertyModel(String name, DevicePropertyNode parent) {
        super(name, parent);
        loadFields();
    }

    public GuiPropertyModel(DevicePropertyNode parent) {
        super(parent);
        loadFields();
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
     * function GuiPropertyModel(context) { 
     *    if (context !== undefined) {
     *       this.myParent = context.parent || undefined;
     *       this.d3ref = context.d3ref || undefined;
     *    }
     *    var children = [];
     *    GuiPropertyModel.prototype.addChild = function(childGui) {
     *       children[children.length] = childGui;
     *       return this;
     *    }
     *    GuiPropertyModel.prototype.showAlert = function() {
     *       alert(this.myParent); 
     *    }
     *    GuiPropertyModel.prototype.getD3 = function() {
     *       return this.d3ref;
     *    }
     * }
     *
     * {@link #getJavascriptPrototypes()}
     * $(document).ready(function(){
     *    {@link #getJavacriptFromPaths()}
     * });
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
        // call the super constructor
        if (extended.isAssignableFrom(GuiPropertyModel.class)) {
            sb.append(extended.getSimpleName());
            sb.append(".call(this, context);\n");
        }
        // This is a great place to add local context variables
        // now add global calls to local variables for class
        sb.append("var d3ref = this.getD3();");
        
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
    
    private void loadFields() {
        Class<?> extern = this.getClass();
//        if (extern.isAssignableFrom(DevicePropertyNode.class)) {
        while (!extern.equals(DevicePropertyNode.class)) {
            System.out.println("Class type is " + extern.getSimpleName());
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
                        System.out.println("Method is " + method.getName() + " field " + match);
                        myMethods.put(match, method);
                        break;
                    }
                }
                }
                }
            }
            extern = extern.getSuperclass();
        }
//        }
    }
    
    public String getContext() {
        StringBuilder sb = new StringBuilder();
        sb.append("{d3ref:");
        sb.append(getD3Ref()); 
        
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
            } catch (Exception ex){};
        }
        sb.append("}");
        return sb.toString();
    }

    public final String getJavacriptFromPaths() {
        StringBuilder sb = new StringBuilder();
        sb.append("var topGui = new GuiPropertyModel(");
        System.out.println("Top level fields: ");
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
