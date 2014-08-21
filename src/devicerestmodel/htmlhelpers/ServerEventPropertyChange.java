/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.htmlhelpers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import org.glassfish.jersey.media.sse.EventOutput;
import org.glassfish.jersey.media.sse.OutboundEvent;

/**
 *
 * @author root
 */
public class ServerEventPropertyChange extends EventOutput implements PropertyChangeListener {
    
    private final PropertyChangeSupport mySupport;
    
    public ServerEventPropertyChange(PropertyChangeSupport support) {
        mySupport = support;
        mySupport.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            //System.out.println("Got a property change!!!! " + evt);
            final OutboundEvent.Builder eventBuilder = new OutboundEvent.Builder();
            eventBuilder.name(evt.getPropertyName());
            eventBuilder.data(String.class,
                    evt.getNewValue().toString());
            final OutboundEvent event = eventBuilder.build();
            write(event);
        } catch (Exception e) {
            System.out.println("Exception in property change, need to cleanup");
            e.printStackTrace();
            mySupport.removePropertyChangeListener(this);
            try {
                close();
            } catch (IOException ioClose) {
                throw new RuntimeException(
                        "Error when closing the event output.", ioClose);
            }
        }

    }
}
