package de.blinkt.openvpn.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.provider.OpenableColumns;
import android.security.KeyChain;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.github.ybq.android.spinkit.SpinKitView;
import com.nineoldandroids.animation.Animator;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.CirclePageIndicator;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;

import net.frakbot.jumpingbeans.JumpingBeans;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.blinkt.openvpn.Adapter.MessageAdapter;
import de.blinkt.openvpn.CircleLoading.CircularBarPager;
import de.blinkt.openvpn.CircleLoading.DemoPagerAdapter;
import de.blinkt.openvpn.CircleLoading.DemoView;
import de.blinkt.openvpn.Constant.Constant;
import de.blinkt.openvpn.Entity.ApkList;
import de.blinkt.openvpn.Entity.ConfigPara;
import de.blinkt.openvpn.Entity.DownLoadArea;
import de.blinkt.openvpn.Entity.DownUpdate;
import de.blinkt.openvpn.Entity.Hot;
import de.blinkt.openvpn.Entity.ListHot;
import de.blinkt.openvpn.Entity.MessageInfo;
import de.blinkt.openvpn.Entity.MessageList;
import de.blinkt.openvpn.Entity.Pager;
import de.blinkt.openvpn.Entity.PagerInfo;
import de.blinkt.openvpn.Entity.PriceList;
import de.blinkt.openvpn.Entity.UpdateList;
import de.blinkt.openvpn.Entity.UserMessage;
import de.blinkt.openvpn.Entity.VideoListData;
import de.blinkt.openvpn.Entity.VpnConfig;
import de.blinkt.openvpn.Entity.VpnList;
import de.blinkt.openvpn.Entity.ApkInfo;
import de.blinkt.openvpn.Entity.navigationList;
import de.blinkt.openvpn.Interface.hotInterface;
import de.blinkt.openvpn.Interface.messageInterface;
import de.blinkt.openvpn.Interface.pagerInterface;
import de.blinkt.openvpn.LaunchVPN;
import de.blinkt.openvpn.Net.AppTool;
import de.blinkt.openvpn.Net.MobClick;
import de.blinkt.openvpn.Net.OkHttp;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.Save.KeyFile;
import de.blinkt.openvpn.Save.KeyUser;
import de.blinkt.openvpn.Service.DialogService;
import de.blinkt.openvpn.Tool.ParamsPutterTool;
import de.blinkt.openvpn.Tool.VipTool;
import de.blinkt.openvpn.Utils.AesUtils;
import de.blinkt.openvpn.Utils.GlideImageLoader;
import de.blinkt.openvpn.Utils.T;
import de.blinkt.openvpn.Utils.Util;
import de.blinkt.openvpn.View.SelfListView;
import de.blinkt.openvpn.View.SystemBarTintManager;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;
import de.blinkt.openvpn.fragments.Utils;
import de.blinkt.openvpn.vpnmovies.vipvpn.wxapi.WXPayEntryActivity;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements VpnStatus.StateListener, messageInterface, pagerInterface, hotInterface {

    private TextView tv_conn, index, navigation, uservip;
    private T t = new T();
    private String mProfilename;
    public static VpnProfile mResult;
    private String mAliasName = null;
    private static final int RESULT_INSTALLPKCS12 = 7;
    private String mEmbeddedPwFile;
    private transient List<String> mPathsegments;
    protected OpenVPNService mService;
    public static boolean vpnIsConn = false;
    private RelativeLayout blackback;

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

    private RecyclerView recyclerview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tv_timer;
    private recyclerViewAdapter adapter = new recyclerViewAdapter();
    private Map<Integer, Object> allDataMap = new HashMap<>();
    private MessageAdapter messageAdapter;
    private RelativeLayout progressbar;
    private Util util = new Util(this);
    private AesUtils aesUtils = new AesUtils();
    private SpinKitView skit1;
    private String configFilePath = Constant.VPN_CONFIG_DOWNLOAD_DIRECTORY_ONE_PATH + Constant.VPN_CONFIG_DOWNLOAD_DIRECTORY_TWO_PATH + Constant.VPN_CONFIG_DOWNLOAD_DIRECTORY_THREE_PATH + Constant.VPN_CONFIG_DOWNLOAD_FILE_PATH;
    private String VPN_SHIYONG_FILE = Constant.VPN_SHIYONG_DIRECTORY + "/" + Constant.VPN_SHIYONG_FILE;
    public static boolean userClickInVpn = false;
    static int second = 30;
    //    Timer timer;
//    TimerTask timerTask;
    boolean isBind = false;
    private hotInterface hotInterface;
    private Handler startHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 555:
                    getApkUpdate();
                    break;
                case 5000:
//                    this.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (dialog2 == null) {
//                                open(indexHtmlStr);
//                            } else {
//                                if (!dialog2.isShowing()) {
//                                    open(indexHtmlStr);
//                                }
//                            }
//                        }
//                    }, 60000);
                    break;
                case 1234:
                    alertTiShis();
                    break;
                case 12345:
                    if (netDialog != null) {
                        netDialog.hide();
                    }
                    break;
                case 3453:
                    this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog3 != null && dialog3.isShowing()) {
                                dialog3.dismiss();
                            }
                        }
                    }, 2000);
//                case 855:
//                    int progress = msg.getData().getInt("x5downprogress");
//                    String initStr = Util.readFileToSDFile(Constant.FIRST_INSTALL_FINISH);
//                    if (TextUtils.isEmpty(initStr)) {
//                        showX5ProgressDialog(progress, 100, progress / 100 + "");
//                    }
//                    break;
//                case 856:
//                    int installprogress = msg.getData().getInt("x5installprogress");
//                    showX5InstallProgressDialog(installprogress, 200, installprogress / 200 + "");
//                    break;
            }
        }
    };
    //    Handler timerHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            if (second <= 0) {
//                //time out
//                tv_conn.setText("连接中...请稍候");
//                second = 30;
//                if (progressbar != null) {
//                    progressbar.setVisibility(View.GONE);
//                }
//                if (timer != null) {
//                    timer.cancel();
//                    timer = null;
//                }
//                if (timerTask != null) {
//                    timerTask.cancel();
//                    timerTask = null;
//                }
//            } else {
//                second--;
//                tv_conn.setText("连接中...倒计时" + second);
//            }
//        }
//    };
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
        }
    };

    private final int GO_DOWN_VPN_FAILED = 1056;
    private final int GO_DOWN_VPN_SUCCES = 1089;
    private final int GO_DOWN_VPN_PROGRESS = 1058;
    private final int GO_DOWN_TV_FAILED = 1066;
    private final int GO_DOWN_TV_SUCCES = 1099;
    private final int GO_VPN_TRY_OUT_TIME_START = 1198;
    private final int GO_VPN_TRY_OUT_TIME_END = 1197;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_DOWN_VPN_FAILED:
                    MobclickAgent.onEvent(MainActivity.this, MobClick.DOWNLOAD_UPDATE_FAILED);//埋点统计
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    apkDataDownLoad();
                    break;
                case GO_DOWN_VPN_SUCCES:
                    MobclickAgent.onEvent(MainActivity.this, MobClick.DOWNLOAD_UPDATE);//埋点统计
                    Log.d("h_bl", "文件下载安装");
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    try {
                        AppTool.installApk(MainActivity.this, Util.getSDCardPath() + "/" + apkDataPath);
                    } catch (Exception e) {
                        return;
                    }
                    break;
                case GO_DOWN_TV_FAILED:
                    MobclickAgent.onEvent(MainActivity.this, MobClick.DOWNLOAD_TV_FAILED);//埋点统计
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    apkDownLoad();
                    break;
                case GO_DOWN_TV_SUCCES:
                    MobclickAgent.onEvent(MainActivity.this, MobClick.DOWNLOAD_TV);//埋点统计
                    Log.d("h_bl", "文件下载安装");
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    try {
                        AppTool.installApk(MainActivity.this, Util.getSDCardPath() + "/" + apkPath);
                    } catch (Exception e) {
                        return;
                    }
                    break;
                case GO_DOWN_VPN_PROGRESS:
                    if (dialog3 != null) {
                        dialog3.dismiss();
                    }
                    showProgressDialog(msg.arg1, msg.arg2);
                    break;
                case GO_VPN_TRY_OUT_TIME_START:
                    //启动VPN试用
                    tv_timer.setText("试 用 倒 计 时 " + formatLongToTimeStr(timeSecond));
                    break;
                case GO_VPN_TRY_OUT_TIME_END:
                    //试用结束
                    destroyTimer();
                    tv_timer.setText("您 的 试 用 期 已 过 期,请 立 即 充 值!");
                    stopVpnConnection();
                    alertPay();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatus();
        setContentView(R.layout.activity_main);
        initView();
        initData();
        isBind = bindService(new Intent(MainActivity.this, DialogService.class), conn, Context.BIND_AUTO_CREATE);
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
//    public String init = null;

//    public void initWebBrowser() {
//        if (TextUtils.isEmpty(init)) {
//            init = "init";
//            if (Constant.mobileIsHaveQQAndWx) {
//                Log.i("qbStr", "===============执行分享");
//                downLoadX5();
//            } else {
//                Log.i("qbStr", "===============执行下载");
//                downLoadX5();
//            }
//        }
//    }


