package de.blinkt.openvpn.Entity;

import java.io.Serializable;

/**
 * Author: Jan
 * CreateTime:on 2016/10/18.
 */
public class VideoInfo implements Serializable {
    private String id;
    private String clientPic_Url;//海报
    private String live_Url;//视频地址
    private String remarks;//评论内容
    private String tv_name;//栏目名称 "湖南卫视"
    private String video_name;//"完美大咖秀"
    private String wonderful;//"精彩度
    private String logo_url;
    private String isLook;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientPic_Url() {
        return clientPic_Url;
    }

    public void setClientPic_Url(String clientPic_Url) {
        this.clientPic_Url = clientPic_Url;
    }

    public String getLive_Url() {
        return live_Url;
    }

    public void setLive_Url(String live_Url) {
        this.live_Url = live_Url;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTv_name() {
        return tv_name;
    }

    public void setTv_name(String tv_name) {
        this.tv_name = tv_name;
    }

    public String getVideo_name() {
        return video_name;
    }

    public void setVideo_name(String video_name) {
        this.video_name = video_name;
    }

    public String getWonderful() {
        return wonderful;
    }

    public void setWonderful(String wonderful) {
        this.wonderful = wonderful;
    }

    public String getLogo_url() {
        return logo_url;
    }

    public void setLogo_url(String logo_url) {
        this.logo_url = logo_url;
    }

    public String getIsLook() {
        return isLook;
    }

    public void setIsLook(String isLook) {
        this.isLook = isLook;
    }
}
