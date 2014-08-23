/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.types;

import devicerestmodel.app.MdUtil;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Element;

/**
 *
 * @author root
 */
public class SystemConfigType {

    private String deviceName = "";
    private String deviceDescription = "";
    private String deviceProxyIpAddress = "";
    private int deviceProxyIpPort = 0;
    private boolean deviceProxyIpSet = false;
    private String deviceServerIpAddress = "";
    private int deviceServerIpPort = 0;
    private boolean deviceServerIpSet=false;

    public String getDeviceName() {
        return deviceName;
    }

    public Element getDeviceNameElement() {
        return new Element("devicename").setText(deviceName);
    }

    public void setDeviceName(String name) {
        this.deviceName = name;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public Element getDeviceDescriptionElement() {
        return new Element("devicedescription").setText(deviceDescription);
    }

    public void setDeviceDescription(String description) {
        this.deviceDescription = description;
    }

    public String getDeviceProxyIpAddress() {
        return deviceProxyIpAddress;
    }

    public Element getDeviceProxyElement() {
        return new Element("deviceproxy").setText(deviceProxyIpAddress + ":" + deviceProxyIpPort);
    }

    public void setDeviceProxyIpAddress(String deviceProxyIpAddress) {
        this.deviceProxyIpAddress = deviceProxyIpAddress;
    }

    public int getDeviceProxyIpPort() {
        return deviceProxyIpPort;
    }

    public void setDeviceProxyIpPort(int deviceProxyIpPort) {
        this.deviceProxyIpPort = deviceProxyIpPort;
    }

    public boolean isDeviceProxyIpSet() {
        return deviceProxyIpSet;
    }
    
    public String getDeviceServerIpAddress() {
        return deviceServerIpAddress;
    }

    public Element getDeviceServerElement() {
        return new Element("deviceserver").setText(deviceServerIpAddress + ":" + deviceServerIpPort);
    }

    public void setDeviceServerIpAddress(String deviceServerIpAddress) {
        System.out.println("Setting ip to " + deviceServerIpAddress);
        this.deviceServerIpAddress = deviceServerIpAddress;
    }

    public int getDeviceServerIpPort() {
        return deviceServerIpPort;
    }

    public void setDeviceServerIpPort(int deviceServerIpPort) {
        this.deviceServerIpPort = deviceServerIpPort;
    }

    public boolean isDeviceServerIpSet() {
        return deviceServerIpSet;
    }
    
    public void setXml(Element device) {
        Element childElement;
        String childText;
        
        try {
            System.out.println("Got xml " + MdUtil.element2XmlString(device));
        } catch (IOException ex) {
            Logger.getLogger(SystemConfigType.class.getName()).log(Level.SEVERE, null, ex);
        }
        Element root = device.getChild("systemconfig");

        childElement = root.getChild("devicename");
        if (childElement != null) {
            setDeviceName(childElement.getText());
        }

        childElement = root.getChild("devicedescription");
        if (childElement != null) {
            setDeviceDescription(childElement.getText());
        }

        childElement = root.getChild("deviceproxy");
        if (childElement != null) {
            childText = childElement.getText();
            IpInfo ipInfo = new IpInfo(childText);
            setDeviceProxyIpAddress(ipInfo.getAddress());
            setDeviceProxyIpPort(ipInfo.getPort());
            deviceProxyIpSet = true;
        }

        childElement = root.getChild("deviceserver");
        if (childElement != null) {
            childText = childElement.getText();
            IpInfo ipInfo = new IpInfo(childText);
            setDeviceServerIpAddress(ipInfo.getAddress());
            setDeviceServerIpPort(ipInfo.getPort());
            deviceServerIpSet = true;
        }
    }

    public Element getXml() {
        Element root = new Element("systemconfig");

        root.addContent(getDeviceNameElement());
        root.addContent(getDeviceDescriptionElement());
        root.addContent(getDeviceProxyElement());
        root.addContent(getDeviceServerElement());
        return root;
    }
}
