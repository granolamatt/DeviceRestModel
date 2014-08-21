/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.html;

/**
 *
 * @author root
 */
public class HTMLhtml extends HTMLType {

    @Override
    public void getHTML(StringBuilder html) {
        html.append("<!DOCTYPE html>\n");
        html.append("<").append(getTag());
        getAttributes(html);
        html.append(">\n");

        getValue(html);
        for (HTMLType comp : getComponents()) {
            comp.getHTML(html);
        }
        html.append("</").append(getTag()).append(">\n");

    }

    @Override
    public void getValue(StringBuilder output) {
    }
}
