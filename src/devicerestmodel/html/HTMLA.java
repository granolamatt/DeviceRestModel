/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.html;

import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author root
 */
public class HTMLA extends HTMLP {
        

    public HTMLA setSource(URI source) {
        this.addAttribute("href", source.toString());
        return this;
    }

    public HTMLA setSource(String source) throws URISyntaxException {
        return setSource(new URI(source));
    }
    
}
