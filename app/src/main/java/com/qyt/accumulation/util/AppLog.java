package com.qyt.accumulation.util;

import android.util.Log;

import com.qyt.accumulation.App;
import com.qyt.accumulation.R;


/**
 * App日志类
 * Created by Relish on 2016/11/4.
 */
public class AppLog {

    private static final String TAG = App.CONTEXT.getString(R.string.app_name);

    public static void v(String className, String methodName, String msg) {
        Log.v(TAG + "_" + className, "#" + methodName + ": " + msg);
    }

    public static void d(String className, String methodName, String msg) {
        Log.d(TAG + "_" + className, "#" + methodName + ": " + msg);
    }

    public static void i(String className, String methodName, String msg) {
        Log.i(TAG + "_" + className, "#" + methodName + ": " + msg);
    }

    public static void w(String className, String methodName, String msg) {
        Log.w(TAG + "_" + className, "#" + methodName + ": " + msg);
    }

    public static void e(String className, String methodName, String msg) {
        Log.e(TAG + "_" + className, "#" + methodName + ": " + msg);
    }

}
