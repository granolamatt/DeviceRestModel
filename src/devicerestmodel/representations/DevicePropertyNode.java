/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.representations;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import org.jdom2.Element;

/**
 *
 * @author root
 */
public abstract class DevicePropertyNode implements PropertyChangeListener {

    private String name = "";
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private final ArrayList<DevicePropertyNode> children = new ArrayList<>();
    private final DevicePropertyNode parent;

    public DevicePropertyNode(String name, PropertyChangeListener listener) {
        this.name = name;
        DevicePropertyNode myParent = null;
        if (listener != null) {
            if (listener instanceof DevicePropertyNode) {
                myParent = (DevicePropertyNode) listener;
            }
            changeSupport.addPropertyChangeListener(listener);
        }
        parent = myParent;
    }

    public DevicePropertyNode(PropertyChangeListener listener) {
        this.name = this.getClass().getSimpleName().toLowerCase();
        DevicePropertyNode myParent = null;
        if (listener != null) {
            if (listener instanceof DevicePropertyNode) {
                myParent = (DevicePropertyNode) listener;
            }
            changeSupport.addPropertyChangeListener(listener);
        }
        parent = myParent;
    }

    public String getName() {
        return name;
    }

    public DevicePropertyNode getParent() {
        return parent;
    }

    protected final ArrayList<DevicePropertyNode> getChildren() {
        return children;
    }

    public synchronized final void addChild(DevicePropertyNode child) {
        children.add(child);
    }
    
    public synchronized final Element getXml() throws Exception {
        return getXml(null);
    }

    /**
     * Retrieve the current value of the device. This can be from a virtual
     * value or from the actual hard
     *
     * @param containerRequestContext
     * @return
     * @throws Exception
     */
    public synchronized final Element getXml(ContainerRequestContext containerRequestContext) throws Exception {
        Element root = getElement();

        for (DevicePropertyNode node : children) {
            root.addContent(node.getXml(containerRequestContext));
        }

        return root;
    }

    public String getRootPath() {
        return "/" + getName();
    }

    public synchronized final String[] addPaths() {
        ArrayList<String> myPath = new ArrayList<>();
        myPath.add(getRootPath());
        for (DevicePropertyNode node : children) {
            String[] myChildren = node.addPaths();
            for (String childPath : myChildren) {
                myPath.add(getRootPath() + childPath);
            }
        }
        String[] ret = new String[myPath.size()];
        for (int cnt = 0; cnt < ret.length; cnt++) {
            ret[cnt] = myPath.get(cnt);
        }
        return ret;
    }

    public synchronized final DevicePropertyNode getPropertyNodeFromPath(String path) throws Exception {
        String myPath = path.trim();
        if (myPath.endsWith("/")) {
            myPath = myPath.substring(0, myPath.length() - 1);
        }
        DevicePropertyNode ret = null;

        if (myPath.startsWith(getRootPath())) {
            myPath = myPath.replaceFirst(getRootPath(), "");
            if (myPath.equals("")) {
                return this;
            }
            for (DevicePropertyNode node : children) {
                DevicePropertyNode now = node.getPropertyNodeFromPath(myPath);
                if (now != null) {
                    return now;
                }
            }
        }
        return ret;
    }

    public Element getElement() {
        Element element = new Element(getName());
        return element;
    }


    /**
     * Set the current value in the device. This can set a virtual value or the
     * value in the actual hardware.
     *
     * @param element
     * @return
     * @throws Exception
     */
    public synchronized final boolean setXml(Element element) throws Exception {
        if (isXmlValid(element)) {
            setElement(element);
            for (Element child : element.getChildren()) {
                for (DevicePropertyNode node : children) {
                    if (node.setXml(child)) {
                        break;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public abstract void setElement(Element element) throws Exception;

    /**
     * Used by hardware polling to update a virtual property value.
     *
     * @param element
     * @return
     * @throws Exception
     */
    public synchronized final boolean updateXml(Element element) throws Exception {
        if (isXmlValid(element)) {
            updateElement(element);
            for (Element child : element.getChildren()) {
                for (DevicePropertyNode node : children) {
                    if (node.updateXml(child)) {
                        break;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public abstract void updateElement(Element element) throws Exception;

    /**
     * Checks if the argument XML tag matches the tag name for this property
     *
     * @param element
     * @return
     */
    public boolean isXmlValid(Element element) {
        if (element != null && element.getName().equalsIgnoreCase(getName())) {
            return true;
        } else {
            return false;
        }
    }

    public PropertyChangeSupport getPropertyChangeSupport() {
        return changeSupport;
    }

    public final void generateXmlEvent() throws Exception {
        Element root = getXml();
        //XXX Change the xml element in events to do this
        changeSupport.firePropertyChange(getRootPath(), null, root);
    }

    public final void generateXmlEvent(Element myElement) throws Exception {
        Element root = myElement;
        //XXX Change the xml element in events to do this
        changeSupport.firePropertyChange(getRootPath(), null, root);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
//        if (evt.getPropertyName().equals("XmlEvent")) {
        try {
            Element changedElement = (Element) evt.getNewValue();
            Element root = getElement();
            root.addContent(changedElement);
            changeSupport.firePropertyChange(getRootPath() + evt.getPropertyName(), null, root);
        } catch (Exception ex) {
            Logger.getLogger(DevicePropertyNode.class.getName()).log(Level.SEVERE, null, ex);
        }
//        }

    }

}
