package de.blinkt.openvpn.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.net.URLEncoder;

import de.blinkt.openvpn.Bean.SharedBean.sharedLogin;
import de.blinkt.openvpn.Bean.login.LoginBean;
import de.blinkt.openvpn.Constant.Constant;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.Save.KeyFile;
import de.blinkt.openvpn.Utils.AesUtils;
import de.blinkt.openvpn.Utils.Nick;
import de.blinkt.openvpn.Utils.PhoneInfo;
import de.blinkt.openvpn.Utils.T;
import de.blinkt.openvpn.Utils.Util;
import de.blinkt.openvpn.View.SystemBarTintManager;
import de.blinkt.openvpn.vpnmovies.vipvpn.wxapi.WXPayEntryActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends Activity {

    private Button login,back;
    private EditText et_username, et_userpass;
    private String nickname;
    private String username;
    private String password;
    private String iMeilLastId;
    private String imsi;
    private String email;
    private String mobieBrand;
    private String mobileModel;
    private String tel_phone;
    private String tele_supo;
    private String area;
    private String show;
    private Util util;
    private T t;
    private AesUtils aesUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatus();
        setContentView(R.layout.activity_login);
        initView();
        initData();
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

    private void initView() {
        login = (Button) findViewById(R.id.login);
        back = (Button) findViewById(R.id.back);
        et_username = (EditText) findViewById(R.id.et_username);
        et_userpass = (EditText) findViewById(R.id.et_userpass);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    public void close(View view){
        this.finish();
    }

    private void initData() {
        t = new T();
        aesUtils = new AesUtils();
        util = new Util(this);
        String usernameStr = util.sharedPreferencesReadData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "userName");
        String passWordStr = util.sharedPreferencesReadData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "passWord");
        String showStr = util.sharedPreferencesReadData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "show");

        if (TextUtils.isEmpty(showStr) || TextUtils.isEmpty(usernameStr) || TextUtils.isEmpty(passWordStr)) {
            //分配默认的昵称和密码
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            username = util.getAndroidId(this);//用户名
            et_username.setText(username);
            String twoPass = util.sharedPreferencesReadData(LoginActivity.this, KeyFile.PASS_DATA, "pass");
            String pa = aesUtils.decrypt(twoPass);
            if (TextUtils.isEmpty(pa)) {
                et_userpass.setText("123456");
            } else {
                et_userpass.setText(pa);
            }
            return;
        } else {
            et_username.setText(aesUtils.getInstance().decrypt(usernameStr));
            et_userpass.setText(aesUtils.getInstance().decrypt(passWordStr));
            return;
        }

    }

//    private ServiceConnection mConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName className,
//                                       IBinder service) {
//            // We've bound to LocalService, cast the IBinder and get LocalService instance
//            OpenVPNService.LocalBinder binder = (OpenVPNService.LocalBinder) service;
//            mService = binder.getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            mService = null;
//        }
//
//    };

    /***
     * 断开VPN连接
     */
