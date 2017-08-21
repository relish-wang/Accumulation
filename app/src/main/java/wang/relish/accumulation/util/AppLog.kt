package wang.relish.accumulation.util

import android.util.Log

import wang.relish.accumulation.App
import wang.relish.accumulation.R


/**
 * App日志类
 * Created by Relish on 2016/11/4.
 */
object AppLog {

    private val TAG = App.CONTEXT!!.getString(R.string.app_name)

    fun v(className: String, methodName: String, msg: String) {
        Log.v(TAG + "_" + className, "#$methodName: $msg")
    }

    fun d(className: String, methodName: String, msg: String) {
        Log.d(TAG + "_" + className, "#$methodName: $msg")
    }

    fun i(className: String, methodName: String, msg: String) {
        Log.i(TAG + "_" + className, "#$methodName: $msg")
    }

    fun w(className: String, methodName: String, msg: String) {
        Log.w(TAG + "_" + className, "#$methodName: $msg")
    }

    fun e(className: String, methodName: String, msg: String) {
        Log.e(TAG + "_" + className, "#$methodName: $msg")
    }

}
