package de.blinkt.openvpn.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.SpinKitView;
import com.umeng.analytics.MobclickAgent;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.blinkt.openvpn.Adapter.SNavagationAdapter;
import de.blinkt.openvpn.Constant.Constant;
import de.blinkt.openvpn.Entity.NavigationInfo;
import de.blinkt.openvpn.Entity.navigationList;
import de.blinkt.openvpn.Interface.navigationInterface;
import de.blinkt.openvpn.R;
import de.blinkt.openvpn.Save.KeyFile;
import de.blinkt.openvpn.Utils.AesUtils;
import de.blinkt.openvpn.Utils.T;
import de.blinkt.openvpn.Utils.Util;
import de.blinkt.openvpn.View.GlideRoundTransform;
import de.blinkt.openvpn.View.SelfGridView;
import de.blinkt.openvpn.View.SystemBarTintManager;
import de.blinkt.openvpn.core.ICSOpenVPNApplication;
import de.blinkt.openvpn.vpnmovies.vipvpn.wxapi.WXPayEntryActivity;

public class NavigationActivity extends Activity implements navigationInterface {

    private TextView index, navigation, uservip;
    private ImageView user_lianxi, fankui;
    private RecyclerView navi_recyclerview;
    private Util util = new Util(this);
    private T t = new T();
    private AesUtils aesUtils = new AesUtils();
    private RvAdapter adapter = new RvAdapter();
    private SpinKitView skit;
    private Map<Integer, Object> allDataMap = new HashMap<>();
    List<NavigationInfo> list_Haiwai = new ArrayList<>();
    List<NavigationInfo> list_Hot = new ArrayList<>();
    List<NavigationInfo> list_Jarpanse = new ArrayList<>();
    List<NavigationInfo> list_China = new ArrayList<>();
    List<NavigationInfo> list_Korea = new ArrayList<>();
    List<NavigationInfo> list_TonXin = new ArrayList<>();
    List<NavigationInfo> list_Europe = new ArrayList<>();

    private navigationList banner;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager mLinearLayoutManager;
    private int lastVisibleItemPosition;
    private boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStatus();
        setContentView(R.layout.activity_navigation);
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
        navi_recyclerview = (RecyclerView) findViewById(R.id.navi_recyclerview);
        skit = (SpinKitView) findViewById(R.id.skit);
        index = (TextView) findViewById(R.id.index);
        navigation = (TextView) findViewById(R.id.navigation);
        uservip = (TextView) findViewById(R.id.uservip);
        user_lianxi = (ImageView) findViewById(R.id.user_lianxi);
        fankui = (ImageView) findViewById(R.id.fankui);
        index.setBackgroundResource(R.mipmap.jiasu);

        navigation.setBackgroundResource(R.mipmap.daohang_select);

        uservip.setBackgroundResource(R.mipmap.huiyuan);

