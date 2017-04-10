package de.blinkt.openvpn.YinLian;

/**
 * 银联支付验证数据
 */
public class Vertification {
    private String out_trade_no;//订单号
    private String userName;//用户名
    private String imeiLastId;
    private String payTime;//购买会员时长
    private String txnTime;//支付时间

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

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

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(String txnTime) {
        this.txnTime = txnTime;
    }
}