//    /***
//     * 初始化内核
//     */
//    public void downLoadX5() {
//        //web
//        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
//        //TbsDownloader.needDownload(this, false);
//        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
//            @Override
//            public void onViewInitFinished(boolean arg0) {
//                // TODO Auto-generated method stub
//                //Log.e("0828", " onViewInitFinished is " + arg0);
//                Log.i("qbStr", "x5内核视图初始化完成boolean arg0===" + arg0);
//                Constant.initWeb = arg0;
//                if (x5dialog != null) {
//                    x5dialog.dismiss();
//                }
//                if (x5InstallDialog != null) {
//                    x5InstallDialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onCoreInitFinished() {
//                // TODO Auto-generated method stub
//                Log.i("qbStr", "x5内核核心初始化完成");
//                File file = new File(Constant.FIRST_INSTALL_FINISH);
//                if (!file.exists()) {
//                    Util.createFile(Constant.FIRST_INSTALL_FINISH);
//                    Util.writeFileToSDFile(Constant.FIRST_INSTALL_FINISH, "ADSFFINISH");
//                }
//            }
//        };
//        QbSdk.setTbsListener(new TbsListener() {
//            @Override
//            public void onDownloadFinish(int i) {
//                Log.i("qbStr", "x5内核下载完成===" + i);
//                if (x5dialog != null) {
//                    x5dialog.dismiss();
//                }
//                util.sharedPreferencesWriteData(MainActivity.this, KeyFile.USER_FIRST_INIT, KeyFile.USER_FIRST_INIT, KeyFile.USER_FIRST_INIT);
//            }
//
//            @Override
//            public void onInstallFinish(int i) {
//                Log.i("qbStr", "x5内核安装完成===" + i);
//                if (i == 200) {
//                    if (x5dialog != null) {
//                        x5dialog.dismiss();
//                    }
//                    if (x5InstallDialog != null) {
//                        x5InstallDialog.dismiss();
//                    }
//                    File file = new File(Constant.FIRST_INSTALL_FINISH);
//                    if (!file.exists()) {
//                        Util.createFile(Constant.FIRST_INSTALL_FINISH);
//                        Util.writeFileToSDFile(Constant.FIRST_INSTALL_FINISH, "ADSFFINISH");
//                    }
//                    t.centershow(MainActivity.this, "播放器安装完成，您可以点击网站使用了", 3000);
//                } else if (i < 200) {
//                    Message msg = new Message();
//                    msg.what = 856;
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("x5installprogress", i);
//                    msg.setData(bundle);
//                    startHandler.sendMessage(msg);
//                }
//            }
//
//            @Override
//            public void onDownloadProgress(int i) {
//                Log.i("qbStr", "x5内核下载中===" + i);
//                if (i > 0 && i < 100) {
//                    stopVpnConnection();
//                }
//                Message msg = new Message();
//                msg.what = 855;
//                Bundle bundle = new Bundle();
//                bundle.putInt("x5downprogress", i);
//                msg.setData(bundle);
//                startHandler.sendMessage(msg);
//            }
//        });
//        QbSdk.initX5Environment(getApplicationContext(), cb);
//    }
//
//    ProgressDialog x5dialog;
//    private boolean isShowDown = true;
//
//    public void showX5ProgressDialog(int current, int total, String format) {
//        //改变样式，水平样式的进度条可以显示出当前百分比进度
//        if (x5dialog == null) {
//            x5dialog = new ProgressDialog(this);
//            x5dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            x5dialog.setTitle("视频播放器内核下载提示");
//            x5dialog.setMessage("app视频播放器内核正在下载中，请您耐心等待(请您在较好的网络环境下进行下载,您仍然可以在下载过程中点击网站播放视频，下载中不会影响您的使用)。下载完成后,app就能使用内置视频播放器,快速流畅的播放上万视频网站，给您更好的体验。（注意：没有下载完成时，app自动使用手机自带浏览器播放视频）");
//            x5dialog.setButton(DialogInterface.BUTTON_POSITIVE, "后台下载"
//                    , new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface d, int i) {
//                            isShowDown = false;
//                            x5dialog.dismiss();
//                        }
//                    });
//        }
//        //设置进度条最大值
//        x5dialog.setProgress(current);
//        x5dialog.setMax(total);
//        //x5dialog.setProgressNumberFormat(format);
//        if (current >= 100) {
//            x5dialog.dismiss();
//            util.sharedPreferencesWriteData(this, KeyFile.USER_FIRST_INIT, KeyFile.USER_FIRST_INIT, KeyFile.USER_FIRST_INIT);
//        } else {
//            if (isShowDown) {
//                x5dialog.show();
//            }
//        }
//    }
//
//    ProgressDialog x5InstallDialog;
//
//    public void showX5InstallProgressDialog(int current, int total, String format) {
//        //改变样式，水平样式的进度条可以显示出当前百分比进度
//        if (!Constant.mobileIsHaveQQAndWx) {
//            if (x5InstallDialog == null) {
//                x5InstallDialog = new ProgressDialog(this);
//                x5InstallDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                x5InstallDialog.setTitle("视频播放器内核下载提示");
//                x5InstallDialog.setMessage("内置视频播放器正在安装，请您耐心等待......");
//            }
//            //设置进度条最大值
//            x5InstallDialog.setProgress(current);
//            x5InstallDialog.setMax(total);
//            //x5dialog.setProgressNumberFormat(format);
//            x5InstallDialog.show();
//        }
//    }

    //    private void initTimer() {
