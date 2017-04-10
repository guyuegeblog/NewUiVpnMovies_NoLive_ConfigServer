package de.blinkt.openvpn.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.blinkt.openvpn.Bean.SharedBean.sharedLogin;
import de.blinkt.openvpn.Bean.login.LoginBean;
import de.blinkt.openvpn.Constant.Constant;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.Save.KeyFile;
import de.blinkt.openvpn.Save.KeyUser;
import de.blinkt.openvpn.Tool.ParamsPutterTool;
import de.blinkt.openvpn.Utils.AesUtils;
import de.blinkt.openvpn.Utils.CheckPermissionUtil;
import de.blinkt.openvpn.Utils.Nick;
import de.blinkt.openvpn.Utils.PhoneInfo;
import de.blinkt.openvpn.Utils.T;
import de.blinkt.openvpn.Utils.Util;

/**
 * @{# SplashActivity.java Create on 2013-5-2 下午9:10:01
 * <p/>
 * class desc: 启动画面 (1)判断是否是首次加载应用--采取读取SharedPreferences的方法
 * (2)是，则进入GuideActivity；否，则进入MainActivity (3)3s后执行(2)操作
 * <p/>
 * <p>
 * Copyright: Copyright(c) 2013
 * </p>
 * @Version 1.0
 * @Author <a href="mailto:gaolei_xj@163.com">Leo</a>
 */


public class SplashActivity extends AppCompatActivity {
    boolean isFirstIn = false;

    private final int GO_HOME = 1000;
    private final int GO_GUIDE = 1001;
    // 延迟3秒
    private final long SPLASH_DELAY_MILLIS = 3000;

    private final String SHAREDPREFERENCES_NAME = "first_pref";

