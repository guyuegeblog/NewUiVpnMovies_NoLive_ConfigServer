package de.blinkt.openvpn.vpnmovies.vipvpn.wxapi;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.unionpay.UPPayAssistEx;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import de.blinkt.openvpn.Activity.LoginActivity;
import de.blinkt.openvpn.Activity.MainActivity;
import de.blinkt.openvpn.Activity.NavigationActivity;
import de.blinkt.openvpn.Bean.SharedBean.sharedLogin;
import de.blinkt.openvpn.Bean.login.LoginBean;
import de.blinkt.openvpn.Constant.Constant;
import de.blinkt.openvpn.Entity.YouMeng;
import de.blinkt.openvpn.Interface.payInterface;
import de.blinkt.openvpn.Net.AppTool;
import de.blinkt.openvpn.Net.OkHttp;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.Save.KeyFile;
import de.blinkt.openvpn.Utils.AesUtils;
import de.blinkt.openvpn.Utils.Nick;
import de.blinkt.openvpn.Utils.PhoneInfo;
import de.blinkt.openvpn.Utils.T;
import de.blinkt.openvpn.Utils.Util;
import de.blinkt.openvpn.View.ScrollTextView;
import de.blinkt.openvpn.View.SystemBarTintManager;
import de.blinkt.openvpn.WeiXin.Convert;
import de.blinkt.openvpn.WeiXin.GetIP;
import de.blinkt.openvpn.WeiXin.GetWxOrderno;
import de.blinkt.openvpn.WeiXin.ReqEntity;
import de.blinkt.openvpn.WeiXin.RequestHandler;
import de.blinkt.openvpn.WeiXin.Utils;
import de.blinkt.openvpn.WeiXin.WX;
import de.blinkt.openvpn.WeiXin.WXYZ;
import de.blinkt.openvpn.YinLian.DataTn;
import de.blinkt.openvpn.YinLian.Vertification;
import de.blinkt.openvpn.ZhiFuBao.Alipay;
import de.blinkt.openvpn.ZhiFuBao.AplipayYZ;
import de.blinkt.openvpn.ZhiFuBao.PayResult;
import de.blinkt.openvpn.ZhiFuBao.SignUtils;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WXPayEntryActivity extends FragmentActivity implements payInterface, IWXAPIEventHandler, Handler.Callback,
        Runnable {
    private ImageView login, user_update;
    private LinearLayout btn_year, btn_one;
    //    btn_six,
    private RelativeLayout userxieyi;
    private TextView index, navigation, uservip, pay_price;
    private TextView tv_three_price, tv_year_price;
    //    tv_six_price
    private TextView tv_three_type, tv_year_type;
    //    tv_six_type,
    private TextView three_original_price, year_original_price;
    //    six_original_price,
    private ImageView WXPay, YinLianPay, ZhiFuBaoPay;
    public String VIP_TIME = "一年";//购买的会员时间
    private String VIP_LAST_TIME = null;//会员到期时间
    public String body = "H站大全年卡会员";

    //微信会员
    private IWXAPI api;
    public WXYZ entity = new WXYZ();
    public Dialog dialog, dialogyz;

    //银联
    public final String LOG_TAG = "PayDemo";
    private int mGoodsIdx = 0;
    private Handler mHandler = null;

    /*****************************************************************
     * mMode参数解释： "00" - 启动银联正式环境 "01" - 连接银联测试环境
     *****************************************************************/
    private final String mMode = "00";
    private final View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isYinLian = false;
            YinLianPay.setEnabled(false);//设置空间不可用
            dialog.show();
            dialog.setCancelable(false);
            Log.e(LOG_TAG, " " + v.getTag());
            mGoodsIdx = (Integer) v.getTag();

//            mLoadingDialog = ProgressDialog.show(mContext, // context
//                    "", // title
//                    "正在努力的获取tn中,请稍候...", // message
//                    true); // 进度是否是不确定的，这只和创建进度条有关
            /*************************************************
             * 步骤1：从网络开始,获取交易流水号即TN
             ************************************************/
            new Thread(WXPayEntryActivity.this).start();
        }
    };
    //支付宝
    // 商户PID
    public String PARTNER = "2088901717972382";
    // 商户收款账号
    public String SELLER = "bjjczw@foxmail.com";
    // 商户私钥，pkcs8格式
    public String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOPaxtsejxgalfO5wGEqlUN8OHt5+wAnD4" +
            "cWgHD5XYR5ApkqpnyshM7G7YJI0ViU0bfKNHo7RHwwYSLqZn6Ip23z01LuplppIqhv1oO+v0iwRCMwqC8p4gdbn4j7HZ2onz2DA91k" +
            "+oQLkvW9/oa3YeXoCqXJ0Q5BqSPvAk2AUjXJAgMBAAECgYASNER29TYRgu5ADrMkEDbksWQB2XkIRhajgFS6sfGax+BBRHsQsufZbWN" +
            "EaXTwUtN+j5Upvtp14ZehJoER0vEtXRoMP+kgb5YT6/1yb0jqGMoGRRjocbeENAe9Teo9zoA0sT+t/a8otNhbyIUWkL4nBDwCVHc8B2" +
            "odcRCl3RCX0QJBAPGMUMmjQ3A53woXnaYRPqmjr/Id2Kjo8heajeQtU5eo7MdeuGZpaP0X8kP9fC55y8MZnypAWM4g9w4AcYlj0bMCQQ" +
            "DxfLlMDwCkfwIC6pZ2x8lnn4SxYTFps8H5tLty4NpcKGumgWR82SdgkUcZvc9iJhv7uVveRDeSIae8h3w/XgSTAkBtR8AdGaIfGe+Qj1K" +
            "hmVeyQ/4MGfi1on40s5XST7dr+97z7CSdIL+BEd5naD1QgYXwRJ0/7lC/ISbkzMqRD/oFAkEAvs+WeorALzpMFJHYIjLq6X4aEy6BJMxs0" +
            "SoFk1goMfmeVgqXpC7R9nPUgnqAi0Uhh12HQbEVV9pP95/2hPt9EQJAZUMSHZ59AsGfarqYiLxN7K1GoGCc5Lygx0jQooIndyvtyuADp+pg" +
            "JkmRgKXgrJnm+JCsHPtBA+U1zR9Kcng3Tg==\n";
    // 支付宝公钥
    private final int SDK_PAY_FLAG = 1;

    //支付宝验证信息
    private String Zfb_out_trade_no = "";//支付宝订单号
    private String userName;//用户名
    private String imeilLastId;//IMEL最后一个id数字
    private String nickName;
    private String password;
    private String imsi;
    private String show;
    private T t;
    private Util util;
    private AesUtils aesUtils;
    public String USER_VIP_STATUS = null;

    Handler yinLianHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 50) {
                yinLianHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setPay();
                    }
                }, 3000);
            }
            if (msg.what == 60) {
                setPay();
            }
            //微信验证
            if (msg.what == 101) {
                yinLianHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        weiXinYanZhen("");
                    }
                }, 3000);
            }
            //支付宝验证
            if (msg.what == 102) {
                yinLianHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        zhiFuBaoYanZhen("");
                    }
                }, 3000);

            }
            //微信有参验证
            if (msg.what == 8989) {
                this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("payJson", "wx开始执行");
                        String wxAesJson = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.WX_USER_PAY_FAILED_FILE, pay_key);
                        String wxJson = aesUtils.decrypt(wxAesJson);
                        weiXinYanZhen(wxJson);
                    }
                }, 3000);
            }

            //支付宝有参验证
            if (msg.what == 1919) {
                this.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("payJson", "zfb开始执行");
                        String zfbAesJson = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.ZHI_FU_BAO_USER_PAY_FAILED_FILE, pay_key);
                        String zfbJson = aesUtils.decrypt(zfbAesJson);
                        zhiFuBaoYanZhen(zfbJson);
                    }
                }, 3000);
            }
            if (msg.what == 8888) {
                dialog.dismiss();
            }
        }
    };

    public void setPay() {
        dialog.dismiss();
//        YinLianPay.setEnabled(true);
        WXPay.setEnabled(true);
        ZhiFuBaoPay.setEnabled(true);
    }

    private final int GO_DOWN_VPN_PROGRESS = 1058;
    private final int GO_DOWN_TV_FAILED = 1066;
    private final int GO_DOWN_TV_SUCCES = 1099;

    private Handler smHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_DOWN_TV_FAILED:
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    apkDownLoad();
                    break;
                case GO_DOWN_TV_SUCCES:
                    Log.d("h_bl", "文件下载安装");
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    try {
                        AppTool.installApk(WXPayEntryActivity.this, Util.getSDCardPath() + "/" + apkPath);
                    } catch (Exception e) {
                        return;
                    }
                    break;
                case GO_DOWN_VPN_PROGRESS:
                    showProgressDialog(msg.arg1, msg.arg2);
                    break;
            }
        }
    };
    public static final String USER_NOTIFY_WEIXIN = "http://114.215.28.26/YeVpnServer/wxPayBack.shtml";

    //三大支付是否成功
    public boolean isWx = false;
    public boolean isYinLian = false;
    public boolean isZhiFuBao = false;
    public int zfbFaildIjk = 0;
    public int ylFaildIjk = 0;
    public int wxFaildIjk = 0;
    //private View sanbaline;
    private String VPN_SHIYONG_FILE = Constant.VPN_SHIYONG_DIRECTORY + "/" + Constant.VPN_SHIYONG_FILE;
    private payInterface payInterface;

    private String vipYearPrice = "150";
    //    private String vipSixPrice = "260";
    private String vipThreePrice = "66";

    private String vipYearType = "一年";
    private String vipOneType = "一月";
    private String vipThreeType = "三个月";

    public String body_three = "H站大全三个月会员";
    public String body_one = "H站大全一个月会员";
    public String body_year = "H站大全一年会员";

    private String pay_Type_Choice = "zfb";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatus();
        setContentView(R.layout.activity_wxpay_entry);
        this.payInterface = this;
        //微信注册
        api = WXAPIFactory.createWXAPI(this, null);
        api.registerApp(Constant.APP_ID);
        api.handleIntent(getIntent(), this);
        initView();
        initData();
        initHorientalViews();
        initHorienTextView();

        //银联
        mHandler = new Handler(this);
    }


    private void initStatus() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.shenlan);//通知栏所需颜色
        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void initData() {
        dialog = createLoadingDialog(this, "正在初始化环境...请稍候");
        dialogyz = createLoadingDialog(this, "正在验证您的支付情况...请稍候");
        //获取用户账户数据
        getUserDataInfo();
    }

    public void getUserDataInfo() {
        show = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "show");
        imsi = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "imsi");
        nickName = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "nickName");
        userName = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "userName");
        password = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "passWord");
        USER_VIP_STATUS = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status");
        VIP_LAST_TIME = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_lastTme");
