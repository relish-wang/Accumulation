package wang.relish.accumulation.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import wang.relish.accumulation.App
import wang.relish.accumulation.R
import wang.relish.accumulation.ui.view.LoadingDialog
import wang.relish.accumulation.util.AppLog
import wang.relish.accumulation.util.BarUtil
import wang.relish.accumulation.util.GoActivity

/**
 * Activity基础类
 * Created by Relish on 2016/11/4.
 */
abstract class BaseActivity : AppCompatActivity(), BaseView {

    /**
     * 布局ID

     * @return int
     */
    protected abstract fun layoutId(): Int

    /**
     * 初始化Toolbar

     * @param savedInstanceState 数据
     * *
     * @param mToolbar           标题栏
     */
    protected abstract fun initToolbar(savedInstanceState: Bundle?, mToolbar: Toolbar?)

    /**
     * 初始化所有控件

     * @param savedInstanceState 数据
     */
    protected abstract fun initViews(savedInstanceState: Bundle?)

    protected var mToolbar: Toolbar? = null

    private var loadingDialog: LoadingDialog? = null

    protected open fun parseIntent(intent: Intent) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parseIntent(intent)

        val resId = layoutId()
        if (resId != 0) {
            if (removeParent()) {
                setContentView(resId)
                BarUtil.setBarTransparent(this)
            } else {
                // 在子类和Toolbar上再套一层布局
                val inflater = LayoutInflater.from(this)
                @SuppressLint("InflateParams") val clRoot = inflater.inflate(R.layout.activity_base, null) as ViewGroup


                val llRoot = clRoot.findViewById(R.id.llRoot) as LinearLayout
                // 子类Activity的布局
                val contentView = inflater.inflate(resId, null)
                llRoot.addView(contentView, LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                setContentView(clRoot)
                //沉浸式状态栏
                BarUtil.setStatusBarTransparent(this)
                // 初始化Toolbar
                mToolbar = clRoot.findViewById(R.id.toolbar) as Toolbar
                //透明状态栏
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                }
                // 初始化自定义ActionBar,下面的顺序很重要
                initToolbar(savedInstanceState, mToolbar)
                setSupportActionBar(mToolbar)
                if (isBtnBackEnable) {
                    val actionBar = supportActionBar
                    if (actionBar != null) {
                        actionBar.setHomeButtonEnabled(true)
                        actionBar.setDisplayHomeAsUpEnabled(true)
                    }
                }
            }

            EventBus.getDefault().register(this)
            //Activity入栈
            App.addActivity(this)

            //初始化所有控件
            initViews(savedInstanceState)
        } else {
            AppLog.e(TAG, "onCreate", "did you forget set layoutId()?")
        }

    }

    /**
     * 返回键是否生效

     * @return 默认生效，除非子类重写
     */
    protected open var isBtnBackEnable: Boolean = true
        get() = true

    /**
     * 是否不使用BaseActivity的根布局

     * @return 默认使用，除非子类重写
     */
    protected open fun removeParent(): Boolean {
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (isBtnBackEnable) {
                onBackPressed()//厉害了，居然要这么监听
            } else {
                val mActionBar = supportActionBar
                if (mActionBar != null) {
                    mActionBar.setDisplayHomeAsUpEnabled(false)
                    mActionBar.setHomeButtonEnabled(false)
                }
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)//反注册EventBus
        val className = arrayOf<String>(javaClass.name)
        App.removeActivities(className)
    }

    override fun showMessage(msg: Any) {
        if (msg == null) {
            AppLog.e(TAG, "showMessage", "The incoming parameter is null！！！！")
            return
        }
        if (msg is Int) {
            Toast.makeText(this, (msg as Int?)!!, Toast.LENGTH_SHORT).show()
        } else if (msg is CharSequence) {
            Toast.makeText(this, msg as CharSequence?, Toast.LENGTH_SHORT).show()
        } else {
            AppLog.e(TAG, "showMessage",
                    "The type of incoming param is " + msg.javaClass.simpleName)
        }
    }

    fun showLoading(msg: Any) {
        val str: String
        if (msg is Int) {
            str = getString(msg)
        } else if (msg is String) {
            str = msg
        } else {
            showLoading(true)
            return
        }
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.getInstance(str)
        }
        if (!loadingDialog!!.isShowing) {
            loadingDialog!!.show(supportFragmentManager, str)
        }
    }

    override fun showLoading(isShown: Boolean) {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.getInstance("")
        }
        if (isShown) {
            if (!loadingDialog!!.isShowing) {
                loadingDialog!!.show(supportFragmentManager, "")
            }
        } else {
            if (loadingDialog!!.isShowing) {
                loadingDialog!!.dismiss()
            }
        }
    }

    override fun goActivity(clazz: Class<*>) {
        GoActivity.Builder(clazz).build().act()
    }

    override val activity: Activity
        get() = this

    override fun goBrowser(url: String) {
        var url = url
        if (url == null) {
            showMessage(R.string.url_is_null)
            return
        }

        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url
        }
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val content_url = Uri.parse(url)
        intent.data = content_url
        startActivity(intent)
    }

    fun showKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
    }

    fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0) //强制隐藏键盘
    }

    @Subscribe
    fun onEventMainThread(obj: Any) {

    }

    companion object {

        private val TAG = "BaseActivity"
    }
}