package de.blinkt.openvpn.YinLian;

import android.app.Activity;

import com.unionpay.UPPayAssistEx;

import de.blinkt.openvpn.vpnmovies.vipvpn.wxapi.WXPayEntryActivity;

public class JARActivity extends WXPayEntryActivity {

    @Override
    public void doStartUnionPayPlugin(Activity activity, String tn, String mode) {
        UPPayAssistEx.startPay(activity, null, null, tn, mode);
    }
}