//        if (timerTask == null) {
//            timerTask = new TimerTask() {
//                @Override
//                public void run() {
//                    Message msg = new Message();
//                    msg.what = 0;
//                    timerHandler.sendMessage(msg);
//                }
//            };
//            //启动倒计时
//            timer = new Timer();
//            timer.schedule(timerTask, 0, 1000);
//        }
//    }
    //弹窗
    private AlertDialog netDialog;

    //vip时间已过期
    private void alertTiShis() {
        netDialog = new AlertDialog.Builder(this).setTitle("连接不上外网,无法看片?")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage("连接缓慢?连接不上外网?看不上精彩大片?建议用户您重启手机或者在网络环境好的情况下进行连接。")
                .setPositiveButton("重新连接", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        dialog.dismiss();
                        second = 30;
                        //initTimer();
                        open(indexHtmlStr);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
//                        if (timerTask != null) {
//                            timerTask.cancel();
//                        }
                        progressbar.setVisibility(View.GONE);
                        dialog.dismiss();
                        second = 30;
                    }
                }).show();
    }

    private void initData() {
        File file = new File(Util.getSDCardPath() + "/" + "vpn.apk");
        if (file.exists()) {
            Util.deleteFile(file);
        }
        Util.createFileDir(Constant.VPN_CONFIG_DOWNLOAD_DIRECTORY_ONE_PATH);
        Util.createFileDir(Constant.VPN_CONFIG_DOWNLOAD_DIRECTORY_ONE_PATH + Constant.VPN_CONFIG_DOWNLOAD_DIRECTORY_TWO_PATH);
        Util.createFileDir(Constant.VPN_CONFIG_DOWNLOAD_DIRECTORY_ONE_PATH + Constant.VPN_CONFIG_DOWNLOAD_DIRECTORY_TWO_PATH + Constant.VPN_CONFIG_DOWNLOAD_DIRECTORY_THREE_PATH);
        Util.createFileDir(Constant.VPN_CONFIG_DOWNLOAD_DIRECTORY_ONE_PATH + Constant.VPN_CONFIG_DOWNLOAD_DIRECTORY_TWO_PATH + Constant.VPN_CONFIG_DOWNLOAD_DIRECTORY_THREE_PATH + Constant.VPN_CONFIG_DOWNLOAD_FILE_PATH);
        Util.createFileDir(Constant.VPN_SHIYONG_DIRECTORY);
        Util.createFileDir(Constant.USER_AUTO_DIRECTORY);
        Util.createFile(Constant.USER_AUTO_GUOQI);
        if (Constant.isFirstRegister) {
            alertMess();//显示提示弹窗
        }
        getNetCigAndVer();
        String intentJson = getIntent().getStringExtra("intentJson");
        if (TextUtils.isEmpty(intentJson)) {
            getNetData();
        } else {
            doData(intentJson);
        }
        if (!Constant.isFirstRegister) {
            getApkData();
        }
        getApkUpdate();
        getNavigationData();
        getTvData();
        util.getAllBrowserInfo(this);
        if (than_Three_Total(this)) {
            isThreeSplash = true;
            alert_Dilaog_Max();
        }
    }

    public static boolean than_Three_Total(Activity mContext) {
        boolean thanthree = false;
        if (!TextUtils.isEmpty(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_THREE_TOTAL,
                KeyUser.THREE_TOTAL))) {
            //改动成了4次启动app
            if (Integer.parseInt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_THREE_TOTAL, KeyUser.THREE_TOTAL)) >= 3) {
                thanthree = true;
            }
        }
        return thanthree;
    }

    private void initView() {
        progressbar = (RelativeLayout) findViewById(R.id.progressbar);
        progressbar.getBackground().setAlpha(120);
        skit1 = (SpinKitView) findViewById(R.id.skit1);
        blackback = (RelativeLayout) findViewById(R.id.blackback);
        blackback.setEnabled(false);
        blackback.getBackground().setAlpha(120);
        blackback = (RelativeLayout) findViewById(R.id.blackback);
        index = (TextView) findViewById(R.id.index);
        navigation = (TextView) findViewById(R.id.navigation);
        uservip = (TextView) findViewById(R.id.uservip);
        tv_conn = (TextView) findViewById(R.id.tv_conn);
        tv_timer = (TextView) findViewById(R.id.tv_timer);
        index.setBackgroundResource(R.mipmap.jiasu_select);

        navigation.setBackgroundResource(R.mipmap.daohang);

        uservip.setBackgroundResource(R.mipmap.huiyuan);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_index);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_blue_dark, android.R.color.holo_orange_dark, R.color.menuback);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressbar.setVisibility(View.VISIBLE);
                tv_conn.setText("刷新数据中...");
                tv_conn.setVisibility(View.VISIBLE);
                failIjk = 0;
                getNetData();
            }
        });
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerview.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(adapter);

        index.setOnClickListener(connClick);
        uservip.setOnClickListener(userClick);
        navigation.setOnClickListener(naviClick);
    }

    private void adapterData() {
        failIjk = 0;
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        progressbar.setVisibility(View.GONE);
        skit1.setVisibility(View.GONE);
        if (vpnIsConn == false) {
            initAlertConn("VPN  玩命连接中......", false);
        }
    }


    /***
     * 下载apk
     */
    private String apkUpdateUrl = "";

    public void getApkData() {
        if (listProp != null) {
            try {
                DownUpdate downUpdate = listProp.getListProp().get(0);
                double newVersion = Double.parseDouble(downUpdate.getVersion());
                double currentVersion = Double.parseDouble(util.getVersion(this));
                if (newVersion > currentVersion) {
                    apkUpdateUrl = downUpdate.getUrl();
                    newPackange(downUpdate.getPackName(), downUpdate.getVersion());
                    alert_Dilaog_DownLoad();
                }
            } catch (Exception e) {
            }
        }
    }

    /***
     * 判断是否新包名app
     */
    private AlertDialog.Builder dialog_Vpn;

    private void newPackange(final String packageName, String version) {
        double newVersion = Double.parseDouble(version);
        double currentVersion = Double.parseDouble(util.getVersion(this));
        String currentPckageName = this.getPackageName();
        if (newVersion > currentVersion) {
            if (!packageName.equals(currentPckageName)) {
                if (util.appIsExist(this, packageName)) {
                    if (dialog_Vpn == null) {
                        dialog_Vpn = new AlertDialog.Builder(this);
                        dialog_Vpn.setCancelable(false);
                        dialog_Vpn.setTitle("使用提示");
                        dialog_Vpn.setIcon(android.R.drawable.ic_dialog_info);
                        dialog_Vpn.setMessage("H站大全当前旧版本已不能使用,请打开新版本VPN");
                        dialog_Vpn.setPositiveButton("打开新版本", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openNewVpn(packageName, MainActivity.this);
                            }
                        }).setNegativeButton("卸载旧版本", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    unstallApp(MainActivity.this.getPackageName());
                                } catch (Exception e) {
                                    T.showCenterToast(MainActivity.this, "卸载失败!");
                                    openNewVpn(packageName, MainActivity.this);
                                }
                            }
                        }).show();
                    } else {
                        dialog_Vpn.show();
                    }
                }
            }
        }
    }

    public void unstallApp(String packageName) {
        Intent uninstall_intent = new Intent();
        uninstall_intent.setAction(Intent.ACTION_DELETE);
        uninstall_intent.setData(Uri.parse("package:" + packageName));
        startActivity(uninstall_intent);
    }


    public void openNewVpn(String packageName, Context context) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
        } catch (Exception e) {
        }
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);
        List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0);
        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String className = ri.activityInfo.name;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            context.startActivity(intent);
        }
    }


    private AlertDialog.Builder dialog_DownLoad;
    private AlertDialog alertDialog_DownLoad;

    public void alert_Dilaog_DownLoad() {
        MobclickAgent.onEvent(MainActivity.this, MobClick.DOWNLOAD_UPDATE_TAN);//埋点统计
        if (listProp != null) {
            double newVersion = Double.parseDouble(listProp.getListProp().get(0).getVersion());
            double currentVersion = Double.parseDouble(util.getVersion(this));
            String currentPckageName = this.getPackageName();
            String newPackageName = listProp.getListProp().get(0).getPackName();
            if (newVersion > currentVersion) {
                if (!newPackageName.equals(currentPckageName)) {
                    if (util.appIsExist(this, newPackageName)) {
                        if (alertDialog_DownLoad != null) {
                            alertDialog_DownLoad.dismiss();
                        }
                        return;
                    }
                }
            }
        }
        if (dialog_DownLoad == null) {
            dialog_DownLoad = new AlertDialog.Builder(this);
            dialog_DownLoad.setCancelable(false);
            dialog_DownLoad.setTitle("升级提示");
            dialog_DownLoad.setIcon(android.R.drawable.ic_dialog_info);
            dialog_DownLoad.setMessage("H站大全有最新的版本,尽快下载吧。");
            alertDialog_DownLoad = dialog_DownLoad.setPositiveButton("马上下载", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    dialog_DownLoad = null;
                    stopVpnConnection();
                    apkDataDownLoad();
                }
            }).show();
        } else {
            dialog_DownLoad.show();
        }
    }

    private String apkDataPath = "vpnupdate.apk";

    public void apkDataDownLoad() {
        /***
         * apk文件下载
         */
        if (util.isNetworkConnected(this)) {
            //网络已连接
        } else {
            t.centershow(this, "世界上最远的距离就是没网", 500);
            startHandler.sendEmptyMessage(5000);
            return;
        }
        if (TextUtils.isEmpty(apkUpdateUrl)) {
            return;
        }
        File file = new File(Util.getSDCardPath() + "/" + apkDataPath);
        if (file.exists()) {
            Util.deleteFile(file);
        }
        Request request = new Request.Builder().url(apkUpdateUrl).build();
        OkHttp.getInstance().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("h_bl", "文件下载失败");
                stopVpnConnection();
                isDownLoad = false;
                mHandler.sendEmptyMessage(GO_DOWN_VPN_FAILED);
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
                    File file = new File(SDPath, apkDataPath);
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
                        mHandler.sendMessage(msg);
                    }
                    fos.flush();
                    Log.d("h_bl", "文件下载成功");
                    mHandler.sendEmptyMessage(GO_DOWN_VPN_SUCCES);
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

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy--MM--dd HH:mm:ss");
    /***
     * 更新apk
     */
    public static String apkUrl = "";
    public static ApkInfo apkInfo;

    public void getApkUpdate() {
        if (util.isNetworkConnected(this)) {
            //网络已连接
        } else {
            t.centershow(this, "世界上最远的距离就是没网", 500);
            return;
        }
        //渠道号
        String area = util.getAppMetaData(this, "UMENG_CHANNEL");//暂时测试渠道号
        if (TextUtils.isEmpty(area)) {
            area = "vpn999";//渠道为空，则上传此渠道号
        }
        DownLoadArea downLoadArea = new DownLoadArea();
        downLoadArea.setArea(area);
        String json = JSON.toJSONString(downLoadArea);
        String aesJson = aesUtils.encrypt(json);
        //发起请求
        RequestParams params = new RequestParams(Constant.APK_DOWN_INTERFACE);
        params.setCacheMaxAge(0);//最大数据缓存时间
        params.setConnectTimeout(15000);//连接超时时间
        params.setCharset("UTF-8");
        params.addQueryStringParameter("data", aesJson);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                try {
                    String json = result.toString();
                    String aesJson = aesUtils.decrypt(json);
                    JSONObject object = new JSONObject(aesJson);
                    String status = object.getString("respMsg");
                    if (status.equals("success")) {
                        ApkList info = JSON.parseObject(aesJson, ApkList.class);
                        apkInfo = info.getListJson().get(0);
                        apkUrl = apkInfo.getUrl();
                        String vipStatus = aesUtils.decrypt(util.sharedPreferencesReadData(MainActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status"));
                        if (vipStatus.equals("1")) {
                            isThreeSplash = false;
                            alert_Dilaog_Max();
                        } else {
                            //看2天升级 //yyyy-MM-dd HH:mm:ss
                            String path = Util.getSDCardPath() + "/" + Constant.VPN_FIRST_REGISTER_FILE;
                            String firstRegister = Util.readFileToSDFile(path);
                            Date newDate = new Date();
                            //指定2小时的试用
                            long[] time = getTime(newDate, simpleDateFormat.parse(firstRegister));
                            if (time == null) {
                                if (vpnIsConn) {
                                    stopVpnConnection();
                                }
                                if (!dialog2.isShowing()) {
                                    alertPay();
                                }
                                return;
                            }
                            //2天强制升级到tv
                            if (time[0] >= 1) {
                                isThreeSplash = false;
                                alert_Dilaog_Max();
                                return;
                            }
                        }
                    } else {
                        startHandler.sendEmptyMessage(5000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                getApkUpdate();
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    //    private AlertDialog.Builder dialog_Max;
//    private AlertDialog alertDialog;
    private boolean isDownLoad = false;
    private boolean isThreeSplash = false;
    Dialog dialog_Vip_Apk;
    LinearLayout layou_Tv;
    SpannableStringBuilder mSpannableStringBuilder_Tv;

    public void alert_Dilaog_Max() {
        if (!isDownLoad) {
            isDownLoad = true;
            if (apkInfo != null) {
                if (!util.appIsExist(this, apkInfo.getPackName())) {
                    //tv不存在
                    if (isThreeSplash) {
                        MobclickAgent.onEvent(MainActivity.this, MobClick.DOWNLOAD_TV_SPLASH_THREE);//埋点统计
                    } else {
                        MobclickAgent.onEvent(MainActivity.this, MobClick.DOWNLOAD_TV_TAN);//埋点统计
                    }
                    layou_Tv = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog_vip_tv_download, null);
                    //对话框
                    //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
                    if (dialog_Vip_Apk == null) {
                        dialog_Vip_Apk = new Dialog(this, R.style.Dialog);
                        dialog_Vip_Apk.show();
                        dialog_Vip_Apk.setCancelable(false);
                        Window window = dialog_Vip_Apk.getWindow();
                        window.getDecorView().setPadding(0, 0, 0, 0);
                        WindowManager.LayoutParams lp = window.getAttributes();
                        //          layout.getBackground().setAlpha(150);
                        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;//ScreenTool.getWidth(this) / 5 * 3;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        window.setAttributes(lp);
                        window.setContentView(layou_Tv);
                        ImageButton dialog_vipdownload_down = (ImageButton) layou_Tv.findViewById(R.id.dialog_vipdownload_down);
                        TextView vpn_text_decription = (TextView) layou_Tv.findViewById(R.id.vpn_text_decription);
                        ImageView close_vpn = (ImageView) layou_Tv.findViewById(R.id.close_vpn);
                        close_vpn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog_Vip_Apk.dismiss();
                            }
                        });
                        if (isThreeSplash) {
                            close_vpn.setVisibility(View.VISIBLE);
                        } else {
                            close_vpn.setVisibility(View.GONE);
                        }
                        mSpannableStringBuilder_Tv = new SpannableStringBuilder(getResources().getString(R.string.tvdescription));
                        mSpannableStringBuilder_Tv.setSpan
                                (new ForegroundColorSpan(this.getResources().getColor(R.color.danhuang)), 0, 15, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        vpn_text_decription.setText(mSpannableStringBuilder_Tv);
                        dialog_vipdownload_down.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog_Vip_Apk.dismiss();
                                stopVpnConnection();
                                isDownLoad = false;
                                apkDownLoad();
                            }
                        });
                    } else {
                        dialog_Vip_Apk.show();
                    }
                } else {
//                if (alertDialog != null) {
//                    isDownLoad = false;
//                    alertDialog.dismiss();
//                }
                }
            }else{
                isDownLoad = false;
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
            startHandler.sendEmptyMessage(5000);
            return;
        }
        if (TextUtils.isEmpty(apkUrl)) {
            getApkUpdate();
            return;
        }
        File file = new File(Util.getSDCardPath() + "/" + apkPath);
        if (file.exists()) {
            Util.deleteFile(file);
        }
        Request request = new Request.Builder().url(apkUrl).build();
        OkHttp.getInstance().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("h_bl", "文件下载失败");
                stopVpnConnection();
                isDownLoad = false;
                mHandler.sendEmptyMessage(GO_DOWN_TV_FAILED);
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
                        mHandler.sendMessage(msg);
                    }
                    fos.flush();
                    Log.d("h_bl", "文件下载成功");
                    mHandler.sendEmptyMessage(GO_DOWN_TV_SUCCES);
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
            progressDialog.setTitle("应用正在下载中，请稍候...");
        }
        //设置进度条最大值
        progressDialog.setProgress(current);
