/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.types;

import org.jdom2.Element;

/**
 *
 * @author root
 */
public enum DeviceConnectionType {

    Invalid("Device software has not yet detected the connection state."),
    NotApplicable("Device has no hardware with which to communicate."),
    Connected("Device software is communicating with hardware."),
    Disconnected("Device software is unable to communicate with hardware."),
    Offline("Device software is purposely not communicating with hardware.");

    DeviceConnectionType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    
    private String message;
}
