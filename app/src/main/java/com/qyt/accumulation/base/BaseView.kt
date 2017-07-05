package com.qyt.accumulation.base

import android.app.Activity

/**
 * View 基础类
 * Created by Relish on 2016/11/4.
 */
interface BaseView {

    fun showMessage(msg: Any)

    fun showLoading(isShown: Boolean)

    val activity: Activity

    /**
     * 快速跳转，没有多余参数

     * @param clazz 跳转到的Activity
     */
    fun goActivity(clazz: Class<*>)

    fun goBrowser(url: String)
}