//      progressDialog.setMax(total / 1024 / 1024);
        progressDialog.setProgressNumberFormat(total / 1024 / 1024 + "MB");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    /**
     * 获取导航数据
     *
     * @param
     * @param
     * @param
     */
    private int failNaIjk = 0;


    public void getNavigationData() {
        if (util.isNetworkConnected(this)) {
            //网络已连接
        } else {
            t.centershow(this, "世界上最远的距离就是没网", 500);
            return;
        }
        //发起请求
        RequestParams params = new RequestParams(Constant.NAVIGATION_DATA);
        params.setCacheMaxAge(0);//最大数据缓存时间
        params.setConnectTimeout(15000);//连接超时时间
        params.setCharset("UTF-8");

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.i("indexStr", "获取导航数据发送成功");
                try {
                    String json = result.toString();
                    String aesJson = aesUtils.decrypt(json);
                    Log.i("aes", json);
                    navigationList banner = JSON.parseObject(aesJson, navigationList.class);
                    if (banner.getListJson() == null) {
                        failNaIjk++;
                        if (failNaIjk <= 5) {
                            getNavigationData();
                        } else {
                            failNaIjk = 0;
                        }
                        return;
                    }

                    if (banner.getListJson().size() == 0) {
                        failNaIjk++;
                        if (failNaIjk <= 5) {
                            getNavigationData();
                        } else {
                            failNaIjk = 0;
                        }
                        return;
                    }
                    ICSOpenVPNApplication.navigationList = banner;
                } catch (Exception e) {
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("indexStr", "获取导航数据失败");
                failNaIjk++;
                if (failNaIjk <= 5) {
                    getNavigationData();
                } else {
                    failNaIjk = 0;
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }


    public void getTvData() {
        //发起请求
        RequestParams params = new RequestParams(Constant.TV_VIDEO_DATA);
        params.setCacheMaxAge(0);//最大数据缓存时间
        params.setConnectTimeout(5000);//连接超时时间
        params.setCharset("UTF-8");

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                String aesJson = aesUtils.decrypt(result);
                VideoListData listData = JSON.parseObject(aesJson, VideoListData.class);
                ICSOpenVPNApplication.videoListData = listData;
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

    /**
     * 获取首页数据
     *
     * @param
     * @param
     * @param
     */
    private int failIjk = 0;

    public void getNetData() {
        if (tv_conn.getVisibility() == View.VISIBLE) {
            tv_conn.setText("数据获取中...");
        } else {
            tv_conn.setVisibility(View.VISIBLE);
            tv_conn.setText("数据获取中...");
        }
        if (util.isNetworkConnected(this)) {
            //网络已连接
        } else {
            doData(de.blinkt.openvpn.Tool.Util.getFromRaw(this));
            swipeRefreshLayout.setRefreshing(false);
            t.centershow(this, "世界上最远的距离就是没网,下拉刷新重新拉取数据吧", 500);
            progressbar.setVisibility(View.GONE);
            return;
        }

        //发起请求
        RequestParams params = new RequestParams(Constant.USER_MESSAGE);
        params.setCacheMaxAge(0);//最大数据缓存时间
        params.setConnectTimeout(15000);//连接超时时间
        params.setCharset("UTF-8");
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Log.i("indexStr", "首页获取数据发送成功");
                try {
                    String json = result.toString();
                    String aesJson = aesUtils.decrypt(json);
                    doData(aesJson);
                } catch (Exception e) {
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                doData(de.blinkt.openvpn.Tool.Util.getFromRaw(MainActivity.this));
                swipeRefreshLayout.setRefreshing(false);
                progressbar.setVisibility(View.GONE);
                failIjk++;
                if (failIjk <= 5) {
                    if (failIjk == 2) {
                        T.showCenterToast(MainActivity.this, "没有获取到数据,下拉刷新试试吧" + ex.getMessage());
                    }
                    getNetData();
                } else {
                    failIjk = 0;
                    T.showCenterToast(MainActivity.this, "没有获取到数据,下拉刷新试试吧" + ex.getMessage());
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });

    }

    private UpdateList listProp;

    private void doData(String aesJson) {
        Pager banner = JSON.parseObject(aesJson, Pager.class);
        MessageList message = JSON.parseObject(aesJson, MessageList.class);
        ListHot hot = JSON.parseObject(aesJson, ListHot.class);

        List<?> bannerlist = banner.getListAdvJson();
        List<?> messagelist = message.getListJson();
        List<?> hotlist = hot.getListHotWebJson();
        listProp = JSON.parseObject(aesJson, UpdateList.class);
        PriceList priceList = JSON.parseObject(aesJson, PriceList.class);
        Constant.priceInfoList = priceList.getListPrice();

        if (bannerlist == null || messagelist == null || hotlist == null) {
            t.centershow(MainActivity.this, "没有获取到数据,下拉刷新试试吧", 50);
            getNetData();
            return;
        }

        if (bannerlist.size() == 0 || messagelist.size() == 0 || hotlist.size() == 0) {
            t.centershow(MainActivity.this, "没有获取到数据,下拉刷新试试吧", 50);
            getNetData();
            return;
        }
        allDataMap.clear();
        allDataMap.put(0, bannerlist);
        allDataMap.put(1, new Object());
        allDataMap.put(2, messagelist);
        allDataMap.put(3, hotlist);
        adapterData();
    }

    /**
     * 获取配置文件下载地址和版本
     *
     * @param
     * @param
     * @param
     */
    private int getConfigFailIjk = 0;

    public void getNetCigAndVer() {
        if (util.isNetworkConnected(this)) {
            //网络已连接
        } else {
            swipeRefreshLayout.setRefreshing(false);
            t.centershow(this, "世界上最远的距离就是没网,下拉刷新重新拉取数据吧", 500);
            progressbar.setVisibility(View.GONE);
            return;
        }

        RequestParams params = new RequestParams(Constant.CONFIG_INTERFACE);
        ConfigPara configPara = new ConfigPara();
        configPara.setType("android");
        String json = JSON.toJSONString(configPara);
        String aesJson = aesUtils.encrypt(json);
        params.addQueryStringParameter("data", aesJson);

        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                String json = result.toString();
                String aesJson = aesUtils.decrypt(json);
                try {
                    JSONObject object = new JSONObject(aesJson);
                    String state = object.getString("respMsg");
                    if (state.equals("success")) {
                        VpnList list = com.alibaba.fastjson.JSONObject.parseObject(aesJson, VpnList.class);
                        VpnConfig config = list.getListJson().get(0);

                        util.sharedPreferencesWriteData(MainActivity.this, KeyFile.COFIG_DOWN_FILE, KeyFile.COFIG_DOWN_FILE, aesUtils.encrypt(config.getUrl()));
                        String newVersion = config.getVersion();
                        Log.i("indexStr", "新版本:" + newVersion);
                        //取出本地数据
                        String oldVersion = aesUtils.decrypt(util.sharedPreferencesReadData(MainActivity.this, KeyFile.CONFIG_VERSION, "mUUi9jgQd0iIz8"));
                        Log.i("indexStr", "旧版本:" + oldVersion);
                        if (TextUtils.isEmpty(oldVersion)) {
                            //本地没有配置数据(第一次进入)
                            util.sharedPreferencesDelByFileAllData(MainActivity.this, KeyFile.CONFIG_VERSION);
                            Util.deleteFile(new File(configFilePath));
                            //写入
                            util.sharedPreferencesWriteData(MainActivity.this, KeyFile.CONFIG_VERSION, "mUUi9jgQd0iIz8", aesUtils.encrypt(newVersion));
                            configDownLoad();
                        } else {
                            if (oldVersion.equals(newVersion)) {
                                //旧版本和新版本一样,说明服务器没有更新配置文件
                                File file = new File(Util.getSDCardPath() + "/" + Constant.VPN_LOCAL_CONFIG_FILE);
                                if (!file.exists()) {
                                    util.sharedPreferencesDelByFileAllData(MainActivity.this, KeyFile.CONFIG_VERSION);
                                    Util.deleteFile(new File(configFilePath));
                                    //写入
                                    util.sharedPreferencesWriteData(MainActivity.this, KeyFile.CONFIG_VERSION, "mUUi9jgQd0iIz8", aesUtils.encrypt(newVersion));
                                    configDownLoad();
                                } else {
                                    startHandler.sendEmptyMessage(555);
                                }
                            } else {
                                //服务器更新了配置文件
                                util.sharedPreferencesDelByFileAllData(MainActivity.this, KeyFile.CONFIG_VERSION);
                                Util.deleteFile(new File(configFilePath));
                                //写入
                                util.sharedPreferencesWriteData(MainActivity.this, KeyFile.CONFIG_VERSION, "mUUi9jgQd0iIz8", aesUtils.encrypt(newVersion));
                                configDownLoad();
                            }
                        }
                    } else {
                        getNetCigAndVer();
                        return;
                    }

                } catch (JSONException e) {
                    getNetCigAndVer();
                    return;
                }
                Log.i("indexStr", "获取配置文件数据成功");
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("indexStr", "获取配置文件数据失败" + ex.getMessage());
                getConfigFailIjk++;
                if (getConfigFailIjk <= 1) {
                    getNetCigAndVer();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    public void configDownLoad() {
        String configDownLoadUrl = aesUtils.decrypt(util.sharedPreferencesReadData(this, KeyFile.COFIG_DOWN_FILE, KeyFile.COFIG_DOWN_FILE));
//        String configDownLoadUrl = "http://www.kunmingquansheng.com/vpn/my-server.ovpn";
        if (TextUtils.isEmpty(configDownLoadUrl)) {
            getNetCigAndVer();
            return;
        }
        File file = new File(Util.getSDCardPath() + "/" + Constant.VPN_LOCAL_CONFIG_FILE);
        if (file.exists()) {
            file.delete();
        }
        Request request = new Request.Builder().url(configDownLoadUrl).build();
        OkHttp.getInstance().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                configDownLoad();
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
                    File file = new File(SDPath, Constant.VPN_LOCAL_CONFIG_FILE);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        stopVpnConnection();
                        isDownLoad = true;
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        Log.d("h_bl", "progress=" + progress);
                    }
                    fos.flush();
                    Log.d("h_bl", "文件下载成功");
                    startHandler.sendEmptyMessage(555);
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

    /**
     * recyclerView适配器
     */

    class recyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
        private static final int TYPE_HEADER = 0;//头部
        private static final int TYPE_BODY = 1;//中间
        private static final int TYPE_FOOTER = 2;//底部

        /***
         * 先创建ViewHolder，并分类型
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            switch (viewType) {
                case TYPE_HEADER:
                    return onCreateHeaderViewHolder(parent, viewType);
                case TYPE_BODY:
                    return onCreateBodyViewHolder(parent, viewType);
                case TYPE_FOOTER:
                    return onCreateFooterViewHolder(parent, viewType);
            }
            return null;
        }

        /**
         * 3、头部类型 轮播
         *
         * @param parent
         * @param viewType
         * @return
         */
        private RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_lunbo, parent, false);
            HeaderViewHolder holder = new HeaderViewHolder(view);
            return holder;
        }

        /**
         * 4、使用ViewHolder
         */
        class HeaderViewHolder extends RecyclerView.ViewHolder {

            public Banner banner;
            public TextView tv_timer;
            public LinearLayout top_time, top_title;
            public ImageView daodu, kefu;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                banner = (Banner) itemView.findViewById(R.id.banner);
                tv_timer = (TextView) itemView.findViewById(R.id.tv_timer);
                top_time = (LinearLayout) itemView.findViewById(R.id.top_time);
                top_title = (LinearLayout) itemView.findViewById(R.id.top_title);
                daodu = (ImageView) itemView.findViewById(R.id.daodu);
                kefu = (ImageView) itemView.findViewById(R.id.kefu);
            }
        }

        /**
         * 中间部分类型 热门推荐
         *
         * @param parent
         * @param viewType
         * @return
         */
        private RecyclerView.ViewHolder onCreateBodyViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.index_body, parent, false);
            BodyViewHolder holder = new BodyViewHolder(view);
            return holder;
        }

        /**
         * 4、使用ViewHolder
         */
        class BodyViewHolder extends RecyclerView.ViewHolder {

            public HorizontalScrollView imParent;
            public LinearLayout imageliner, body;
            public ImageView left, right;

            public BodyViewHolder(View itemView) {
                super(itemView);
                body = (LinearLayout) itemView.findViewById(R.id.body);
                imageliner = (LinearLayout) itemView.findViewById(R.id.images);
                imParent = (HorizontalScrollView) itemView.findViewById(R.id.imParent);
                left = (ImageView) itemView.findViewById(R.id.left);
                right = (ImageView) itemView.findViewById(R.id.right);
            }
        }


        private RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.index_footer, parent, false);
            FooterViewHolder holder = new FooterViewHolder(view);
            return holder;
        }

        class FooterViewHolder extends RecyclerView.ViewHolder {

            public SelfListView hot_message_lv;
            public EditText et_message;
            public ImageView submit;

            public FooterViewHolder(View itemView) {
                super(itemView);
                hot_message_lv = (SelfListView) itemView.findViewById(R.id.hot_message_lv);
                et_message = (EditText) itemView.findViewById(R.id.et_message);
                submit = (ImageView) itemView.findViewById(R.id.submit);
            }
        }

        //绑定视图
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == 0) {
                onBindHeaderViewHolder((HeaderViewHolder) holder, position);
            } else if (getItemViewType(position) == 1) {
                onBindBodyViewHolder((BodyViewHolder) holder, position);
            } else if (getItemViewType(position) == 2) {
                onBindFooterViewholder((FooterViewHolder) holder, position);
            }
        }

        @Override
        public void onClick(View view) {

        }

        //数据长度
        @Override
        public int getItemCount() {
            return allDataMap.size() - 1;
        }


        /**
         * 1设置视图类型
         * 这句话是关键
         * adapter会将此方法的返回值传入onCreateViewHolder
         *
         * @param position
         * @return
         */
        @Override
        public int getItemViewType(int position) {

            if (position == 0) {
                return TYPE_HEADER;
            } else if (position == 1) {
                return TYPE_BODY;
            } else if (position == 2) {
                return TYPE_FOOTER;
            } else {
                return TYPE_FOOTER;
            }
        }

        /**
         * 绑定头部轮播
         */
        private List<PagerInfo> bannerList = new ArrayList<>();
        private List<String> titles = null;
        private List<String> imageUrls = null;
        private int xScroll, yScroll = 0;
        private pagerInterface pagerInterface;
        private pagerInterface choiceInterface;

        private void onBindHeaderViewHolder(final HeaderViewHolder holder, int position) {
            //轮播效果
            pagerInterface = MainActivity.this;
            bannerList = (List<PagerInfo>) allDataMap.get(0);
            if (titles == null) {
                titles = new ArrayList<>();
            }
            if (imageUrls == null) {
                imageUrls = new ArrayList<>();
            }
            titles.clear();
            imageUrls.clear();
            for (int i = 0; i < bannerList.size(); i++) {
                titles.add(i, bannerList.get(i).getDescription() + "  " + bannerList.get(i).getUid());
            }
            for (int i = 0; i < bannerList.size(); i++) {
                imageUrls.add(i, bannerList.get(i).getPic_link());
                ;
            }
            //轮播逻辑
            //显示圆形指示器和标题
            holder.banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
            //设置标题列表
            holder.banner.setBannerTitles(titles);
            //设置轮播间隔时间 在布局文件中设置了5秒
            holder.banner.setDelayTime(3000);
            //设置动画
            //holder.banner.setBannerAnimation(Transformer.CubeOut);//立体
            holder.banner.setBannerAnimation(com.youth.banner.Transformer.Accordion);//延伸
            //holder.banner.setBannerAnimation(Transformer.BackgroundToForeground);//淡入放大
            //holder.banner.setBannerAnimation(Transformer.CubeIn);
            //holder.banner.setBannerAnimation(Transformer.DepthPage);
            //holder.banner.setBannerAnimation(Transformer.ZoomOutSlide);
            /**
             * 可以选择设置图片网址或者资源文件，默认用Glide加载
             * 如果你想设置默认图片就在xml里设置default_image
             * banner.setImages(images);
             */
            //如果你想用自己项目的图片加载,那么----->自定义图片加载框架
            //设置图片加载器
            holder.banner.setImageLoader(new GlideImageLoader());
            //设置图片集合
            holder.banner.setImages(imageUrls);
            //设置点击事件
            holder.banner.setOnBannerClickListener(new OnBannerClickListener() {
                @Override
                public void OnBannerClick(int position) {
                    try {
                        int pos = position - 1;
                        pos = pos > bannerList.size() - 1 ? 0 : pos;
                        pagerInterface.pagerClick(null, bannerList.get(pos).getLink(), bannerList.get(pos).getUid());
                    } catch (Exception e) {

                    }
                }
            });
            //banner设置方法全部调用完毕时最后调用
            holder.banner.start();
            File file = new File(VPN_SHIYONG_FILE);
            if (!file.exists()) {
                //没有控制试用vpn的文件(試用)
                Util.createFile(VPN_SHIYONG_FILE);
                Util.writeFileToSDFile(VPN_SHIYONG_FILE, String.valueOf(Constant.lookTime));
            }
            holder.daodu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    util.alertZhiNan(MainActivity.this);
                }
            });

            holder.kefu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    util.alertKeFu(MainActivity.this);
                }
            });
        }

        //绑定中间方法
        private void onBindBodyViewHolder(final BodyViewHolder holder, int position) {
            final List<Hot> hotList = (List<Hot>) allDataMap.get(3);
            if (hotList == null) {
                return;
            }
            hotInterface = MainActivity.this;
            holder.imageliner.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            for (int i = 0; i < hotList.size(); i++) {
                final Hot hot = hotList.get(i);
                View view = inflater.inflate(R.layout.hirentalscroll_item, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_scroll);
                TextView iv_name = (TextView) view.findViewById(R.id.iv_name);
                TextView umeventid = (TextView) view.findViewById(R.id.umeventid);

                umeventid.setText(hot.getUid());
                iv_name.setText(hot.getWeb_name());
                x.image().bind(imageView, hot.getLogo_url(), util.getOptions());
                RelativeLayout.LayoutParams llp = new RelativeLayout.LayoutParams(3 * util.getWidth() / 7, ViewGroup.LayoutParams.MATCH_PARENT);
                llp.setMargins(10, 0, 0, 0);
                view.setLayoutParams(llp);
                imageView.setMinimumHeight(280);
                imageView.setMaxHeight(280);
                //imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hotInterface.hotClick(view, hot.getWeb_url(), hot.getUid());
                    }
                });
                holder.imageliner.addView(view);
            }
            holder.left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    xScroll = xScroll - 500;
                    if (xScroll <= 0) {
                        xScroll = 1128;
                    }
                    holder.imParent.smoothScrollTo(xScroll, yScroll);
                }
            });
            holder.right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    xScroll = xScroll + 200;
                    if (xScroll >= 1128) {
                        xScroll = 0;
                    }
                    holder.imParent.smoothScrollTo(xScroll, yScroll);
                }
            });

        }


        //绑定底部方法
        private void onBindFooterViewholder(final FooterViewHolder holder, int position) {
            //
            List<MessageInfo> messageInfos = (List<MessageInfo>) allDataMap.get(2);
            File file;
            file = new File(configFilePath + "/" + Constant.VPN_LOCAL_CONFIG_FILE);
            if (messageAdapter == null) {
                messageAdapter = new MessageAdapter(MainActivity.this, holder.hot_message_lv);
                messageAdapter.setMessageInterface(MainActivity.this);
                messageAdapter.setList(messageInfos);
                holder.hot_message_lv.setAdapter(messageAdapter);
                if (file.exists()) {
                    if (Constant.firstConnTime == null) {
                        startHandler.sendEmptyMessage(555);
                    }
                }
            }
            if (file.exists()) {
                if (Constant.firstConnTime == null) {
                    startHandler.sendEmptyMessage(555);
                }
            }
            messageAdapter.setList(messageInfos);
            messageAdapter.notifyDataSetChanged();

            //评论
            holder.submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String message = holder.et_message.getText().toString();
                    if (TextUtils.isEmpty(message)) {
                        t.centershow(MainActivity.this, "请输入评论内容", 1000);
                        return;
                    }
                    userRequestMessage(message);
                    holder.et_message.setText("");
                    holder.et_message.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            holder.et_message.setInputType(InputType.TYPE_NULL); // 关闭软键盘
                            return false;
                        }
                    });
                }
            });

        }
    }

    /**
     * tab选中事件
     */
    View.OnClickListener connClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (blackback.getVisibility() == View.VISIBLE) {
                return;
            } else {
                index.setBackgroundResource(R.mipmap.jiasu_select);

                navigation.setBackgroundResource(R.mipmap.daohang);

                uservip.setBackgroundResource(R.mipmap.huiyuan);
            }
        }
    };


    View.OnClickListener naviClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (blackback.getVisibility() == View.VISIBLE) {
                return;
            } else {
                //跳转activity
                Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }

        }
    };

    View.OnClickListener userClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (blackback.getVisibility() == View.VISIBLE) {
                return;
            } else {
                //跳转activity
                Intent intent = new Intent(MainActivity.this, WXPayEntryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade, R.anim.hold);
            }
        }
    };


    /***
     * 断开VPN连接
     */
    public void stopVpnConnection() {
        ProfileManager.setConntectedVpnProfileDisconnected(MainActivity.this);
        if (mService != null && mService.getManagement() != null) {
            mService.getManagement().stopVPN(false);
            vpnIsConn = false;//
        }
    }

    Dialog dialog1;

    public void alertPay() {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_pay, null);
        //对话框
        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
        if (dialog1 == null) {
            dialog1 = new Dialog(this, R.style.Dialog);
            dialog1.show();
            Window window = dialog1.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            layout.getBackground().setAlpha(50);
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
            window.setContentView(layout);

            //取消按钮
            ImageView btn_close = (ImageView) layout.findViewById(R.id.btn_close);

            btn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog1.dismiss();
                }
            });
            ImageView pay1 = (ImageView) layout.findViewById(R.id.pay1);
            pay1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转activity
                    Intent intent = new Intent(MainActivity.this, WXPayEntryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                    dialog1.dismiss();
                }
            });

            ImageView pay2 = (ImageView) layout.findViewById(R.id.pay2);
            pay2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转activity
                    Intent intent = new Intent(MainActivity.this, WXPayEntryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                    dialog1.dismiss();
                }
            });

            ImageView pay3 = (ImageView) layout.findViewById(R.id.pay3);
            pay3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转activity
                    Intent intent = new Intent(MainActivity.this, WXPayEntryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                    dialog1.dismiss();
                }
            });
        } else {
            dialog1.show();
        }
    }

    Dialog dialog2;
    int secondStr = 10;
    SpannableStringBuilder mSpannableStringBuilder;

    public void alertMess() {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.alert_message, null);
        //对话框
        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
        if (dialog2 == null) {
            dialog2 = new Dialog(this, R.style.Dialog);
            dialog2.show();
            dialog2.setCancelable(false);
            Window window = dialog2.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
//            layout.getBackground().setAlpha(130);
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setContentView(layout);

            mSpannableStringBuilder = new SpannableStringBuilder(getResources().getString(R.string.info));
            mSpannableStringBuilder.setSpan
                    (new ForegroundColorSpan(this.getResources().getColor(R.color.juhuang)), 53, 133, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            mSpannableStringBuilder.setSpan
                    (new ForegroundColorSpan(this.getResources().getColor(R.color.juhuang)), 137, 183, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            //取消按钮
            final Button ok = (Button) layout.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog2.dismiss();
                    getApkData();
                }
            });
            TextView tv_conn_content = (TextView) layout.findViewById(R.id.tv_conn_content);
            JumpingBeans.with(tv_conn_content).makeTextJump(0, tv_conn_content.length() - 1)
                    .build();
            TextView txtContent_disclaimer = (TextView) layout.findViewById(R.id.txtContent_disclaimer);
            txtContent_disclaimer.setMovementMethod(ScrollingMovementMethod.getInstance());
            txtContent_disclaimer.setText(mSpannableStringBuilder);

            final TextView first_timer = (TextView) layout.findViewById(R.id.first_timer);

            if (Constant.isFirstRegister) {
                first_timer.setVisibility(View.VISIBLE);
                ok.setEnabled(false);
                Runnable firstRegRunnable = new Runnable() {
                    @Override
                    public void run() {
                        secondStr--;
                        if (secondStr <= 0) {
                            ok.setEnabled(true);
                            first_timer.setVisibility(View.GONE);
                        } else {
                            startHandler.postDelayed(this, 1000);
                            first_timer.setText("(" + secondStr + "s" + ")");
                        }
                    }
                };
                startHandler.postDelayed(firstRegRunnable, 1000);
            }

        } else {
            dialog2.show();
        }
    }


    private Dialog dialog3;
    private CircularBarPager mCircularBarPager;
    private static final int BAR_ANIMATION_TIME = 10000;
    private RelativeLayout layout;
    private DemoView demoView;
    private View[] views = new View[1];
    private DemoPagerAdapter demoPagerAdapter;

    private void initAlertConn(String conn_conent, boolean isShow) {
//        LayoutInflater inflater = LayoutInflater.from(this);
//        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.vpn_conn_sussces, null);
//        //对话框
//        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
//        if (dialog3 == null) {
//            dialog3 = new Dialog(this, R.style.Dialog);
//            dialog3.show();
//            Window window = dialog3.getWindow();
//            window.getDecorView().setPadding(0, 0, 0, 0);
//            WindowManager.LayoutParams lp = window.getAttributes();
//            layout.getBackground().setAlpha(50);
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//            window.setAttributes(lp);
//            window.setContentView(layout);
//
//            //取消按钮
//            ImageView conn_close = (ImageView) layout.findViewById(R.id.pay_close);
//            conn_close.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog3.dismiss();
//                }
//            });
//        } else {
//            dialog3.show();
//        }
        Log.i("allstr", "执行了一次");
        if (dialog3 == null) {
            Log.i("allstr", "dialog是空");
            if (demoView == null) {
                demoView = new DemoView(this, conn_conent);
            }
            demoView.setConn_text_content(conn_conent);
            LayoutInflater inflater = LayoutInflater.from(this);
            if (layout == null) {
                layout = (RelativeLayout) inflater.inflate(R.layout.vpn_conn_sussces, null);
            }
            dialog3 = new Dialog(this, R.style.Dialog);
            Window window = dialog3.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            layout.getBackground().setAlpha(50);
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
            window.setContentView(layout);

            if (mCircularBarPager == null) {
                mCircularBarPager = (CircularBarPager) layout.findViewById(R.id.circularBarPager);
            }
            views[0] = demoView;
            if (demoPagerAdapter == null) {
                demoPagerAdapter = new DemoPagerAdapter(this, views);
                mCircularBarPager.setViewPagerAdapter(demoPagerAdapter);
            }

            ViewPager viewPager = mCircularBarPager.getViewPager();
            viewPager.setClipToPadding(true);

            CirclePageIndicator circlePageIndicator = mCircularBarPager.getCirclePageIndicator();
            circlePageIndicator.setFillColor(getResources().getColor(R.color.light_grey));
            circlePageIndicator.setPageColor(getResources().getColor(R.color.very_light_grey));
            circlePageIndicator.setStrokeColor(getResources().getColor(R.color.transparent));

            //Do stuff based on animation
            mCircularBarPager.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //TODO do stuff
                    mCircularBarPager.getCircularBar().setProgress(0);
                    alertConn();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            //Do stuff based on when pages change
            circlePageIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    if (mCircularBarPager != null && mCircularBarPager.getCircularBar() != null) {
                        switch (position) {
                            case 0:
                                mCircularBarPager.getCircularBar().animateProgress(-25, 100, BAR_ANIMATION_TIME);
                                break;
                            case 1:
                                mCircularBarPager.getCircularBar().animateProgress(100, -75, BAR_ANIMATION_TIME);
                                break;
                            default:
                                mCircularBarPager.getCircularBar().animateProgress(0, 75, BAR_ANIMATION_TIME);
                                break;
                        }
                    }
                }
            });

        } else {
            if (!TextUtils.isEmpty(conn_conent)) {
                demoView = new DemoView(this, conn_conent);
                demoView.setConn_text_content(conn_conent);
                views[0] = demoView;
                demoPagerAdapter.setmViews(views);
                mCircularBarPager.setViewPagerAdapter(demoPagerAdapter);
                //demoPagerAdapter.notifyDataSetChanged();
            }
        }
        if (isShow == true) {
            dialog3.show();
        }
    }

    private void alertConn() {
        if (mCircularBarPager != null) {
            mCircularBarPager.getCircularBar().animateProgress(0, 100, 1000);
        }
    }

    private void vipVpnStarConn() {
        String nickNameStr = util.sharedPreferencesReadData(MainActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "nickName");
        String usernameStr = util.sharedPreferencesReadData(MainActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "userName");
        String passWordStr = util.sharedPreferencesReadData(MainActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "passWord");
        String showStr = util.sharedPreferencesReadData(MainActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "show");
        String vipStatus = aesUtils.decrypt(util.sharedPreferencesReadData(MainActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status"));
        String vipLastTime = aesUtils.decrypt(util.sharedPreferencesReadData(MainActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_lastTme"));
        if (TextUtils.isEmpty(showStr) || TextUtils.isEmpty(nickNameStr)
                || TextUtils.isEmpty(usernameStr) ||
                TextUtils.isEmpty(passWordStr) || TextUtils.isEmpty(vipStatus)
                || TextUtils.isEmpty(vipLastTime)) {
            t.centershow(MainActivity.this, "~请到会员菜单进行账户登录哦~", 1000);
            return;
        }
        String result = util.compareTime(new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()), vipLastTime);

        //是会员
        if (vipStatus.equals("1")) {
            //查看vip会员时间是否过期
            //result  1过期 2未过期
            if (result.equals("2")) {
                if (isBind == true) {
                    unbindService(conn);
                    isBind = false;
                }
                vpnjk();
            } else if (result.equals("1")) {
                blackback.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                alertPay();
            }
            //不是会员
        } else if (vipStatus.equals("2")) {
            //查看普通用户试用时间是否过期(本地控制)
            //result  1过期 2未过期
            int timeSecond = Integer.parseInt(Util.readFileToSDFile(VPN_SHIYONG_FILE));
            if (timeSecond <= 0) {
                alertPay();
                tv_timer.setText("H 站 大 全");
                return;
            }
            File file = new File(VPN_SHIYONG_FILE);
            if (!file.exists()) {
                //没有控制试用vpn的文件(試用)
                //没有控制试用vpn的文件(試用)
                Util.createFile(VPN_SHIYONG_FILE);
                Util.writeFileToSDFile(VPN_SHIYONG_FILE, String.valueOf(Constant.lookTime));
                vpnjk();
            } else {
                //有文件
                try {
                    blackback.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    //指定2小时的试用
                    if (!VipTool.judgeIsThanSendVpnTime()) {
                        vpnjk();
                    } else {
                        vpnjk();
                    }
                } catch (Exception e) {
                    alertPay();
                }
            }
        }
    }

    /***
     * 计算2个日期的差值
     *
     * @return
     */
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
        time[2] = minutes;
        return time;
    }

    public void vpnjk() {
        if (vpnIsConn == false) {
            //vpn连接逻辑处理
            //initTimer();
            start();
        } else {
            userClickInVpn = false;
            //util.starBrowser(indexHtmlStr, this);
            startWeb(indexHtmlStr);
        }
    }

    private boolean isGoStart = false;

    public void start() {
        if (!util.isNetworkConnected(this)) {
            t.show(this, "世界上最远的距离就是没网", 500);
            return;
        }
        progressbar.setVisibility(View.GONE);
        //tv_conn.setVisibility(View.VISIBLE);
        //tv_conn.setText("连接中...请稍候");
        if (dialog3 != null) {
            if (!dialog3.isShowing()) {
                initAlertConn("VPN  玩命连接中......", true);
                alertConn();
            }
        }
        Uri uri = null;
        File file = new File(Util.getSDCardPath() + "/" + Constant.VPN_LOCAL_CONFIG_FILE);
        if (!file.exists()) {
            //去下载配置文件
            configDownLoad();
            return;
        }
        //标识是用户连接了vpn
        isGoStart = true;
        uri = Uri.fromFile(file);
        doImportUri(uri);
    }

    /***
     * 指定文件后的数据处理
     *
     * @param data
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void doImportUri(Uri data) {
        //log(R.string.import_experimental);
        //log(R.string.importing_config, data.toString());
        try {
            String possibleName = null;
            if ((data.getScheme() != null && data.getScheme().equals("file")) || (data.getLastPathSegment() != null && (data.getLastPathSegment().endsWith(".ovpn") ||
                    data.getLastPathSegment().endsWith(".conf")))
                    ) {
                possibleName = data.getLastPathSegment();
                if (possibleName.lastIndexOf('/') != -1)
                    possibleName = possibleName.substring(possibleName.lastIndexOf('/') + 1);

            }

            mPathsegments = data.getPathSegments();

            Cursor cursor = getContentResolver().query(data, null, null, null, null);

            try {

                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

                    if (columnIndex != -1) {
                        String displayName = cursor.getString(columnIndex);
                        if (displayName != null)
                            possibleName = displayName;
                    }
                    columnIndex = cursor.getColumnIndex("mime_type");
                    if (columnIndex != -1) {
                        //log("Opening Mime TYPE: " + cursor.getString(columnIndex));
                    }
                }
            } finally {
                if (cursor != null)
                    cursor.close();
            }
            if (possibleName != null) {
                possibleName = possibleName.replace(".ovpn", "");
                possibleName = possibleName.replace(".conf", "");
            }
            try {
                InputStream is = getContentResolver().openInputStream(data);
//                InputStream is = getResources().getAssets().open("testzhuli.ovpn");
                doImport(is, possibleName);
            } catch (NetworkOnMainThreadException nom) {
                throw new RuntimeException("Network on Main: + " + data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始OpenVpn连接
     */
    private void startVPN(VpnProfile profile) {

        getPM().saveProfile(this, profile);

        Intent intent = new Intent(this, LaunchVPN.class);
        intent.putExtra(LaunchVPN.EXTRA_KEY, profile.getUUID().toString());
        intent.setAction(Intent.ACTION_MAIN);
        startActivity(intent);
    }

    private ProfileManager getPM() {
        return ProfileManager.getInstance(this);
    }


    /***
     * ok按钮后根据数据开始配置
     */
    private boolean userActionSaveProfile() {
        if (mResult == null) {
            //log(R.string.import_config_error);
            Toast.makeText(this, "配置错误", Toast.LENGTH_LONG).show();
            return true;
        }

        mResult.mName = mProfilename;
        ProfileManager vpl = ProfileManager.getInstance(this);
        if (vpl.getProfileByName(mResult.mName) != null) {
            t.show(MainActivity.this, "请选择一个服务器连接", 1000);
            return true;
        }

        Intent in = installPKCS12();

        if (in != null)
            startActivityForResult(in, RESULT_INSTALLPKCS12);
        else
            saveProfile();

        return true;
    }

    private void saveProfile() {
        Intent result = new Intent();
        ProfileManager vpl = ProfileManager.getInstance(this);

        if (!TextUtils.isEmpty(mEmbeddedPwFile))
            ConfigParser.useEmbbedUserAuth(mResult, mEmbeddedPwFile);

        vpl.addProfile(mResult);
        vpl.saveProfile(this, mResult);
        vpl.saveProfileList(this);
//        result.putExtra(VpnProfile.EXTRA_PROFILEUUID, mResult.getUUID().toString());
//        setResult(Activity.RESULT_OK, result);
//        finish();

        startVPN(mResult);


    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Intent installPKCS12() {

//        if (!((CheckBox) findViewById(R.id.importpkcs12)).isChecked()) {
//            setAuthTypeToEmbeddedPKCS12();
//            return null;
//
//        }
        String pkcs12datastr = mResult.mPKCS12Filename;
        if (VpnProfile.isEmbedded(pkcs12datastr)) {
            Intent inkeyIntent = KeyChain.createInstallIntent();

            pkcs12datastr = VpnProfile.getEmbeddedContent(pkcs12datastr);


            byte[] pkcs12data = Base64.decode(pkcs12datastr, Base64.DEFAULT);


            inkeyIntent.putExtra(KeyChain.EXTRA_PKCS12, pkcs12data);

            if (mAliasName.equals(""))
                mAliasName = null;

            if (mAliasName != null) {
                inkeyIntent.putExtra(KeyChain.EXTRA_NAME, mAliasName);
            }
            return inkeyIntent;

        }
        return null;
    }

    private void doImport(InputStream is, String newName) {
        ConfigParser cp = new ConfigParser();
        try {
            InputStreamReader isr = new InputStreamReader(is);
            cp.parseConfig(isr);
            mResult = cp.convertProfile();
            embedFiles(cp);
            displayWarnings();
            mResult.mName = getUniqueProfileName(newName);
            mProfilename = mResult.getName();

            //log(R.string.import_done);
            //
            userActionSaveProfile();
            return;

        } catch (IOException | ConfigParser.ConfigParseError e) {
//            log(R.string.error_reading_config_file);
//            log(e.getLocalizedMessage());
        }
        mResult = null;

    }

    void embedFiles(ConfigParser cp) {
        // This where I would like to have a c++ style
        // void embedFile(std::string & option)

        if (mResult.mPKCS12Filename != null) {
            File pkcs12file = findFileRaw(mResult.mPKCS12Filename);
            if (pkcs12file != null) {
                mAliasName = pkcs12file.getName().replace(".p12", "");
            } else {
                mAliasName = "Imported PKCS12";
            }
        }


        mResult.mCaFilename = embedFile(mResult.mCaFilename, Utils.FileType.CA_CERTIFICATE, false);
        mResult.mClientCertFilename = embedFile(mResult.mClientCertFilename, Utils.FileType.CLIENT_CERTIFICATE, false);
        mResult.mClientKeyFilename = embedFile(mResult.mClientKeyFilename, Utils.FileType.KEYFILE, false);
        mResult.mTLSAuthFilename = embedFile(mResult.mTLSAuthFilename, Utils.FileType.TLS_AUTH_FILE, false);
        mResult.mPKCS12Filename = embedFile(mResult.mPKCS12Filename, Utils.FileType.PKCS12, false);
        mResult.mCrlFilename = embedFile(mResult.mCrlFilename, Utils.FileType.CRL_FILE, true);
        if (cp != null) {
            mEmbeddedPwFile = cp.getAuthUserPassFile();
            mEmbeddedPwFile = embedFile(cp.getAuthUserPassFile(), Utils.FileType.USERPW_FILE, false);
        }

        //updateFileSelectDialogs();
    }

    private String embedFile(String filename, Utils.FileType type, boolean onlyFindFileAndNullonNotFound) {
        if (filename == null)
            return null;

        // Already embedded, nothing to do
        if (VpnProfile.isEmbedded(filename))
            return filename;

        File possibleFile = findFile(filename, type);
        if (possibleFile == null)
            if (onlyFindFileAndNullonNotFound)
                return null;
            else
                return filename;
        else if (onlyFindFileAndNullonNotFound)
            return possibleFile.getAbsolutePath();
        else
            return readFileContent(possibleFile, type == Utils.FileType.PKCS12);

    }


    String readFileContent(File possibleFile, boolean base64encode) {
        byte[] filedata;
        try {
            filedata = readBytesFromFile(possibleFile);
        } catch (IOException e) {
            //log(e.getLocalizedMessage());
            return null;
        }

        String data;
        if (base64encode) {
            data = Base64.encodeToString(filedata, Base64.DEFAULT);
        } else {
            data = new String(filedata);

        }

        return VpnProfile.DISPLAYNAME_TAG + possibleFile.getName() + VpnProfile.INLINE_TAG + data;

    }

    private byte[] readBytesFromFile(File file) throws IOException {
        InputStream input = new FileInputStream(file);

        long len = file.length();
        if (len > VpnProfile.MAX_EMBED_FILE_SIZE)
            throw new IOException("File size of file to import too large.");

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) len];

        // Read in the bytes
        int offset = 0;
        int bytesRead;
        while (offset < bytes.length
                && (bytesRead = input.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += bytesRead;
        }

        input.close();
        return bytes;
    }

    private File findFile(String filename, Utils.FileType fileType) {
        File foundfile = findFileRaw(filename);

        if (foundfile == null && filename != null && !filename.equals("")) {
            //log(R.string.import_could_not_open, filename);
        }

        //addFileSelectDialog(fileType);

        return foundfile;
    }

    private void displayWarnings() {
        if (mResult.mUseCustomConfig) {
            //log(R.string.import_warning_custom_options);
            String copt = mResult.mCustomConfigOptions;
            if (copt.startsWith("#")) {
                int until = copt.indexOf('\n');
                copt = copt.substring(until + 1);
            }

            //log(copt);
        }

        if (mResult.mAuthenticationType == VpnProfile.TYPE_KEYSTORE ||
                mResult.mAuthenticationType == VpnProfile.TYPE_USERPASS_KEYSTORE) {
            //findViewById(R.id.importpkcs12).setVisibility(View.VISIBLE);
        }

    }


    private File findFileRaw(String filename) {
        if (filename == null || filename.equals(""))
            return null;

        // Try diffent path relative to /mnt/sdcard
        File sdcard = Environment.getExternalStorageDirectory();
        File root = new File("/");

        HashSet<File> dirlist = new HashSet<>();

        for (int i = mPathsegments.size() - 1; i >= 0; i--) {
            String path = "";
            for (int j = 0; j <= i; j++) {
                path += "/" + mPathsegments.get(j);
            }
            // Do a little hackish dance for the Android File Importer
            // /document/primary:ovpn/openvpn-imt.conf


            if (path.indexOf(':') != -1 && path.lastIndexOf('/') > path.indexOf(':')) {
                String possibleDir = path.substring(path.indexOf(':') + 1, path.length());
                possibleDir = possibleDir.substring(0, possibleDir.lastIndexOf('/'));

                dirlist.add(new File(sdcard, possibleDir));
            }
            dirlist.add(new File(path));
        }
        dirlist.add(sdcard);
        dirlist.add(root);


        String[] fileparts = filename.split("/");
        for (File rootdir : dirlist) {
            String suffix = "";
            for (int i = fileparts.length - 1; i >= 0; i--) {
                if (i == fileparts.length - 1)
                    suffix = fileparts[i];
                else
                    suffix = fileparts[i] + "/" + suffix;

                File possibleFile = new File(rootdir, suffix);
                if (possibleFile.canRead())
                    return possibleFile;

            }
        }
        return null;
    }


    private String getUniqueProfileName(String possibleName) {

        int i = 0;

        ProfileManager vpl = ProfileManager.getInstance(this);

        String newname = possibleName;

        // 	Default to
        if (mResult.mName != null && !ConfigParser.CONVERTED_PROFILE.equals(mResult.mName))
            newname = mResult.mName;

        while (newname == null || vpl.getProfileByName(newname) != null) {
            i++;
            if (i == 1)
                newname = getString(R.string.converted_profile);
            else
                newname = getString(R.string.converted_profile_i, i);
        }
        return newname;
    }

    private int ijk = 0;
    private int failConnCount = 0;
    private boolean isStartConn = false;

    @Override
    public void updateState(String state, final String logmessage, final int localizedResId, VpnStatus.ConnectionStatus level) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (getString(localizedResId)) {
                    case "未运行":
                        //未运行
                        destroyTimer();
                        startHandler.sendEmptyMessage(3453);
                        isStartConn = false;
                        if (second == 0) {
                            second = 30;
                        }
                        Constant.firstConnTime = null;
                        progressbar.setVisibility(View.GONE);
                        vpnIsConn = false;
                        ijk++;
                        String link = util.sharedPreferencesReadData(MainActivity.this, KeyFile.CONN_DATA, "link");
                        if (!TextUtils.isEmpty(link)) {
                            userClickInVpn = true;
                            open(link);
                            util.sharedPreferencesDelByFileAllData(MainActivity.this, KeyFile.CONN_DATA);
                            return;
                        }
                        if (ijk > 1) {
                            failConnCount++;
                            if (failConnCount <= 1) {
                                if (vpnIsConn == false) {
                                }
                            } else {
                                if (isStartConn == true) {
                                    startHandler.sendEmptyMessage(12345);//隐藏
                                } else {
                                    if (userClickInVpn == true) {
                                        startHandler.sendEmptyMessage(12345);//隐藏
                                    } else {
                                        startHandler.sendEmptyMessage(1234);//显示
                                    }
                                }
                                blackback.setVisibility(View.GONE);
                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                    case "WaitingUserPermission":
                        //WaitingUserPermission
                        isStartConn = true;
                        vpnIsConn = false;
                        progressbar.setVisibility(View.GONE);
                        startHandler.sendEmptyMessage(12345);
                        vpnjk();
                        break;
                    case "正在生成配置":
                        //正在生成配置
                        isStartConn = true;
                        vpnIsConn = false;
                        progressbar.setVisibility(View.GONE);
                        startHandler.sendEmptyMessage(12345);
                        break;
                    case "等待可用网络":
                        //等待可用网络
                        isStartConn = true;
                        vpnIsConn = false;
                        progressbar.setVisibility(View.GONE);
                        startHandler.sendEmptyMessage(12345);
                        break;
                    case "等待服务器响应":
                        //等待服务器响应
                        isStartConn = true;
                        vpnIsConn = false;
                        progressbar.setVisibility(View.GONE);
                        startHandler.sendEmptyMessage(12345);
                        break;
                    case "验证中":
                        //验证中
                        isStartConn = true;
                        vpnIsConn = false;
                        progressbar.setVisibility(View.GONE);
                        startHandler.sendEmptyMessage(12345);
                        break;
                    case "正在获取客户端配置":
                        //正在获取客户端配置
                        isStartConn = true;
                        vpnIsConn = false;
                        progressbar.setVisibility(View.GONE);
                        startHandler.sendEmptyMessage(12345);
                        break;
                    case "正在分配IP地址":
                        //正在分配IP地址
                        isStartConn = true;
                        vpnIsConn = false;
                        progressbar.setVisibility(View.GONE);
                        startHandler.sendEmptyMessage(12345);
                        break;
                    case "添加路由":
                        //添加路由
                        isStartConn = true;
                        vpnIsConn = false;
                        progressbar.setVisibility(View.GONE);
                        startHandler.sendEmptyMessage(12345);
                        break;
                    case "已连接":
                        //已连接
                        if (isGoStart == true) {
                            initAlertConn("VPN 已连接", false);
                            startHandler.sendEmptyMessage(3453);
                        }
                        isGoStart = false;
                        isStartConn = false;
                        startHandler.sendEmptyMessage(12345);
                        vpnIsConn = true;//更改连接状态
                        failConnCount = 0;
                        blackback.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        progressbar.setVisibility(View.GONE);
                        tv_conn.setVisibility(View.GONE);
                        connHandler.sendEmptyMessage(1001);

                        vpnTimer();
                        break;
                }
            }
        });
    }

    private void vpnTimer() {
        String vipStatus = aesUtils.decrypt(util.sharedPreferencesReadData(MainActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status"));
        if (TextUtils.isEmpty(vipStatus)) {
            //无登陆；
            T.showCenterToast(MainActivity.this, "请到会员菜单登陆!!");
            stopVpnConnection();
        } else if (vipStatus.equals("2")) {
            //不是会员
            startVpnTimer();
        } else {
            tv_timer.setText("H 站 大 全");
        }
    }

    Handler connHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1001) {
                if (userClickInVpn) {
                    userClickInVpn = false;
                    if (TextUtils.isEmpty(indexHtmlStr)) {
                        return;
                    }
                    //util.starBrowser(indexHtmlStr, MainActivity.this);
                    startWeb(indexHtmlStr);
                }
            }
        }
    };


    private String indexHtmlStr;

    public void open(String indexHtml) {
        indexHtmlStr = indexHtml;
        Log.i("vpnstatus", "是否连接" + vpnIsConn);
        if (vpnIsConn) {
            userClickInVpn = false;
            if (TextUtils.isEmpty(indexHtmlStr)) {
                return;
            }
            //util.starBrowser(indexHtmlStr, this);
            startWeb(indexHtmlStr);
        } else {
            vipVpnStarConn();
        }
    }

    /**
     * 用户评论
     *
     * @param
     */
    public void userRequestMessage(String context) {
        if (!util.isNetworkConnected(this)) {
            t.show(this, "网络没有连接,请检查您的网络", 1000);
            return;
        }
        UserMessage info = new UserMessage();
        info.setNickName(util.getAndroidId(this));
        info.setContext(context);
        String json = JSON.toJSONString(info);
        String aesJson = aesUtils.encrypt(json);
        //发起请求
        RequestParams params = new RequestParams(Constant.USER_ADD_MESSAGE_INTERFACE);
        params.setCacheMaxAge(0);//最大数据缓存时间
        params.setConnectTimeout(5000);//连接超时时间
        params.setCharset("UTF-8");
        params.addQueryStringParameter("data", aesJson);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = aesUtils.decrypt(result);
                try {
                    JSONObject object = new JSONObject(json);
                    String resp = object.getString("respMsg");
                    if (resp.equals("success")) {
                        t.centershow(MainActivity.this, "亲~发表评论成功。", 500);
                    } else {
                        t.centershow(MainActivity.this, "发表评论失败了,暂不支持表情发送。", 500);
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


    public void startWeb(String weburl) {
        if (Constant.initWeb == true) {
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("weburl", weburl);
            startActivity(intent);
        } else {
            util.starBrowser(indexHtmlStr, this);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (listProp != null) {
            DownUpdate downUpdate = listProp.getListProp().get(0);
            newPackange(downUpdate.getPackName(), downUpdate.getVersion());
        }
        getApkData();
        getApkUpdate();
        //判断是否自动刷新数据
        String autoRefresh = util.sharedPreferencesReadData(this, KeyFile.AUTO_REFRESH_DATA, "autorefreshdata");
        if (!TextUtils.isEmpty(autoRefresh)) {
            //需要自动刷新
            util.sharedPreferencesDelByFileAllData(this, KeyFile.AUTO_REFRESH_DATA);
            //getNetData();
        }
        MobclickAgent.onPageStart("加速主界面"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        if (second == 0) {
            second = 30;
        }
        VpnStatus.addStateListener(this);
        Intent intent = new Intent(this, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        MobclickAgent.onResume(this);//友盟统计
    }

    @Override
    public void itemClick(View view, String link, String id) {
        MobclickAgent.onEvent(this, id);//评论埋点统计
        userClickInVpn = true;
        open(link);
    }

    @Override
    public void pagerClick(View view, String link, String id) {
        MobclickAgent.onEvent(this, id);//轮播埋点统计
        userClickInVpn = true;
        open(link);
    }

    @Override
    public void hotClick(View view, String link, String id) {
        MobclickAgent.onEvent(this, id);//热门埋点统计
        userClickInVpn = true;
        open(link);
    }

    @Override
    public void onPause() {
        super.onPause();
        destroyTimer();
        MobclickAgent.onPageEnd("加速主界面"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        VpnStatus.removeStateListener(this);
        unbindService(mConnection);
        MobclickAgent.onPause(this);//友盟统计
    }

    @Override
    public void onBackPressed() {
        ExitApp();
    }

    private long exitTime = 0;

    public void ExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 3000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
//            if (timer != null) {
//                timer.cancel();
//                timer = null;
//            }
//            if (timerTask != null) {
//                timerTask.cancel();
//                timerTask = null;
//            }
            ProfileManager.setConntectedVpnProfileDisconnected(MainActivity.this);
            if (mService != null && mService.getManagement() != null) {
                mService.getManagement().stopVPN(false);
                if (vpnIsConn) {
                    mService.vpnStateClose();//在服务中上报连接状态
                }
            }
            this.finish();
            System.exit(0);//正常退出App
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeUi();
    }

    public void closeUi() {
        second = -1;
        ProfileManager.setConntectedVpnProfileDisconnected(MainActivity.this);
        if (mService != null && mService.getManagement() != null) {
            mService.getManagement().stopVPN(false);
            if (vpnIsConn) {
                mService.vpnStateClose();//在服务中上报连接状态
            }
        }
        //销毁时候去绑定
        unbindService(mConnection);
        this.finish();
        System.exit(0);//正常退出App
    }

    private long timeSecond = 0;

    public String formatLongToTimeStr(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = l.intValue();
        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }

        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        String strtime = hour + ":" + minute + ":" + second + "";
        return strtime;
    }

    public void getConnTime(Date curDate, Date endDate) {
        if (curDate == null) {
            return;
        }
        long diff = endDate.getTime() - curDate.getTime();
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        if (days <= 0) {
            days = 0;
        }
        if (hours <= 0) {
            hours = 0;
        }
        if (minutes <= 0) {
            minutes = 0;
        }
        timeSecond = hours * 3600 + minutes * 60;
    }


    public void destroyTimer() {
        if (Constant.timerTask != null) {
            Constant.timerTask.cancel();
            Constant.timerTask = null;
        }
        if (Constant.timer != null) {
            Constant.timer.cancel();
            Constant.timer = null;
        }
    }

    private void startVpnTimer() {
        if (Constant.timerTask == null) {
            Constant.timerTask = new TimerTask() {
                @Override
                public void run() {
                    timeSecond = Integer.parseInt(Util.readFileToSDFile(VPN_SHIYONG_FILE));
                    timeSecond--;
                    if (timeSecond <= 0) {
                        Util.writeFileToSDFile(VPN_SHIYONG_FILE, String.valueOf(timeSecond));
                        mHandler.sendEmptyMessage(GO_VPN_TRY_OUT_TIME_END);
                    } else {
                        Util.writeFileToSDFile(VPN_SHIYONG_FILE, String.valueOf(timeSecond));
                        mHandler.sendEmptyMessage(GO_VPN_TRY_OUT_TIME_START);
                    }
                }
            };
            Constant.timer = new Timer();
            Constant.timer.schedule(Constant.timerTask, 0, 1000);
        }
    }

    //字体需要的设置
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}