//
//        if (TextUtils.isEmpty(nickName) || TextUtils.isEmpty(USER_VIP_STATUS) || TextUtils.isEmpty(userName) || TextUtils.isEmpty(password) || TextUtils.isEmpty(VIP_LAST_TIME)) {
//            //没有登陆
//            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//            String username = util.getAndroidId(this);//用户名
//            tv_username.setText(username);
//            tv_usertype.setText("普通用户");
//            tv_viplasttime.setText("您还没有开通VIP哦");
//        } else {
//            //已经登陆,但要区分会员和非会员状态
//            String statusStr = aesUtils.getInstance().decrypt(USER_VIP_STATUS);
//            if (TextUtils.isEmpty(statusStr)) {
//                return;
//            } else if (statusStr.equals("1")) {
//                //会员
//                TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//                String username = util.getAndroidId(this);//用户名
//                tv_username.setText(username);
//                tv_usertype.setText("VIP高级会员");
//                String time = aesUtils.getInstance().decrypt(VIP_LAST_TIME);
//                time = time.substring(0, 10);
//                tv_viplasttime.setText(time);
//            } else if (statusStr.equals("2")) {
//                //不是会员
//                TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//                String username = util.getAndroidId(this);//用户名
//                tv_username.setText(username);
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//                String oldDate = aesUtils.decrypt(Util.readFileToSDFile(VPN_SHIYONG_FILE));
//                Date newDate = new Date();//
//                if (TextUtils.isEmpty(oldDate)) {
//                    tv_usertype.setText("试用用户");
//                    newDate.setHours(newDate.getHours() + 2);
//                    String ltime = sdf.format(newDate);
//                    tv_viplasttime.setText(ltime);
//                    return;
//                } else {
//                    tv_usertype.setText("试用用户");
//                    try {
//                        Date old = sdf.parse(oldDate);
//                        long[] time = getTime(newDate, old);
//                        if (time == null) {
//                            tv_viplasttime.setText("您的试用期已过期");
//                            return;
//                        }
//                        if (time[0] >= 1) {
//                            tv_viplasttime.setText("您的试用期已过期");
//                            return;
//                        }
//                        if (time[1] >= 2) {
//                            tv_viplasttime.setText("您的试用期已过期");
//                            return;
//                        } else {
//                            old.setHours(old.getHours() + 2);
//                            String ltime = sdf1.format(old);
//                            tv_viplasttime.setText(ltime);
//                        }
//                    } catch (ParseException e) {
//                    }
//                }
//            }
//        }
    }

    private void initView() {
        t = new T();
        util = new Util(this);
        aesUtils = new AesUtils();

        index = (TextView) findViewById(R.id.index);
        navigation = (TextView) findViewById(R.id.navigation);
        uservip = (TextView) findViewById(R.id.uservip);
        pay_price = (TextView) findViewById(R.id.pay_price);
        index.setBackgroundResource(R.mipmap.jiasu);

        navigation.setBackgroundResource(R.mipmap.daohang);

        uservip.setBackgroundResource(R.mipmap.huiyuan_select);

        index.setOnClickListener(connClick);
        uservip.setOnClickListener(userClick);
        navigation.setOnClickListener(naviClick);

        user_update = (ImageView) findViewById(R.id.user_update);
//        btn_six = (LinearLayout) findViewById(R.id.six);
        btn_year = (LinearLayout) findViewById(R.id.year);
        btn_one = (LinearLayout) findViewById(R.id.one);

        WXPay = (ImageView) findViewById(R.id.WXPay);
        ZhiFuBaoPay = (ImageView) findViewById(R.id.ZhiFuBaoPay);

        login = (ImageView) findViewById(R.id.login);
        user_update.setOnClickListener(updateClick);


        tv_three_type = (TextView) findViewById(R.id.tv_three_type);
//        tv_six_type = (TextView) findViewById(R.id.tv_six_type);
        tv_year_type = (TextView) findViewById(R.id.tv_year_type);

        tv_three_price = (TextView) findViewById(R.id.tv_three_price);
//        tv_six_price = (TextView) findViewById(R.id.tv_six_price);
        tv_year_price = (TextView) findViewById(R.id.tv_year_price);

        three_original_price = (TextView) findViewById(R.id.three_original_price);
//        six_original_price = (TextView) findViewById(R.id.six_original_price);
        year_original_price = (TextView) findViewById(R.id.year_original_price);

        if (Constant.priceInfoList == null || Constant.priceInfoList.size() == 0) {
            //没有获取到价格数据(使用默认)
            tv_three_price.setText(vipThreePrice + "元");
//            tv_six_price.setText(vipSixPrice + "元");
            tv_year_price.setText(vipYearPrice + "元");

            tv_three_type.setText(vipOneType);
//            tv_six_type.setText(vipSixType);
            tv_year_type.setText(vipThreeType);

        } else {
            //获取到了服务器的价格数据(从低到高)
            if (Constant.isShowThreeMonthPay) {
                vipThreePrice = Constant.priceInfoList.get(1).getPrice();
                vipYearPrice = Constant.priceInfoList.get(2).getPrice();
                tv_three_price.setText(vipThreePrice + "元");
//            tv_six_price.setText(vipSixPrice + "元");
                tv_year_price.setText(vipYearPrice + "元");

                three_original_price.setText("(原价" + (Integer.parseInt(vipThreePrice) + 30) + "元)");
//            six_original_price.setText("(原价" + (Integer.parseInt(vipSixPrice) + 40) + "元)");
                year_original_price.setText("(原价" + (Integer.parseInt(vipYearPrice) + 50) + "元)");

                vipThreeType = URLDecoder.decode(Constant.priceInfoList.get(1).getType());
//            vipSixType = URLDecoder.decode(Constant.priceInfoList.get(1).getType());
                vipYearType = URLDecoder.decode(Constant.priceInfoList.get(2).getType());

                tv_three_type.setText(vipThreeType);
//            tv_six_type.setText(vipSixType);
                tv_year_type.setText(vipYearType);

                body_three = URLDecoder.decode(Constant.priceInfoList.get(1).getDescription());
//            body_six = URLDecoder.decode(Constant.priceInfoList.get(1).getDescription());
                body_year = URLDecoder.decode(Constant.priceInfoList.get(2).getDescription());
            } else {
                vipThreePrice = Constant.priceInfoList.get(0).getPrice();
//            vipSixPrice = Constant.priceInfoList.get(1).getPrice();
                vipYearPrice = Constant.priceInfoList.get(1).getPrice();
                tv_three_price.setText(vipThreePrice + "元");
//            tv_six_price.setText(vipSixPrice + "元");
                tv_year_price.setText(vipYearPrice + "元");

                three_original_price.setText("(原价" + (Integer.parseInt(vipThreePrice) + 30) + "元)");
//            six_original_price.setText("(原价" + (Integer.parseInt(vipSixPrice) + 40) + "元)");
                year_original_price.setText("(原价" + (Integer.parseInt(vipYearPrice) + 50) + "元)");

                vipOneType = URLDecoder.decode(Constant.priceInfoList.get(0).getType());
//            vipSixType = URLDecoder.decode(Constant.priceInfoList.get(1).getType());
                vipThreeType = URLDecoder.decode(Constant.priceInfoList.get(1).getType());

                tv_three_type.setText(vipOneType);
//            tv_six_type.setText(vipSixType);
                tv_year_type.setText(vipThreeType);

                body_one = URLDecoder.decode(Constant.priceInfoList.get(0).getDescription());
//            body_six = URLDecoder.decode(Constant.priceInfoList.get(1).getDescription());
                body_three = URLDecoder.decode(Constant.priceInfoList.get(1).getDescription());
            }
        }

        initThreeMonthPay();

        if (Constant.isShowThreeMonthPay) {
            pay_price.setText(vipThreePrice + "~");
        } else {
            pay_price.setText("66" + "~");
        }

        btn_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.isShowThreeMonthPay) {
                    vipThreePrice = "150";
                    pay_price.setText(vipThreePrice);
                    VIP_TIME = vipThreeType;
                    body = body_three;
                    payInterface.payClick(pay_Type_Choice);
                } else {
                    vipThreePrice = "66";
                    pay_price.setText(vipThreePrice);
                    VIP_TIME = vipOneType;
                    body = body_one;
                    payInterface.payClick(pay_Type_Choice);
                }
            }
        });


//        btn_six.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                pay_price.setText(vipSixPrice);
////                btn_one.setImageResource(R.mipmap.three_month);
////                btn_six.setImageResource(R.mipmap.six_month_select);
////                btn_year.setImageResource(R.mipmap.year);
//
//                VIP_TIME = vipSixType;
//                body = body_six;
//                payInterface.payClick(pay_Type_Choice);
//            }
//        });

        btn_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay_price.setText(vipYearPrice);
//                btn_one.setImageResource(R.mipmap.three_month);
//                btn_six.setImageResource(R.mipmap.six_month);
//                btn_year.setImageResource(R.mipmap.year_select);
                if (Constant.isShowThreeMonthPay) {
                    vipYearPrice = "365";
                    VIP_TIME = vipYearType;
                    body = body_year;
                    payInterface.payClick(pay_Type_Choice);
                } else {
                    vipYearPrice = "150";
                    VIP_TIME = vipThreeType;
                    body = body_three;
                    payInterface.payClick(pay_Type_Choice);
                }
            }
        });

        String usernameStr = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "userName");
        String passWordStr = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "passWord");
        String showStr = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "show");

        if (TextUtils.isEmpty(showStr) || TextUtils.isEmpty(usernameStr) || TextUtils.isEmpty(passWordStr)) {
            login.setImageResource(R.mipmap.login);
        } else {
            login.setImageResource(R.mipmap.zhuxiao);
        }
        login.setOnClickListener(loginClick);

//        if (Constant.isShowOneMonthPay == false) {
//            sanbaline.setVisibility(View.GONE);
//            sanbapay.setVisibility(View.GONE);
//            openSixPay();
//        }

        //微信支付点击事件
        WXPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                WXPay.setImageResource(R.mipmap.iv_wx_select);
                ZhiFuBaoPay.setImageResource(R.mipmap.iv_zfb);
                pay_Type_Choice = "wx";
            }
        });
