package de.blinkt.openvpn.Entity;

/**
 * 友盟
 */
public class YouMeng {
    private String userName;
    private String area;//渠道号
    private String imeiLastId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImeiLastId() {
        return imeiLastId;
    }

    public void setImeiLastId(String imeiLastId) {
        this.imeiLastId = imeiLastId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
