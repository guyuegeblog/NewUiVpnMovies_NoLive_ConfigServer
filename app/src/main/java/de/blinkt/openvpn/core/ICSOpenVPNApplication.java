/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.core;

import android.app.Application;
import android.util.Log;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;

import org.xutils.x;

import de.blinkt.openvpn.BuildConfig;
import de.blinkt.openvpn.Constant.Constant;
import de.blinkt.openvpn.Entity.VideoListData;
import de.blinkt.openvpn.Entity.navigationList;
import de.blinkt.openvpn.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/*
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
*/

/*
@ReportsCrashes(
        formKey = "",
        formUri = "http://reports.blinkt.de/report-icsopenvpn",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        formUriBasicAuthLogin="report-icsopenvpn",
        formUriBasicAuthPassword="Tohd4neiF9Ai!!!!111eleven",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
*/
public class ICSOpenVPNApplication extends Application implements
        Thread.UncaughtExceptionHandler {
    public static navigationList navigationList;
    public static VideoListData videoListData;

    @Override
    public void onCreate() {
        super.onCreate();
        //首先初始化qb
        initWebBrowser();

        PRNGFixes.apply();
        if (BuildConfig.DEBUG) {
            //ACRA.init(this);
        }
        VpnStatus.initLogCache(getApplicationContext().getCacheDir());

        //字体文件配置
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/DroidSerif-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        x.Ext.init(this);
        x.Ext.setDebug(false); //是否输出debug日志，开启debug会影响性能。

        //设置Thread Exception Handler(这行代码造成了程序捕获app应用异常后，无法在studio logcat里准确输出提示开发者的错误日志)
        //Thread.setDefaultUncaughtExceptionHandler(this);

        // 不耗时，做一些简单初始化准备工作，不会启动下载进程
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
//        Log.i("uncaughtException","uncaughtException"+ex.getMessage());
//        System.exit(0);
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }

    public de.blinkt.openvpn.Entity.navigationList getNavigationList() {
        return navigationList;
    }

    public void setNavigationList(de.blinkt.openvpn.Entity.navigationList navigationList) {
        this.navigationList = navigationList;
    }

    public static VideoListData getVideoListData() {
        return videoListData;
    }

    public static void setVideoListData(VideoListData videoListData) {
        ICSOpenVPNApplication.videoListData = videoListData;
    }

    public void initWebBrowser() {
        //web
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
//        TbsDownloader.needDownload(this, false);

        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //Log.e("0828", " onViewInitFinished is " + arg0);
                Log.i("qbStr", "x5内核视图初始化完成boolean arg0===" + arg0);
                Constant.initWeb = arg0;
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
                Log.i("qbStr", "x5内核核心初始化完成");
            }
        };
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                Log.i("qbStr", "x5内核下载完成===" + i);
            }

            @Override
            public void onInstallFinish(int i) {
                Log.i("qbStr", "x5内核安装完成===" + i);
            }

            @Override
            public void onDownloadProgress(int i) {
                // Log.d("0828","onDownloadProgress:"+i);
                Log.i("qbStr", "x5内核下载中===" + i);
            }
        });
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

}
