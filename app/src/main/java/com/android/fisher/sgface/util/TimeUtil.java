package com.android.fisher.sgface.util;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA. User: weiguo.ren Date: 13-10-30 Time: 下午3:39 To
 * change this template use File | Settings | File Templates.
 */
public class TimeUtil {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_DATE    = new SimpleDateFormat("yyyy-MM-dd");
    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }


    /*
   * 2014-01-27
   */
    public static String getDateStr(long time) {
        String formatStr = "yyyy-M-dd";
        SimpleDateFormat df = new SimpleDateFormat(formatStr,
                Locale.getDefault());
        String dateString = df.format(time);
        return dateString;
    }

    public static String getDateZhtoEN(String time){
        DateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat format2= new SimpleDateFormat("yyyy年MM月dd日");
        Date date = null;
        try {
            date = format2.parse(time);
            return format1.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }
    public static String longToDate(String strDate) {
        if(StringUtils.isNull(strDate)){
            return "暂无时间数据";
        }else {
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
//前面的lSysTime是秒数，先乘1000得到毫秒数，再转为java.util.Date类型
            //  long lSysTime2 = System.currentTimeMillis();
            //   System.out.println(lSysTime2);
             long a = Long.parseLong(strDate);
            Date dt = new Date(a);
            String sDateTime = sdf.format(dt);  //得到精确到秒的表示：08/31/2006 21:08:00
            //   System.out.println(sDateTime);
            return sDateTime;
        }

    }
}
