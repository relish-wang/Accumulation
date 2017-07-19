package wang.relish.accumulation.base;

import android.app.Activity;

/**
 * View 基础类
 * Created by Relish on 2016/11/4.
 */
public interface BaseView {

    void showMessage(Object msg);

    void showLoading(boolean isShown);

    Activity getActivity();

    /**
     * 快速跳转，没有多余参数
     *
     * @param clazz 跳转到的Activity
     */
    void goActivity(Class<?> clazz);

    void goBrowser(String url);
}
