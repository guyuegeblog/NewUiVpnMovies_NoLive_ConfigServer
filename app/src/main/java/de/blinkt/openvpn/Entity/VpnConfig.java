package de.blinkt.openvpn.Entity;

/**
 * Created by Administrator on 2016/7/4.
 */
public class VpnConfig {
    private String  id;
    private String  desciptions;
    private String  name;
    private String  upTime;
    private String  url;
    private String  version;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesciptions() {
        return desciptions;
    }

    public void setDesciptions(String desciptions) {
        this.desciptions = desciptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
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
