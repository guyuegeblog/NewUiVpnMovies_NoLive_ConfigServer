package de.blinkt.openvpn.Entity;

/**
 * Created by Administrator on 2016/7/12.
 */
public class ApkInfo {
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
