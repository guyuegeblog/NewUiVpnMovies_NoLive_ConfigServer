package de.blinkt.openvpn.Constant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.blinkt.openvpn.Entity.PriceInfo;
import de.blinkt.openvpn.Utils.Util;
import de.blinkt.openvpn.WeiXin.WXYZ;

/**
 * 接口存储
 */
public class Constant {
    /**
     * 登陆接口
     */
    public static final String LOGIN_INTERFACE = "http://139.129.97.33:8080/YeVpnServer/UserServlet.shtml";

    /**
     * 用户信息修改接口
     */
    public static final String USER_UPDATE = "http://139.129.97.33:8080/YeVpnServer/UpUserServlet.shtml";


    /****
     * 微信支付
     */

    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wx5c9f8c91af605d16";

    public static final String WX_PAY_ORDER = "http://114.215.28.26:80/YeVpnServer/wxPayInit.shtml";

    //微信支付接口 支付验证

    public static final String WX_PAY_YZ = "http://114.215.28.26:80/YeVpnServer/payQuery.shtml";

    public static WXYZ wxyz = new WXYZ();

    /***
     * 支付宝支付
     */
    /**
     * 支付宝获取订单信息接口
     */
    public static final String ZFB_QUERY = "http://114.215.28.26:80/YeVpnServer/aliPayInit.shtml";

    /**
     * 支付宝验证接口
     */
    public static final String ZFB_YZ = "http://114.215.28.26:80/YeVpnServer/aliPayQuery.shtml";


    /***
     * 渠道接口
     */
    public static final String YOU_MENG_AREA = "http://139.129.97.33:8080/YeVpnServer/AreaServlet.shtml";


    /***
     * OpenVpn连接断开接口
     */
    public static final String VPN_CONNECTION = "http://139.129.97.33:8080/YeVpnServer/ConnVpnServlet.shtml";


    /***
     * 统计服务器在线人数接口
     */
    public static final String SERVER_LINE_USER_COUNT = "http://139.129.97.33:8080/YeVpnServer/ConnUserCountServlet.shtml";
    /***
     * 轮播和评论接口(一般由服务器根据会员状态分配，但是这里给用户请求彻底失败的情况，默认一个接口地址)
     */
    public static String USER_MESSAGE = "http://139.129.97.33:8080/YeVpnServer/YeCommentServlet.shtml?";
    /**
     * 导航接口
     */
    public static final String NAVIGATION_DATA = "http://139.129.97.33:8080/YeVpnServer/NavigationServlet.shtml";
    public static Date firstConnTime = null;
    public static Date AppFirstTime = null;
    /**
     * 配置文件接口
     */
    public static final String CONFIG_INTERFACE = "http://139.129.97.33:8080/YeVpnServer/OvpnServlet.shtml";
    /***
     * apk下载接口
     */
    public static final String APK_DOWN_INTERFACE = "http://139.129.97.33:8080/YeVpnServer/ApkServlet.shtml";
    /***
     * 服务器分配接口
     */
    public static final String SREVER_CONFIG_INTERFACE = "http://139.129.97.33:8080/YeVpnServer/InformationServlet.shtml";
    /***
     * 添加评论接口
     */
    public static final String USER_ADD_MESSAGE_INTERFACE = "http://139.129.97.33:8080/YeVpnServer/AddCommentServlet.shtml";
    /**
     * tv直播接口
     */
    public static final String TV_VIDEO_DATA = "http://139.129.97.33:8080/YeVpnServer/FindLiveServlet.shtml";
    /***
     * vpn配置文件路径
     */
    //文件一级目录
    public static final String VPN_CONFIG_DOWNLOAD_DIRECTORY_ONE_PATH = Util.getSDCardPath() + "//.ForasdflmATelevsision";
    //文件二级目录
    public static final String VPN_CONFIG_DOWNLOAD_DIRECTORY_TWO_PATH = "//.Ah0nofdafds6SAQsiv";

    //文件三级目录
    public static final String VPN_CONFIG_DOWNLOAD_DIRECTORY_THREE_PATH = "//.FJ45asdfjXAyRBQ";
    //文件名称
    public static final String VPN_CONFIG_DOWNLOAD_FILE_PATH = "//.SyuioqweriIwK1pAoUQoXM";
    //本地配置文件
    public static final String VPN_LOCAL_CONFIG_FILE = ".companyserver.ovpn";
    public static final String VPN_SHIYONG_DIRECTORY = Util.getSDCardPath() + "//" + "asd4z93Aw";
    public static final String VPN_SHIYONG_FILE = ".VPNTRYOUTA";//试用vpn
    public static final String VPN_FIRST_REGISTER_FILE = ".VPNREGEISTER";//第一次注册

    /***
     * uuid文件路径
     */
    public static final String UUID_AUTO_CREATE_DIRECTORY = Util.getSDCardPath() + "//.b3hBLaasfdAASAnOS";
    //二级目录
    public static final String UUID_AUTO_TWOFILE_DIRECTORY = "//.Aasfd3ATA6SAQsiv";
    public static final String UUID_AUTO_FILE_PATH = ".FSfAN3343vzaAadl";
    public static final String USER_AUTO_DIRECTORY = Util.getSDCardPath() + "//.b3hB98asQsSAnOS";
    public static final String USER_AUTO_GUOQI = USER_AUTO_DIRECTORY + ".jga4diAb";//jga4dasxBAiAb
    public static final String FIRST_INSTALL_FINISH = Util.getSDCardPath() + "/ADSFFINISH";
    public static boolean initWeb = false;
    public static boolean isFirstRegister = false;
    public static boolean mobileIsHaveQQAndWx = false;
    public static List<PriceInfo> priceInfoList = new ArrayList<>();

    //送vpn时间
    public static final String send_Vpn_Directory = Util.getSDCardPath() + "//.sendvpndireass";//sendvpndire
    public static final String send_Vpn_File = ".sendora";
    public static final String send_Vpn = send_Vpn_Directory + "/" + send_Vpn_File;
    public static boolean isShowThreeMonthPay = false;
    public static int lookTime = 20 * 60;//20分钟
    public static Timer timer;
    public static TimerTask timerTask;
}
