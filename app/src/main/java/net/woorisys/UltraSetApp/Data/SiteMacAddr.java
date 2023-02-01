package net.woorisys.UltraSetApp.Data;

public enum SiteMacAddr {
    DONGTAN("41:59:04:20"),
    E5("41:59:04:20");

    private final String addr;

    SiteMacAddr(String addr) {
        this.addr = addr;
    }

    public String addr() {
        return addr;
    }
}
