package de.blinkt.openvpn.Tool;

import java.text.ParseException;
import java.util.Date;

import de.blinkt.openvpn.Constant.Constant;
import de.blinkt.openvpn.Utils.Util;

/**
 * Created by ASUS on 2016/12/27.
 */
public class VipTool {
    public static boolean isThanSendTvTime = false;

    public static boolean judgeIsThanSendVpnTime() {
        if (SendTvTimeTool.SEND_TV_TIME_TYPE == SendTvTimeTool.SEND_TV_TIME_DAY_TYPE) {
            //判断天数
            String createDatabaseDate = Util.readFileToSDFile(Constant.send_Vpn);//yyyy-MMM-dd
            try {
                long[] times = Util.compareSendTvTime(new Date(), Util.simpleDateFormat.parse(createDatabaseDate));
//               time[0] = days;//天数
//               time[1] = hours;//小时
//               time[2] = minutes;//分钟
//               time[3] = second;
                isThanSendTvTime = times[0] >= SendTvTimeTool.Time_Day ? true : false;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (SendTvTimeTool.SEND_TV_TIME_TYPE == SendTvTimeTool.SEND_TV_TIME_HOURS_TYPE) {
            //判断小时数
            String createDatabaseDate = Util.readFileToSDFile(Constant.send_Vpn);//yyyy-MMM-dd
            try {
                long[] times = Util.compareSendTvTime(new Date(), Util.simpleDateFormat.parse(createDatabaseDate));
//               time[0] = days;//天数
//               time[1] = hours;//小时
//               time[2] = minutes;//分钟
//               time[3] = second;
                isThanSendTvTime = times[1] >= SendTvTimeTool.Time_Hours ? true : false;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else if (SendTvTimeTool.SEND_TV_TIME_TYPE == SendTvTimeTool.SEND_THREE_MONTH_TYPE) {
            //判断三个月
            String createDatabaseDate = Util.readFileToSDFile(Constant.send_Vpn);//yyyy-MMM-dd
            try {
                long[] times = Util.compareSendTvTime(new Date(), Util.simpleDateFormat.parse(createDatabaseDate));
//               time[0] = days;//天数
//               time[1] = hours;//小时
//               time[2] = minutes;//分钟
//               time[3] = second;
                isThanSendTvTime = times[0] >= SendTvTimeTool.Time_Day_Three_Month ? true : false;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return isThanSendTvTime;
    }

}
