package de.blinkt.openvpn.Utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.frakbot.jumpingbeans.JumpingBeans;

import org.apache.http.util.EncodingUtils;
import org.xutils.image.ImageOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.blinkt.openvpn.Constant.Constant;
import de.blinkt.openvpn.R;

/**
 * Created by Administrator on 2016/2/13.
 */
public class Util {

    //    private HttpUtils httpUtils;
//    /**
//     * bitmapUtils单例模式
//     *
//     * @return图片加载框架
//     */
//    private BitmapUtils bitmapUtils;
//    private BitmapUtils lbBitmapUtils;
    private Context cxt;

    public Util(Context context) {
        this.cxt = context;
    }

//    public BitmapUtils getBitmapUtils(Context context, SelfGridView gridview, SelfListView listView) {
//        if (bitmapUtils == null) {
//            bitmapUtils = new BitmapUtils(context);
//            //给BitMapUtils基本配置
//            //bitmapUtils.configDefaultLoadingImage(R.drawable.faild);
////			//加载失败的图片
////            bitmapUtils.configDefaultLoadFailedImage(R.drawable.lbfail);
//            //设置图片的解码格式
//            bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
//            //设置内存缓存(不推荐)
//            //bitmapUtils.configMemoryCacheEnabled(true);
//
//            //设置磁盘缓存(SDCard)
//            bitmapUtils.configDiskCacheEnabled(true);
//
//            //如果图片过大,会对图片进行x倍的压缩
//            bitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils.getScreenSize(context).scaleDown(5));
//
//            //设置核心线程池的数量 默认5个
//            bitmapUtils.configThreadPoolSize(10);
//
//            //设置listview 和gridview快速滑动时不加载图片,
//            if (gridview != null) {
//                gridview.setOnScrollListener(new PauseOnScrollListener(bitmapUtils, false, true));
//            }
//            if (listView != null) {
//                listView.setOnScrollListener(new PauseOnScrollListener(bitmapUtils, false, true));
//            }
//
//            //长度(会导致内存疯涨,重大注意)
//            //bitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils.getScreenSize(cxt));
//
//            //加载动画
//            //Animation animation = AnimationUtils.loadAnimation(context, R.anim.guide_welcome_fade_in);
//
//            //bitmapUtils.configDefaultImageLoadAnimation(animation);
//        }
//
//        return bitmapUtils;
//    }

//    public BitmapUtils getLbBitmapUtils(Context context, SelfGridView gridview, SelfListView listView) {
//        if (lbBitmapUtils == null) {
//            lbBitmapUtils = new BitmapUtils(context);
//            //给BitMapUtils基本配置
//            lbBitmapUtils.configDefaultLoadingImage(R.drawable.lbfail);
////			//加载失败的图片
//            lbBitmapUtils.configDefaultLoadFailedImage(R.drawable.lbfail);
//            //设置图片的解码格式
//            lbBitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
//            //设置内存缓存(不推荐)
//            //bitmapUtils.configMemoryCacheEnabled(true);
//
//            //设置磁盘缓存(SDCard)
//            lbBitmapUtils.configDiskCacheEnabled(true);
//
//            //如果图片过大,会对图片进行x倍的压缩
//            lbBitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils.getScreenSize(context).scaleDown(5));
//
//            //设置核心线程池的数量 默认5个
//            lbBitmapUtils.configThreadPoolSize(10);
//
//            //设置listview 和gridview快速滑动时不加载图片,
//            if (gridview != null) {
//                gridview.setOnScrollListener(new PauseOnScrollListener(lbBitmapUtils, false, true));
//            }
//            if (listView != null) {
//                listView.setOnScrollListener(new PauseOnScrollListener(lbBitmapUtils, false, true));
//            }
//
//            //长度(会导致内存疯涨,重大注意)
//            //bitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils.getScreenSize(cxt));
//
//            //加载动画
//            //Animation animation = AnimationUtils.loadAnimation(context, R.anim.guide_welcome_fade_in);
//
//            //bitmapUtils.configDefaultImageLoadAnimation(animation);
//        }
//
//        return lbBitmapUtils;
//    }


