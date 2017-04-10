package de.blinkt.openvpn.Service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Date;
import java.util.List;

import de.blinkt.openvpn.Constant.Constant;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.Save.KeyFile;
import de.blinkt.openvpn.Utils.AesUtils;
import de.blinkt.openvpn.Utils.Util;
import de.blinkt.openvpn.vpnmovies.vipvpn.wxapi.WXPayEntryActivity;

public class DialogService extends Service {
    //Android 中的定时任务一般有两种实现方式，一种是使用Java API 里提供的Timer 类，
    //一种是使用Android 的Alarm 机制。
    //这两种方式在多数情况下都能实现类似的效果，但Timer有一个明显的短板，它并不太适用于
    // 那些需要长期在后台运行的定时任务。
    // 我们都知道，为了能让电池更加耐用，每种手机都会有自己的休眠策略，Android 手机就会在长时间
    // 不操作的情况下自动让CPU 进入到睡眠状态，这就有可能导致Timer 中的定时任务无法正常运行。
    // 而Alarm 机制则不存在这种情况，它具有唤醒CPU 的功能，即可以保证每次需要执行定时任务的时候
    // CPU 都能正常工作。需要注意，这里唤醒CPU 和唤醒屏幕完全不是同一个概念，千万不要产生混淆。
    AlarmManager mAlarmManager = null;//不间断轮询服务
    PendingIntent mPendingIntent = null;
    private int dialogIjk = 0;
    private boolean isShowDialog = true;
    private Handler dialogHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            /***
             * 提示支付
             */
            if (msg.what == 5432532) {
                showThreeDialog();
            }
            if (msg.what == 533) {
                showScreenDialog();
            }
        }
    };

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
    private Util util = new Util(this);
    private AesUtils aesUtils = new AesUtils();

    @Override
    public void onCreate() {
        //start the service through alarm repeatly
        //http://blog.csdn.net/csd_xiaojin/article/details/50814234alerm机制详解
        Intent intent = new Intent(getApplicationContext(), DialogService.class);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mPendingIntent = PendingIntent.getService(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
        long now = System.currentTimeMillis();
        mAlarmManager.setInexactRepeating(AlarmManager.RTC, now, 600000, mPendingIntent);
             super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //如果是会员，则关闭服务
        String vipStatus = aesUtils.decrypt(sharedPreferencesReadData(DialogService.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "vip_status"));
        boolean isScreen = isScreenChange();
        boolean isQH = isBackground(this);
        if (isQH) {
            Log.i("screenStr", "手机是后台");
        } else {
            Log.i("screenStr", "手机是前台");
        }

        if (isScreen) {
            Log.i("screenStr", "手机是横屏");
        } else {
            Log.i("screenStr", "手机是竖屏");
        }

        if (TextUtils.isEmpty(vipStatus)) {
        } else {
            Log.i("vipstatusStr", vipStatus);
            if (vipStatus.equals("1")) {

            } else if (vipStatus.equals("2")) {
                //非会员
                if (isQH) {
                    Log.i("screenStr", "手机是后台");
                    if (dialogIjk <= 9999) {
                        long minutes = getConnTime("minutes");
                        if (minutes == -1) {
                        } else {
                            if (minutes >= 120) {
                                Log.i("dialogServiceStr", "发送弹窗消息了");
                                if (isShowDialog == true) {
                                    isShowDialog = false;
                                    boolean isScreenStr = isScreenChange();
                                    if (isScreenStr) {
                                        Log.i("screenStr", "手机是横屏");
                                        dialogHandler.sendEmptyMessage(533);
                                    } else {
                                        Log.i("screenStr", "手机是竖屏");
                                        dialogHandler.sendEmptyMessage(5432532);
                                    }
                                }
                            }
                        }
                    } else {
                        unbindService(conn);
                        Log.i("dialogServiceStr", "停止服务了");
                    }
                } else {
                    Log.i("screenStr", "手机是前台");
                    if (dialogIjk <= 9999) {
                        long minutes = getConnTime("minutes");
                        if (minutes == -1) {
                        } else {
                            if (minutes >= 120) {
                                Log.i("dialogServiceStr", "发送弹窗消息了");
                                if (isShowDialog == true) {
                                    isShowDialog = false;
                                    boolean isScreenStr = isScreenChange();
                                    if (isScreenStr) {
                                        Log.i("screenStr", "手机是横屏");
                                        dialogHandler.sendEmptyMessage(533);
                                    } else {
                                        Log.i("screenStr", "手机是竖屏");
                                        dialogHandler.sendEmptyMessage(5432532);
                                    }
                                }
                            }
                        }
                    } else {
                        unbindService(conn);
                        Log.i("dialogServiceStr", "停止服务了");
                    }
                }
            }
        }
        return START_STICKY;
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
				GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
				 */
                Log.i(context.getPackageName(), "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.getClass().getName());
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    Log.i(context.getPackageName(), "处于后台"
//                            + appProcess.processName);
                    return true;
                } else {
//                    Log.i(context.getPackageName(), "处于前台"
//                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    public boolean isScreenChange() {

        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向

        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {

            //横屏
            return true;
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {

            //竖屏
            return false;
        }
        return false;
    }


    /**
     * 获取数据
     */
    public String sharedPreferencesReadData(Context context, String filename, String key) {
        //实例化SharedPreferences对象
        SharedPreferences mySharePerferences = context.getSharedPreferences(filename, Activity.MODE_MULTI_PROCESS);

        //用getString获取值
        return mySharePerferences.getString(key, "");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public long getConnTime(String type) {
        long returnTime = -1;
        Date curDate = Constant.AppFirstTime;
        if (curDate == null) {
            Log.i("dialogServiceStr", "curDate是null");
            Constant.AppFirstTime = new Date();
            return -1;
        }
        Date endDate = new Date();
        long diff = endDate.getTime() - curDate.getTime();

        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        if (type.equals("hourse")) {//hourse
            returnTime = hours;
        } else if (type.equals("minutes")) {
            returnTime = minutes;
        }
        if (returnTime == -1) {
            Log.i("dialogServiceStr", "returnTime是-1");
        }
        return returnTime;
    }

    /***
     * 3次支付
     */
    private View view;

    private void showThreeDialog() {
        dialogIjk++;
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService("window");
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        //竖屏
        view = LayoutInflater.from(this).inflate(R.layout.dialog_pay,
                null);
        view.getBackground().setAlpha(150);
        ImageView btn_close = (ImageView) view.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                view.setVisibility(View.GONE);
                isShowDialog = true;
            }
        });

        ImageView pay1 = (ImageView) view.findViewById(R.id.pay1);
        pay1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(getApplicationContext(), WXPayEntryActivity.class);
                startActivity(intent);
                view.setVisibility(View.GONE);
                isShowDialog = true;
            }
        });

        ImageView pay2 = (ImageView) view.findViewById(R.id.pay2);
        pay2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(getApplicationContext(), WXPayEntryActivity.class);
                startActivity(intent);
                view.setVisibility(View.GONE);
                isShowDialog = true;
            }
        });
        ImageView pay3 = (ImageView) view.findViewById(R.id.pay3);
        pay3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(getApplicationContext(), WXPayEntryActivity.class);
                startActivity(intent);
                view.setVisibility(View.GONE);
                isShowDialog = true;
            }
        });

        /**
         *以下都是WindowManager.LayoutParams的相关属性
         * 具体用途请参考SDK文档
         */
        wmParams.type = 2002;   //这里是关键，你也可以试试2003
        wmParams.format = 1;
        /**
         *这里的flags也很关键
         *代码实际是wmParams.flags |= FLAG_NOT_FOCUSABLE;
         *40的由来是wmParams的默认属性（32）+ FLAG_NOT_FOCUSABLE（8）
         */
        wmParams.flags = 40;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        ;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        ;
        wm.addView(view, wmParams);  //创建View
        Constant.AppFirstTime = null;
    }

    private void showScreenDialog() {
        dialogIjk++;
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService("window");
        WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();
        //横屏
        view = LayoutInflater.from(this).inflate(R.layout.vipscreenforeign,
                null);
        view.getBackground().setAlpha(150);
        ImageView pay_close = (ImageView) view.findViewById(R.id.pay_close);
        pay_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                view.setVisibility(View.GONE);
                isShowDialog = true;
            }
        });

        FrameLayout layout_yin = (FrameLayout) view.findViewById(R.id.layout_yin);
        layout_yin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setClass(getApplicationContext(), WXPayEntryActivity.class);
                startActivity(intent);
                view.setVisibility(View.GONE);
                isShowDialog = true;
            }
        });
        /**
         *以下都是WindowManager.LayoutParams的相关属性
         * 具体用途请参考SDK文档
         */
        wmParams.type = 2002;   //这里是关键，你也可以试试2003
        wmParams.format = 1;
        /**
         *这里的flags也很关键
         *代码实际是wmParams.flags |= FLAG_NOT_FOCUSABLE;
         *40的由来是wmParams的默认属性（32）+ FLAG_NOT_FOCUSABLE（8）
         */
        wmParams.flags = 40;
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        ;
        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        ;
        wm.addView(view, wmParams);  //创建View
        Constant.AppFirstTime = null;
    }
}
