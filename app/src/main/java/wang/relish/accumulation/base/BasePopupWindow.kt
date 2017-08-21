package wang.relish.accumulation.base

import android.app.Activity
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.WindowManager
import android.widget.PopupWindow

/**
 * <h3>封装PopupWindow</h3>

 * @author QiuYing qiuy@servyou.com.cn
 * *
 * @version 1.0 2015-8-2
 */
abstract class BasePopupWindow(var mActivity: Activity// 所在的activity
) {

    var anchor: View = null!!// 调用显示PopupWindow的控件

    /**
     * @return 返回弹出窗实例，方便进行一些此基础类没有的属性设置
     */
    var window: PopupWindow? = null// 弹出窗

    var mRoot: View// 弹出窗布局

    var mBackground: Drawable? = null// 弹出窗的背景

    var windowManager: WindowManager// 获取设备窗口信息的管理类，确定窗口显示位置时会用到。

    init {
        this.window = PopupWindow(mActivity)
        windowManager = mActivity
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    /**
     * 需要根据父控件定位弹出窗口显示地点的构造方法

     * @param anchor   调用弹出窗口的父控件
     * *
     * @param activity 调用弹出窗口的acticity
     */
    constructor(anchor: View, activity: Activity) : this(activity) {
        this.anchor = anchor
    }

    /**
     * 显示弹出窗口的方法,在此方法中传入弹出窗布局，完成基本设置，并显示。
     *
     *
     * 需要依次调用:setAttr--->onSetWindowWidth--->setContentView---> show***.
     *
     */
    abstract fun onShow()

    /**
     * 显示弹出窗之前调用的属性设置方法。
     */
    fun setAttr() {
        window!!.setBackgroundDrawable(BitmapDrawable())
        window!!.isTouchable = true
        window!!.isFocusable = true
        window!!.isOutsideTouchable = true
        window!!.animationStyle = android.R.style.Animation_Dialog
    }

    /**
     * 设置窗口长宽的方法，值为0时默认为WRAP_CONTENT

     * @param windowWidth
     * *
     * @param windowHeight
     */
    fun setWindowLength(windowWidth: Int, windowHeight: Int) {
        if (windowWidth != 0) {
            window!!.width = windowWidth
        } else {
            window!!.width = WRAP_CONTENT
        }

        if (windowHeight != 0) {
            window!!.height = windowHeight
        } else {
            window!!.height = WRAP_CONTENT
        }
    }

    /**
     * 设置弹出窗背景方法，需在onShow()方法之前调用

     * @param background
     */
    fun setBackgroundDrawable(background: Drawable?) {
        if (background != null) {
            this.mBackground = background
            window!!.setBackgroundDrawable(background)
        }
    }

    /**
     * 通过代码直接加载布局

     * @param root
     */
    fun setContentView(root: View?) {
        if (root != null) {
            this.mRoot = root
            window!!.contentView = root
        }
    }

    /**
     * 通过加载资源文件布局来设置弹出窗布局，在此方法调用后，可通过 root 变量获取布局内的控件

     * @param layoutResID
     */
    fun setContentView(layoutResID: Int) {
        mActivity.layoutInflater
        val inflator = mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        setContentView(inflator.inflate(layoutResID, null))
    }

    /**
     * 设置弹出窗口的消失监听事件

     * @param listener
     */
    fun setOnDismissListener(listener: PopupWindow.OnDismissListener?) {
        if (listener != null) {
            window!!.setOnDismissListener(listener)
        }
    }

    /**
     * 以调用窗口的控件为基准，分别向右、向下偏移xOffset、yOffset显示弹出窗口

     * @param xOffset
     * *
     * @param yOffset
     */
    @JvmOverloads fun showDropDown(xOffset: Int = 0, yOffset: Int = 0) {
        window!!.showAsDropDown(anchor, xOffset, yOffset)
    }

    /**
     * 在指定位置显示弹出窗口

     * @param parent
     * *
     * @param gravity
     * *
     * @param x
     * *
     * @param y
     */
    fun showAtLocation(parent: View, gravity: Int, x: Int, y: Int) {
        try {
            window!!.showAtLocation(parent, gravity, x, y)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 关闭弹出窗
     */
    fun dismiss() {
        if (window != null && window!!.isShowing) {
            window!!.dismiss()
            backgroundLight()
        }
    }

    val isShowing: Boolean
        get() {
            if (window != null) {
                return window!!.isShowing
            } else {
                return false
            }
        }

    /**
     * 弹出窗消失，所在aciticty变亮
     */
    fun backgroundLight() {
        val params = mActivity.window
                .attributes
        params.alpha = 1.0f
        mActivity.window.attributes = params
    }

    /**
     * 弹出窗弹出，所在aciticty变暗
     */
    fun backgroundDark() {
        val params = mActivity.window
                .attributes
        params.alpha = 0.7f
        mActivity.window.attributes = params
    }

    /**
     * 设置弹出窗口的动画效果

     * @param animResID
     */
    fun setAnimation(animResID: Int) {
        window!!.animationStyle = animResID
    }

    fun setbackKeyListener() {
        // 监听返回键，关闭弹出窗

        mRoot.isFocusable = true
        mRoot.isFocusableInTouchMode = true
        mRoot.requestFocus()

        mRoot.setOnKeyListener(OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (window != null && window!!.isShowing) {
                    dismiss()
                }
                return@OnKeyListener true
            }
            false
        })
    }

    companion object {
        val WRAP_CONTENT = WindowManager.LayoutParams.WRAP_CONTENT
        val MATCH_PARENT = WindowManager.LayoutParams.MATCH_PARENT
    }
}
/**
 * 在调用弹出窗口控件的正下方显示弹出窗口
 */