package de.blinkt.openvpn.Entity;

import java.util.List;

/**
 * Created by ASUS on 2016/12/6.
 */
public class DownUpdate {
    private String url;
    private String version;
    private String packName;

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
