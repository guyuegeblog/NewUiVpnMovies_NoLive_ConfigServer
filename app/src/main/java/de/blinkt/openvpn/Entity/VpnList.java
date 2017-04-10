package de.blinkt.openvpn.Entity;

import java.util.List;

/**
 * Created by Administrator on 2016/7/4.
 */
public class VpnList {
    private List<VpnConfig> listJson;

    public List<VpnConfig> getListJson() {
        return listJson;
    }

    public void setListJson(List<VpnConfig> listJson) {
        this.listJson = listJson;
    }
}