    public ImageOptions options;

    public ImageOptions getOptions() {
        /**
         * 通过ImageOptions.Builder().set方法设置图片的属性
         */
        if (options == null) {
            ImageOptions.Builder builder = new ImageOptions.Builder();
            //ImageOptions.Builder()的一些其他属性：
            //.setCircular(true) //设置图片显示为圆形
            //.setSquare(true) //设置图片显示为正方形
            //builder.setCrop(true).setSize(500, 500); //对图片进行裁剪
            //.setAnimation(animation) //设置动画
            //.setFailureDrawable(Drawable failureDrawable) //设置加载失败的动画
            builder.setFailureDrawableId(R.drawable.imgerror); //以资源id设置加载失败的动画
            //.setLoadingDrawable(Drawable loadingDrawable) //设置加载中的动画
            builder.setLoadingDrawableId(R.drawable.imgerror); //以资源id设置加载中的动画
            builder.setIgnoreGif(false); //是否忽略Gif图片
            //.setParamsBuilder(ParamsBuilder paramsBuilder) //在网络请求中添加一些参数
            //.setRaduis(int raduis) //设置拐角弧度
            builder.setUseMemCache(true); //设置使用MemCache，默认true
            options = builder.setFadeIn(true).build(); //淡入效果
        }

        return options;
    }

    public ImageOptions circleOptions;

    public ImageOptions getCircleOptions() {
        /**
         * 通过ImageOptions.Builder().set方法设置图片的属性
         */
        if (circleOptions == null) {
            ImageOptions.Builder builder = new ImageOptions.Builder();
            //ImageOptions.Builder()的一些其他属性：
            builder.setCircular(true); //设置图片显示为圆形
            //.setSquare(true) //设置图片显示为正方形
            //setCrop(true).setSize(200,200) //设置大小
            //.setAnimation(animation) //设置动画
            //.setFailureDrawable(Drawable failureDrawable) //设置加载失败的动画
            //.setFailureDrawableId(int failureDrawable) //以资源id设置加载失败的动画
            //.setLoadingDrawable(Drawable loadingDrawable) //设置加载中的动画
            //.setLoadingDrawableId(int loadingDrawable) //以资源id设置加载中的动画
            //.setIgnoreGif(false) //忽略Gif图片
            //.setParamsBuilder(ParamsBuilder paramsBuilder) //在网络请求中添加一些参数
            //.setRaduis(int raduis) //设置拐角弧度
            //.setUseMemCache(true) //设置使用MemCache，默认true
            circleOptions = builder.setFadeIn(true).build(); //淡入效果
        }

        return circleOptions;
    }



    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /*
      * MD5加密
      */
    public String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        //16位加密，从第9位到25位
        return md5StrBuff.substring(8, 24).toString().toUpperCase();
    }

