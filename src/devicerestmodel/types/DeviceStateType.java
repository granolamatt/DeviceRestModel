/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devicerestmodel.types;

/**
 *
 * @author root
 */
public enum DeviceStateType {

    Invalid("hardware state not yet detected."),
    Ok("hardware operating normally."),
    Degraded("hardware operation is degraded."),
    NotOk("Hardware is not operating normally.");
//    ColdStandby("hardware in a cold standby state"),
//    HotStandby("hardware in a hot standby state"),
//    Online("hardware in an online state");//    ColdStandby("hardware in a cold standby state"),
//    HotStandby("hardware in a hot standby state"),
//    Online("hardware in an online state");//    ColdStandby("hardware in a cold standby state"),
//    HotStandby("hardware in a hot standby state"),
//    Online("hardware in an online state");//    ColdStandby("hardware in a cold standby state"),
//    HotStandby("hardware in a hot standby state"),
//    Online("hardware in an online state");//    ColdStandby("hardware in a cold standby state"),
//    HotStandby("hardware in a hot standby state"),
//    Online("hardware in an online state");//    ColdStandby("hardware in a cold standby state"),
//    HotStandby("hardware in a hot standby state"),
//    Online("hardware in an online state");//    ColdStandby("hardware in a cold standby state"),
//    HotStandby("hardware in a hot standby state"),
//    Online("hardware in an online state");//    ColdStandby("hardware in a cold standby state"),
//    HotStandby("hardware in a hot standby state"),
//    Online("hardware in an online state");

    DeviceStateType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private String message;

}
