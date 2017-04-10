package de.blinkt.openvpn.Bean.login;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/24.
 */
public class LoginBean implements Serializable {
    private String userName;
    private String passWord;
    private String nickName;
    private String email;
    private String imei;
    private String imsi;
    private String imeiLastId;
    private String mobieBrand;
    private String mobileModel;
    private String tel_phone;
    private String tele_supo;
    private String area;
    private String show;//特殊铭文字符


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getImeiLastId() {
        return imeiLastId;
    }

    public void setImeiLastId(String imeiLastId) {
        this.imeiLastId = imeiLastId;
    }

    public String getMobieBrand() {
        return mobieBrand;
    }

    public void setMobieBrand(String mobieBrand) {
        this.mobieBrand = mobieBrand;
    }

    public String getMobileModel() {
        return mobileModel;
    }

    public void setMobileModel(String mobileModel) {
        this.mobileModel = mobileModel;
    }

    public String getTel_phone() {
        return tel_phone;
    }

    public void setTel_phone(String tel_phone) {
        this.tel_phone = tel_phone;
    }

    public String getTele_supo() {
        return tele_supo;
    }

    public void setTele_supo(String tele_supo) {
        this.tele_supo = tele_supo;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }
}
