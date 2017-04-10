package de.blinkt.openvpn.Bean.SharedBean;

/**
 * Created by Administrator on 2016/4/7.
 */
public class sharedLogin {
    private String id;
    private String nickName;
    private String vip_status;
    private String userName;
    private String vip_lastTme;
    private String passWord;
    private String email;
    private String tel_phone;
    private String pay_count;

    public String getPay_count() {
        return pay_count;
    }

    public void setPay_count(String pay_count) {
        this.pay_count = pay_count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getVip_status() {
        return vip_status;
    }

    public void setVip_status(String vip_status) {
        this.vip_status = vip_status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getVip_lastTme() {
        return vip_lastTme;
    }

    public void setVip_lastTme(String vip_lastTme) {
        this.vip_lastTme = vip_lastTme;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel_phone() {
        return tel_phone;
    }

    public void setTel_phone(String tel_phone) {
        this.tel_phone = tel_phone;
    }
}
