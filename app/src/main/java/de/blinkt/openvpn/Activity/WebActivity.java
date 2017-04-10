package de.blinkt.openvpn.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.SpinKitView;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.utils.TbsLog;
import com.umeng.analytics.MobclickAgent;

import java.util.Timer;
import java.util.TimerTask;

import de.blinkt.openvpn.Constant.Constant;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.Save.KeyFile;
import de.blinkt.openvpn.Utils.AesUtils;
import de.blinkt.openvpn.Utils.T;
import de.blinkt.openvpn.Utils.Util;
import de.blinkt.openvpn.Utils.X5WebView;
import de.blinkt.openvpn.View.SystemBarTintManager;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;

public class WebActivity extends Activity implements VpnStatus.StateListener {

    private ProgressBar progressbar_horient;
    private X5WebView mWebView;
    private ViewGroup mViewParent;
    private ImageView back, close;
    private static final String TAG = "str";
    private boolean mNeedTestPage = false;
    private ValueCallback<Uri> uploadFile;
    private String url = "http://porndoe.com/category/4/asian";
    private String webUrl;
    private SpinKitView spinkit_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                        android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
        }
        initStatus();
        setContentView(R.layout.activity_web);
        initView();
        this.webViewTransportTest();
        mTestHandler.sendEmptyMessageDelayed(MSG_INIT_UI, 10);
    }

    private void initStatus() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.webbg);//通知栏所需颜色
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
        spinkit_progress = (SpinKitView) findViewById(R.id.spinkit_progress);
        mViewParent = (ViewGroup) findViewById(R.id.webView1);
        back = (ImageView) findViewById(R.id.back);
        close = (ImageView) findViewById(R.id.close);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //设置加载模式
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                if (mWebView != null && mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    destroyTimer();
                    finish();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destroyTimer();
                finish();
            }
        });
    }

    private void webViewTransportTest() {
        X5WebView.setSmallWebViewEnabled(true);
    }


    private void initProgressBar() {
        progressbar_horient = (ProgressBar) findViewById(R.id.progressbar_horient);// new
    }


    private void init() {
        mWebView = new X5WebView(this);
        mViewParent.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));
        initProgressBar();
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //为了处理返回上一页和加载下一页，设置数据加载模式
                spinkit_progress.setVisibility(View.VISIBLE);
                mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            /**
             * 通过webview实现广告过滤
             * @param
             * @param request
             * @return
             * shouldInterceptRequest有两种重载。
             * public WebResourceResponse shouldInterceptRequest (WebView view, String url) 从API 11开始引入，API 21弃用
             * public WebResourceResponse shouldInterceptRequest (WebView view, WebResourceRequest request) 从API 21开始引入
             */
            @Override     //处理api 21+版本的广告过滤
            public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest request) {
                String url = webView.getUrl();
                url = url.toLowerCase();
                Log.i("weburk", "=======================" + url);
                return super.shouldInterceptRequest(webView, request);
            }

            @Override     //处理从api 11到api 21(不包含)的广告过滤
            public WebResourceResponse shouldInterceptRequest(WebView webView, String url) {
                url = url.toLowerCase();
                Log.i("weburk", "=======================" + url);
                return super.shouldInterceptRequest(webView, url);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // mTestHandler.sendEmptyMessage(MSG_OPEN_TEST_URL);
                mTestHandler.sendEmptyMessageDelayed(MSG_OPEN_TEST_URL, 5000);// 5s?
                spinkit_progress.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebViewClient.a a) {
                super.onReceivedError(webView, webResourceRequest, a);
                spinkit_progress.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView webView, int i, String s, String s1) {
                super.onReceivedError(webView, i, s, s1);
                spinkit_progress.setVisibility(View.GONE);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {

            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                progressbar_horient.setProgress(newProgress);
                if (progressbar_horient != null && newProgress != 100) {
                    progressbar_horient.setVisibility(View.VISIBLE);
                } else if (progressbar_horient != null) {
                    progressbar_horient.setVisibility(View.GONE);
                }
            }
        });

        mWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String arg0, String arg1, String arg2,
                                        String arg3, long arg4) {

            }
        });

        //web浏览器相关设置
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setJavaScriptEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        webSetting.setAppCacheEnabled(true);
        webSetting.setSupportZoom(true);  //支持缩放，默认为true。是下面那个的前提。
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);  //提高渲染的优先级
        webSetting.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
        webSetting.setLoadsImagesAutomatically(true);  //支持自动加载图片
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setPreFectch(true);
        loadUrl();
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }

    public void loadUrl() {
        try {
            webUrl = getIntent().getStringExtra("weburl");
            mWebView.loadUrl(webUrl);
        } catch (Exception e) {
            mWebView.loadUrl(url);
        }
    }


    boolean[] m_selected = new boolean[]{true, true, true, true, false,
            false, true};

    private enum TEST_ENUM_FONTSIZE {
        FONT_SIZE_SMALLEST, FONT_SIZE_SMALLER, FONT_SIZE_NORMAL, FONT_SIZE_LARGER, FONT_SIZE_LARGEST
    }

    ;

    private TEST_ENUM_FONTSIZE m_font_index = TEST_ENUM_FONTSIZE.FONT_SIZE_NORMAL;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //设置加载模式
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else {
                destroyTimer();
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TbsLog.d(TAG, "onActivityResult, requestCode:" + requestCode
                + ",resultCode:" + resultCode);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    if (null != uploadFile) {
                        Uri result = data == null || resultCode != RESULT_OK ? null
                                : data.getData();
                        uploadFile.onReceiveValue(result);
                        uploadFile = null;
                    }
                    break;
                case 1:

                    Uri uri = data.getData();
                    String path = uri.getPath();


                    break;
                default:
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (null != uploadFile) {
                uploadFile.onReceiveValue(null);
                uploadFile = null;
            }

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null || mWebView == null || intent.getData() == null)
            return;
        mWebView.loadUrl(intent.getData().toString());
    }

    public static final int MSG_ALERT_TIMER = 10;
    public static final int MSG_OPEN_TEST_URL = 0;
    public static final int MSG_INIT_UI = 1;
    private final int mUrlStartNum = 0;
    private int mCurrentUrl = mUrlStartNum;
    private Handler mTestHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OPEN_TEST_URL:
                    if (!mNeedTestPage) {
                        return;
                    }

                    String testUrl = "file:///sdcard/outputHtml/html/"
                            + Integer.toString(mCurrentUrl) + ".html";
                    if (mWebView != null) {
                        mWebView.loadUrl(testUrl);
                    }

                    mCurrentUrl++;
                    break;
                case MSG_INIT_UI:
                    init();
                    break;
                case MSG_ALERT_TIMER:
                    alertPay();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /***
     * 友盟统计
     */
    public void onResume() {
        super.onResume();
        VpnStatus.addStateListener(this);
        Intent intent = new Intent(this, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        destroyTimer();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        //销毁绑定的服务
        try {
            unbindService(mConnection);
            if (mWebView != null) {
                mViewParent.removeView(mWebView);
                mWebView.removeAllViews();
                mWebView.destroy();
                super.onDestroy();
            }
        }catch (Exception e){
        }
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

    private String VPN_SHIYONG_FILE = Constant.VPN_SHIYONG_DIRECTORY + "/" + Constant.VPN_SHIYONG_FILE;

    private void startVpnTimer() {
        if (Constant.timerTask == null) {
            Constant.timerTask = new TimerTask() {
                @Override
                public void run() {
                    int timeSecond = Integer.parseInt(Util.readFileToSDFile(VPN_SHIYONG_FILE));
                    timeSecond--;
                    if (timeSecond <= 0) {
                        stopVpnConnection();
                        Util.writeFileToSDFile(VPN_SHIYONG_FILE, String.valueOf(timeSecond));
                        mTestHandler.sendEmptyMessage(MSG_ALERT_TIMER);
                    } else {
                        Util.writeFileToSDFile(VPN_SHIYONG_FILE, String.valueOf(timeSecond));
                    }
                }
            };
            Constant.timer = new Timer();
            Constant.timer.schedule(Constant.timerTask, 0, 1000);
        }
    }

    /***
     * 断开VPN连接
     */
    protected OpenVPNService mService;

    public void stopVpnConnection() {
        ProfileManager.setConntectedVpnProfileDisconnected(WebActivity.this);
        if (mService != null && mService.getManagement() != null) {
            mService.getManagement().stopVPN(false);
        }
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

    @Override
    public void updateState(String state, String logmessage, final int localizedResId, VpnStatus.ConnectionStatus level) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (getString(localizedResId)) {
                    case "未运行":
                        //未运行
                        destroyTimer();
                        break;
                    case "已连接":
                        //已连接
                        vpnTimer();
                        break;
                }
            }
        });
    }

    private void vpnTimer() {
        String vipStatus = new AesUtils().decrypt(new Util(this).sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status"));
        if (TextUtils.isEmpty(vipStatus)) {
            //无登陆；
            T.showCenterToast(this, "请到会员菜单登陆!!");
            stopVpnConnection();
        } else if (vipStatus.equals("2")) {
            //不是会员
            startVpnTimer();
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
                    destroyTimer();
                    dialog1.dismiss();
                    WebActivity.this.finish();
                }
            });

            ImageView pay2 = (ImageView) layout.findViewById(R.id.pay2);
            pay2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转activity
                    destroyTimer();
                    dialog1.dismiss();
                    WebActivity.this.finish();
                }
            });

            ImageView pay3 = (ImageView) layout.findViewById(R.id.pay3);
            pay3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转activity
                    destroyTimer();
                    dialog1.dismiss();
                    WebActivity.this.finish();
                }
            });
        } else {
            dialog1.show();
        }
    }

}
