package com.qyt.accumulation.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.qyt.accumulation.App;
import com.qyt.accumulation.base.BaseFragment;


/**
 * 跳转Activity工具类
 * Created by Relish on 2016/10/27.
 */
public class GoActivity {

    /**
     * 必填项，上下文
     */
    private Context mContext;
    /**
     * 必填项,跳转的Activity
     */
    private Class<?> mClass;

    /**
     * 数据包
     */
    private Bundle mBundle;

    /**
     * 请求码
     * <p></p>
     * 配合{@link Activity#onActivityResult(int, int, Intent)}使用
     */
    private int mRequestCode = -1;

    /**
     * 跳转后是否关闭当前页面
     */
    private boolean isClose = false;

    /**
     * 是否跳转前端页面
     */
    private boolean isWebPage;
    /**
     * 跳转前端页面的URL
     */
    private String mUrl;

    /**
     * 从Fragment开始跳转
     */
    private BaseFragment mFragment;

    private GoActivity(Class<?> clazz) {
        mContext = App.CONTEXT;
        mClass = clazz;
    }
    public static Builder obtain(Class cla){
        return new Builder(cla);
    }

    private Intent getIntent() throws Exception {
        if (mContext == null || this.mClass == null) {
            throw new Exception("Context and Activity can not be Null");
        }

        Intent intent = new Intent(mContext, mClass);
        if (isWebPage) {
            if (mBundle == null) {
                mBundle = new Bundle();
                mBundle.putString("url", mUrl);
            } else {
                mBundle.putString("url", mUrl);
            }
        }
        if (mBundle != null) {
            intent.putExtras(mBundle);
        }
        return intent;
    }

    /**
     * 开始执行跳转
     */
    public void act() {
        if (mContext != null) {
            try {
                if (mRequestCode == -1) {
                    mContext.startActivity(getIntent());
                } else {
                    if (mFragment != null) {
                        mFragment.startActivityForResult(getIntent(), mRequestCode);
                    } else {
                        ((Activity) mContext).startActivityForResult(getIntent(), mRequestCode);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (isClose) {
                    ((Activity) mContext).finish();
                }
            }
        }
    }

    public static class Builder {
        /**
         * 必填项，上下文
         */
        private Context mContext;
        /**
         * 必填项,跳转的Activity
         */
        private Class<?> mClass;

        /**
         * 数据包
         */
        private Bundle mBundle;

        /**
         * 请求码
         * <p></p>
         * 配合{@link Activity#onActivityResult(int, int, Intent)}使用
         */
        private int mRequestCode = -1;

        /**
         * 跳转后是否关闭当前页面
         */
        private boolean isClose = false;

        /**
         * 是否跳转前端页面
         */
        private boolean isWebPage;
        /**
         * 跳转前端页面的URL
         */
        private String mUrl;

        /**
         * 从Fragment开始跳转
         */
        private BaseFragment mFragment;

        public Builder( Class<?> clazz) {
            this.mContext = App.CONTEXT;
            this.mClass = clazz;
        }


        public Builder setBundle(Bundle bundle) {
            this.mBundle = bundle;
            return this;
        }

        public Builder setRequestCode(int requestCode) {
            this.mRequestCode = requestCode;
            return this;
        }

        public Builder setClose(boolean isClose) {
            this.isClose = isClose;
            return this;
        }

        public Builder setIsWebPage(boolean isWebPage) {
            this.isWebPage = isWebPage;
            return this;
        }

        public Builder setUrl(String url) {
            this.mUrl = url;
            return this;
        }

        public Builder setFragment(BaseFragment fragment) {
            this.mFragment = fragment;
            return this;
        }

        public void act(){
            build().act();
        }

        public GoActivity build() {
            GoActivity bean = new GoActivity(mClass);
            bean.mBundle = mBundle;
            bean.mRequestCode = mRequestCode;
            bean.isClose = isClose;
            bean.isWebPage = isWebPage;
            bean.mUrl = mUrl;
            bean.mFragment = mFragment;
            return bean;
        }
    }


}