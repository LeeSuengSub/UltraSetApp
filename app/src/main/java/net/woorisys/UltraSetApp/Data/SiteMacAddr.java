package net.woorisys.UltraSetApp.Data;

public enum SiteMacAddr {
    DONGTAN("41:59:04:20"),
    F19("28:18:51:06");
    private final String addr;

    SiteMacAddr(String addr) {
        this.addr = addr;
    }

    public String addr() {
        return addr;
    }
}
