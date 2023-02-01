package net.woorisys.UltraSetApp.SingletonData;

import net.woorisys.UltraSetApp.ble.BeaconDomain;

import java.util.ArrayList;

public class BeaconSingleton {

    private ArrayList<BeaconDomain> beaconDomainList;

    public ArrayList<BeaconDomain> getBeaconDomainList() {
        return beaconDomainList;
    }

    public void setBeaconDomainList(ArrayList<BeaconDomain> beaconDomainList) {
        this.beaconDomainList = beaconDomainList;
    }

    private static final BeaconSingleton instance = new BeaconSingleton();

    public static BeaconSingleton getInstance() {
        return instance;
    }

    private BeaconSingleton() {
        beaconDomainList = new ArrayList<>();
    }


}
