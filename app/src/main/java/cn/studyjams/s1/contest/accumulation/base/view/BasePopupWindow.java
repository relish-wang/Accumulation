package cn.studyjams.s1.contest.accumulation.base.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * <h3>封装PopupWindow</h3>
 *
 * @author QiuYing qiuy@servyou.com.cn
 * @version 1.0 2015-8-2
 */
public abstract class BasePopupWindow {
    public static final int WRAP_CONTENT = WindowManager.LayoutParams.WRAP_CONTENT;
    public static final int MATCH_PARENT = WindowManager.LayoutParams.MATCH_PARENT;

    public View anchor;// 调用显示PopupWindow的控件

    public PopupWindow mWindow;// 弹出窗

    public View mRoot;// 弹出窗布局

    public Activity mActivity;// 所在的activity

    public Drawable mBackground = null;// 弹出窗的背景

    public WindowManager windowManager;// 获取设备窗口信息的管理类，确定窗口显示位置时会用到。

    public BasePopupWindow(Activity activity) {
        this.mWindow = new PopupWindow(activity);
        windowManager = (WindowManager) activity
                .getSystemService(Context.WINDOW_SERVICE);
        this.mActivity = activity;
    }

    /**
     * 需要根据父控件定位弹出窗口显示地点的构造方法
     *
     * @param anchor   调用弹出窗口的父控件
     * @param activity 调用弹出窗口的acticity
     */
    public BasePopupWindow(View anchor, Activity activity) {
        this(activity);
        this.anchor = anchor;
    }

    /**
     * 显示弹出窗口的方法,在此方法中传入弹出窗布局，完成基本设置，并显示。
     * <p>
     * 需要依次调用:setAttr--->onSetWindowWidth--->setContentView---> show***.
     * </p>
     */
    public abstract void onShow();

    /**
     * 显示弹出窗之前调用的属性设置方法。
     */
    @SuppressWarnings("deprecation")
    public void setAttr() {
        mWindow.setBackgroundDrawable(new BitmapDrawable());
        mWindow.setTouchable(true);
        mWindow.setFocusable(true);
        mWindow.setOutsideTouchable(true);
        mWindow.setAnimationStyle(android.R.style.Animation_Dialog);
    }

    /**
     * 设置窗口长宽的方法，值为0时默认为WRAP_CONTENT
     *
     * @param windowWidth
     * @param windowHeight
     */
    public void setWindowLength(int windowWidth, int windowHeight) {
        if (windowWidth != 0) {
            mWindow.setWidth(windowWidth);
        } else {
            mWindow.setWidth(WRAP_CONTENT);
        }

        if (windowHeight != 0) {
            mWindow.setHeight(windowHeight);
        } else {
            mWindow.setHeight(WRAP_CONTENT);
        }
    }

    /**
     * 设置弹出窗背景方法，需在onShow()方法之前调用
     *
     * @param background
     */
    public void setBackgroundDrawable(Drawable background) {
        if (background != null) {
            this.mBackground = background;
            mWindow.setBackgroundDrawable(background);
        }
    }

    /**
     * 通过代码直接加载布局
     *
     * @param root
     */
    public void setContentView(View root) {
        if (root != null) {
            this.mRoot = root;
            mWindow.setContentView(root);
        }
    }

    /**
     * 通过加载资源文件布局来设置弹出窗布局，在此方法调用后，可通过 root 变量获取布局内的控件
     *
     * @param layoutResID
     */
    public void setContentView(int layoutResID) {
        mActivity.getLayoutInflater();
        LayoutInflater inflator = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setContentView(inflator.inflate(layoutResID, null));
    }

    /**
     * 设置弹出窗口的消失监听事件
     *
     * @param listener
     */
    public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
        if (listener != null) {
            mWindow.setOnDismissListener(listener);
        }
    }

    /**
     * 在调用弹出窗口控件的正下方显示弹出窗口
     */
    public void showDropDown() {
        showDropDown(0, 0);
    }

    /**
     * 以调用窗口的控件为基准，分别向右、向下偏移xOffset、yOffset显示弹出窗口
     *
     * @param xOffset
     * @param yOffset
     */
    public void showDropDown(int xOffset, int yOffset) {
        mWindow.showAsDropDown(anchor, xOffset, yOffset);
    }

    /**
     * 在指定位置显示弹出窗口
     *
     * @param parent
     * @param gravity
     * @param x
     * @param y
     */
    public void showAtLocation(View parent, int gravity, int x, int y) {
        try {
            mWindow.showAtLocation(parent, gravity, x, y);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭弹出窗
     */
    public void dismiss() {
        if (mWindow != null && mWindow.isShowing()) {
            mWindow.dismiss();
            backgroundLight();
        }
    }

    public boolean isShowing() {
        if (mWindow != null) {
            return mWindow.isShowing();
        } else {
            return false;
        }
    }

    /**
     * 弹出窗消失，所在aciticty变亮
     */
    public void backgroundLight() {
        WindowManager.LayoutParams params = mActivity.getWindow()
                .getAttributes();
        params.alpha = 1.0f;
        mActivity.getWindow().setAttributes(params);
    }

    /**
     * 弹出窗弹出，所在aciticty变暗
     */
    public void backgroundDark() {
        WindowManager.LayoutParams params = mActivity.getWindow()
                .getAttributes();
        params.alpha = 0.7f;
        mActivity.getWindow().setAttributes(params);
    }

    /**
     * 设置弹出窗口的动画效果
     *
     * @param animResID
     */
    public void setAnimation(int animResID) {
        mWindow.setAnimationStyle(animResID);
    }

    /**
     * @return 返回弹出窗实例，方便进行一些此基础类没有的属性设置
     */
    public PopupWindow getWindow() {
        return mWindow;
    }

    public void setbackKeyListener() {
        // 监听返回键，关闭弹出窗

        mRoot.setFocusable(true);
        mRoot.setFocusableInTouchMode(true);
        mRoot.requestFocus();

        mRoot.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mWindow != null && mWindow.isShowing()) {
                        dismiss();
                    }
                    return true;
                }
                return false;
            }
        });
    }
}