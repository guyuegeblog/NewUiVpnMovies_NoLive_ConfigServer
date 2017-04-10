package de.blinkt.openvpn.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
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

import de.blinkt.openvpn.Bean.login.LoginBean;
import de.blinkt.openvpn.Constant.Constant;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.Save.KeyFile;
import de.blinkt.openvpn.Utils.AesUtils;
import de.blinkt.openvpn.Utils.T;
import de.blinkt.openvpn.Utils.Util;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class UserUpdateActivity extends Activity {

    private ImageView back;
    private EditText et_up_username, et_pass, et_up_phone, et_up_pass;
    private Button update_button;
    private Util util;
    private T t;
    private AesUtils aesUtils;
    private String username;
    private String password;
    private String iMeilLastId;
    private String tel_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_update);
        initView();
        initData();
    }

    private void initData() {
        util = new Util(this);
        t = new T();
        aesUtils = new AesUtils();
        String phoneStr = util.sharedPreferencesReadData(UserUpdateActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "tel_phone");
        String usernameStr = util.sharedPreferencesReadData(UserUpdateActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "userName");
        String passWordStr = util.sharedPreferencesReadData(UserUpdateActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "passWord");
        final TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String phone = tm.getLine1Number();
        if (TextUtils.isEmpty(phone)) {
            phone = "";
        }
        if (TextUtils.isEmpty(phoneStr) || TextUtils.isEmpty(usernameStr) || TextUtils.isEmpty(passWordStr)) {
            //本地没有登陆的数据
            String username =util.getAndroidId(this);//用户名
            et_pass.setText("123456");
            et_up_username.setText(username);
            et_up_phone.setText(phone);
        } else {
            //本地有登陆的数据
            et_pass.setText(aesUtils.decrypt(passWordStr));
            et_up_username.setText(aesUtils.decrypt(usernameStr));
            et_up_phone.setText(phone);
        }
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        et_up_pass = (EditText) findViewById(R.id.et_up_pass);
        et_up_username = (EditText) findViewById(R.id.et_up_username);
        et_pass = (EditText) findViewById(R.id.et_pass);
        et_up_phone = (EditText) findViewById(R.id.et_up_phone);
        update_button = (Button) findViewById(R.id.update_button);
    }

    public void updateDB(View view) {
        final Dialog toast = createLoadingDialog(UserUpdateActivity.this, "账户修改中...请稍候");
        if (isEmty()) {
            //修改逻辑处理
            toast.show();
            if (!util.isNetworkConnected(this)) {
                toast.dismiss();
                t.show(this, "网络没有连接", 1000);
                return;
            }
            final LoginBean info = new LoginBean();
            info.setUserName(username);
            info.setPassWord(util.getMD5Str(password));
            iMeilLastId = username.substring(username.length() - 1);
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

            x.http().post(params, new Callback.CommonCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    String jsonStr = result.toString();
                    String aesJson = aesUtils.decrypt(jsonStr);
                    try {
                        JSONObject object = new JSONObject(aesJson);
                        String updateStatus = object.getString("respMsg");
                        if (updateStatus.equals("fail")) {
                            t.show(UserUpdateActivity.this, "用户信息修改失败", 2000);
                            toast.dismiss();
                            return;
                        } else if (updateStatus.equals("success")) {
                            //用户信息修改成功 逻辑处理
                            //重新写入昵称和密码(用户名imeiId无法改动)
                            //删除旧的数据
                            util.sharedPreferencesDelOrderData(UserUpdateActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "passWord");
                            util.sharedPreferencesDelOrderData(UserUpdateActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "show");
                            util.sharedPreferencesDelOrderData(UserUpdateActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "tel_phone");

                            //写入修改成功后的数据
                            util.sharedPreferencesWriteData(UserUpdateActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "passWord", aesUtils.getInstance().encrypt(password));
                            util.sharedPreferencesWriteData(UserUpdateActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "show", aesUtils.getInstance().encrypt(password));
                            util.sharedPreferencesWriteData(UserUpdateActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, "tel_phone", aesUtils.getInstance().encrypt(tel_phone));

                            util.sharedPreferencesWriteData(UserUpdateActivity.this, KeyFile.PASS_DATA, "pass", aesUtils.getInstance().encrypt(password));
                            t.centershow(UserUpdateActivity.this, "用户信息修改成功", 2000);
                            toast.dismiss();
                            //自动登录
                            //autoLogin();
                            util.sharedPreferencesDelByFileAllData(UserUpdateActivity.this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE);
                            Intent intent = new Intent(UserUpdateActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            t.show(UserUpdateActivity.this, "系统繁忙,请稍候再试", 1000);
                            toast.dismiss();
                        }
                    } catch (JSONException e) {
                        t.show(UserUpdateActivity.this, "系统繁忙,请稍候再试", 3000);
                        toast.dismiss();
                    }
                }
                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    t.show(UserUpdateActivity.this, "连接超时", 1000);
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


    /**
     * 对修改非空判断
     */
//    et_pass.setText("123456");
//    et_up_username.setText(username);
//    et_up_phone.setText(tel_phone);
    public boolean isEmty() {
        boolean flag = false;
        username = et_up_username.getText().toString();
        password = et_up_pass.getText().toString();
        tel_phone = et_up_phone.getText().toString();
        if (TextUtils.isEmpty(username)) {
            flag = false;
            t.show(this, "请输入用户名", 1000);
        }
        if (TextUtils.isEmpty(password)) {
            flag = false;
            t.show(this, "请输入密码", 1000);
        }

        if (TextUtils.isEmpty(tel_phone)) {
            flag = false;
            t.show(this, "请输入手机号码", 1000);
        }

        if (!TextUtils.isEmpty(tel_phone) && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
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
        super.onBackPressed();
        this.finish();
    }

    /***
     * 友盟统计
     */
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("用户信息修改界面"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("用户信息修改界面"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    //字体需要的设置
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