//        //银联支付点击事件
//        YinLianPay.setTag(0);
//        YinLianPay.setOnClickListener(mClickListener);
        //支付宝支付点击事件
        ZhiFuBaoPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZhiFuBaoPay.setImageResource(R.mipmap.iv_zfb_select);
                WXPay.setImageResource(R.mipmap.iv_wx);
                pay_Type_Choice = "zfb";
            }
        });
    }

    private void initThreeMonthPay() {
//        if (Constant.isShowThreeMonthPay) {
//            three_original_price.setText("(原价" + (Integer.parseInt("150") + 30) + "元)");
//            vipThreePrice = "150";
//            tv_three_price.setText(vipThreePrice + "元");
//            vipThreeType = "三个月";
//            VIP_TIME = "三个月";
//            tv_three_type.setText(vipThreeType);
//        }
    }

    public void openSixPay() {
//        pay_price.setText(vipSixPrice);
////        btn_one.setImageResource(R.mipmap.three_month);
////        btn_six.setImageResource(R.mipmap.six_month_select);
////        btn_year.setImageResource(R.mipmap.year);
//
//        VIP_TIME = vipSixType;
//        body = body_six;
    }

    @Override
    public void payClick(String type) {
        if (type.equals("wx")) {
            //微信支付友盟统计
            stopVpnConnection();//断开vpn
            MobclickAgent.onEvent(this, "41");//埋点统计
            isWx = false;
            WinXinPay();
        } else if (type.equals("zfb")) {
            //支付宝支付友盟统计
            stopVpnConnection();//断开vpn
            MobclickAgent.onEvent(this, "42");//埋点统计
            isZhiFuBao = false;
            ZFBPay();
        }
    }

    LinearLayout images;
    //LinearLayout sanbapay;
    int ims[] = new int[]{
            R.mipmap.a, R.mipmap.b
            , R.mipmap.c, R.mipmap.d,
            R.mipmap.e, R.mipmap.f,
            R.mipmap.g, R.mipmap.h,
            R.mipmap.a, R.mipmap.b
            , R.mipmap.c, R.mipmap.d,
            R.mipmap.e, R.mipmap.f,
            R.mipmap.g, R.mipmap.h,
            R.mipmap.a, R.mipmap.b
            , R.mipmap.c, R.mipmap.d,
            R.mipmap.e, R.mipmap.f,
            R.mipmap.g, R.mipmap.h,
            R.mipmap.a, R.mipmap.b
            , R.mipmap.c, R.mipmap.d,
            R.mipmap.e, R.mipmap.f,
            R.mipmap.g, R.mipmap.h,
            R.mipmap.a, R.mipmap.b
            , R.mipmap.c, R.mipmap.d,
            R.mipmap.e, R.mipmap.f,
            R.mipmap.g, R.mipmap.h,
            R.mipmap.a, R.mipmap.b
            , R.mipmap.c, R.mipmap.d,
            R.mipmap.e, R.mipmap.f,
            R.mipmap.g, R.mipmap.h, R.mipmap.a, R.mipmap.b
            , R.mipmap.c, R.mipmap.d,
            R.mipmap.e, R.mipmap.f,
            R.mipmap.g, R.mipmap.h, R.mipmap.a, R.mipmap.b
            , R.mipmap.c, R.mipmap.d,
            R.mipmap.e, R.mipmap.f,
            R.mipmap.g, R.mipmap.h,


    };

    HorizontalScrollView imParent;
    int x = 0;
    int y = 0;

    private void initHorientalViews() {
        images = (LinearLayout) findViewById(R.id.images);
        imParent = (HorizontalScrollView) findViewById(R.id.imParent);
        imParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        for (int i = 0; i < ims.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(ims[i]);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            imageView.setLayoutParams(llp);
            images.addView(imageView);
        }
        scroll();
    }

    public void scroll() {
        imParent.postDelayed(new Runnable() {
            @Override
            public void run() {
                scroll();
                x++;
                imParent.scrollTo(x, y);
            }
        }, 20);
    }

    ScrollTextView intro;

    private void initHorienTextView() {
        intro = (ScrollTextView) findViewById(R.id.intro);
        List<String> tBeans = new ArrayList<String>();
        int randomF;
        int randomL;
        for (int i = 0; i <= 50; i++) {
            randomF = 10 + (int) (Math.random() * 90);
            randomL = 100 + (int) (Math.random() * 900);
            tBeans.add(randomF + "**********" + randomL + "会员已经成功领取上千网站!");
        }
        intro.setStopTime(2000);  //设置停留时间
        if (tBeans != null && tBeans.size() > 0) {

            StringBuilder sBuilder = new StringBuilder();
            for (String threadlistBean : tBeans) {
                String content = threadlistBean;
                //content = content.length()>=15?content.substring(0, 14)+"..":content;
                sBuilder.append(content).append("k#");
            }
            sBuilder.deleteCharAt(sBuilder.lastIndexOf("#"));
            sBuilder.deleteCharAt(sBuilder.lastIndexOf("k"));
            intro.setScrollText(sBuilder.toString().trim());
        }
    }

    /**
     * tab选中事件
     */
    View.OnClickListener connClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //跳转activity
            Intent intent = new Intent(WXPayEntryActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade, R.anim.hold);
        }
    };


    View.OnClickListener userClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String usernameStr = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "userName");
            String passWordStr = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "passWord");
            String showStr = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "show");

            if (TextUtils.isEmpty(showStr) || TextUtils.isEmpty(usernameStr) || TextUtils.isEmpty(passWordStr)) {
                //没有登陆
                Intent intent = new Intent(WXPayEntryActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
                WXPayEntryActivity.this.finish();
            } else {
                //已经登陆过
                Dialog dialogZ = createLoadingDialog(WXPayEntryActivity.this, "正在退出登陆...");
                dialogZ.show();
                dialogZ.setCancelable(false);
                stopVpnConnection();//断开vpn
                util.sharedPreferencesDelByFileAllData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE);
                dialogZ.dismiss();
                t.centershow(WXPayEntryActivity.this, "用户退出了登陆", 1000);
                login.setImageResource(R.mipmap.login);
            }
        }
    };

    public long[] getTime(Date endDate, Date curDate) {
        long[] time = new long[3];
        if (curDate == null || endDate == null) {
            return null;
        }
        long diff = endDate.getTime() - curDate.getTime();

        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        time[0] = days;//天数
        time[1] = hours;//小时
        time[2] = minutes;//分
        return time;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            OpenVPNService.LocalBinder binder = (OpenVPNService.LocalBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }

    };

    /***
     * 断开VPN连接
     */
    protected OpenVPNService mService;

    public void stopVpnConnection() {
        ProfileManager.setConntectedVpnProfileDisconnected(WXPayEntryActivity.this);
        if (mService != null && mService.getManagement() != null) {
            mService.getManagement().stopVPN(false);
            mService.vpnStateClose();//在服务中上报连接状态
        }
    }

    View.OnClickListener naviClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //跳转activity
            Intent intent = new Intent(WXPayEntryActivity.this, NavigationActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade, R.anim.hold);
        }
    };

    View.OnClickListener updateClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            alertUpdate();
        }
    };


    Dialog dialog_update_data;
    EditText et_up_username, et__up_userpass, et_up_phone;
    TextView vip_username, vip_lastTime, vip_type;
    String vipUserName;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy--MM--dd HH:mm:ss");

    public void alertUpdate() {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_update, null);
        //对话框
        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
        if (dialog_update_data == null) {
            dialog_update_data = new Dialog(this, R.style.Dialog);
            dialog_update_data.show();
            Window window = dialog_update_data.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            layout.getBackground().setAlpha(50);
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
            window.setContentView(layout);

            vip_username = (TextView) layout.findViewById(R.id.vip_username);
            vip_lastTime = (TextView) layout.findViewById(R.id.vip_lastTime);
            vip_type = (TextView) layout.findViewById(R.id.vip_type);

            et_up_username = (EditText) layout.findViewById(R.id.et_up_username_dialog);
            et__up_userpass = (EditText) layout.findViewById(R.id.et__up_userpass_dialog);
            et_up_phone = (EditText) layout.findViewById(R.id.et_up_phone_dialog);

            vipUserName = util.getAndroidId(WXPayEntryActivity.this);
            et_up_username.setText(vipUserName);

            String nickName = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "nickName");
            String userName = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "userName");
            String password = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "passWord");
            String USER_VIP_STATUS = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status");
            String VIP_LAST_TIME = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_lastTme");

            if (TextUtils.isEmpty(nickName) || TextUtils.isEmpty(USER_VIP_STATUS) || TextUtils.isEmpty(userName) || TextUtils.isEmpty(password) || TextUtils.isEmpty(VIP_LAST_TIME)) {
                //没有登陆
                vip_username.setText(vipUserName);
                vip_type.setText("普通用户");
                vip_lastTime.setText("试用用户");
            } else {
                //已经登陆,但要区分会员和非会员状态
                String statusStr = aesUtils.getInstance().decrypt(USER_VIP_STATUS);
                if (TextUtils.isEmpty(statusStr)) {
                } else if (statusStr.equals("1")) {
                    //会员
                    vip_username.setText(vipUserName);
                    vip_type.setText("VIP高级会员");
                    String time = aesUtils.getInstance().decrypt(VIP_LAST_TIME);
                    time = time.substring(0, 10);
                    vip_lastTime.setText(time);
                } else if (statusStr.equals("2")) {
                    //不是会员
                    vip_username.setText(vipUserName);
                    int timerVpn = Integer.parseInt(Util.readFileToSDFile(VPN_SHIYONG_FILE));
                    if (timerVpn <= 0) {
                        vip_type.setText("试用用户");
                        vip_lastTime.setText(String.valueOf(Integer.parseInt(Util.readFileToSDFile(VPN_SHIYONG_FILE)) / 60) + "分钟");
                    } else {
                        vip_type.setText("试用用户");
                        try {
                            int timeSecond = Integer.parseInt(Util.readFileToSDFile(VPN_SHIYONG_FILE));
                            if (timeSecond <= 0) {
                                vip_lastTime.setText("试用期已过期");
                            } else {
                                vip_lastTime.setText(String.valueOf(timeSecond / 60) + "分钟");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            //update_ok按钮
            ImageView updatebtn = (ImageView) layout.findViewById(R.id.updatebtn);
            updatebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String userpass = et__up_userpass.getText().toString();
                    String phone = et_up_phone.getText().toString();
                    updateDB(vipUserName, userpass, phone);
                }
            });

            ImageView update_ret = (ImageView) layout.findViewById(R.id.update_ret);
            update_ret.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_update_data.dismiss();
                }
            });

        } else {
            dialog_update_data.show();
        }
    }


    public void updateDB(String up_username, final String up_password, final String up_tel_phone) {
        final Dialog toast = createLoadingDialog(this, "账户修改中...请稍候");
        if (isEmty(up_username, up_password, up_tel_phone)) {
            //修改逻辑处理
            toast.show();
            if (!util.isNetworkConnected(this)) {
                toast.dismiss();
                t.show(this, "网络没有连接", 1000);
                return;
            }
            final LoginBean info = new LoginBean();
            info.setUserName(up_username);
            info.setPassWord(util.getMD5Str(up_password));
            String iMeilLastId = up_username.substring(up_username.length() - 1);
            info.setImeiLastId(iMeilLastId);
            final TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String phone = tm.getLine1Number();
            if (TextUtils.isEmpty(phone)) {
                phone = "phone is null";
            }
            tel_phone = phone;
            info.setTel_phone(phone);
            final String json = JSON.toJSONString(info);
            String aesJson = aesUtils.encrypt(json);
            //发起请求
            RequestParams params = new RequestParams(Constant.USER_UPDATE);
            params.setCacheMaxAge(0);//最大数据缓存时间
            params.setConnectTimeout(15000);//连接超时时间
            params.setCharset("UTF-8");
            params.addQueryStringParameter("data", aesJson);

            org.xutils.x.http().post(params, new Callback.CommonCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    String jsonStr = result.toString();
                    String aesJson = aesUtils.decrypt(jsonStr);
                    try {
                        JSONObject object = new JSONObject(aesJson);
                        String updateStatus = object.getString("respMsg");
                        if (updateStatus.equals("fail")) {
                            t.show(WXPayEntryActivity.this, "用户信息修改失败", 2000);
                            toast.dismiss();
                            return;
                        } else if (updateStatus.equals("success")) {
                            //用户信息修改成功 逻辑处理
                            //重新写入昵称和密码(用户名imeiId无法改动)
                            //删除旧的数据
                            util.sharedPreferencesDelOrderData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "passWord");
                            util.sharedPreferencesDelOrderData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "show");
                            util.sharedPreferencesDelOrderData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "tel_phone");

                            //写入修改成功后的数据
                            util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "passWord", aesUtils.getInstance().encrypt(up_password));
                            util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "show", aesUtils.getInstance().encrypt(up_password));
                            util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "tel_phone", aesUtils.getInstance().encrypt(up_tel_phone));

                            util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.PASS_DATA, "pass", aesUtils.getInstance().encrypt(up_password));
                            t.centershow(WXPayEntryActivity.this, "用户信息修改成功", 2000);
                            toast.dismiss();
                            //自动登录
                            //autoLogin();
