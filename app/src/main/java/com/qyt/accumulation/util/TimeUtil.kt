package com.qyt.accumulation.util


import android.text.TextUtils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * 一个烦死了的时间计算
 *
 *
 * Created by 鑫 on 2015/8/20.
 */
object TimeUtil {
    /**
     * 时间long转datetime格式

     * @param l long类型时间
     * *
     * @return String
     */
    fun longToDateTime(l: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val date = Date(l)
        return formatter.format(date)
    }


    /**
     * datetime格式转时间long

     * @return long类型时间
     */
    fun dateTimeToLong(datetime: String): Long {
        if (TextUtils.isEmpty(datetime)) return 0
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        try {
            val d = formatter.parse(datetime)
            return d.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return 0
    }

    fun toYMDStr(datetime: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        val formatter2 = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        try {
            val d = formatter.parse(datetime)
            return formatter2.format(d)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }

    /**
     * 获取当前系统时间

     * @return String
     */
    val nowTime: String
        get() = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())

    /**
     * 获取当前时间是星期几

     * @return int [0~7]:星期日~星期六
     */
    fun dayForWeek(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1
    }

    /**
     * 获取当前时间是星期几

     * @param date string
     * *
     * @return 0~7
     */
    fun dayForWeek(date: String): Int {
        val dateLong = dateTimeToLong(date)
        return dayForWeek(dateLong)//0~7:星期日~星期六
    }

    /**
     * 获取当前时间是星期几

     * @param l long
     * *
     * @return 0~7
     */
    fun dayForWeek(l: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = l
        return cal.get(Calendar.DAY_OF_WEEK) - 1//0~7:星期日~星期六
    }

    /**
     * 获得当天零点时间

     * @return
     */
    // 每天的毫秒数
    // 从1970-1-1 8点开始的毫秒数
    val todayZeroLong: Long
        get() {
            val oneDay = (24 * 60 * 60 * 1000).toLong()
            val now = System.currentTimeMillis()
            return now - (now + 8 * 60 * 60 * 1000) % oneDay
        }

    /**
     * 获得当天零点时间

     * @return
     */
    fun getTodayZeroLong(date: String): Long {
        val oneDay = (24 * 60 * 60 * 1000).toLong() // 每天的毫秒数
        val now = dateTimeToLong(date)// 从1970-1-1 8点开始的毫秒数
        return now - (now + 8 * 60 * 60 * 1000) % oneDay
    }

    /**
     * long类型转00:00:00类型

     * @param hardTime long
     * *
     * @return string
     */
    fun getHardTime(hardTime: Long): String {
        val hour = hardTime / 60 / 60 / 1000
        val min = hardTime / 60 / 1000 % 60
        val sec = hardTime / 1000 % 60
        return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, sec)
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
