package de.blinkt.openvpn.Entity;

import java.util.List;

/**
 * Created by Administrator on 2016/6/21.
 */
public class MessageList {
    private List<MessageInfo> listJson;

    public List<MessageInfo> getListJson() {
        return listJson;
    }

    public void setListJson(List<MessageInfo> listJson) {
        this.listJson = listJson;
    }
}