//                            util.sharedPreferencesDelByFileAllData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE);
//                            Intent intent = new Intent(WXPayEntryActivity.this, LoginActivity.class);
//                            startActivity(intent);
                        } else {
                            t.show(WXPayEntryActivity.this, "系统繁忙,请稍候再试", 1000);
                            toast.dismiss();
                        }
                    } catch (JSONException e) {
                        t.show(WXPayEntryActivity.this, "系统繁忙,请稍候再试", 3000);
                        toast.dismiss();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    toast.dismiss();
                    t.show(WXPayEntryActivity.this, "连接超时", 1000);
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                }
            });
        }

    }

    public boolean isEmty(String username, String password, String tel_phone) {
        boolean flag = false;
        if (TextUtils.isEmpty(username)) {
            flag = false;
            t.centershow(this, "请输入用户名", 1000);
        } else {
            if (TextUtils.isEmpty(password)) {
                flag = false;
                t.centershow(this, "请输入密码", 1000);
            } else if (!TextUtils.isEmpty(password)) {
                if (password.length() < 6 || password.length() > 6) {
                    flag = false;
                    t.centershow(this, "请输入6位长度的密码", 1000);
                } else {
                    if (TextUtils.isEmpty(tel_phone)) {
                        flag = false;
                        t.centershow(this, "请输入手机号码", 1000);
                    } else {
                        if (!util.isMobileNO(tel_phone)) {
                            flag = false;
                            t.centershow(this, "请输入正确格式的手机号码", 1000);
                        } else {
                            flag = true;
                        }
                    }
                }
            }
        }
        return flag;

    }


    /***
     * 加载窗
     *
     * @param context
     * @param msg
     * @return
     */

    public Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.progressdialog_no_deal, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.anim);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(false);// 可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        loadingDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                } else {
                    return true;
                }
            }
        });
        return loadingDialog;
    }

    /***
     * 微信调起控件开始支付
     */
    private String pay_key = "payfail";
    private ReqEntity reqEntity;
    private String WX_out_trade_no = "";//微信订单号

    public void WinXinPay() {
        //判断微信版本是否支持支付
        dialog.show();
        dialog.setCancelable(false);
        WXPay.setEnabled(false);//设置按钮不可用
        boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        if (isPaySupported) {
            //微信版本支持支付
            if (!util.isNetworkConnected(this)) {
                Toast.makeText(WXPayEntryActivity.this, "网络没有连接,请查看您的网络", Toast.LENGTH_LONG).show();
                yinLianHandler.sendEmptyMessage(50);
                return;
            }
            if (TextUtils.isEmpty(nickName) || TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
                setPay();//
                t.show(WXPayEntryActivity.this, "您还未登录哦", 1000);
                startActivity(new Intent(WXPayEntryActivity.this, LoginActivity.class).putExtra("qud", "qud"));
                finish();
                return;
            }
            if (TextUtils.isEmpty(VIP_TIME)) {
                t.show(WXPayEntryActivity.this, "请选择购买会员时间", 1000);
                yinLianHandler.sendEmptyMessage(50);
                return;
            }
            //获取服务器参数
            WX wx = new WX();
            if (VIP_TIME.equals(vipYearType)) {
                wx.setTotal_fee(vipYearPrice);//支付金额
                body = body_year;
            }
//            else if (VIP_TIME.equals(vipSixType)) {
//                wx.setTotal_fee(vipSixPrice);//支付金额
//            }
            else if (VIP_TIME.equals(vipThreeType)) {
                wx.setTotal_fee("150");//支付金额
                body = body_three;
            } else if (VIP_TIME.equals(vipOneType)) {
                wx.setTotal_fee("66");//支付金额
                body = body_one;
            }
            wx.setBody(URLEncoder.encode(body));//商品描述
            wx.setSpbill_create_ip("null");//ip
            String json = JSON.toJSONString(wx);
            //发起请求
            RequestParams params = new RequestParams(Constant.WX_PAY_ORDER);
            params.setCacheMaxAge(0);//最大数据缓存时间
            params.setConnectTimeout(5000);//连接超时时间
            params.setCharset("UTF-8");
            params.addQueryStringParameter("data", json);
            //断开vpn
            stopVpnConnection();//断开vpn

//            org.xutils.x.http().post(params, new Callback.CommonCallback<String>() {
//
//                @Override
//                public void onSuccess(String result) {
//                    stopVpnConnection();//断开vpn
//                    String jsonStr = result.toString();
//                    try {
//                        JSONObject json = new JSONObject(jsonStr);
//                        //初始化是否成功
//                        String respCode = json.getString("respCode");
//                        if (respCode.equals("000")) {
//                            PayReq req = new PayReq();
//                            //保存验证信息
//                            Constant.wxyz.setOut_trade_no(json.getString("out_trade_no"));
//                            //支付调起参数
//                            req.appId = json.getString("appid");
//                            req.partnerId = json.getString("partnerid");
//                            req.prepayId = json.getString("prepayid");
//                            req.nonceStr = json.getString("noncestr");
//                            req.timeStamp = json.getString("timestamp");
//                            req.packageValue = json.getString("package");
//                            req.sign = json.getString("sign");
//                            req.extData = "app data"; // optional
//
//                            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//                            api.sendReq(req);
//
//                            yinLianHandler.sendEmptyMessage(50);
//
//                            //存储用户支付信息，应对支付验证失败情况(重要)
//                            WXYZ wxyz = new WXYZ();
//                            imeilLastId = aesUtils.getInstance().decrypt(userName).trim().substring(aesUtils.getInstance().decrypt(userName).length() - 1);
//                            wxyz.setUserName(aesUtils.getInstance().decrypt(userName));
//                            wxyz.setImeiLastId(imeilLastId);
//                            wxyz.setOut_trade_no(Constant.wxyz.getOut_trade_no());//订单号
//                            wxyz.setPayTime(URLEncoder.encode(VIP_TIME));
//                            String wxjson = JSON.toJSONString(wxyz);
//                            String aesJson = aesUtils.encrypt(wxjson);
//                            util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.WX_USER_PAY_FAILED_FILE, pay_key, aesJson);
//                            stopVpnConnection();//断开vpn
//                        } else if (respCode.equals("111")) {
//                            t.show(WXPayEntryActivity.this, "微信初始化环境错误", 2000);
//                            yinLianHandler.sendEmptyMessage(50);
//                            return;
//                        }
//
//                    } catch (JSONException e) {
//                        yinLianHandler.sendEmptyMessage(50);
//                        //e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onError(Throwable ex, boolean isOnCallback) {
//                    WinXinPay();
//                    yinLianHandler.sendEmptyMessage(50);
//                }
//
//                @Override
//                public void onCancelled(CancelledException cex) {
//                }
//
//                @Override
//                public void onFinished() {
//                }
//            });
            final String appid = Constant.APP_ID;   //应用ID
            final String mch_id = "1400775702";  //商户号
            final String nonce_str = Convert.getNonceStr(); //随机字符串
            String sign = "";      //签名
            String out_trade_no = Utils.getOutTradeNo();  //商户订单号
            String notify_url = USER_NOTIFY_WEIXIN;    //通知地址
            String trade_type = "APP";  //交易类型
            String spbill_create_ip = GetIP.getIpAddr(); //终端IP
            SortedMap<String, String> packageParams = new TreeMap<String, String>();
            packageParams.put("appid", appid);
            packageParams.put("mch_id", mch_id);
            packageParams.put("nonce_str", nonce_str);
            packageParams.put("out_trade_no", out_trade_no);
            packageParams.put("notify_url", notify_url);
            packageParams.put("trade_type", trade_type);
            packageParams.put("body", body);
            String price = String.valueOf((Integer.parseInt(wx.getTotal_fee()) * 100));
            packageParams.put("total_fee", price);//价格单位是分  //String.valueOf((Integer.parseInt(pay_Price) * 100))
            packageParams.put("spbill_create_ip", spbill_create_ip);

            //保存订单号
            WX_out_trade_no = out_trade_no;

            final RequestHandler reqHandler = new RequestHandler();
            sign = reqHandler.createSign(packageParams);
            String xml = "<xml>" + "<appid>" + appid + "</appid>" + "<mch_id>"
                    + mch_id + "</mch_id>" + "<nonce_str>" + nonce_str
                    + "</nonce_str>" + "<sign>" + sign + "</sign>"
                    + "<body><![CDATA[" + body + "]]></body>" + "<out_trade_no>"
                    + out_trade_no + "</out_trade_no>" + "<total_fee>" + price + "</total_fee>"
                    + "<spbill_create_ip>" + spbill_create_ip
                    + "</spbill_create_ip>" + "<notify_url>" + notify_url
                    + "</notify_url>" + "<trade_type>" + trade_type
                    + "</trade_type>" + "</xml>";

            String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
            //获取预支付会话ID
//            String prepay_id = new GetWxOrderno().getPayNo(createOrderURL, xml,mContext);
            String prepay_id = "";
            RequestBody body = RequestBody.create(MediaType.parse("text/xml;charset=UTF-8"), xml);
            Request request = new Request.Builder().url(createOrderURL)
                    .post(body).build();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WXPayEntryActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                        }
                    });
                }
            }, 5000);
            Call call = new OkHttpClient().newCall(request);
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    yinLianHandler.sendEmptyMessage(8888);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        String prepay_id = "";
                        String jsonStr = response.body().string();
                        Log.d("zgx", "response=====" + jsonStr);
                        if (jsonStr.indexOf("FAIL") != -1) {
                        }
                        Map map = null;
                        try {
                            map = new GetWxOrderno().doXMLParse(jsonStr);
                        } catch (Exception e) {
                        }
                        prepay_id = (String) map.get("prepay_id");
                        if (!TextUtils.isEmpty(prepay_id)) {
                            SortedMap<String, String> packageParam = new TreeMap<String, String>();
                            String timestamp = Convert.getTimeStamp();
                            packageParam.put("appid", appid);
                            packageParam.put("partnerid", mch_id);
                            packageParam.put("prepayid", prepay_id);
                            packageParam.put("noncestr", nonce_str);
                            packageParam.put("timestamp", timestamp);
                            packageParam.put("package", "Sign=WXPay");
                            String sign_two = reqHandler.createSign(packageParam);

                            reqEntity = new ReqEntity();
                            reqEntity.setAppid(appid);
                            reqEntity.setPartnerid(mch_id);
                            reqEntity.setPrepayid(prepay_id);
                            reqEntity.setNoncestr(nonce_str);
                            reqEntity.setTimestamp(timestamp);
                            reqEntity.setPackages("Sign=WXPay");
                            reqEntity.setSign_two(sign_two);

//                            Log.i("paystr", "appid= " + appid + " partnerid= " + mch_id + " prepayid= " + prepay_id +
//                                    " noncestr= " + nonce_str + " timestamp= " + timestamp + " package= " + "Sign=WXPay" + " sign_two= " + sign_two);
                            //存储用户支付信息，应对支付验证失败情况(重要)
                            WXYZ wxyz = new WXYZ();
                            imeilLastId = aesUtils.getInstance().decrypt(userName).trim().substring(aesUtils.getInstance().decrypt(userName).length() - 1);
                            wxyz.setUserName(aesUtils.getInstance().decrypt(userName));
                            wxyz.setImeiLastId(imeilLastId);
                            wxyz.setOut_trade_no(WX_out_trade_no);//订单号
                            wxyz.setPayTime(URLEncoder.encode(VIP_TIME));
                            String wxjson = JSON.toJSONString(wxyz);
                            String aesJson = aesUtils.encrypt(wxjson);
                            util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.WX_USER_PAY_FAILED_FILE, pay_key, aesJson);
                            stopVpnConnection();//断开vpn
                            sendReg();
                        } else {
                        }
                    } catch (Exception e) {
                    }
                }
            });

        } else {
            yinLianHandler.sendEmptyMessage(8888);
            //微信版本不支持支付
            yinLianHandler.sendEmptyMessage(50);
            t.show(WXPayEntryActivity.this, "您还没有使用微信应用,或者您的微信版本不支持支付,请下载微信最新版本", 3000);
        }
    }

    private void sendReg() {
        boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        if (isPaySupported) {
            //微信版本支持支付
            PayReq req = new PayReq();
            //支付调起参数
            req.appId = reqEntity.getAppid();
            req.partnerId = reqEntity.getPartnerid();
            req.prepayId = reqEntity.getPrepayid();
            req.nonceStr = reqEntity.getNoncestr();
            req.timeStamp = reqEntity.getTimestamp();
            req.packageValue = reqEntity.getPackages();
            req.sign = reqEntity.getSign_two();
            req.extData = "app data"; // optional
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
            api.sendReq(req);
        } else {
            //微信版本不支持支付
            T.showCenterToast(this, "您还没有使用微信应用,或者您的微信版本不支持支付,请下载微信最新版本");
        }
    }

    /**
     * 微信回调
     *
     * @param
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        Log.i("spl", "回调1");
    }


    //微信后台验证
    @Override
    public void onResp(BaseResp resp) {
        dialogyz.show();
        dialogyz.setCancelable(false);
        if (!util.isNetworkConnected(this)) {
            dialogyz.dismiss();
            Toast.makeText(WXPayEntryActivity.this, "网络没有连接,请查看您的网络", Toast.LENGTH_LONG).show();
            return;
        }
        String errorStr = resp.errStr;
        int code = resp.errCode;
        //回调后验证用户是否支付成功
        switch (code) {
            case 0://支付成功后的界面
                //后台验证用户微信是否支付成功
                dialogyz.dismiss();
                weiXinYanZhen("");
                break;
            case -1:
                dialogyz.dismiss();
                dialog.dismiss();
                alertFaild();
                //签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。" + String.valueOf(resp.errCode)
                //t.show(WXPayEntryActivity.this, "您的账号在另外一处登陆,请重新登陆微信再支付", 3000);
                t.show(WXPayEntryActivity.this, "支付异常", 3000);
                break;
            case -2://用户取消支付后的界面
                dialogyz.dismiss();
                dialog.dismiss();
                alertFaild();
                t.show(WXPayEntryActivity.this, "您取消了支付", 3000);
                //用户取消支付删除应对微信支付失败的记录
                util.sharedPreferencesDelByFileAllData(WXPayEntryActivity.this, KeyFile.WX_USER_PAY_FAILED_FILE);
                break;
        }
    }

    private boolean wxIsSend = true;

    //微信验证方法
    public void weiXinYanZhen(final String payJson) {
        if (wxIsSend == false) {
            return;
        }
        Log.i("sendStr", "执行了一次这个方法weiXinYanZhen");
        wxIsSend = false;
        stopVpnConnection();//关闭vpn连接
        dialogyz.show();
        dialogyz.setCancelable(false);
        if (TextUtils.isEmpty(nickName) || TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            wxIsSend = true;
            t.show(WXPayEntryActivity.this, "您还未登录哦", 2000);
            dialogyz.dismiss();
            startActivity(new Intent(WXPayEntryActivity.this, LoginActivity.class).putExtra("qud", "qud"));
            finish();
            return;
        }
        RequestParams params = null;
        if (TextUtils.isEmpty(payJson)) {
            if (TextUtils.isEmpty(WX_out_trade_no)) {
                wxIsSend = true;
                t.show(WXPayEntryActivity.this, "您还没有支付哦", 2000);
                dialogyz.dismiss();
                return;
            }
            WXYZ wxyz = new WXYZ();
            imeilLastId = aesUtils.getInstance().decrypt(userName).trim().substring(aesUtils.getInstance().decrypt(userName).length() - 1);
            wxyz.setUserName(aesUtils.getInstance().decrypt(userName));
            wxyz.setImeiLastId(imeilLastId);
            wxyz.setOut_trade_no(WX_out_trade_no);//订单号
            wxyz.setPayTime(URLEncoder.encode(VIP_TIME));
            String wxjson = JSON.toJSONString(wxyz);
            //发起请求
            params = new RequestParams(Constant.WX_PAY_YZ);
            params.setCacheMaxAge(0);//最大数据缓存时间
            params.setConnectTimeout(5000);//连接超时时间
            params.setCharset("UTF-8");
            params.addQueryStringParameter("data", wxjson);
        } else {
            //发起请求
            params = new RequestParams(Constant.WX_PAY_YZ);
            params.setCacheMaxAge(0);//最大数据缓存时间
            params.setConnectTimeout(5000);//连接超时时间
            params.setCharset("UTF-8");
            params.addQueryStringParameter("data", payJson);
        }

        org.xutils.x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                wxIsSend = true;
                stopVpnConnection();//断开vpn
                dialogyz.dismiss();
                String jsonStr = result.toString();
                if (TextUtils.isEmpty(jsonStr)) {
                    return;
                }
                try {
                    JSONObject object = new JSONObject(jsonStr);
                    Iterator keys = object.keys();
                    boolean isHave = false;//是否有错误节点
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        if (key.equals("error")) {
                            isHave = true;
                            break;
                        }
                    }
                    if (isHave == false) {
                        String msg = object.getString("respCode");
                        String respMsg = object.getString("respMsg");
                        if (msg.equals("000")) {
                            //微信支付成功
                            if (respMsg.equals("SUCCESS")) {
                                Constant.isShowThreeMonthPay = true;
                                initThreeMonthPay();
                                wxIsSend = true;
                                wxFaildIjk = 0;
                                isWx = true;
                                alertSucces();
                                //隐藏38元支付
//                                sanbapay.setVisibility(View.GONE);
//                                sanbaline.setVisibility(View.GONE);
                                openSixPay();
                                //支付成功后删除应对微信支付失败的记录
                                util.sharedPreferencesDelByFileAllData(WXPayEntryActivity.this, KeyFile.WX_USER_PAY_FAILED_FILE);
                                //支付处理
                                util.sharedPreferencesDelOrderData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status");
                                util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status", aesUtils.getInstance().encrypt("1"));
                                syncLocalData();
                                SendYouMeng();
                                alertDownApk();
                            } else {
                                wxFaildIjk++;
                                if (wxFaildIjk <= 3) {
                                    wxIsSend = true;
                                    t.show(WXPayEntryActivity.this, "正在验证您的支付情况,请耐心等待。", 1000);
                                    if (TextUtils.isEmpty(payJson)) {
                                        //不是做失败记录验证
                                        yinLianHandler.sendEmptyMessage(101);
                                    } else {
                                        //做失败记录验证
//                                        String json = payJson;
//                                        weiXinYanZhen(json);
                                        yinLianHandler.sendEmptyMessage(8989);
                                    }
                                } else {
                                    t.show(WXPayEntryActivity.this, "验证失败", 1000);
                                    alerPayFailTiShi();
                                }
                            }
                        } else {
                            wxFaildIjk++;
                            wxIsSend = true;
                            if (wxFaildIjk <= 3) {
                                t.show(WXPayEntryActivity.this, "正在验证您的支付情况,请不要离开哦", 1000);
                                if (TextUtils.isEmpty(payJson)) {
                                    //不是做失败记录验证
                                    yinLianHandler.sendEmptyMessage(101);//验证3次
                                } else {
                                    //做失败记录验证
//                                    String json = payJson;
//                                    weiXinYanZhen(json);
                                    yinLianHandler.sendEmptyMessage(8989);
                                }
                            } else {
                                t.show(WXPayEntryActivity.this, "验证失败", 1000);
                                alerPayFailTiShi();
                            }
                        }
                    } else if (isHave == true) {
                        //用户取消支付删除应对微信支付失败的记录
                        util.sharedPreferencesDelByFileAllData(WXPayEntryActivity.this, KeyFile.WX_USER_PAY_FAILED_FILE);
                    }

                } catch (JSONException e) {
                    //wxFaildIjk++;
                    wxIsSend = true;
                    dialogyz.dismiss();
                    syncLocalData();
                    t.show(WXPayEntryActivity.this, "由于网络原因,请您重新进入支付界面，完成您的支付验证。", 3000);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                wxIsSend = true;
                dialogyz.dismiss();
                syncLocalData();
                t.show(WXPayEntryActivity.this, "由于服务器连接异常超时原因,请您重新进入支付界面，完成您的支付验证。", 3000);
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }


    //银联
    public void doStartUnionPayPlugin(Activity activity, String tn,
                                      String mode) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }

        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            // 支付成功后，extra中如果存在result_data，取出校验
            // result_data结构见c）result_data参数说明
            if (data.hasExtra("result_data")) {
                String result = data.getExtras().getString("result_data");
                try {
                    JSONObject resultJson = new JSONObject(result);
                    String sign = resultJson.getString("sign");
                    String dataOrg = resultJson.getString("data");
                    // 验签证书同后台验签证书
                    // 此处的verify，商户需送去商户后台做验签
                    // 验证通过后，显示支付结果
                    //后台验证支付是否成功
                    dialogyz.dismiss();
                    UPPayYz();

                } catch (JSONException e) {
                    //e.printStackTrace();
                }
            } else {
                // 未收到签名信息
                // 建议通过商户后台查询支付结果
                msg = "支付成功！";
                UPPayYz();
            }
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
            alertFaild();
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
            alertFaild();
        }


    }

    //银联支付最后验证
    public void UPPayYz() {
        if (tn == null) {
            //tn号不存在
            Toast.makeText(WXPayEntryActivity.this, "您还没有支付哦", Toast.LENGTH_LONG).show();
            return;
        }
        VertificationData();
    }

    //弹窗
    private AlertDialog myDialog1 = null;

    private void alertSucces() {
        if (myDialog1 == null) {
            myDialog1 = new AlertDialog.Builder(WXPayEntryActivity.this).create();

            myDialog1.show();

            myDialog1.getWindow().setLayout(2 * util.getWidth() / 2, 2 * util.getHeight() / 7);

            myDialog1.getWindow().setContentView(R.layout.alert_succes);

            myDialog1.getWindow()
                    .findViewById(R.id.alert_btn)
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            myDialog1.dismiss();
                        }

                    });
        } else {
            myDialog1.show();
        }


    }

    private AlertDialog myDialog = null;

    private void alertFaild() {

        if (myDialog == null) {
            myDialog = new AlertDialog.Builder(WXPayEntryActivity.this).create();

            myDialog.show();

            myDialog.getWindow().setLayout(2 * util.getWidth() / 2, 2 * util.getHeight() / 7);

            myDialog.getWindow().setContentView(R.layout.alert_faild);

            myDialog.getWindow()
                    .findViewById(R.id.alert_btn)
                    .setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            myDialog.dismiss();
                        }

                    });
        } else {
            myDialog.show();
        }


    }

    //银联支付后台验证用户是否支付
    private void VertificationData() {
        dialogyz.show();
        dialogyz.setCancelable(false);
        if (TextUtils.isEmpty(nickName) || TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            t.show(WXPayEntryActivity.this, "您还未登录哦", 2000);
            dialogyz.dismiss();
            startActivity(new Intent(WXPayEntryActivity.this, LoginActivity.class).putExtra("qud", "qud"));
            finish();
            return;
        }
        Vertification str = new Vertification();
        str.setOut_trade_no(tn.getOrdernumber());
        str.setTxnTime(tn.getTxntime());

        Vertification data = new Vertification();
        data.setOut_trade_no(str.getOut_trade_no());//订单号
        data.setTxnTime(str.getTxnTime());//支付时间
        data.setUserName(aesUtils.getInstance().decrypt(userName));//用户名
        imeilLastId = aesUtils.getInstance().decrypt(userName).trim().substring(aesUtils.getInstance().decrypt(userName).length() - 1);
        data.setImeiLastId(imeilLastId);//lastid
        data.setPayTime(URLEncoder.encode(VIP_TIME));
        String json = JSON.toJSONString(data);

        //发起请求
        RequestParams params = new RequestParams("");
        params.setCacheMaxAge(0);//最大数据缓存时间
        params.setConnectTimeout(5000);//连接超时时间
        params.setCharset("UTF-8");
        params.addQueryStringParameter("data", json);

        org.xutils.x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                String uppayJson = result.toString();
                try {
                    JSONObject object = new JSONObject(uppayJson);
                    String payCode = object.getString("respCode");
                    String payMsg = object.getString("respMsg");
                    if (payCode.equals("000")) {
                        if (payMsg.equals("00")) {
                            ylFaildIjk = 0;
                            isYinLian = true;
                            alertSucces();
                            util.sharedPreferencesDelOrderData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status");
                            util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status", aesUtils.getInstance().encrypt("1"));
                            syncLocalData();
                            SendYouMeng();
                        }
                    } else {
                        ylFaildIjk++;
                        if (ylFaildIjk <= 5) {
                            t.show(WXPayEntryActivity.this, "正在验证您的支付情况,请不要离开哦", 2000);
                            UPPayYz();//
                        } else {
                            alertFaild();
                        }
                        syncLocalData();
                    }
                } catch (JSONException e) {
                    syncLocalData();
                    //t.show(WXPayEntryActivity.this, "银联后台验证中", 2000);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                syncLocalData();
                t.show(WXPayEntryActivity.this, "服务器连接异常或连接超时", 2000);
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    DataTn tn = new DataTn();

    //银联初始化环境
    @Override
    public void run() {
        if (TextUtils.isEmpty(nickName) || TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            yinLianHandler.sendEmptyMessage(60);
            Looper.prepare();
            t.show(WXPayEntryActivity.this, "您还未登录哦", 2000);
            startActivity(new Intent(WXPayEntryActivity.this, LoginActivity.class).putExtra("qud", "qud"));
            Looper.loop();
            finish();
            return;
        }
        if (!util.isNetworkConnected(WXPayEntryActivity.this)) {
            yinLianHandler.sendEmptyMessage(50);
            Looper.prepare();
            t.show(WXPayEntryActivity.this, "您的网络没有连接", 2000);
            Looper.loop();
            return;
        }
        if (TextUtils.isEmpty(VIP_TIME)) {
            yinLianHandler.sendEmptyMessage(50);
            Looper.prepare();
            t.show(WXPayEntryActivity.this, "请选择购买会员时间", 2000);
            Looper.loop();
            return;
        }
        DataTn data1 = new DataTn();
        if (VIP_TIME.equals("一年")) {
            data1.setPaymoney(vipYearPrice);//支付金额
        }
//        else if (VIP_TIME.equals("半年")) {
//            data1.setPaymoney(vipSixPrice);//支付金额
//        }
        else if (VIP_TIME.equals("三个月")) {
            data1.setPaymoney(vipThreePrice);//支付金额
        }
        data1.setTxntime("");
        data1.setOrdernumber("");

        //发起请求
        RequestParams params = new RequestParams("");
        params.setCacheMaxAge(0);//最大数据缓存时间
        params.setConnectTimeout(5000);//连接超时时间
        params.setCharset("UTF-8");
        params.addQueryStringParameter("data", JSON.toJSONString(data1));
        //断开vpn
        stopVpnConnection();//断开vpn

        org.xutils.x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    String jsonStr = result.toString();
                    tn = JSON.parseObject(jsonStr, DataTn.class);
                    if (tn.getRespCode().equals("111")) {
                        yinLianHandler.sendEmptyMessage(50);
                        Message msg = mHandler.obtainMessage();
                        msg.obj = tn.getTn();
                        mHandler.sendMessage(msg);
                    } else {
                        yinLianHandler.sendEmptyMessage(50);
                        Looper.prepare();
                        t.show(WXPayEntryActivity.this, "银联初始化环境失败", 2000);
                        Looper.loop();
                        return;
                    }
                } catch (Exception e) {
                    //Looper.prepare();
                    yinLianHandler.sendEmptyMessage(50);
                    t.show(WXPayEntryActivity.this, "银联初始化环境失败", 2000);
                    //Looper.loop();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //Looper.prepare();
                yinLianHandler.sendEmptyMessage(50);
                t.show(WXPayEntryActivity.this, "服务器连接异常或连接超时", 2000);
                //Looper.loop();
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });

    }

    int startpay(Activity act, String tn, int serverIdentifier) {
        return 0;
    }


    @Override
    public boolean handleMessage(Message message) {
        Log.e(LOG_TAG, " " + "" + message.obj);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        String tn = "";
        if (message.obj == null || ((String) message.obj).length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(WXPayEntryActivity.this);
            builder.setTitle("错误提示");
            builder.setMessage("网络连接失败,请重试!");
            builder.setNegativeButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        } else {
            tn = (String) message.obj;
            /*************************************************
             * 步骤2：通过银联工具类启动支付插件
             ************************************************/
            //doStartUnionPayPlugin(WXPayEntryActivity.this, tn, mMode);

            UPPayAssistEx.startPay(WXPayEntryActivity.this, null, null, tn, mMode);
        }
        return false;
    }

    //支付宝支付
    /**
     * call alipay sdk pay. 调用SDK支付
     * Alipay保存支付参数信息
     */

    Alipay alipayInfo = new Alipay();//保存支付信息
    public String notify_Url = null;

    public void ZFBPay() {
        //获取支付宝订单信息
        ZhiFuBaoPay.setEnabled(false);//按钮不可用
        dialog.show();
        dialog.setCancelable(false);
        if (TextUtils.isEmpty(nickName) || TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            setPay();//
            t.show(WXPayEntryActivity.this, "您还未登录哦", 2000);
            startActivity(new Intent(WXPayEntryActivity.this, LoginActivity.class).putExtra("qud", "qud"));
            finish();
            return;
        }
        if (!util.isNetworkConnected(WXPayEntryActivity.this)) {
            t.show(WXPayEntryActivity.this, "您的网络没有连接", 2000);
            yinLianHandler.sendEmptyMessage(50);
            return;
        }
        if (TextUtils.isEmpty(VIP_TIME)) {
            t.show(WXPayEntryActivity.this, "请选择购买会员时间", 2000);
            yinLianHandler.sendEmptyMessage(50);
            return;
        }
        //断开vpn
        stopVpnConnection();//断开vpn
        //发起请求
        RequestParams params = new RequestParams(Constant.ZFB_QUERY);
        params.setCacheMaxAge(0);//最大数据缓存时间
        params.setConnectTimeout(5000);//连接超时时间
        params.setCharset("UTF-8");
        org.xutils.x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                stopVpnConnection();//断开vpn
//                String json = result.toString();
//                try {
//                    alipayInfo = JSON.parseObject(json, Alipay.class);
//                    //支付宝初始化是否成功
//                    if (alipayInfo.getRespCode().equals("000")) {
//                        //获得支付宝支付参数
//                        PARTNER = alipayInfo.getPartner();
//                        SELLER = alipayInfo.getSeller();
//                        RSA_PRIVATE = alipayInfo.getRsa_private();
//                        notify_Url = alipayInfo.getNotify_url();
//                        //RSA_PUBLIC = alipayInfo.getRsa_public();
//                        startZFBPayHandler.sendEmptyMessage(10);
//                        yinLianHandler.sendEmptyMessage(50);
//
//                    } else {
//                        t.show(WXPayEntryActivity.this, "支付宝初始化环境失败", 2000);
//                        yinLianHandler.sendEmptyMessage(50);
//                        return;
//                    }
//                } catch (Exception e) {
//                    t.show(WXPayEntryActivity.this, "支付宝初始化环境失败", 2000);
//                    yinLianHandler.sendEmptyMessage(50);
//                    return;
//                }
                notify_Url = "http://114.215.28.26/YeVpnServer/aliPyaBack.shtml";
                //RSA_PUBLIC = alipayInfo.getRsa_public();
                startZFBPayHandler.sendEmptyMessage(10);
                yinLianHandler.sendEmptyMessage(50);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                yinLianHandler.sendEmptyMessage(50);
                ZFBPay();
                return;
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    Handler startZFBPayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 10) {
                //支付参数确定
                if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
                    new AlertDialog.Builder(WXPayEntryActivity.this).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialoginterface, int i) {
                                    //
                                    finish();
                                }
                            }).show();
                    return;
                }
                String bodyTitle = null;
                String payMoney = "";//
                if (VIP_TIME.equals(vipYearType)) {
                    //payMoney = "120";//支付金额
                    payMoney = vipYearPrice;//支付金额
                    bodyTitle = body_year;
                }
