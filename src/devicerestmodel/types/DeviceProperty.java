/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.types;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom2.Element;

/**
 *
 * @author root
 * @param <T>
 */
public abstract class DeviceProperty<T> {

    private String name = "";
    protected T lastValue;
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    private boolean xmlEventsActive = false;

    public DeviceProperty(String name, PropertyChangeListener listener) {
        this.name = name;
        changeSupport.addPropertyChangeListener(listener);
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return lastValue;
    }

    public void setValue(T value) {
        this.lastValue = value;
    }

    /**
     * Retrieve the current value of the device. This can be from a virtual
     * value or from the actual hardware.
     *
     * @return
     * @throws Exception
     */
    public Element getXml() throws Exception {
        Element root = new Element(name);
        return root;
    }

    /**
     * Set the current value in the device. This can set a virtual value or the
     * value in the actual hardware.
     *
     * @param element
     * @throws Exception
     */
    public void setXml(Element element) throws Exception {
    }

    /**
     * Used by hardware polling to update a virtual property.
     *
     * @param element
     * @throws Exception
     */
    public void updateXml(Element element) throws Exception {
    }

    /**
     * Checks if the argument XML tag matches the tag name for this property
     *
     * @param element
     * @return
     */
    public boolean isXmlValid(Element element) {
        if (element.getName().equals(name)) {
            return true;
        } else {
            return false;
        }
    }

    protected void generateChangeEvent() {
        if (xmlEventsActive) {
            try {
                generateXmlChange();
            } catch (Exception ex) {
                Logger.getLogger(DeviceProperty.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setXmlEventsActive(boolean state) {
        xmlEventsActive = state;
    }

    private void generateXmlChange() throws Exception {
        changeSupport.firePropertyChange("XmlChangeEvent", null, getXml());
    }
}
