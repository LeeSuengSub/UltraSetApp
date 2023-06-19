package net.woorisys.UltraSetApp.Data;

public enum SiteMacAddr {
    DONGTAN("41:59:04:20"),
    F19("28:18:51:06"),
    MUNHUENG("29:17:01:12"),
    DUNCHON("11:74:01:06"),
    ONCHUN("26:26:01:08"),
    JISAN("27:26:01:12"),
    OPPO2("41:61:01:14");
    private final String addr;

    SiteMacAddr(String addr) {
        this.addr = addr;
    }

    public String addr() {
        return addr;
    }
}
