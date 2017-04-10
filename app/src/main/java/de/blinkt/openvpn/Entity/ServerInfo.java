package de.blinkt.openvpn.Entity;

import java.io.Serializable;

/**
 * *
 * 服务器信息数据
 */
public class ServerInfo implements Serializable, Comparable {
    private int id;
    private boolean isSelect;//是否选中
    private String ip;
    private String userCount;
    private String address;
    private String addressConfig;
    private boolean ishot;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserCount() {
        return userCount;
    }

    public void setUserCount(String userCount) {
        this.userCount = userCount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressConfig() {
        return addressConfig;
    }

    public void setAddressConfig(String addressConfig) {
        this.addressConfig = addressConfig;
    }

    public boolean ishot() {
        return ishot;
    }

    public void setIshot(boolean ishot) {
        this.ishot = ishot;
    }

    @Override
    public int compareTo(Object arg0) {
        return this.getUserCount().compareTo(((ServerInfo)arg0).getUserCount());
    }
}
