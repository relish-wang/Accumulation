package com.qyt.accumulation

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.DisplayMetrics
import android.view.WindowManager

import com.orhanobut.logger.Logger
import com.qyt.accumulation.dao.DBHelper
import com.qyt.accumulation.entity.User
import com.qyt.accumulation.util.AppLog
import com.qyt.accumulation.util.Temp

import java.util.WeakHashMap

/**
 * App应用管理类
 * Created by Relish on 2016/11/4.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        CONTEXT = this

        //日志打印工具
        Logger.init(TAG).methodCount(10) // 方法栈打印的个数，默认是 2
                .hideThreadInfo() // // 隐藏线程信息，默认显示
                .methodOffset(0) // 设置调用堆栈的函数偏移值，默认是 0
        //.logAdapter(new AndroidLogAdapter()); // 自定义一个打印适配器;

        mActivities = WeakHashMap<String, Activity>()

        //获取屏幕尺寸
        val wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        wm.defaultDisplay.getMetrics(dm)
        screenWidth = dm.widthPixels
        screenHegiht = dm.heightPixels
    }

    companion object {

        var USER: User? = null

        val GOOGLE_PLAY = "com.android.vending"
        private val TAG = "App"
        var screenWidth = 0
        var screenHegiht = 0

        private var mActivities: WeakHashMap<String, Activity>? = null

        var CONTEXT:App? = null


        /**
         * Activity入栈

         * @param activity activity
         */
        @Synchronized fun addActivity(activity: Activity) {
            mActivities!!.put(activity.javaClass.name, activity)
        }

        /**
         * Activity出栈

         * @param activityNames activity名
         */
        @Synchronized @JvmStatic fun removeActivities(activityNames: Array<String>) {
            for (activityClassName in activityNames) {
                val activity = mActivities!![activityClassName]
                activity?.finish()
                mActivities!!.remove(activityClassName)
            }
        }

        /**
         * 退出Activity
         */
        fun exitApp() {
            val allActivityNames = mActivities!!.keys.toTypedArray()
            val names = arrayOfNulls<String>(allActivityNames.size)
            for (i in names.indices) {
                names[i] = allActivityNames[i].toString()
            }
            @Suppress("UNCHECKED_CAST")
            removeActivities(names as Array<String>)
        }


        /**
         * 获取版本名

         * @param context 上下文
         * *
         * @return 版本名
         */
        fun getVersionName(context: Context): String {
            val packageInfo = getPackageInfo(context)
            if (packageInfo == null) {
                AppLog.e(TAG, "getVersionName", "packageInfo equals null!!!")
                return ""
            }
            return packageInfo.versionName
        }

        private fun getPackageInfo(context: Context): PackageInfo? {
            val pi: PackageInfo

            try {
                val pm = context.packageManager
                pi = pm.getPackageInfo(context.packageName,
                        PackageManager.GET_CONFIGURATIONS)

                return pi
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }
    }

}
