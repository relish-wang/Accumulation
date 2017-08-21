package wang.relish.accumulation.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

import wang.relish.accumulation.App
import wang.relish.accumulation.base.BaseFragment


/**
 * 跳转Activity工具类
 * Created by Relish on 2016/10/27.
 */
class GoActivity private constructor(
        /**
         * 必填项,跳转的Activity
         */
        private val mClass: Class<*>?) {

    /**
     * 必填项，上下文
     */
    private val mContext: Context?

    /**
     * 数据包
     */
    private var mBundle: Bundle? = null

    /**
     * 请求码
     *
     *
     * 配合[Activity.onActivityResult]使用
     */
    private var mRequestCode = -1

    /**
     * 跳转后是否关闭当前页面
     */
    private var isClose = false

    /**
     * 是否跳转前端页面
     */
    private var isWebPage: Boolean = false
    /**
     * 跳转前端页面的URL
     */
    private var mUrl: String? = null

    /**
     * 从Fragment开始跳转
     */
    private var mFragment: BaseFragment? = null

    init {
        mContext = App.CONTEXT
    }

    private val intent: Intent
        @Throws(Exception::class)
        get() {
            if (mContext == null || this.mClass == null) {
                throw Exception("Context and Activity can not be Null")
            }

            val intent = Intent(mContext, mClass)
            if (isWebPage) {
                if (mBundle == null) {
                    mBundle = Bundle()
                    mBundle!!.putString("url", mUrl)
                } else {
                    mBundle!!.putString("url", mUrl)
                }
            }
            if (mBundle != null) {
                intent.putExtras(mBundle)
            }
            return intent
        }

    /**
     * 开始执行跳转
     */
    fun act() {
        if (mContext != null) {
            try {
                if (mRequestCode == -1) {
                    mContext.startActivity(intent)
                } else {
                    if (mFragment != null) {
                        mFragment!!.startActivityForResult(intent, mRequestCode)
                    } else {
                        (mContext as Activity).startActivityForResult(intent, mRequestCode)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (isClose) {
                    (mContext as Activity).finish()
                }
            }
        }
    }

    class Builder(
            /**
             * 必填项,跳转的Activity
             */
            private val mClass: Class<*>) {
        /**
         * 必填项，上下文
         */
        private val mContext: Context

        /**
         * 数据包
         */
        private var mBundle: Bundle? = null

        /**
         * 请求码
         *
         *
         * 配合[Activity.onActivityResult]使用
         */
        private var mRequestCode = -1

        /**
         * 跳转后是否关闭当前页面
         */
        private var isClose = false

        /**
         * 是否跳转前端页面
         */
        private var isWebPage: Boolean = false
        /**
         * 跳转前端页面的URL
         */
        private var mUrl: String? = null

        /**
         * 从Fragment开始跳转
         */
        private var mFragment: BaseFragment? = null

        init {
            this.mContext = App.CONTEXT!!
        }


        fun setBundle(bundle: Bundle): Builder {
            this.mBundle = bundle
            return this
        }

        fun setRequestCode(requestCode: Int): Builder {
            this.mRequestCode = requestCode
            return this
        }

        fun setClose(isClose: Boolean): Builder {
            this.isClose = isClose
            return this
        }

        fun setIsWebPage(isWebPage: Boolean): Builder {
            this.isWebPage = isWebPage
            return this
        }

        fun setUrl(url: String): Builder {
            this.mUrl = url
            return this
        }

        fun setFragment(fragment: BaseFragment): Builder {
            this.mFragment = fragment
            return this
        }

        fun act() {
            build().act()
        }

        fun build(): GoActivity {
            val bean = GoActivity(mClass)
            bean.mBundle = mBundle
            bean.mRequestCode = mRequestCode
            bean.isClose = isClose
            bean.isWebPage = isWebPage
            bean.mUrl = mUrl
            bean.mFragment = mFragment
            return bean
        }
    }

    companion object {
        fun obtain(cla: Class<*>): Builder {
            return Builder(cla)
        }
    }


}