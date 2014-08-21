/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.html;


/**
 *
 * @author root
 */
public class HTMLHead extends HTMLType {
 private StringBuilder contentBuilder = new StringBuilder();
    
    public HTMLHead addContent(String newContent) {
        contentBuilder.append(newContent);
        return this;
    }
    @Override
    public void getValue(StringBuilder output) {
        output.append(contentBuilder);

    }   

}
