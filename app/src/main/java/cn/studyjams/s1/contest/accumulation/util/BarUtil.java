package cn.studyjams.s1.contest.accumulation.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import cn.studyjams.s1.contest.accumulation.R;


/**
 * 状态栏工具类
 * Created by Relish on 2016/11/8.
 */
public class BarUtil {

    /**
     * 设置沉浸式状态栏
     *
     * @param activity 当前activity
     * @param toolbar  toolbar
     */
    public static void setImmersiveStatusBar(Activity activity, ViewGroup toolbar) {
        // 在android4.4以后将状态栏设置成透明，然后通过设置actionbar的padding将actionbar填充至状态栏，
        // 以此来实现状态栏的沉浸
        // actionbar的padding已在布局文件中设置过
        // 据说在代码里直接声明状态栏为透明比较有效（未测试）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | localLayoutParams.flags);
            // 设置actionbar的填充状态栏
            toolbar.setPadding(0, getStatusBarHeight(activity), 0, 0);
            // 设置actionbar的背景颜色
            toolbar.setBackground(ContextCompat.getDrawable(activity, R.color.colorPrimary));
            //透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度(像素)
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void setBarTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }
}