        index.setOnClickListener(connClick);
        uservip.setOnClickListener(userClick);
        user_lianxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                util.alertKeFu(NavigationActivity.this);
            }
        });
        fankui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                util.alertZhiNan(NavigationActivity.this);
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_navi);
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
                pageIndex = 1;
                skit.setVisibility(View.VISIBLE);
                isLoading = true;
                getNetData(1);
                alertMore();
            }
        });
        mLinearLayoutManager = new LinearLayoutManager(this);
        navi_recyclerview.setHasFixedSize(true);
        navi_recyclerview.setLayoutManager(mLinearLayoutManager);
        navi_recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("test", "StateChanged = " + newState);
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    Log.d("test", "loading executed");
                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        return;
                    }
                    if (isLoading) {
                        isLoading = false;
                        //加载更多....
                        pageIndex++;
                        if (pageIndex == 2) {
                            allDataMap.put(1, list_Jarpanse);
                        } else if (pageIndex == 3) {
                            allDataMap.put(2, list_Korea);
                        } else if (pageIndex == 4) {
                            allDataMap.put(3, list_TonXin);
                        } else if (pageIndex == 5) {
                            allDataMap.put(4, list_Europe);
                        } else if (pageIndex == 6) {
                            allDataMap.put(5, list_Haiwai);
                        } else if (pageIndex == 7) {
                            allDataMap.put(6, list_Hot);
                        } else {
                            failIjk = 0;
                            swipeRefreshLayout.setRefreshing(false);
                            adapter.notifyItemRemoved(adapter.getItemCount());
                            skit.setVisibility(View.GONE);
                            isLoading = false;
                            alertMore();
                            return;
                        }
                        adapterData();
                    } else {
                        alertMore();
                        swipeRefreshLayout.setRefreshing(false);
                        adapter.notifyItemRemoved(adapter.getItemCount());
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("tesonScrolledt", "onScrolled" + mLinearLayoutManager.findLastVisibleItemPosition());
                lastVisibleItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
            }
        });
        navi_recyclerview.setAdapter(adapter);
    }

    private void initData() {
        if (ICSOpenVPNApplication.navigationList == null) {
            getNetData(1);
        } else {
            clearData();
            banner = ICSOpenVPNApplication.navigationList;
            for (int i = 0; i < banner.getListJson().size(); i++) {
                if (banner.getListJson().get(i).getType().equals("1")) {
                    list_Haiwai.add(banner.getListJson().get(i));
                } else if (banner.getListJson().get(i).getType().equals("2")) {
                    list_Hot.add(banner.getListJson().get(i));
                } else if (banner.getListJson().get(i).getType().equals("3")) {
                    list_Jarpanse.add(banner.getListJson().get(i));
                } else if (banner.getListJson().get(i).getType().equals("4")) {
                    list_China.add(banner.getListJson().get(i));
                } else if (banner.getListJson().get(i).getType().equals("5")) {
                    list_Korea.add(banner.getListJson().get(i));
                } else if (banner.getListJson().get(i).getType().equals("6")) {
                    list_TonXin.add(banner.getListJson().get(i));
                } else if (banner.getListJson().get(i).getType().equals("7")) {
                    list_Europe.add(banner.getListJson().get(i));
                }
            }
            allDataMap.clear();
            allDataMap.put(0, list_China);
            ICSOpenVPNApplication.navigationList = banner;
            adapterData();
        }
        util.getAllBrowserInfo(this);
    }

    private void clearData() {
        list_Haiwai.clear();
        list_Hot.clear();
        list_Jarpanse.clear();
        list_China.clear();
        list_Korea.clear();
        list_TonXin.clear();
        list_Europe.clear();
    }

    Dialog dialog_more;

    public void alertMore() {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_more, null);
        //对话框
        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
        if (dialog_more == null) {
            dialog_more = new Dialog(this, R.style.Dialog);
            dialog_more.show();
            Window window = dialog_more.getWindow();
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
                    dialog_more.dismiss();
                }
            });
            ImageView pay1 = (ImageView) layout.findViewById(R.id.pay1);
            pay1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转activity
                    Intent intent = new Intent(NavigationActivity.this, WXPayEntryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                    dialog_more.dismiss();
                }
            });

            ImageView pay2 = (ImageView) layout.findViewById(R.id.pay2);
            pay2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转activity
                    Intent intent = new Intent(NavigationActivity.this, WXPayEntryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                    dialog_more.dismiss();
                }
            });

            ImageView pay3 = (ImageView) layout.findViewById(R.id.pay3);
            pay3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转activity
                    Intent intent = new Intent(NavigationActivity.this, WXPayEntryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                    dialog_more.dismiss();
                }
            });
        } else {
            dialog_more.show();
        }
    }

    /**
     * 获取导航数据
     *
     * @param
     * @param
     * @param
     */
    private int failIjk = 0;
    private int pageIndex = 1;

    public void getNetData(int pageIndex) {
        skit.setVisibility(View.VISIBLE);
        if (util.isNetworkConnected(this)) {
            //网络已连接
        } else {
            t.centershow(this, "世界上最远的距离就是没网", 500);
            swipeRefreshLayout.setRefreshing(false);
            adapter.notifyItemRemoved(adapter.getItemCount());
            isLoading = true;
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
                try {
                    String json = result.toString();
                    String aesJson = aesUtils.decrypt(json);
                    navigationList banner = JSON.parseObject(aesJson, navigationList.class);
                    clearData();
                    for (int i = 0; i < banner.getListJson().size(); i++) {
                        if (banner.getListJson().get(i).getType().equals("1")) {
                            list_Haiwai.add(banner.getListJson().get(i));
                        } else if (banner.getListJson().get(i).getType().equals("2")) {
                            list_Hot.add(banner.getListJson().get(i));
                        } else if (banner.getListJson().get(i).getType().equals("3")) {
                            list_Jarpanse.add(banner.getListJson().get(i));
                        } else if (banner.getListJson().get(i).getType().equals("4")) {
                            list_China.add(banner.getListJson().get(i));
                        } else if (banner.getListJson().get(i).getType().equals("5")) {
                            list_Korea.add(banner.getListJson().get(i));
                        } else if (banner.getListJson().get(i).getType().equals("6")) {
                            list_TonXin.add(banner.getListJson().get(i));
                        } else if (banner.getListJson().get(i).getType().equals("7")) {
                            list_Europe.add(banner.getListJson().get(i));
                        }
                    }
                    if (banner.getListJson() == null) {
                        failIjk++;
                        if (failIjk <= 1) {
                            getNetData(1);
                        } else {
                            isLoading = true;
                            failIjk = 0;
                            t.centershow(NavigationActivity.this, "没有获取到数据,重新加载试试", 50);
                        }
                        return;
                    }

                    if (banner.getListJson().size() == 0) {
                        failIjk++;
                        if (failIjk <= 1) {
                            getNetData(1);
                        } else {
                            isLoading = true;
                            failIjk = 0;
                            t.centershow(NavigationActivity.this, "没有获取到数据,刷新试试", 500);
                        }
                        return;
                    }
                    allDataMap.clear();
                    allDataMap.put(0, list_China);
                    ICSOpenVPNApplication.navigationList = banner;
                    adapterData();
                    swipeRefreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    t.show(NavigationActivity.this, "数据格式异常", 500);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("indexStr", "获取导航数据失败");
                swipeRefreshLayout.setRefreshing(false);
                failIjk++;
                if (failIjk <= 1) {
                    getNetData(1);
                } else {
                    failIjk = 0;
                    skit.setVisibility(View.GONE);
                    t.centershow(NavigationActivity.this, "没有获取到数据,刷新试试", 500);
                }
                isLoading = true;
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void adapterData() {
        failIjk = 0;
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyItemRemoved(adapter.getItemCount());
        skit.setVisibility(View.GONE);
        isLoading = true;
    }

    /**
     * tab选中事件
     */
    View.OnClickListener connClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //跳转activity
            Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade, R.anim.hold);
        }
    };

    View.OnClickListener userClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //跳转activity
            Intent intent = new Intent(NavigationActivity.this, WXPayEntryActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade, R.anim.hold);
        }
    };

    @Override
    public void naviClick(View view, NavigationInfo NavigationInfo) {
        //友盟统计
        MobclickAgent.onEvent(this, NavigationInfo.getUid());//评论埋点统计
        if (MainActivity.vpnIsConn) {
            //util.starBrowser(NavigationInfo.getWebUrl(), NavigationActivity.this);
            startWeb(NavigationInfo.getWebUrl());
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            util.sharedPreferencesWriteData(this, KeyFile.CONN_DATA, "link", NavigationInfo.getWebUrl());
            startActivity(intent);
        }
    }

    public void startWeb(String weburl) {
        if (Constant.initWeb == true) {
            Intent intent = new Intent(this, WebActivity.class);
            intent.putExtra("weburl", weburl);
            startActivity(intent);
        } else {
            util.starBrowser(weburl, this);
        }
    }

    /**
     * recyclerView适配器
     */

    class RvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int TYPE_HaiWai = 1;//海外精选
        private static final int TYPE_Hot = 2;//热门推荐
        private static final int TYPE_Janpanse = 3;//岛国大片
        private static final int TYPE_China = 4;//大陆自拍
        private static final int TYPE_Korea = 5;//韩国诱惑
        private static final int TYPE_TonXin = 6;//tonxin
        private static final int TYPE_Europe = 7;//欧美精选
        private static final int TYPE_LoadMore = 8;//加载更多

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
                case TYPE_HaiWai:
                    return onCreateHeaderViewHolder(parent, viewType);
                case TYPE_Hot:
                    return onCreateFooterViewHolder(parent, viewType);
                case TYPE_Janpanse:
                    return onCreateJarpanseViewHolder(parent, viewType);
                case TYPE_China:
                    return onCreateChinaViewHolder(parent, viewType);
                case TYPE_Korea:
                    return onCreateKoreaViewHolder(parent, viewType);
                case TYPE_TonXin:
                    return onCreateTonXinViewHolder(parent, viewType);
                case TYPE_Europe:
                    return onCreateEuropeViewHolder(parent, viewType);
                case TYPE_LoadMore:
                    return onCreateLoadMoreViewHolder(parent, viewType);
            }
            return onCreateLoadMoreViewHolder(parent, viewType);
        }


        /**
         * 1海外精选
         *
         * @param parent
         * @param viewType
         * @return
         */
        private RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(NavigationActivity.this).inflate(R.layout.navi_haiwai_jingxuan, parent, false);
            HeaderViewHolder holder = new HeaderViewHolder(view);
            return holder;
        }

        public class HeaderViewHolder extends RecyclerView.ViewHolder {
            public SelfGridView haiwai_jingxuan_gv;

            public HeaderViewHolder(View itemView) {
                super(itemView);
                haiwai_jingxuan_gv = (SelfGridView) itemView.findViewById(R.id.haiwai_jingxuan_gv);
            }
        }

        /***
         * 2热门推荐
         * **
         */
        private RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(NavigationActivity.this).inflate(R.layout.navi_hot_recommend, parent, false);
            FooterViewHolder holder = new FooterViewHolder(view);
            return holder;
        }

        public class FooterViewHolder extends RecyclerView.ViewHolder {

            public ImageView left_shang;
            public ImageView left_xia;
            public ImageView right_shang;
            public ImageView right_zhong;
            public ImageView right_xia;

            public FooterViewHolder(View itemView) {
                super(itemView);
                left_shang = (ImageView) itemView.findViewById(R.id.left_shang);
                left_xia = (ImageView) itemView.findViewById(R.id.left_xia);
                right_shang = (ImageView) itemView.findViewById(R.id.right_shang);
                right_zhong = (ImageView) itemView.findViewById(R.id.right_zhong);
                right_xia = (ImageView) itemView.findViewById(R.id.right_xia);
            }
        }

        /**
         * 3岛国大片
         *
         * @param parent
         * @param viewType
         * @return
         */
        private RecyclerView.ViewHolder onCreateJarpanseViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(NavigationActivity.this).inflate(R.layout.navi_jarpanse, parent, false);
            JarpanseViewHolder holder = new JarpanseViewHolder(view);
            return holder;
        }

        public class JarpanseViewHolder extends RecyclerView.ViewHolder {
            public SelfGridView jarpanse_gv;

            public JarpanseViewHolder(View itemView) {
                super(itemView);
                jarpanse_gv = (SelfGridView) itemView.findViewById(R.id.jarpanse_gv);
            }
        }

        /***
         * 4大陆自拍
         * **
         */
        private RecyclerView.ViewHolder onCreateChinaViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(NavigationActivity.this).inflate(R.layout.navi_china, parent, false);
            ChinaViewHolder holder = new ChinaViewHolder(view);
            return holder;
        }

        public class ChinaViewHolder extends RecyclerView.ViewHolder {

            public ImageView left_shang1;
            public ImageView left_xia2;
            public ImageView right_shang3;
            public ImageView right_zhong4;
            public ImageView right_xia5;

            public ImageView left_shang6;
            public ImageView left_xia7;
            public ImageView right_shang8;
            public ImageView right_zhong9;
            public ImageView right_xia10;

            public ChinaViewHolder(View itemView) {
                super(itemView);
                left_shang1 = (ImageView) itemView.findViewById(R.id.left_shang1);
                left_xia2 = (ImageView) itemView.findViewById(R.id.left_xia2);
                right_shang3 = (ImageView) itemView.findViewById(R.id.right_shang3);
                right_zhong4 = (ImageView) itemView.findViewById(R.id.right_zhong4);
                right_xia5 = (ImageView) itemView.findViewById(R.id.right_xia5);

                left_shang6 = (ImageView) itemView.findViewById(R.id.left_shang6);
                left_xia7 = (ImageView) itemView.findViewById(R.id.left_xia7);
                right_shang8 = (ImageView) itemView.findViewById(R.id.right_shang8);
                right_zhong9 = (ImageView) itemView.findViewById(R.id.right_zhong9);
                right_xia10 = (ImageView) itemView.findViewById(R.id.right_xia10);
            }
        }

        /**
         * 5韩国诱惑
         *
         * @param parent
         * @param viewType
         * @return
         */
        private RecyclerView.ViewHolder onCreateKoreaViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(NavigationActivity.this).inflate(R.layout.navi_korea, parent, false);
            KoreaViewHolder holder = new KoreaViewHolder(view);
            return holder;
        }

        public class KoreaViewHolder extends RecyclerView.ViewHolder {
            public SelfGridView korea_gv;

            public KoreaViewHolder(View itemView) {
                super(itemView);
                korea_gv = (SelfGridView) itemView.findViewById(R.id.korea_gv);
            }
        }

        /***
         * 6tongxin
         * **
         */
        private RecyclerView.ViewHolder onCreateTonXinViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(NavigationActivity.this).inflate(R.layout.navi_tongxin, parent, false);
            TonXinViewHolder holder = new TonXinViewHolder(view);
            return holder;
        }

        public class TonXinViewHolder extends RecyclerView.ViewHolder {

            public ImageView left_shang1;
            public ImageView left_xia2;
            public ImageView right_shang3;
            public ImageView right_zhong4;
            public ImageView right_xia5;

            public ImageView left_shang6;
            public ImageView left_xia7;
            public ImageView right_shang8;
            public ImageView right_zhong9;
            public ImageView right_xia10;

            public TonXinViewHolder(View itemView) {
                super(itemView);
                left_shang1 = (ImageView) itemView.findViewById(R.id.left_shang1);
                left_xia2 = (ImageView) itemView.findViewById(R.id.left_xia2);
                right_shang3 = (ImageView) itemView.findViewById(R.id.right_shang3);
                right_zhong4 = (ImageView) itemView.findViewById(R.id.right_zhong4);
                right_xia5 = (ImageView) itemView.findViewById(R.id.right_xia5);

                left_shang6 = (ImageView) itemView.findViewById(R.id.left_shang6);
                left_xia7 = (ImageView) itemView.findViewById(R.id.left_xia7);
                right_shang8 = (ImageView) itemView.findViewById(R.id.right_shang8);
                right_zhong9 = (ImageView) itemView.findViewById(R.id.right_zhong9);
                right_xia10 = (ImageView) itemView.findViewById(R.id.right_xia10);
            }
        }

        /**
         * 7欧美
         *
         * @param parent
         * @param viewType
         * @return
         */
        private RecyclerView.ViewHolder onCreateEuropeViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(NavigationActivity.this).inflate(R.layout.navi_oumei, parent, false);
            EuropeViewHolder holder = new EuropeViewHolder(view);
            return holder;
        }

        public class EuropeViewHolder extends RecyclerView.ViewHolder {
            public SelfGridView oumei_gv;

            public EuropeViewHolder(View itemView) {
                super(itemView);
                oumei_gv = (SelfGridView) itemView.findViewById(R.id.oumei_gv);
            }
        }

        /**
         * 8加载更多
         *
         * @param parent
         * @param viewType
         * @return
         */
        private RecyclerView.ViewHolder onCreateLoadMoreViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(NavigationActivity.this).inflate(R.layout.navi_loadmore, parent, false);
            LoadMoreViewHolder holder = new LoadMoreViewHolder(view);
            return holder;
        }

        public class LoadMoreViewHolder extends RecyclerView.ViewHolder {

            public LoadMoreViewHolder(View itemView) {
                super(itemView);
            }
        }


        //绑定视图
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (getItemViewType(position) == TYPE_HaiWai) {
                onBindHeaderViewHolder((HeaderViewHolder) holder, position);
            } else if (getItemViewType(position) == TYPE_Hot) {
                onBindFooterViewholder((FooterViewHolder) holder, position);
            } else if (getItemViewType(position) == TYPE_Janpanse) {
                onBindJarpansViewHolder((JarpanseViewHolder) holder, position);
            } else if (getItemViewType(position) == TYPE_China) {
                onBindChinaViewholder((ChinaViewHolder) holder, position);
            } else if (getItemViewType(position) == TYPE_Korea) {
                onBindKoreaViewHolder((KoreaViewHolder) holder, position);
            } else if (getItemViewType(position) == TYPE_TonXin) {
                onBindTongXinViewholder((TonXinViewHolder) holder, position);
            } else if (getItemViewType(position) == TYPE_Europe) {
                onBindEuropeViewHolder((EuropeViewHolder) holder, position);
            }
        }


        //数据长度
        @Override
        public int getItemCount() {
            return allDataMap.size() == 0 ? 0 : allDataMap.size() + 1;
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
            Log.i("1113434", position + "");
            if (allDataMap.size() == 1) {
                if (position == 0) {
                    return TYPE_China;
                } else if (position + 1 == getItemCount()) {
                    return TYPE_LoadMore;
                }
            } else if (allDataMap.size() == 2) {
                if (position == 0) {
                    return TYPE_China;
                } else if (position == 1) {
                    return TYPE_Janpanse;
                } else if (position + 1 == getItemCount()) {
                    return TYPE_LoadMore;
                }
            } else if (allDataMap.size() == 3) {
                if (position == 0) {
                    return TYPE_China;
                } else if (position == 1) {
                    return TYPE_Janpanse;
                } else if (position == 2) {
                    return TYPE_Korea;
                } else if (position + 1 == getItemCount()) {
                    return TYPE_LoadMore;
                }
            } else if (allDataMap.size() == 4) {
                if (position == 0) {
                    return TYPE_China;
                } else if (position == 1) {
                    return TYPE_Janpanse;
                } else if (position == 2) {
                    return TYPE_Korea;
                } else if (position == 3) {
                    return TYPE_TonXin;
                } else if (position + 1 == getItemCount()) {
                    return TYPE_LoadMore;
                }
            } else if (allDataMap.size() == 5) {
                if (position == 0) {
                    return TYPE_China;
                } else if (position == 1) {
                    return TYPE_Janpanse;
                } else if (position == 2) {
                    return TYPE_Korea;
                } else if (position == 3) {
                    return TYPE_TonXin;
                } else if (position == 4) {
                    return TYPE_Europe;
                } else if (position + 1 == getItemCount()) {
                    return TYPE_LoadMore;
                }
            } else if (allDataMap.size() == 6) {
                if (position == 0) {
                    return TYPE_China;
                } else if (position == 1) {
                    return TYPE_Janpanse;
                } else if (position == 2) {
                    return TYPE_Korea;
                } else if (position == 3) {
                    return TYPE_TonXin;
                } else if (position == 4) {
                    return TYPE_Europe;
                } else if (position == 5) {
                    return TYPE_HaiWai;
                } else if (position + 1 == getItemCount()) {
                    return TYPE_LoadMore;
                }
            } else if (allDataMap.size() == 7) {
                if (position == 0) {
                    return TYPE_China;
                } else if (position == 1) {
                    return TYPE_Janpanse;
                } else if (position == 2) {
                    return TYPE_Korea;
                } else if (position == 3) {
                    return TYPE_TonXin;
                } else if (position == 4) {
                    return TYPE_Europe;
                } else if (position == 5) {
                    return TYPE_HaiWai;
                } else if (position == 6) {
                    return TYPE_Hot;
                } else if (position + 1 == getItemCount()) {
                    return TYPE_LoadMore;
                }
            }
            return TYPE_LoadMore;
        }
    }

    /**
     * 海外
     *
     * @param holder
     * @param position
     */
    private SNavagationAdapter mzAdapter;

    private void onBindHeaderViewHolder(final RvAdapter.HeaderViewHolder holder, int position) {
        List<NavigationInfo> mzList = (List<NavigationInfo>) allDataMap.get(5);
        if (mzAdapter == null) {
            mzAdapter = new SNavagationAdapter(this);
            mzAdapter.setNavigationInterface(this);
            mzAdapter.setList(mzList);
            holder.haiwai_jingxuan_gv.setAdapter(mzAdapter);
            return;
        }
        mzAdapter.setList(mzList);
        mzAdapter.notifyDataSetChanged();
    }


    /**
     * 热门推荐
     *
     * @param holder
     * @param position
     */
    private navigationInterface navigationInterface;

    private void onBindFooterViewholder(RvAdapter.FooterViewHolder holder, int position) {
        final List<NavigationInfo> hotList = (List<NavigationInfo>) allDataMap.get(6);
        if (holder == null) {
            return;
        }
        navigationInterface = this;
        Glide.with(this).load(hotList.get(0).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.left_shang);

        Glide.with(this).load(hotList.get(1).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.left_xia);

        Glide.with(this).load(hotList.get(2).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.right_shang);

        Glide.with(this).load(hotList.get(3).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.right_zhong);

        Glide.with(this).load(hotList.get(4).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.right_xia);

        holder.left_shang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(0));
            }
        });
        holder.left_xia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(1));
            }
        });
        holder.right_shang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(2));
            }
        });
        holder.right_zhong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(3));
            }
        });
        holder.right_xia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(4));
            }
        });
    }

    /**
     * 岛国大片
     *
     * @param holder
     * @param position
     */
    private SNavagationAdapter jarpanseAdapter;

    private void onBindJarpansViewHolder(final RvAdapter.JarpanseViewHolder holder, int position) {
        List<NavigationInfo> mzList = (List<NavigationInfo>) allDataMap.get(1);
        if (jarpanseAdapter == null) {
            jarpanseAdapter = new SNavagationAdapter(this);
            jarpanseAdapter.setNavigationInterface(this);
            jarpanseAdapter.setList(mzList);
            holder.jarpanse_gv.setAdapter(jarpanseAdapter);
            return;
        }
        jarpanseAdapter.setList(mzList);
        jarpanseAdapter.notifyDataSetChanged();
    }

    /**
     * 大陆自拍
     *
     * @param holder
     * @param position
     */
    private void onBindChinaViewholder(RvAdapter.ChinaViewHolder holder, int position) {
        final List<NavigationInfo> hotList = (List<NavigationInfo>) allDataMap.get(0);
        navigationInterface = this;
        Glide.with(this).load(hotList.get(0).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.left_shang1);

        Glide.with(this).load(hotList.get(1).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.left_xia2);

        Glide.with(this).load(hotList.get(2).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.right_shang3);

        Glide.with(this).load(hotList.get(3).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.right_zhong4);

        Glide.with(this).load(hotList.get(4).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.right_xia5);


        Glide.with(this).load(hotList.get(5).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.left_shang6);

        Glide.with(this).load(hotList.get(6).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.left_xia7);

        Glide.with(this).load(hotList.get(7).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.right_shang8);

        Glide.with(this).load(hotList.get(8).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.right_zhong9);

        Glide.with(this).load(hotList.get(9).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.right_xia10);


        holder.left_shang1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(0));
            }
        });
        holder.left_xia2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(1));
            }
        });
        holder.right_shang3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(2));
            }
        });
        holder.right_zhong4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(3));
            }
        });
        holder.right_xia5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(4));
            }
        });

        holder.left_shang6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(5));
            }
        });
        holder.left_xia7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(6));
            }
        });
        holder.right_shang8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(7));
            }
        });
        holder.right_zhong9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(8));
            }
        });
        holder.right_xia10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(9));
            }
        });
    }

    /**
     * 韩国诱惑
     *
     * @param holder
     * @param position
     */
    private SNavagationAdapter koreaAdapter;

    private void onBindKoreaViewHolder(final RvAdapter.KoreaViewHolder holder, int position) {
        List<NavigationInfo> mzList = (List<NavigationInfo>) allDataMap.get(2);
        if (koreaAdapter == null) {
            koreaAdapter = new SNavagationAdapter(this);
            koreaAdapter.setNavigationInterface(this);
            koreaAdapter.setList(mzList);
            holder.korea_gv.setAdapter(koreaAdapter);
            return;
        }
        koreaAdapter.setList(mzList);
        koreaAdapter.notifyDataSetChanged();
    }


    /**
     * tongxin
     *
     * @param holder
     * @param position
     */
    private void onBindTongXinViewholder(RvAdapter.TonXinViewHolder holder, int position) {
        final List<NavigationInfo> hotList = (List<NavigationInfo>) allDataMap.get(3);
        navigationInterface = this;
        Glide.with(this).load(hotList.get(0).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.left_shang1);

        Glide.with(this).load(hotList.get(1).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.left_xia2);

        Glide.with(this).load(hotList.get(2).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.right_shang3);

        Glide.with(this).load(hotList.get(3).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.right_zhong4);

        Glide.with(this).load(hotList.get(4).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.right_xia5);


        Glide.with(this).load(hotList.get(5).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.left_shang6);

        Glide.with(this).load(hotList.get(6).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.left_xia7);

        Glide.with(this).load(hotList.get(7).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.right_shang8);

        Glide.with(this).load(hotList.get(8).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.right_zhong9);

        Glide.with(this).load(hotList.get(9).getLogoUrl()).transform(new GlideRoundTransform(this)).placeholder(R.drawable.imgerror).
                error(R.drawable.imgerror).into(holder.right_xia10);


        holder.left_shang1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(0));
            }
        });
        holder.left_xia2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(1));
            }
        });
        holder.right_shang3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(2));
            }
        });
        holder.right_zhong4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(3));
            }
        });
        holder.right_xia5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(4));
            }
        });

        holder.left_shang6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(5));
            }
        });
        holder.left_xia7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(6));
            }
        });
        holder.right_shang8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(7));
            }
        });
        holder.right_zhong9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(8));
            }
        });
        holder.right_xia10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigationInterface.naviClick(view, hotList.get(9));
            }
        });
    }


    /**
     * 欧美精选
     *
     * @param holder
     * @param position
     */
    private SNavagationAdapter europeAdapter;

    private void onBindEuropeViewHolder(final RvAdapter.EuropeViewHolder holder, int position) {
        List<NavigationInfo> mzList = (List<NavigationInfo>) allDataMap.get(4);
        if (europeAdapter == null) {
            europeAdapter = new SNavagationAdapter(this);
            europeAdapter.setNavigationInterface(this);
            europeAdapter.setList(mzList);
            holder.oumei_gv.setAdapter(europeAdapter);
            return;
        }
        europeAdapter.setList(mzList);
        europeAdapter.notifyDataSetChanged();
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
                    Intent intent = new Intent(NavigationActivity.this, WXPayEntryActivity.class);
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
                    Intent intent = new Intent(NavigationActivity.this, WXPayEntryActivity.class);
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
                    Intent intent = new Intent(NavigationActivity.this, WXPayEntryActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade, R.anim.hold);
                    dialog1.dismiss();
                }
            });
        } else {
            dialog1.show();
        }
    }


    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("海外导航"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("海外导航"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

}
