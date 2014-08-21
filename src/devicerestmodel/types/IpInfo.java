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
public class IpInfo {

    private String address = "";
    private int port = 0;

    public IpInfo() {
    }

    public IpInfo(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public IpInfo(String addressPort) {
        String[] split = addressPort.split(":");
        switch (split.length) {
            case 1:
                address = split[0];
                break;
            case 2:
                address = split[0];
                port = Integer.parseInt(split[1]);
                break;
        }
    }

    public String getAddress() {
        return address;
    }

    public Element getAddressElement() {
        return new Element("address").setText(address);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public Element getPortElement() {
        return new Element("port").setText(String.valueOf(port));
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Element getXml() {
        Element root = new Element("ipinfo");
        root.addContent(getAddressElement());
        root.addContent(getPortElement());
        return root;
    }
}
