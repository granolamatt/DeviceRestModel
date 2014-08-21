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
public class DeviceServerType {

    private String name = "";
    private String description = "";
    private String serverIpAddress = "";
    private int serverIpPort = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Element getDescriptionElement() {
        return new Element("description").setText(description);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServerIpAddress() {
        return serverIpAddress;
    }

    public Element getServerElement() {
        return new Element("server").setText(serverIpAddress + ":" + serverIpPort);
    }

    public void setServerIpAddress(String serverIpAddress) {
        this.serverIpAddress = serverIpAddress;
    }

    public int getServerIpPort() {
        return serverIpPort;
    }

    public void setServerIpPort(int serverIpPort) {
        this.serverIpPort = serverIpPort;
    }

    public void setXml(Element root) {
        Element childElement;
        String childText;

        String id = root.getAttributeValue("id");
        if (id != null) {
            setName(id);
        }

        childElement = root.getChild("description");
        if (childElement != null) {
            setDescription(childElement.getText());
        }

        childElement = root.getChild("server");
        if (childElement != null) {
            childText = childElement.getText();
            IpInfo ipInfo = new IpInfo(childText);
            setServerIpAddress(ipInfo.getAddress());
            setServerIpPort(ipInfo.getPort());
        }
    }

    public Element getXml() {
        Element root = new Element("deviceserver");
        root.setAttribute("id",name);
        root.addContent(getDescriptionElement());
        root.addContent(getServerElement());
        return root;
    }
}
