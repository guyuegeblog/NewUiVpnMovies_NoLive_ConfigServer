package de.blinkt.openvpn.Entity;

import java.util.List;

/**
 * Created by Administrator on 2016/6/13.
 */
public class ServerList {
    private List<ServerInfo>  json;

    public List<ServerInfo> getJson() {
        return json;
    }

    public void setJson(List<ServerInfo> json) {
        this.json = json;
    }
}
