/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.html;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author root
 */
public abstract class HTMLType {

    private final List<HTMLType> components = new LinkedList<>();
    private final Map<String, String> attributes = new HashMap<>();
    private boolean closes = true;

    public void setID(String id) {
        addAttribute("id", id);
    }
    
    public String getTag() {
        String myName = this.getClass().getSimpleName();
        if (myName.startsWith("HTML")) {
            myName = myName.replaceFirst("HTML", "");
        }
        myName = myName.replaceAll("_", " ");
        return myName.toLowerCase();
    }
    
    public void setCloses(boolean c) {
        closes = c;
    }

    public abstract void getValue(StringBuilder output);

    public void getHTML(StringBuilder html) {
        html.append("<").append(getTag());
        getAttributes(html);
        html.append(">\n");

        getValue(html);
        for (HTMLType comp : getComponents()) {
            comp.getHTML(html);
        }
        if (closes) {
            html.append("\n</").append(getTag()).append(">\n");
        }

    }

    public void addAttribute(String key, String val) {
        attributes.put(key, val);
    }

    public <T> T addHTMLContent(T type) {
        Class s = type.getClass();
        do {
            if (s.equals(HTMLType.class)) {
                getComponents().add((HTMLType) type);
                return type;
            }
            s = s.getSuperclass();
        } while (s != null);
        System.out.println("Could not add component");
        return type;
    }

    public void getAttributes(StringBuilder html) {
        for (String att : attributes.keySet()) {
            html.append(" ").append(att)
                    .append("=").append("\"")
                    .append(attributes.get(att)).append("\"");
        }
    }

    /**
     * @return the components
     */
    public List<HTMLType> getComponents() {
        return components;
    }
}
