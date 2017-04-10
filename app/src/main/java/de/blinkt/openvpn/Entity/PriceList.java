package de.blinkt.openvpn.Entity;

import java.util.List;

/**
 * Author: Jan
 * CreateTime:on 2016/11/2.
 */
public class PriceList {

    private List<PriceInfo> listPrice;

    public List<PriceInfo> getListPrice() {
        return listPrice;
    }

    public void setListPrice(List<PriceInfo> listPrice) {
        this.listPrice = listPrice;
    }
}
