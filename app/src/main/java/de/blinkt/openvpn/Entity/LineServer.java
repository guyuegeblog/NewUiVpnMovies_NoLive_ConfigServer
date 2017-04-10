package de.blinkt.openvpn.Entity;

/**
 * Created by Administrator on 2016/6/12.
 */
public class LineServer {
    private String userName;
    private String connState;
    private String connStartTime;
    private String connEndTime;
    private String ip;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getConnState() {
        return connState;
    }

    public void setConnState(String connState) {
        this.connState = connState;
    }

    public String getConnStartTime() {
        return connStartTime;
    }

    public void setConnStartTime(String connStartTime) {
        this.connStartTime = connStartTime;
    }

    public String getConnEndTime() {
        return connEndTime;
    }

    public void setConnEndTime(String connEndTime) {
        this.connEndTime = connEndTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