//    protected OpenVPNService mService;
//
//    public void stopVpnConnection() {
//        ProfileManager.setConntectedVpnProfileDisconnected(LoginActivity.this);
//        if (mService != null && mService.getManagement() != null) {
//            mService.getManagement().stopVPN(false);
//        }
//    }

    /**
     * 登录逻辑处理
     *
     * @param view
     */
    public void login() {
        //已经登陆则显示注销登陆
//        if (isLogin) {
//            Dialog dialogZ = createLoadingDialog(LoginActivity.this, "正在退出登陆...");
//            dialogZ.show();
//            dialogZ.setCancelable(false);
//            stopVpnConnection();
//            util.sharedPreferencesDelByFileAllData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE);
//            dialogZ.dismiss();
//            t.show(LoginActivity.this, "用户退出了登陆", 1000);
//            finish();
//            return;
//        } else {
            //否则开始登陆
            if (isEmty()) {
                //登陆逻辑处理
                final Dialog dialog = createLoadingDialog(this, "登录中...请稍候");
                dialog.show();
                dialog.setCancelable(false);
                if (!util.isNetworkConnected(this)) {
                    dialog.dismiss();
                    t.show(this, "网络没有连接,请检查您的网络", 1000);
                    return;
                }
                LoginBean info = new LoginBean();
                //手机铭文数据
                nickname = new String(Nick.getName());//昵称
                TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
                username = util.getAndroidId(this);//用户名
                iMeilLastId = username.substring(username.length() - 1);//lastid
                PhoneInfo phoneInfo = new PhoneInfo(LoginActivity.this);
                phoneInfo.getProvidersName();
                imsi = phoneInfo.getIMSI();//
                if (TextUtils.isEmpty(imsi)) {
                    imsi = "1234567890";//手机卡号
                }
                email = "18376542390@163.com";//邮箱
                mobieBrand = android.os.Build.BRAND;//手机品牌
                if (TextUtils.isEmpty(mobieBrand)) {
                    mobieBrand = "mobieBrand is null";
                }
                mobileModel = android.os.Build.MODEL; // 手机型号
                if (TextUtils.isEmpty(mobileModel)) {
                    mobileModel = "mobileModel is null";
                }
                tel_phone = tm.getLine1Number();//手机号码
                if (TextUtils.isEmpty(tel_phone)) {
                    tel_phone = "tel_phone is null";
                }
                tele_supo = util.getTele_Supo(imsi, this);//运营商
                if (TextUtils.isEmpty(tele_supo)) {
                    tele_supo = "telesupo is null";
                }
                area = util.getAppMetaData(this, "UMENG_CHANNEL");//暂时测试渠道号
                if (TextUtils.isEmpty(area)) {
                    area = "area is null";
                }
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
                params.setConnectTimeout(10000);//连接超时时间
                params.setCharset("UTF-8");
                params.addQueryStringParameter("data", aesJson);

                x.http().post(params, new Callback.CommonCallback<String>() {

                    @Override
                    public void onSuccess(String result) {
                        Log.i("indexStr", "手动登陆发送成功");
                        String jsonStr = result.toString();
                        String aesJson = aesUtils.decrypt(jsonStr);
                        try {
                            if (TextUtils.isEmpty(aesJson)) {
                                Log.i("indexStr", "手动登陆发送成功后数据返回为空");
                                dialog.dismiss();
                                //getServerInterface("2");
                                return;
                            }
                            Log.i("aesStr", aesJson);
                            JSONObject jo = new JSONObject(aesJson);//拿到整体json
                            String loginStatus = jo.getString("respMsg");//登陆是否成功信息判断
                            sharedLogin info = new sharedLogin();//shared保存
                            if (loginStatus.equals("fail")) {
                                t.centershow(LoginActivity.this, "登录失败!请核实您的账户信息或到会员菜单修改您的密码", 3000);
                                util.sharedPreferencesDelByFileAllData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE);
                                dialog.dismiss();
                                //getServerInterface("2");
                                return;
                            } else if (loginStatus.equals("success")) {
                                //登陆后处理
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
                                util.sharedPreferencesDelByFileAllData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE);

                                //保存用户信息
                                util.sharedPreferencesWriteData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "id", aesUtils.getInstance().encrypt(info.getId()));
                                util.sharedPreferencesWriteData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "nickName", aesUtils.getInstance().encrypt(nickname));
                                util.sharedPreferencesWriteData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status", aesUtils.getInstance().encrypt(info.getVip_status()));
                                util.sharedPreferencesWriteData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "userName", aesUtils.getInstance().encrypt(info.getUserName()));
                                util.sharedPreferencesWriteData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_lastTme", aesUtils.getInstance().encrypt(info.getVip_lastTme()));
                                util.sharedPreferencesWriteData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "passWord", aesUtils.getInstance().encrypt(show));
                                util.sharedPreferencesWriteData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "email", aesUtils.getInstance().encrypt(info.getEmail()));
                                util.sharedPreferencesWriteData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "tel_phone", aesUtils.getInstance().encrypt(info.getTel_phone()));

                                //存储一个特殊字符
                                util.sharedPreferencesWriteData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "show", aesUtils.getInstance().encrypt(show));

                                util.sharedPreferencesWriteData(LoginActivity.this, KeyFile.PASS_DATA, "pass", aesUtils.getInstance().encrypt(show));

                                //isLogin = true;//已经登陆
                                t.centershow(LoginActivity.this, "登录成功", 2000);
                                dialog.dismiss();
                                if (Integer.parseInt(info.getPay_count()) >= 1) {
                                    //隐藏38元支付
                                    Constant.isShowThreeMonthPay = true;
                                }
                                String intenStr = getIntent().getStringExtra("qud");
                                //getServerInterface(info.getVip_status());
                                if (TextUtils.isEmpty(intenStr)) {//标识没有接受到字符串
                                    Intent intent = new Intent(LoginActivity.this, WXPayEntryActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {//接收到了支付界面穿过来的要登陆的字符串,为了刷新支付界面数据
                                    Intent intent = new Intent(LoginActivity.this, WXPayEntryActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                util.sharedPreferencesDelByFileAllData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE);
                                t.show(LoginActivity.this, "系统繁忙,请稍候再试", 3000);
                                dialog.dismiss();
                                //getServerInterface("2");
                            }
                        } catch (JSONException e) {
                            util.sharedPreferencesDelByFileAllData(LoginActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE);
                            t.show(LoginActivity.this, "系统繁忙,请稍候再试", 3000);
                            dialog.dismiss();
                            //getServerInterface("2");
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        dialog.dismiss();
                        Log.i("indexStr", "手动登陆发送失败");
                        t.show(LoginActivity.this, "请重新登录", 500);
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
    }

    /***
     * 获取服务器数据接口
     *
     * @return
     */
//    public void getServerInterface(String vipstatus) {
//        if (!util.isNetworkConnected(this)) {
//            t.show(this, "网络没有连接,请检查您的网络", 1000);
//            //mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//            return;
//        }
//        RequestParams params = new RequestParams(Constant.SREVER_CONFIG_INTERFACE);
//        Vip vip = new Vip();
//        if (vipstatus.equals("1")) {
//            //是会员
//            vip.setVipState("true");
//        } else if (vipstatus.equals("2")) {
//            //不是会员
//            vip.setVipState("false");
//        }
//        String json = JSON.toJSONString(vip);
//        String aesJson = aesUtils.encrypt(json);
//        params.setCacheMaxAge(1000);//最大数据缓存时间
//        params.setConnectTimeout(8000);//连接超时时间
//        params.setCharset("UTF-8");
//        params.addQueryStringParameter("data", aesJson);
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                try {
//                    String json = result.toString();
//                    if (TextUtils.isEmpty(json)) {
//                        //mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//                        return;
//                    }
//                    String aesJson = aesUtils.decrypt(json);
//                    JSONObject object = new JSONObject(aesJson);
//                    String status = object.getString("respMsg");
//                    if (status.equals("success")) {
//                        String information_url = object.getString("information_url");
//                        if (TextUtils.isEmpty(information_url)) {
//                            //接口为空
//                            //mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//                        } else {
//                            if (Constant.USER_MESSAGE.equals(information_url)) {
//                                //之前的接口和请求下来的接口一样
//                                Constant.USER_MESSAGE = information_url;
//                            } else {
//                                Constant.USER_MESSAGE = information_url;
//                                util.sharedPreferencesWriteData(LoginActivity.this, KeyFile.AUTO_REFRESH_DATA, "autorefreshdata", "autorefreshdata");
//                            }
//                            //mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//                        }
//
//                    } else {
//                        //请求后返回的数据失败
//                        //mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//                    }
//
//                } catch (Exception e) {
//                    //mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                //mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//            }
//
//            @Override
//            public void onFinished() {
//            }
//        });
//    }


    /**
     * 对登陆非空判断
     */
    public boolean isEmty() {
        boolean flag = false;
        username = et_username.getText().toString();
        password = et_userpass.getText().toString();
        if (TextUtils.isEmpty(username)) {
            flag = false;
            t.show(this, "请输入用户名", 1000);
        }
        if (TextUtils.isEmpty(password)) {
            flag = false;
            t.show(this, "请输入密码", 1000);
        }
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            flag = true;
        }
        return flag;
    }

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
        return loadingDialog;
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(LoginActivity.this, WXPayEntryActivity.class);
        startActivity(intent);
        finish();
    }

    /***
     * 友盟统计
     */
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("登录页面"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("登录页面"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    //字体需要的设置
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
