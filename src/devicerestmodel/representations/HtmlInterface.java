/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package devicerestmodel.representations;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

/**
 *
 * @author root
 */
public interface HtmlInterface {
    
//      
//    public synchronized String getJSON() throws Exception {
//        Element root = getElement();
//        String mystring = "{" + root.getName() + "," + root.getText();
//
//        for (DevicePropertyNode node : children) {
//            mystring += node.getJSON();
//        }
//        mystring += "}";
//
//        return mystring;
//    }
    
    public Response getHTML(ContainerRequestContext containerRequestContext) throws Exception;
    
}
