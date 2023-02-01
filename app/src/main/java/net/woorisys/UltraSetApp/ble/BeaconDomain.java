package net.woorisys.UltraSetApp.ble;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BeaconDomain {

    private String macAddress;
    private int serialNumber;

    public BeaconDomain(String macAddress, int serialNumber){
        this.macAddress = macAddress;
        this.serialNumber = serialNumber;
    }
}
