/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.types;

import org.jdom2.Element;

/**
 *
 * @author root
 */
public class DeviceConfigType {


    public void setXml(Element root) {

    }

    public Element getXml() {
        Element root = new Element("deviceconfig");

        return root;
    }
}
