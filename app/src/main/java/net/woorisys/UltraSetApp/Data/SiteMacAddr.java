package net.woorisys.UltraSetApp.Data;

public enum SiteMacAddr {
    DONGTAN("41:59:04:20"),
    F19("28:18:51:06"),
    MUNHUENG("29:17:01:12"),
    DUNCHON("11:74:01:06"),
    ONCHUN("26:26:01:08"),
    JISAN("27:26:01:12"),
    OPPO2("41:61:01:14"),
    NOHYEONG("50:11:01:22"),
    YEON("50:11:01:37"),
    BAEBANG("44:20:02:53"),
    SOOSUNG("27:26:01:03"),
    OSAN("41:37:01:16"),
    GEOJE("48:31:01:10"),
    YANGPYEONG("41:83:02:50"),
    CHEONAN("44:13:32:56"),
    CHEONGJU("43:11:31:14"),
    HANAM("41:45:01:08"),
    DONGSHINCHOEN("27:14:01:02");

    private final String addr;

    SiteMacAddr(String addr) {
        this.addr = addr;
    }

    public String addr() {
        return addr;
    }
}
