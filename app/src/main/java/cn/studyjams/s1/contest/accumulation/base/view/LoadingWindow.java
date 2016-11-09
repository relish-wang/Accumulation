package cn.studyjams.s1.contest.accumulation.base.view;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.TextView;

import cn.studyjams.s1.contest.accumulation.R;

/**
 * 正在加载对话框（获取焦点，只能通过主动调用dismiss()方法消失）
 *
 * @author qiuy qiuy@servyou.com.cn
 * @version 1.0 2015年8月26日
 */
public class LoadingWindow extends BasePopupWindow {

    Context mContext;

    public LoadingWindow(Activity activity) {
        super(activity);
        mContext = activity;
    }

    @Override
    public void onShow() {
        onShow(mContext.getResources().getString(R.string.loading));
    }

    public void onShow(String content) {
        setAttr();
        setWindowLength(0, 0);
        setContentView(R.layout.view_loading_item);

        // 单独设置这两个属性是为了点击其他区域弹出框不消失，响应返回键的点击事件
        mWindow.setBackgroundDrawable(null);
        mWindow.setOutsideTouchable(false);

        TextView tv_content = (TextView) mRoot.findViewById(R.id.tv_loading);
        if (content == null || "".equals(content)) {
            tv_content.setVisibility(View.GONE);
        } else {
            tv_content.setVisibility(View.VISIBLE);
            tv_content.setText(content);
        }
        setbackKeyListener();
        showAtLocation(mRoot, Gravity.CENTER, 0, 0);
    }

    @Override
    public void dismiss() {
        if (mWindow != null && mWindow.isShowing()) {
            mWindow.dismiss();
        }
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
                        mActivity.onBackPressed();
                    }
                    return true;
                }
                return false;
            }
        });
    }

}
