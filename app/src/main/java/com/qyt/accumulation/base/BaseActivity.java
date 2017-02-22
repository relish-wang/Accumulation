package com.qyt.accumulation.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qyt.accumulation.App;
import com.qyt.accumulation.R;
import com.qyt.accumulation.ui.view.LoadingDialog;
import com.qyt.accumulation.util.AppLog;
import com.qyt.accumulation.util.BarUtil;
import com.qyt.accumulation.util.GoActivity;

/**
 * Activity基础类
 * Created by Relish on 2016/11/4.
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    /**
     * 布局ID
     *
     * @return int
     */
    protected abstract int layoutId();

    /**
     * 初始化Toolbar
     *
     * @param savedInstanceState 数据
     * @param mToolbar           标题栏
     */
    protected abstract void initToolbar(Bundle savedInstanceState, Toolbar mToolbar);

    /**
     * 初始化所有控件
     *
     * @param savedInstanceState 数据
     */
    protected abstract void initViews(Bundle savedInstanceState);

    private static final String TAG = "BaseActivity";

    protected Toolbar mToolbar;

    private LoadingDialog loadingDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int resId = layoutId();
        if (resId != 0) {
            if (removeParent()) {
                setContentView(resId);
                BarUtil.setBarTransparent(this);
            } else {
                // 在子类和Toolbar上再套一层布局
                LayoutInflater inflater = LayoutInflater.from(this);
                @SuppressLint("InflateParams") ViewGroup clRoot =
                        (ViewGroup) inflater.inflate(R.layout.activity_base, null);


                LinearLayout llRoot = (LinearLayout) clRoot.findViewById(R.id.llRoot);
                // 子类Activity的布局
                View contentView = inflater.inflate(resId, null);
                llRoot.addView(contentView, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                setContentView(clRoot);
                //沉浸式状态栏
                BarUtil.setStatusBarTransparent(this);
                // 初始化Toolbar
                mToolbar = (Toolbar) clRoot.findViewById(R.id.toolbar);
                //透明状态栏
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
                // 初始化自定义ActionBar,下面的顺序很重要
                initToolbar(savedInstanceState, mToolbar);
                setSupportActionBar(mToolbar);
                if (isBtnBackEnable()) {
                    ActionBar actionBar = getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.setHomeButtonEnabled(true);
                        actionBar.setDisplayHomeAsUpEnabled(true);
                    }
                }
            }

            //Activity入栈
            App.addActivity(this);

            //初始化所有控件
            initViews(savedInstanceState);
        } else {
            AppLog.e(TAG, "onCreate", "did you forget set layoutId()?");
        }

    }

    /**
     * 返回键是否生效
     *
     * @return 默认生效，除非子类重写
     */
    protected boolean isBtnBackEnable() {
        return true;
    }

    /**
     * 是否不使用BaseActivity的根布局
     *
     * @return 默认使用，除非子类重写
     */
    protected boolean removeParent() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (isBtnBackEnable()) {
                onBackPressed();//厉害了，居然要这么监听
            } else {
                ActionBar mActionBar = getSupportActionBar();
                if (mActionBar != null) {
                    mActionBar.setDisplayHomeAsUpEnabled(false);
                    mActionBar.setHomeButtonEnabled(false);
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.removeActivities(getClass().getName());
    }

    @Override
    public void showMessage(Object msg) {
        if (msg == null) {
            AppLog.e(TAG, "showMessage", "The incoming parameter is null！！！！");
            return;
        }
        if (msg instanceof Integer) {
            Toast.makeText(this, (Integer) msg, Toast.LENGTH_SHORT).show();
        } else if (msg instanceof CharSequence) {
            Toast.makeText(this, (CharSequence) msg, Toast.LENGTH_SHORT).show();
        } else {
            AppLog.e(TAG, "showMessage",
                    "The type of incoming param is " + msg.getClass().getSimpleName());
        }
    }

    public void showLoading(Object msg) {
        String str;
        if (msg instanceof Integer) {
            str = getString((Integer) msg);
        } else if (msg instanceof String) {
            str = (String) msg;
        } else {
            showLoading(true);
            return;
        }
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.getInstance(str);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show(getSupportFragmentManager(), str);
        }
    }

    @Override
    public void showLoading(boolean isShown) {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.getInstance("");
        }
        if (isShown) {
            if (!loadingDialog.isShowing()) {
                loadingDialog.show(getSupportFragmentManager(), "");
            }
        } else {
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    @Override
    public void goActivity(Class<?> clazz) {
        new GoActivity.Builder(clazz).build().act();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void goBrowser(String url) {
        if(url==null){
            showMessage(R.string.url_is_null);
            return;
        }

        if ((!url.startsWith("http://")) && (!url.startsWith("https://"))) {
            url = "http://" + url;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }
}