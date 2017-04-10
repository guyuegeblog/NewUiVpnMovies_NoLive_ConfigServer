package de.blinkt.openvpn.Utils;


import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

//Toast统一管理类
public class T {

    public T() {
    }
    public boolean isShow = true;

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public void show(Context context, CharSequence message, int duration) {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public void show(Context context, int message, int duration) {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    public void centershow(Context context, String message, int duration) {
        if (isShow) {
            Toast toast = Toast.makeText(context, message, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private static Toast centerToast = null;
    public static void showCenterToast(Activity activity, String msg) {
        if (centerToast == null) {
            centerToast = Toast.makeText(activity, msg, 50);
            centerToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            centerToast.setText(msg);
        }
        centerToast.show();
    }


}
