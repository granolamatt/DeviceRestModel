/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.app;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.inject.Provider;
import org.eclipse.jetty.server.Request;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ContainerRequest;

/**
 *
 * @author root
 */
public class RemoteAddrBinder extends AbstractBinder {

    private static class RemoteAddrProviderFactory implements Factory<SocketAddress> {
        
        @Inject
        private Provider<ContainerRequest> request;
        
        @Override
        public SocketAddress provide() {
            ContainerRequest containerRequest = request.get();
            PropertiesDelegate delegate = containerRequest.getPropertiesDelegate();
            try {
                Field requestField = delegate.getClass().getDeclaredField("request");
                requestField.setAccessible(true);
                Request request = (Request) requestField.get(delegate);
                return request.getRemoteInetSocketAddress();
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                return null;
            }
        }
        
        @Override
        public void dispose(SocketAddress t) {
        }
    }

    @Override
    protected void configure() {
        bindFactory(RemoteAddrProviderFactory.class).to(SocketAddress.class).in(RequestScoped.class);
    }
}
