package de.blinkt.openvpn.Entity;

import java.util.List;

/**
 * Created by ASUS on 2016/12/6.
 */
public class UpdateList {
    private List<DownUpdate> listProp;

    public List<DownUpdate> getListProp() {
        return listProp;
    }

    public void setListProp(List<DownUpdate> listProp) {
        this.listProp = listProp;
    }
}
