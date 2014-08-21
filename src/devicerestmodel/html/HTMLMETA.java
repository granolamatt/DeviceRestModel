/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.html;

/**
 *
 * @author root
 */
public class HTMLMETA extends HTMLType {
    
    public HTMLMETA() {
        super();
        setCloses(false);
    }

    @Override
    public void getValue(StringBuilder output) {
    }
    
    @Override
    public String getTag() {
        return "META";
    }

    public HTMLMETA setRefresh(int refresh) {
        addAttribute("HTTP-EQUIV", "refresh");
        addAttribute("CONTENT", Integer.toString(refresh));
        return this;
    }
   
    
}
