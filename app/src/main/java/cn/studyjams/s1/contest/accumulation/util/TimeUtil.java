package cn.studyjams.s1.contest.accumulation.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 一个烦死了的时间计算
 * <p>
 * Created by 鑫 on 2015/8/20.
 */
public class TimeUtil {
    /**
     * 时间long转datetime格式
     *
     * @param l long类型时间
     * @return String
     */
    public static String longToDateTime(long l) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Date date = new Date(l);
        return formatter.format(date);
    }


    /**
     * datetime格式转时间long
     *
     * @return long类型时间
     */
    public static long dateTimeToLong(String datetime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        try {
            Date d = formatter.parse(datetime);
            return d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String toYMDStr(String datetime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            Date d = formatter.parse(datetime);
            return formatter2.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取当前系统时间
     *
     * @return String
     */
    public static String getNowTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
    }

    /**
     * 获取当前时间是星期几
     *
     * @return int [0~7]:星期日~星期六
     */
    public static int dayForWeek() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * 获取当前时间是星期几
     *
     * @param date string
     * @return 0~7
     */
    public static int dayForWeek(String date) {
        long dateLong = dateTimeToLong(date);
        return dayForWeek(dateLong);//0~7:星期日~星期六
    }

    /**
     * 获取当前时间是星期几
     *
     * @param l long
     * @return 0~7
     */
    public static int dayForWeek(long l) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(l);
        return cal.get(Calendar.DAY_OF_WEEK) - 1;//0~7:星期日~星期六
    }

    /**
     * 获得当天零点时间
     *
     * @return
     */
    public static long getTodayZeroLong() {
        long oneDay = 24 * 60 * 60 * 1000; // 每天的毫秒数
        long now = System.currentTimeMillis();// 从1970-1-1 8点开始的毫秒数
        return now - ((now + 8 * 60 * 60 * 1000) % oneDay);
    }

    /**
     * 获得当天零点时间
     *
     * @return
     */
    public static long getTodayZeroLong(String date) {
        long oneDay = 24 * 60 * 60 * 1000; // 每天的毫秒数
        long now = dateTimeToLong(date);// 从1970-1-1 8点开始的毫秒数
        return now - ((now + 8 * 60 * 60 * 1000) % oneDay);
    }

//    /**
//     * 当前时间是否是星期一的八点
//     *
//     * @return
//     */
//    public static boolean isMon8Clock() {
//        long now = System.currentTimeMillis();
//        long zero = getTodayZeroLong();
//        long eightHours = 11 * 60 * 60 * 1000;
//        return dayForWeek() == 1 && now - zero >= eightHours && now - zero <= eightHours + 1000 * 60;
//    }

//    public static boolean isTaskOver3Days(TaskEntity task) {
//        return System.currentTimeMillis() - dateTimeToLong(task.getuTime()) >= 3 * 24 * 60 * 60 * 1000;
//    }
}