    /**
     * Handler:跳转到不同界面
     */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private Animation mFadeIn;
    private Animation mFadeInScale;
    private Animation mFadeOut;
    private ImageView splash_imageview;
    private Util util = new Util(this);
    private T t;
    private AesUtils aesUtils;
    private TextView appVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        initView();
        startData();
    }


    private void startData() {
        initData();
        init();
        setListener();
        MobclickAgent.setDebugMode(true);//集成模式
        MobclickAgent.openActivityDurationTrack(false);//禁止默认的页面统计方式，
        int three_total = TextUtils.isEmpty(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_THREE_TOTAL,
                KeyUser.THREE_TOTAL)) ? 1 : Integer.parseInt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_THREE_TOTAL,
                KeyUser.THREE_TOTAL)) + 1;
        ParamsPutterTool.sharedPreferencesWriteData(this, KeyFile.SAVE_THREE_TOTAL, KeyUser.THREE_TOTAL, String.valueOf(three_total));
    }


    private void initView() {
        Util.createFileDir(Constant.send_Vpn_Directory);
        Util.createFile(Constant.send_Vpn);
        splash_imageview = (ImageView) findViewById(R.id.splash_imageview);
        appVersion = (TextView) findViewById(R.id.appVersion);
        appVersion.setText("专业版" + getAPPVersionCode());
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy--MM--dd HH:mm:ss");

    private void initData() {
        t = new T();
        aesUtils = new AesUtils();
        //存储tv时间文件
        Util.writeFileToSDFile(Constant.send_Vpn, Util.simpleDateFormat.format(new Date()));
        String path = Util.getSDCardPath() + "/" + Constant.VPN_FIRST_REGISTER_FILE;
        File file = new File(path);
        if (!file.exists()) {
            Util.createFile(path);
            Util.writeFileToSDFile(path, simpleDateFormat.format(new Date()));
        }
    }

    private void init() {
        initAnim();
        //splash_imageview.startAnimation(mFadeIn);

        // 读取SharedPreferences中需要的数据
        // 使用SharedPreferences来记录程序的使用次数
        SharedPreferences preferences = getSharedPreferences(
                SHAREDPREFERENCES_NAME, MODE_PRIVATE);

        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        isFirstIn = preferences.getBoolean("VpnIsFirstIn", true);
        refreshLocalData();
        // 判断程序与第几次运行，如果是第一次运行则跳转到引导界面，否则跳转到主界面
//        if (!isFirstIn) {
//            // 使用Handler的postDelayed方法，3秒后执行跳转到MainActivity
//            //刷新本地数据
//            refreshLocalData();
//            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//        } else {
//            refreshLocalData();
//            mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
//        }
    }

    private void initAnim() {
        mFadeIn = AnimationUtils.loadAnimation(this,
                R.anim.guide_welcome_fade_in);
        mFadeInScale = AnimationUtils.loadAnimation(this,
                R.anim.guide_welcome_fade_in_scale);
        mFadeOut = AnimationUtils.loadAnimation(this,
                R.anim.guide_welcome_fade_out);
    }

    private void setListener() {
        mFadeIn.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                //splash_imageview.startAnimation(mFadeInScale);
            }
        });
        mFadeInScale.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                // splash_imageview.startAnimation(mFadeOut);
            }
        });
        mFadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                //splash_imageview.startAnimation(mFadeIn);
            }
        });
    }

    private void goHome() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("intentJson", intentJson);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    private void goGuide() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("intentJson", intentJson);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    private String nickname;
    private String username;
    private String password;
    private String imsi;
    private String iMeilLastId;
    private String email;
    private String mobieBrand;
    private String mobileModel;
    private String tel_phone;
    private String tele_supo;
    private String area;
    private String show;
    private int failIjk = 0;//失败次数

    private void refreshLocalData() {
        String firstReg = util.sharedPreferencesReadData(this, KeyFile.USER_FIRST_RGISTER, KeyFile.USER_FIRST_RGISTER);
        if (TextUtils.isEmpty(firstReg)) {
            Constant.isFirstRegister = true;
            util.sharedPreferencesWriteData(this, KeyFile.USER_FIRST_RGISTER, KeyFile.USER_FIRST_RGISTER, KeyFile.USER_FIRST_RGISTER);
        }

        if (!util.isNetworkConnected(this)) {
            t.show(this, "网络没有连接,请检查您的网络", 1000);
            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
            return;
        }
        //自动登录
        //本地获取数据如果不空,则直接登陆,如果空,则直接获取手机数据
        final String aesShow = util.sharedPreferencesReadData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "show");
        show = aesUtils.getInstance().decrypt(aesShow);
        nickname = aesUtils.getInstance().decrypt(util.sharedPreferencesReadData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "nickName"));
        username = aesUtils.getInstance().decrypt(util.sharedPreferencesReadData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "userName"));
        password = aesUtils.getInstance().decrypt(util.sharedPreferencesReadData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "passWord"));
        imsi = aesUtils.getInstance().decrypt(util.sharedPreferencesReadData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "imsi"));
        email = aesUtils.getInstance().decrypt(util.sharedPreferencesReadData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "email"));
        mobieBrand = aesUtils.getInstance().decrypt(util.sharedPreferencesReadData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "mobieBrand"));
        mobileModel = aesUtils.getInstance().decrypt(util.sharedPreferencesReadData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "mobileModel"));
        tel_phone = aesUtils.getInstance().decrypt(util.sharedPreferencesReadData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "tel_phone"));
        tele_supo = aesUtils.getInstance().decrypt(util.sharedPreferencesReadData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "tele_supo"));
        area = aesUtils.getInstance().decrypt(util.sharedPreferencesReadData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "area"));

        //取加密或手机直接数据
        LoginBean info = new LoginBean();
        if (TextUtils.isEmpty(username)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(imsi)
                || TextUtils.isEmpty(show)) {
            //手机铭文数据
            nickname = new String(Nick.getName());//昵称
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            username = util.getAndroidId(this);//用户名
            iMeilLastId = username.substring(username.length() - 1);//lastid
            PhoneInfo phoneInfo = new PhoneInfo(SplashActivity.this);
            phoneInfo.getProvidersName();
            imsi = phoneInfo.getIMSI();//
            if (TextUtils.isEmpty(imsi)) {
                imsi = "1234567890";//手机卡号
            }
            String passWordStr = util.sharedPreferencesReadData(SplashActivity.this, KeyFile.PASS_DATA, "pass");
            if (TextUtils.isEmpty(passWordStr)) {
                password = "123456";
                show = "123456";//默认密码
            } else {
                password = aesUtils.decrypt(passWordStr);
                show = password;
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
            area = getAreaName();//area

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
        } else {
            //本地加密数据
            iMeilLastId = username.trim().substring(username.length() - 1);
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
        }
        final String json = JSON.toJSONString(info);
        String aesJson = aesUtils.encrypt(json);//对json加密
        //发起请求
        RequestParams params = new RequestParams(Constant.LOGIN_INTERFACE);
        params.setCacheMaxAge(0);//最大数据缓存时间
        params.setConnectTimeout(5000);//连接超时时间
        params.setCharset("UTF-8");
        params.addQueryStringParameter("data", aesJson);
        //异步线程获取数据
        getNetData();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String jsonStr = result.toString();
                String aesJson = aesUtils.decrypt(jsonStr);
                if (TextUtils.isEmpty(aesJson)) {
                    failIjk++;
                    if (failIjk <= 0) {
                        refreshLocalData();
                    } else {
                        //mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
                    }
                    return;
                }
                try {
                    JSONObject jo = new JSONObject(aesJson);//拿到整体json
                    String loginStatus = jo.getString("respMsg");//登陆是否成功信息判断
                    sharedLogin info = new sharedLogin();//shared保存
                    if (loginStatus.equals("fail")) {
                        refreshLocalData();
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
                        util.sharedPreferencesDelByFileAllData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE);

                        //保存用户信息
                        util.sharedPreferencesWriteData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "id", aesUtils.getInstance().encrypt(info.getId()));
                        util.sharedPreferencesWriteData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "nickName", aesUtils.getInstance().encrypt(nickname));
                        util.sharedPreferencesWriteData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status", aesUtils.getInstance().encrypt(info.getVip_status()));
                        util.sharedPreferencesWriteData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "userName", aesUtils.getInstance().encrypt(info.getUserName()));
                        util.sharedPreferencesWriteData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_lastTme", aesUtils.getInstance().encrypt(info.getVip_lastTme()));
                        util.sharedPreferencesWriteData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "passWord", aesUtils.getInstance().encrypt(show));
                        util.sharedPreferencesWriteData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "email", aesUtils.getInstance().encrypt(info.getEmail()));
                        util.sharedPreferencesWriteData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "tel_phone", aesUtils.getInstance().encrypt(info.getTel_phone()));

                        //存储一个特殊字符
                        util.sharedPreferencesWriteData(SplashActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "show", aesUtils.getInstance().encrypt(show));

                        util.sharedPreferencesWriteData(SplashActivity.this, KeyFile.PASS_DATA, "pass", aesUtils.getInstance().encrypt(show));

                        failIjk = 0;
                        if (Integer.parseInt(info.getPay_count()) >= 1) {
                            //隐藏38元支付
                            Constant.isShowThreeMonthPay = true;
                        }
                        //mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
                        //获取服务器给用户分配的数据接口
                    } else {
                        refreshLocalData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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

//    /***
//     * 获取服务器数据接口
//     *
//     * @return
//     */
//    public void getServerInterface(String vipstatus) {
//        if (!util.isNetworkConnected(this)) {
//            t.show(this, "网络没有连接,请检查您的网络", 1000);
//            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//            return;
//        }
//        getNetData();
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
//        params.setConnectTimeout(7000);//连接超时时间
//        params.setCharset("UTF-8");
//        params.addQueryStringParameter("data", aesJson);
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                try {
//                    String json = result.toString();
//                    if (TextUtils.isEmpty(json)) {
//                        mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//                        return;
//                    }
//                    String aesJson = aesUtils.decrypt(json);
//                    JSONObject object = new JSONObject(aesJson);
//                    String status = object.getString("respMsg");
//                    if (status.equals("success")) {
//                        String information_url = object.getString("information_url");
//                        if (TextUtils.isEmpty(information_url)) {
//                            //接口为空
//                            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//                        } else {
//                            //Constant.USER_MESSAGE = information_url;
//                            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//                        }
//
//                    } else {
//                        //请求后返回的数据失败
//                        mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//                    }
//
//                } catch (Exception e) {
//                    mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
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


    private String intentJson = "";

    public void getNetData() {
        if (!util.isNetworkConnected(this)) {
            //网络已连接
            mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
            return;
        }
        //发起请求
        RequestParams params = new RequestParams(Constant.USER_MESSAGE);
        params.setCacheMaxAge(0);//最大数据缓存时间
        params.setConnectTimeout(5000);//连接超时时间
        params.setCharset("UTF-8");

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    String json = result.toString();
                    String aesJson = aesUtils.decrypt(json);
                    intentJson = aesJson;
                    mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
                } catch (Exception e) {
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                intentJson = de.blinkt.openvpn.Tool.Util.getFromRaw(SplashActivity.this);
                mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });

    }


    //获取友盟渠道
    public String getAreaName() {
        //渠道号
        String area = util.getAppMetaData(this, "UMENG_CHANNEL");//暂时测试渠道号
        if (TextUtils.isEmpty(area)) {
            return "area is null";
        }
        return area;
    }

    /***
     * 友盟统计
     */
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("启动页面"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("启动页面"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public static double getSDKVersion() {
        double sdkVersion;
        try {
            String str = (Build.VERSION.RELEASE).substring(0, 1);
            sdkVersion = Double.parseDouble(str);
        } catch (NumberFormatException e) {
            sdkVersion = 0;
        }
        return sdkVersion;
    }

    public String getAPPVersionCode() {
        int currentVersionCode = 0;
        String appVersionName = null;
        PackageManager manager = this.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            appVersionName = info.versionName; // 版本名
            currentVersionCode = info.versionCode; // 版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersionName;
    }

}