//                else if (VIP_TIME.equals(vipSixType)) {
//                    //payMoney = "99";//支付金额
//                    payMoney = vipSixPrice;//支付金额
//                    bodyTitle = body_six;
//                }
                else if (VIP_TIME.equals(vipThreeType)) {
                    payMoney = "150";//支付金额
                    bodyTitle = body_three;
                } else if (VIP_TIME.equals(vipOneType)) {
                    payMoney = "66";//支付金额
                    bodyTitle = body_one;
                }
                String orderInfo = getOrderInfo(URLEncoder.encode(bodyTitle), URLEncoder.encode(body), payMoney);

                //保存支付宝支付信息，应对支付失败的情况（重要）,代码之所以在这里，是因为订单号在这里才进行生成。
                AplipayYZ aplipayYZ = new AplipayYZ();
                aplipayYZ.setOut_trade_no(Zfb_out_trade_no);
                aplipayYZ.setUserName(aesUtils.getInstance().decrypt(userName));
                imeilLastId = aesUtils.getInstance().decrypt(userName).trim().substring(aesUtils.getInstance().decrypt(userName).length() - 1);
                aplipayYZ.setImeiLastId(imeilLastId);
                aplipayYZ.setPayTime(URLEncoder.encode(VIP_TIME));
                final String zfbJson = JSON.toJSONString(aplipayYZ);
                String aesJson = aesUtils.encrypt(zfbJson);
                util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.ZHI_FU_BAO_USER_PAY_FAILED_FILE, pay_key, aesJson);

                /**
                 * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
                 */
                String sign = sign(orderInfo);
                try {
                    /**
                     * 仅需对sign 做URL编码
                     */
                    sign = URLEncoder.encode(sign, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                /**
                 * 完整的符合支付宝参数规范的订单信息
                 */
                final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

                Runnable payRunnable = new Runnable() {

                    @Override
                    public void run() {
                        // 构造PayTask 对象
                        PayTask alipay = new PayTask(WXPayEntryActivity.this);
                        // 调用支付接口，获取支付结果
                        String result = alipay.pay(payInfo, true);

                        Message msg = new Message();
                        msg.what = SDK_PAY_FLAG;
                        msg.obj = result;
                        zfbHandler.sendMessage(msg);
                    }
                };

                // 必须异步调用
                Thread payThread = new Thread(payRunnable);
                payThread.start();
            }
            super.handleMessage(msg);
        }
    };


    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }

    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        Zfb_out_trade_no = getOutTradeNo();
        orderInfo += "&out_trade_no=" + "\"" + Zfb_out_trade_no + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + URLDecoder.decode(subject) + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + URLDecoder.decode(body) + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + notify_Url + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }


    //支付宝后台验证
    @SuppressLint("HandlerLeak")
    private Handler zfbHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            dialogyz.show();
            dialogyz.setCancelable(false);
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    final String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        //后台验证支付宝支付是否成功
                        //支付宝后台验证
                        isZfbSend = true;
                        dialogyz.dismiss();
                        zhiFuBaoYanZhen("");

                    } else {
                        //支付宝支付失败
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            alertFaild();
                            isZfbSend = true;
                            dialogyz.dismiss();
                            Toast.makeText(WXPayEntryActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            isZfbSend = false;
                            alertFaild();
                            dialogyz.dismiss();
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(WXPayEntryActivity.this, "您取消了支付", Toast.LENGTH_SHORT).show();
                            //删除支付宝应对支付失败的记录
                            util.sharedPreferencesDelByFileAllData(WXPayEntryActivity.this, KeyFile.ZHI_FU_BAO_USER_PAY_FAILED_FILE);
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    //支付宝验证方法
    private boolean isZfbSend = false;

    public void zhiFuBaoYanZhen(final String payJson) {
        if (isZfbSend == false) {
            return;
        }
        Log.i("sendStr", "执行了一次这个方法zhiFuBaoYanZhen");
        isZfbSend = false;
        stopVpnConnection();//关闭vpn连接
        dialogyz.show();
        dialogyz.setCancelable(false);
        if (TextUtils.isEmpty(nickName) || TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            isZfbSend = true;
            t.show(WXPayEntryActivity.this, "您还未登录哦", 2000);
            dialogyz.dismiss();
            startActivity(new Intent(WXPayEntryActivity.this, LoginActivity.class).putExtra("qud", "qud"));
            finish();
            return;
        }
        RequestParams params = null;
        if (TextUtils.isEmpty(payJson)) {
            if (TextUtils.isEmpty(Zfb_out_trade_no)) {
                isZfbSend = true;
                t.show(WXPayEntryActivity.this, "您还没有支付哦", 2000);
                dialogyz.dismiss();
                return;
            }
            AplipayYZ aplipayYZ = new AplipayYZ();
            aplipayYZ.setOut_trade_no(Zfb_out_trade_no);
            aplipayYZ.setUserName(aesUtils.getInstance().decrypt(userName));
            imeilLastId = aesUtils.getInstance().decrypt(userName).trim().substring(aesUtils.getInstance().decrypt(userName).length() - 1);
            aplipayYZ.setImeiLastId(imeilLastId);
            aplipayYZ.setPayTime(URLEncoder.encode(VIP_TIME));
            final String zfbJson = JSON.toJSONString(aplipayYZ);
            //发起请求
            params = new RequestParams(Constant.ZFB_YZ);
            params.setCacheMaxAge(0);//最大数据缓存时间
            params.setConnectTimeout(5000);//连接超时时间
            params.setCharset("UTF-8");
            params.addQueryStringParameter("data", zfbJson);
        } else {
            params = new RequestParams(Constant.ZFB_YZ);
            params.setCacheMaxAge(0);//最大数据缓存时间
            params.setConnectTimeout(5000);//连接超时时间
            params.setCharset("UTF-8");
            params.addQueryStringParameter("data", payJson);
        }

        org.xutils.x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                String ZFBJson = result.toString();
                if (TextUtils.isEmpty(ZFBJson)) {
                    isZfbSend = true;
                    return;
                }
                try {
                    JSONObject object = new JSONObject(ZFBJson);
                    Iterator keys = object.keys();
                    boolean isHave = false;
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        if (key.equals("error")) {
                            isHave = true;
                            break;
                        }
                    }
                    if (isHave == false) {
                        String respCode = object.getString("respCode");
                        String respMsg = object.getString("respMsg");
                        if (respCode.equals("000")) {
                            if (respMsg.equals("TRADE_SUCCESS") || respMsg.equals("TRADE_FINISHED")) {
                                Constant.isShowThreeMonthPay = true;
                                initThreeMonthPay();
                                isZfbSend = true;
                                zfbFaildIjk = 0;
                                isZhiFuBao = true;
                                alertSucces();
                                dialogyz.dismiss();
                                //隐藏38元支付
//                                sanbapay.setVisibility(View.GONE);
//                                sanbaline.setVisibility(View.GONE);
                                openSixPay();
                                //删除支付宝应对支付失败的记录
                                util.sharedPreferencesDelByFileAllData(WXPayEntryActivity.this, KeyFile.ZHI_FU_BAO_USER_PAY_FAILED_FILE);

                                //修改用户会员状态
                                util.sharedPreferencesDelOrderData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status");
                                util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status", aesUtils.getInstance().encrypt("1"));

                                syncLocalData();
                                SendYouMeng();
                                alertDownApk();
                            } else {
                                isZfbSend = true;
                                zfbFaildIjk++;
                                dialogyz.dismiss();
                                if (zfbFaildIjk <= 3) {
                                    t.show(WXPayEntryActivity.this, "正在验证您的支付情况,请耐心等待。", 1000);
                                    if (TextUtils.isEmpty(payJson)) {
                                        //不是做失败记录验证
                                        yinLianHandler.sendEmptyMessage(102);
                                    } else {
                                        //做失败记录验证
//                                        String json = payJson;
//                                        zhiFuBaoYanZhen(json);
                                        yinLianHandler.sendEmptyMessage(1919);
                                    }
                                } else {
                                    t.show(WXPayEntryActivity.this, "验证失败", 1000);
                                    alerPayFailTiShi();
                                }
                                return;
                            }

                        } else {
                            zfbFaildIjk++;
                            isZfbSend = true;
                            dialogyz.dismiss();
                            if (zfbFaildIjk <= 3) {
                                t.show(WXPayEntryActivity.this, "正在验证您的支付情况,请耐心等待.", 2000);
                                if (TextUtils.isEmpty(payJson)) {
                                    //不是做失败记录验证
                                    yinLianHandler.sendEmptyMessage(102);//验证3次
                                } else {
                                    //做失败记录验证
//                                    String json = payJson;
//                                    zhiFuBaoYanZhen(json);
                                    yinLianHandler.sendEmptyMessage(1919);
                                }
                            } else {
                                t.show(WXPayEntryActivity.this, "验证失败", 1000);
                                alerPayFailTiShi();
                            }
                            return;
                        }
                    } else if (isHave == true) {
                        //支付成功后删除应对微信支付失败的记录
                        util.sharedPreferencesDelByFileAllData(WXPayEntryActivity.this, KeyFile.ZHI_FU_BAO_USER_PAY_FAILED_FILE);
                    }

                } catch (JSONException e) {
                    isZfbSend = true;
                    dialogyz.dismiss();
                    alertFaild();
                    //t.show(WXPayEntryActivity.this, "正在验证您的支付情况,请不要离开哦", 2000);
                    //zhiFuBaoYanZhen();//
                    syncLocalData();
                    t.show(WXPayEntryActivity.this, "由于网络原因,请您重新进入支付界面完成您的支付验证。", 3000);
                    return;
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                isZfbSend = true;
                dialogyz.dismiss();
                syncLocalData();
                t.show(WXPayEntryActivity.this, "由于网络原因,请您重新进入支付界面完成您的支付验证。", 3000);
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }


    //支付成功后同步本地数据
    private String tel_phone;

    public void syncLocalData() {
        dialogyz.show();
        dialogyz.setCancelable(false);
        if (!util.isNetworkConnected(this)) {
            dialog.dismiss();
            t.show(this, "网络没有连接,请检查您的网络", 1000);
            return;
        }
        stopVpnConnection();
        LoginBean info = new LoginBean();
        //手机铭文数据
        final String nickname = new String(Nick.getName());//昵称
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        final String username = util.getAndroidId(this);//用户名
        String iMeilLastId = username.substring(username.length() - 1);//lastid
        PhoneInfo phoneInfo = new PhoneInfo(WXPayEntryActivity.this);
        phoneInfo.getProvidersName();
        imsi = phoneInfo.getIMSI();//
        if (TextUtils.isEmpty(imsi)) {
            imsi = "1234567890";//手机卡号
        }
        final String email = "18376542390@163.com";//邮箱
        String mobieBrand = android.os.Build.BRAND;//手机品牌
        if (TextUtils.isEmpty(mobieBrand)) {
            mobieBrand = "mobieBrand is null";
        }
        String mobileModel = android.os.Build.MODEL; // 手机型号
        if (TextUtils.isEmpty(mobileModel)) {
            mobileModel = "mobileModel is null";
        }
        tel_phone = tm.getLine1Number();//手机号码
        if (TextUtils.isEmpty(tel_phone)) {
            tel_phone = "tel_phone is null";
        }
        String tele_supo = util.getTele_Supo(imsi, this);//运营商
        if (TextUtils.isEmpty(tele_supo)) {
            tele_supo = "telesupo is null";
        }
        String area = util.getAppMetaData(this, "UMENG_CHANNEL");//暂时测试渠道号
        if (TextUtils.isEmpty(area)) {
            area = "area is null";
        }
        password = aesUtils.decrypt(util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.PASS_DATA, "pass"));
        show = password;
        info.setUserName(username);
        info.setPassWord(util.getMD5Str(password));
        info.setNickName(URLEncoder.encode(nickname));
        info.setEmail(email);
        info.setImei(username);
        info.setImeiLastId(iMeilLastId);
        info.setImsi(imsi);
        info.setMobieBrand(mobieBrand);
        info.setMobileModel(mobileModel);
        info.setTel_phone(tel_phone);
        info.setTele_supo(tele_supo);
        info.setArea(area);

        final String json = JSON.toJSONString(info);
        String aesJson = aesUtils.encrypt(json);

        //发起请求
        RequestParams params = new RequestParams(Constant.LOGIN_INTERFACE);
        params.setCacheMaxAge(0);//最大数据缓存时间
        params.setConnectTimeout(15000);//连接超时时间
        params.setCharset("UTF-8");
        params.addQueryStringParameter("data", aesJson);
        stopVpnConnection();//断开vpn

        org.xutils.x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                String jsonStr = result.toString();
                String aesJson = aesUtils.decrypt(jsonStr);
                if (TextUtils.isEmpty(aesJson)) {
                    syncLocalData();
                    return;
                }
                try {
                    JSONObject jo = new JSONObject(aesJson);//拿到整体json
                    String loginStatus = jo.getString("respMsg");//登陆是否成功信息判断
                    sharedLogin info = new sharedLogin();//shared保存
                    if (loginStatus.equals("fail")) {
                        Log.i("payJson", "同步失败");
                        dialogyz.dismiss();
                        //getServerInterface("2");
                        stopVpnConnection();//断开vpn
                        util.sharedPreferencesDelByFileAllData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE);
                        return;
                    } else if (loginStatus.equals("success")) {
                        //登陆后处理
                        dialog_update_data = null;
                        Log.i("payJson", "同步成功");
                        JSONObject data = jo.getJSONObject("json");//成功后的信息
                        info.setId(data.getString("id"));
                        info.setVip_status(data.getString("vip_status"));
                        info.setUserName(data.getString("userName"));
                        info.setVip_lastTme(data.getString("vip_lastTime"));
                        info.setPay_count(data.getString("pay_count"));
                        info.setEmail(email);
                        info.setTel_phone(tel_phone);
                        //保存到shared
                        //保存数据之前清空旧的数据
                        util.sharedPreferencesDelByFileAllData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE);

                        //保存用户信息
                        util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "id", aesUtils.getInstance().encrypt(info.getId()));
                        util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "nickName", aesUtils.getInstance().encrypt(nickname));
                        util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status", aesUtils.getInstance().encrypt(info.getVip_status()));
                        util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "userName", aesUtils.getInstance().encrypt(info.getUserName()));
                        util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_lastTme", aesUtils.getInstance().encrypt(info.getVip_lastTme()));
                        util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "passWord", aesUtils.getInstance().encrypt(show));
                        util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "email", aesUtils.getInstance().encrypt(info.getEmail()));
                        util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "tel_phone", aesUtils.getInstance().encrypt(info.getTel_phone()));

                        //存储一个特殊字符
                        util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "show", aesUtils.getInstance().encrypt(show));

                        util.sharedPreferencesWriteData(WXPayEntryActivity.this, KeyFile.PASS_DATA, "pass", aesUtils.getInstance().encrypt(show));
                        dialogyz.dismiss();
                        if (Integer.parseInt(info.getPay_count()) >= 1) {
                            //隐藏38元支付
//                            sanbapay.setVisibility(View.GONE);
//                            sanbaline.setVisibility(View.GONE);
                            openSixPay();
                        }
                        //getUserDataInfo();
                        //getServerInterface(info.getVip_status());
                    } else {
                        //t.show(WXPayEntryActivity.this, "未知异常,程序员调试", 3000);
                        Log.i("payJson", "同步失败");
                        dialogyz.dismiss();
                        stopVpnConnection();//断开vpn
                        util.sharedPreferencesDelByFileAllData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE);
                    }
                } catch (JSONException e) {
                    //t.show(WXPayEntryActivity.this, "未知异常,程序员调试", 3000);
                    dialogyz.dismiss();
                    //getServerInterface("2");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("payJson", "同步失败");
                dialogyz.dismiss();
                t.show(WXPayEntryActivity.this, "连接超时,请您重新登陆验证您的会员身份", 3000);
                stopVpnConnection();//断开vpn
                util.sharedPreferencesDelByFileAllData(WXPayEntryActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE);
                //getServerInterface("2");
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }


    //友盟发送
    public void SendYouMeng() {
        //渠道号
        String area = util.getAppMetaData(this, "UMENG_CHANNEL");//暂时测试渠道号
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(area)) {
            return;
        }
        String Uusername = aesUtils.getInstance().decrypt(userName);
        YouMeng info = new YouMeng();
        info.setUserName(Uusername);
        info.setArea(area);
        info.setImeiLastId(Uusername.substring(Uusername.length() - 1));
        String jsonStr = JSON.toJSONString(info);
        String aesJson = aesUtils.encrypt(jsonStr);
        //发起请求
        RequestParams params = new RequestParams(Constant.YOU_MENG_AREA);
        params.setCacheMaxAge(0);//最大数据缓存时间
        params.setConnectTimeout(15000);//连接超时时间
        params.setCharset("UTF-8");
        params.addQueryStringParameter("data", aesJson);

        org.xutils.x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                String json = result.toString();
                String aesJson = aesUtils.decrypt(json);
                try {
                    JSONObject object = new JSONObject(aesJson);
                    String resultStr = object.getString("respMsg");
                    if (resultStr.equals("success")) {
                        Log.i("exception", "渠道成功");
                    } else {
                        Log.i("exception", "渠道失败");
                    }
                } catch (JSONException e) {
                    Log.i("exception", "渠道失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /***
     * 友盟统计
     */
    public void onResume() {
        super.onResume();
        try {
            //应对微信支付宝支付失败无法上报的情况
            payFailProcess();
            MobclickAgent.onPageStart("用户支付界面"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
            Intent intent = new Intent(this, OpenVPNService.class);
            intent.setAction(OpenVPNService.START_SERVICE);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
        }
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("用户支付界面"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }


    //应对微信支付宝支付失败无法上报的情况
    public void payFailProcess() {
        String wxAesJson = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.WX_USER_PAY_FAILED_FILE, pay_key);
        String zfbAesJson = util.sharedPreferencesReadData(WXPayEntryActivity.this, KeyFile.ZHI_FU_BAO_USER_PAY_FAILED_FILE, pay_key);

        String wxJson = aesUtils.decrypt(wxAesJson);
        String zfbJson = aesUtils.decrypt(zfbAesJson);
        if (TextUtils.isEmpty(wxJson) && TextUtils.isEmpty(zfbJson)) {
            //不需要支付失败的处理
            return;
        }
        if (!TextUtils.isEmpty(wxJson)) {
            //有微信失败记录
            weiXinYanZhen(wxJson);
        }
        if (!TextUtils.isEmpty(zfbJson)) {
            zhiFuBaoYanZhen(zfbJson);
        }
    }

    public void alerPayFailTiShi() {
        new android.support.v7.app.AlertDialog.Builder(this).setTitle("支付消息提示")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setCancelable(false)
                .setMessage("由于网络原因,验证您的支付状态失败,请您切换到应用的'海外导航'菜单界面,其他菜单亦可,再回到'会员'支付信息界面,系统会自动为您完成会员验证。")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁时候去绑定
        unbindService(mConnection);
    }

    //字体需要的设置
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    private android.support.v7.app.AlertDialog.Builder dialog_Max;
    private boolean isDownLoad = false;

    public void alertDownApk() {
        if (!isDownLoad) {
            if(MainActivity.apkInfo!=null){
                if (!util.appIsExist(this, MainActivity.apkInfo.getPackName())) {
                    //tv不存在
                    if (dialog_Max == null) {
                        dialog_Max = new android.support.v7.app.AlertDialog.Builder(this);
                        dialog_Max.setCancelable(false);
                        dialog_Max.setTitle("升级提示");
                        dialog_Max.setIcon(android.R.drawable.ic_dialog_info);
                        dialog_Max.setMessage("免费送您成人直播福利,尽快下载吧。");
                        dialog_Max.setPositiveButton("马上下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                apkDownLoad();
                            }
                        }).show();
                    } else {
                        dialog_Max.show();
                    }
                }
            }
        }
    }

    private String apkPath = "viptv.apk";

    public void apkDownLoad() {
        /***
         * apk文件下载
         */
        if (util.isNetworkConnected(this)) {
            //网络已连接
        } else {
            t.centershow(this, "世界上最远的距离就是没网", 500);
            return;
        }
        if (TextUtils.isEmpty(MainActivity.apkUrl)) {
            return;
        }
        File file = new File(Util.getSDCardPath() + "/" + apkPath);
        if (file.exists()) {
            Util.deleteFile(file);
        }
        stopVpnConnection();
        Request request = new Request.Builder().url(MainActivity.apkUrl).build();
        OkHttp.getInstance().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("h_bl", "文件下载失败");
                smHandler.sendEmptyMessage(GO_DOWN_TV_FAILED);
                isDownLoad = false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                stopVpnConnection();
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                String SDPath = Util.getSDCardPath();
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(SDPath, apkPath);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        stopVpnConnection();
                        isDownLoad = true;
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        Log.d("h_bl", "progress=" + progress);
                        Message msg = mHandler.obtainMessage();
                        msg.what = GO_DOWN_VPN_PROGRESS;
                        msg.arg1 = progress;
                        msg.arg2 = (int) total;
                        smHandler.sendMessage(msg);
                    }
                    fos.flush();
                    Log.d("h_bl", "文件下载成功");
                    smHandler.sendEmptyMessage(GO_DOWN_TV_SUCCES);
                    isDownLoad = false;
                } catch (Exception e) {
                    Log.d("h_bl", "文件下载失败");
                    isDownLoad = false;
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        Log.d("h_bl", "文件下载失败" + e.getMessage());
                    }
                }
            }
        });
    }

    ProgressDialog progressDialog;

    public void showProgressDialog(int current, int total) {
        //改变样式，水平样式的进度条可以显示出当前百分比进度
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("成人直播正在下载中，请稍候...");
        }
        //设置进度条最大值
        progressDialog.setProgress(current);
//      progressDialog.setMax(total / 1024 / 1024);
        progressDialog.setProgressNumberFormat(total / 1024 / 1024 + "MB");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


}
