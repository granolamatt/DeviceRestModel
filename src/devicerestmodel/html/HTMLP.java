/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.html;

/**
 *
 * @author root
 */
public class HTMLP extends HTMLType {

    private StringBuilder paragraph = new StringBuilder();
    
    public HTMLP addLine(String line) {
        paragraph.append(line).append("<br>\n");
        return this;
    }
    
    
    public HTMLP addText(String ntext) {
        paragraph.append(ntext);
        return this;
    }

    @Override
    public void getValue(StringBuilder output) {
        output.append(paragraph);

    }
    
}