//    /***
//     * httpUtils网络访问
//     */
//    public HttpUtils getHttpUtils() {
//        if (httpUtils == null) {
//            //实例化
//            httpUtils = new HttpUtils();
//            //配置超时时间
//            httpUtils.configTimeout(15000);
//            //配置返回值编码
//            httpUtils.configResponseTextCharset("UTF-8");
//            //配置get请求缓存时间
//            httpUtils.configCurrentHttpCacheExpiry(0);
//
//            httpUtils.configSoTimeout(15000);
//
//        }
//        return httpUtils;
//    }

    /***
     * SharedPreferences 写入数据
     */
    public void sharedPreferencesWriteData(Context context, String filename, String key, String value) {
        //实例化SharedPreferences对象,参数1是存储文件的名称，参数2是文件的打开方式，当文件不存在时，直接创建，如果存在，则直接使用
        SharedPreferences mySharePreferences = context.getSharedPreferences(filename, Activity.MODE_MULTI_PROCESS);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = mySharePreferences.edit();

        //用putString的方法保存数据
        editor.putString(key, value);

        //提交数据
        editor.commit();
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

    /***
     * shared删除文件中的全部数据
     *
     * @param context
     * @param filename
     */
    public void sharedPreferencesDelByFileAllData(Context context, String filename) {
        //实例化SharedPreferences对象,参数1是存储文件的名称，参数2是文件的打开方式，当文件不存在时，直接创建，如果存在，则直接使用
        SharedPreferences mySharePreferences = context.getSharedPreferences(filename, Activity.MODE_MULTI_PROCESS);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = mySharePreferences.edit();

        //用clear()的方法删除数据
        editor.clear();
        editor.clear();
        editor.clear();
        editor.clear();
        editor.clear();

        //提交数据
        editor.commit();
    }

    /***
     * shared删除指定的数据
     *
     * @param context
     * @param filename
     */
    public void sharedPreferencesDelOrderData(Context context, String filename, String key) {
        //实例化SharedPreferences对象,参数1是存储文件的名称，参数2是文件的打开方式，当文件不存在时，直接创建，如果存在，则直接使用
        SharedPreferences mySharePreferences = context.getSharedPreferences(filename, Activity.MODE_MULTI_PROCESS);

        //实例化SharedPreferences.Editor对象
        SharedPreferences.Editor editor = mySharePreferences.edit();

        //删除指定数据
        editor.remove(key);

        //提交数据
        editor.commit();
    }

    public String getChinese() {
        String str = null;
        int highPos, lowPos;
        Random random = new Random();
        highPos = (176 + Math.abs(random.nextInt(71)));//区码，0xA0打头，从第16区开始，即0xB0=11*16=176,16~55一级汉字，56~87二级汉字
        random = new Random();
        lowPos = 161 + Math.abs(random.nextInt(94));//位码，0xA0打头，范围第1~94列

        byte[] bArr = new byte[2];
        bArr[0] = (new Integer(highPos)).byteValue();
        bArr[1] = (new Integer(lowPos)).byteValue();
        try {
            str = new String(bArr, "GB2312");    //区位码组合成汉字
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }


    /***
     * 随机生成昵称
     */
    public String getRandomNick() {
        Random random = new Random(System.currentTimeMillis());
        /* 598 百家姓 */
        String[] Surname = {"赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许",
                "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章", "云", "苏", "潘", "葛", "奚", "范", "彭", "郎",
                "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳", "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺", "倪", "汤", "滕", "殷",
                "罗", "毕", "郝", "邬", "安", "常", "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "平", "黄", "和",
                "穆", "萧", "尹", "姚", "邵", "湛", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明", "臧", "计", "伏", "成", "戴", "谈", "宋", "茅", "庞", "熊", "纪", "舒",
                "屈", "项", "祝", "董", "梁", "杜", "阮", "蓝", "闵", "席", "季", "麻", "强", "贾", "路", "娄", "危", "江", "童", "颜", "郭", "梅", "盛", "林", "刁", "钟",
                "徐", "邱", "骆", "高", "夏", "蔡", "田", "樊", "胡", "凌", "霍", "虞", "万", "支", "柯", "昝", "管", "卢", "莫", "经", "房", "裘", "缪", "干", "解", "应",
                "宗", "丁", "宣", "贲", "邓", "郁", "单", "杭", "洪", "包", "诸", "左", "石", "崔", "吉", "钮", "龚", "程", "嵇", "邢", "滑", "裴", "陆", "荣", "翁", "荀",
                "羊", "于", "惠", "甄", "曲", "家", "封", "芮", "羿", "储", "靳", "汲", "邴", "糜", "松", "井", "段", "富", "巫", "乌", "焦", "巴", "弓", "牧", "隗", "山",
                "谷", "车", "侯", "宓", "蓬", "全", "郗", "班", "仰", "秋", "仲", "伊", "宫", "宁", "仇", "栾", "暴", "甘", "钭", "厉", "戎", "祖", "武", "符", "刘", "景",
                "詹", "束", "龙", "叶", "幸", "司", "韶", "郜", "黎", "蓟", "溥", "印", "宿", "白", "怀", "蒲", "邰", "从", "鄂", "索", "咸", "籍", "赖", "卓", "蔺", "屠",
                "蒙", "池", "乔", "阴", "郁", "胥", "能", "苍", "双", "闻", "莘", "党", "翟", "谭", "贡", "劳", "逄", "姬", "申", "扶", "堵", "冉", "宰", "郦", "雍", "却",
                "璩", "桑", "桂", "濮", "牛", "寿", "通", "边", "扈", "燕", "冀", "浦", "尚", "农", "温", "别", "庄", "晏", "柴", "瞿", "阎", "充", "慕", "连", "茹", "习",
                "宦", "艾", "鱼", "容", "向", "古", "易", "慎", "戈", "廖", "庾", "终", "暨", "居", "衡", "步", "都", "耿", "满", "弘", "匡", "国", "文", "寇", "广", "禄",
                "阙", "东", "欧", "殳", "沃", "利", "蔚", "越", "夔", "隆", "师", "巩", "厍", "聂", "晁", "勾", "敖", "融", "冷", "訾", "辛", "阚", "那", "简", "饶", "空",
                "曾", "毋", "沙", "乜", "养", "鞠", "须", "丰", "巢", "关", "蒯", "相", "查", "后", "荆", "红", "游", "郏", "竺", "权", "逯", "盖", "益", "桓", "公", "仉",
                "督", "岳", "帅", "缑", "亢", "况", "郈", "有", "琴", "归", "海", "晋", "楚", "闫", "法", "汝", "鄢", "涂", "钦", "商", "牟", "佘", "佴", "伯", "赏", "墨",
                "哈", "谯", "篁", "年", "爱", "阳", "佟", "言", "福", "南", "火", "铁", "迟", "漆", "官", "冼", "真", "展", "繁", "檀", "祭", "密", "敬", "揭", "舜", "楼",
                "疏", "冒", "浑", "挚", "胶", "随", "高", "皋", "原", "种", "练", "弥", "仓", "眭", "蹇", "覃", "阿", "门", "恽", "来", "綦", "召", "仪", "风", "介", "巨",
                "木", "京", "狐", "郇", "虎", "枚", "抗", "达", "杞", "苌", "折", "麦", "庆", "过", "竹", "端", "鲜", "皇", "亓", "老", "是", "秘", "畅", "邝", "还", "宾",
                "闾", "辜", "纵", "侴", "万俟", "司马", "上官", "欧阳", "夏侯", "诸葛", "闻人", "东方", "赫连", "皇甫", "羊舌", "尉迟", "公羊", "澹台", "公冶", "宗正",
                "濮阳", "淳于", "单于", "太叔", "申屠", "公孙", "仲孙", "轩辕", "令狐", "钟离", "宇文", "长孙", "慕容", "鲜于", "闾丘", "司徒", "司空", "兀官", "司寇",
                "南门", "呼延", "子车", "颛孙", "端木", "巫马", "公西", "漆雕", "车正", "壤驷", "公良", "拓跋", "夹谷", "宰父", "谷梁", "段干", "百里", "东郭", "微生",
                "梁丘", "左丘", "东门", "西门", "南宫", "第五", "公仪", "公乘", "太史", "仲长", "叔孙", "屈突", "尔朱", "东乡", "相里", "胡母", "司城", "张廖", "雍门",
                "毋丘", "贺兰", "綦毋", "屋庐", "独孤", "南郭", "北宫", "王孙"};

        int index = random.nextInt(Surname.length - 1);
        String name = Surname[index]; //获得一个随机的姓氏

		/* 从常用字中选取一个或两个字作为名 */
        if (random.nextBoolean()) {
            name += getChinese() + getChinese();
        } else {
            name += getChinese();
        }
        try {
            name = new String(name.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return name;
    }


    //获取apk渠道名称
    public String getAppMetaData(Context ctx, String key) {
        if (ctx == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String resultData = null;
        try {
            PackageManager packageManager = ctx.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        resultData = applicationInfo.metaData.getString(key);
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
        return resultData;
    }

    /**
     * 获取屏幕宽度
     */
    public int getWidth() {
        WindowManager wm = (WindowManager) cxt
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    /**
     * 获取屏幕高度
     */
    public int getHeight() {
        WindowManager wm = (WindowManager) cxt
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    //创建文件夹
    public static void createFileDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                //按照指定的路径创建文件夹
                file.mkdirs();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }


    //创建文件
    public static void createFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                //在指定的文件夹中创建文件
                file.createNewFile();
            } catch (Exception e) {
            }
        }
    }


    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public static void deleteFile(File file) {
        if (file.exists() == false) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFile = file.listFiles();
                if (childFile == null || childFile.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFile) {
                    deleteFile(f);
                }
                file.delete();
            }
        }
    }


    //写入文件
    public static void writeFileToSDFile(String fileName, String text) {
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                file.mkdir();
            }
            FileOutputStream outputStream = new FileOutputStream(fileName);
            byte[] bytes = text.getBytes();
            outputStream.write(bytes);
            outputStream.close();//关闭写人流
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    //读取文件
    public static String readFileToSDFile(String fileName) {
        String res = "";
        try {
            FileInputStream inputStream = new FileInputStream(fileName);
            int length = inputStream.available();//获取值的长度
            byte[] bytes = new byte[length];//接收读取的值
            inputStream.read(bytes);

            res = EncodingUtils.getString(bytes, "UTF-8");
            inputStream.close();//关闭输入流
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
        }

        return res;
    }

    //获取Sd卡根路径
    public static String getSDCardPath() {
        String path = "";
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            path = Environment.getRootDirectory().getPath();//system
        }
        return path;
    }

    /**
     * 获取运营商
     *
     * @return
     */
    private String tele_Supo_Name = null;

    public String getTele_Supo(String IMSI, Context context) {
        TelephonyManager telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String operator = telManager.getSimOperator();
        if (operator != null) {
            if (operator.equals("46000") || operator.equals("46002") || operator.equals("46007")) {
                //中国移动
                tele_Supo_Name = "中国移动";
            } else if (operator.equals("46001")) {
                //中国联通
                tele_Supo_Name = "中国联通";
            } else if (operator.equals("46003")) {
                //中国电信
                tele_Supo_Name = "中国电信";
            }
        }
        return tele_Supo_Name;
    }

    /***
     * 获取当前系统时间
     */
    public String getSystemTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault());
        String time = sdf.format(new Date());
        return time;
    }

    /***
     * 比对时间大小
     */
    public String compareTime(String currentSysTime, String vipTime) {
        //result  1过期 2未过期
        String result = "1";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Calendar sysC1 = java.util.Calendar.getInstance();
        Calendar vipC2 = java.util.Calendar.getInstance();
        try {
            sysC1.setTime(sdf.parse(currentSysTime));
            vipC2.setTime(sdf.parse(vipTime));
        } catch (java.text.ParseException e) {
            System.err.println("格式不正确");
        }
        int code = sysC1.compareTo(vipC2);
        if (code == 0)
            //System.out.println("c1相等c2");
            result = "1";
        else if (code < 0)
            //System.out.println("c1小于c2");
            result = "2";
        else
            //System.out.println("c1大于c2");
            result = "1";
        return result;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            return "";
        }
    }


    /***
     * 启动android手机的一些浏览器打开网站
     */
    private int ijk = 0;
    private boolean isHaveUc = false;

    public void starBrowser(String indexHtmlStr, Context context) {
        //根据获取的浏览器信息打开网站
        int count = 0;
        if (broserList.size() == 0) {
            //手机没有浏览器
            new T().centershow(context, "~请您安装网页浏览器,访问我们的网站。", 2000);
        } else {
            try {
                count = broserList.size() - 1;//浏览器总数  //最大小标取count
                for (int i = 0; i < broserList.size(); i++) {
                    ActivityInfo activityInfo = broserList.get(i);
                    //uc
                    if (activityInfo.packageName.equals("com.uc.browser")
                            || activityInfo.packageName.equals("com.UCMobile")) {
                        isHaveUc = true;
                        openHtml(context, i, indexHtmlStr);
                        break;
                    }
                }
                if (isHaveUc == true) {
                    return;
                }
                openHtml(context, ijk, indexHtmlStr);
            } catch (Exception e) {
                ijk++;
                if (ijk > count) {
                    new T().centershow(context, "~没有浏览器能打开这个网站~", 2000);
                } else {
                    openHtml(context, ijk, indexHtmlStr);
                }
            }
        }

    }

    public void openHtml(Context context, int ijk, String indexHtmlStr) {
        ActivityInfo info = broserList.get(ijk);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(indexHtmlStr);
        intent.setData(content_url);
        intent.setClassName(info.packageName, info.name);
        context.startActivity(intent);
    }

    /***
     * 获取当前手机所有浏览器信息
     */
    public List<ActivityInfo> broserList = new ArrayList<ActivityInfo>();

    public List<ActivityInfo> getAllBrowserInfo(Context context) {
        broserList.clear();
        String default_browser = "android.intent.category.DEFAULT";
        String browsable = "android.intent.category.BROWSABLE";
        String view = "android.intent.action.VIEW";

        Intent intent = new Intent(view);
        intent.addCategory(browsable);
        intent.addCategory(default_browser);
        Uri uri = Uri.parse("http://");
        intent.setDataAndType(uri, null);


        // 找出手机当前安装的所有浏览器程序
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
        if (resolveInfoList.size() > 0) {
            for (int i = 0; i < resolveInfoList.size(); i++) {
                ActivityInfo activityInfo = resolveInfoList.get(i).activityInfo;
                broserList.add(activityInfo);
            }
//            String packageName = activityInfo.packageName;
//            String className = activityInfo.name;
            return broserList;
        } else {
            return broserList;
        }
    }

    /**
     * Android SDK平台获取高唯一性设备识别码
     */
    public String getAndroidId(Context context) {
        String deviceId = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = tm.getDeviceId();//第一次获取
            if (TextUtils.isEmpty(deviceId)) {
                //没有获取到imel号码
                deviceId = autoCreateImeil(context);
            }
            if (!TextUtils.isEmpty(deviceId)) {
                String lastStr = deviceId.substring(deviceId.length() - 1);
                if (!isNumeric(lastStr)) {
                    deviceId = autoCreateImeil(context);
                }
            }
        } catch (Exception e) {
            deviceId = autoCreateImeil(context);
        }
        return deviceId;
    }


    /**
     * 判断是否是数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /***
     * 为了处理某些安卓设备获取不到唯一识别码的情况，需要手动随机生成
     */
    public String autoCreateImeil(Context context) {
        String deviceId = "";
        File deviceFile = new File(Constant.UUID_AUTO_CREATE_DIRECTORY + Constant.UUID_AUTO_TWOFILE_DIRECTORY + Constant.UUID_AUTO_FILE_PATH);
        if (!deviceFile.exists()) {
            File file = new File(Constant.UUID_AUTO_CREATE_DIRECTORY);
            if (!file.exists()) {
                createFileDir(Constant.UUID_AUTO_CREATE_DIRECTORY);
            }
            File fileTwo = new File(Constant.UUID_AUTO_CREATE_DIRECTORY + Constant.UUID_AUTO_TWOFILE_DIRECTORY);
            if (!fileTwo.exists()) {
                createFileDir(Constant.UUID_AUTO_CREATE_DIRECTORY + Constant.UUID_AUTO_TWOFILE_DIRECTORY);
            }
            File filePath = new File(Constant.UUID_AUTO_CREATE_DIRECTORY + Constant.UUID_AUTO_TWOFILE_DIRECTORY + Constant.UUID_AUTO_FILE_PATH);
            if (!filePath.exists()) {
                createFile(Constant.UUID_AUTO_CREATE_DIRECTORY + Constant.UUID_AUTO_TWOFILE_DIRECTORY + Constant.UUID_AUTO_FILE_PATH);
            }
            //生成
            long uuid_first = System.currentTimeMillis();
            deviceId = uuid_first + "" + createRandom();
            String aesJson = new AesUtils().encrypt(deviceId);//
            writeFileToSDFile(Constant.UUID_AUTO_CREATE_DIRECTORY + Constant.UUID_AUTO_TWOFILE_DIRECTORY + Constant.UUID_AUTO_FILE_PATH, aesJson);
        } else {
            //第二次读取
            String text = readFileToSDFile(Constant.UUID_AUTO_CREATE_DIRECTORY + Constant.UUID_AUTO_TWOFILE_DIRECTORY + Constant.UUID_AUTO_FILE_PATH);
            deviceId = new AesUtils().decrypt(text);
        }
        return deviceId;
    }

    /***
     * 生成指定位数的随机
     */

    public String createRandom() {
        String strRand = "";
        for (int i = 0; i < 5; i++) {
            strRand += String.valueOf((int) (Math.random() * 10));
        }
        return strRand;
    }

    /**
     * @param activity
     */
    private Random random;

    public int createTranslateRandom() {
        if (random == null) {
            random = new Random();
        }
        int randNum = random.nextInt(70) + 30;
        return randNum;
    }

    public void alertKeFu(final Activity activity) {
        new AlertDialog.Builder(activity).setTitle("消息提示")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage("联系客服(QQ):3534528886")
                .setPositiveButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        dialog.dismiss();
                    }
                }).setNegativeButton("复制", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 点击“确认”后的操作
                ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText("3534528886"); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                new T().centershow(activity, "文本已复制", 100);
            }
        }).show();
    }

    Dialog dialog_zn;
    SpannableStringBuilder mSpannableStringBuilder;


    public void alertZhiNan(Activity activity) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.alert_zhinan, null);
        //对话框
        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
        if (dialog_zn == null) {
            dialog_zn = new Dialog(activity, R.style.Dialog);
            dialog_zn.show();
            dialog_zn.setCancelable(false);
            Window window = dialog_zn.getWindow();
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            layout.getBackground().setAlpha(130);
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
            window.setContentView(layout);

            mSpannableStringBuilder = new SpannableStringBuilder(activity.getResources().getString(R.string.info));
            mSpannableStringBuilder.setSpan
                    (new ForegroundColorSpan(activity.getResources().getColor(R.color.juhuang)), 53, 133, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            mSpannableStringBuilder.setSpan
                    (new ForegroundColorSpan(activity.getResources().getColor(R.color.juhuang)), 137, 183, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            //取消按钮
            final Button ok = (Button) layout.findViewById(R.id.ok);
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_zn.dismiss();
                }
            });
            TextView tv_conn_content = (TextView) layout.findViewById(R.id.tv_conn_content);
            JumpingBeans.with(tv_conn_content).makeTextJump(0, tv_conn_content.length() - 1)
                    .build();
            TextView txtContent_disclaimer = (TextView) layout.findViewById(R.id.txtContent_disclaimer);
            txtContent_disclaimer.setMovementMethod(ScrollingMovementMethod.getInstance());
            txtContent_disclaimer.setText(mSpannableStringBuilder);
        } else {
            dialog_zn.show();
        }
    }


    /***
     * 手机号码验证表达式
     */
    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 重新启动app
     */
    public void startApp(Activity context) {
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
        System.exit(0);
    }

    /**
     * 判断相对应的APP是否存在
     *
     * @param context
     * @param packageName(包名)(若想判断QQ，则改为com.tencent.mobileqq，若想判断微信，则改为com.tencent.mm)
     * @return
     */
    public  boolean appIsExist(Context context, String  qqPackageName) {
        PackageManager packageManager = context.getPackageManager();

        //获取手机系统的所有APP包名，然后进行一一比较
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (((PackageInfo) pinfo.get(i)).packageName.equalsIgnoreCase(qqPackageName)) {
                return true;
            }
        }
        return false;
    }

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");

    public static long[] compareSendTvTime(Date endDate, Date curDate) {
        long[] time = new long[4];
        if (curDate == null || endDate == null) {
            return null;
        }
        long diff = endDate.getTime() - curDate.getTime();//毫秒

        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        long second = diff / 1000;
        time[0] = days;//天数
        time[1] = hours;//小时
        time[2] = minutes;//分钟
        time[3] = second;

        return time;
    }

}

