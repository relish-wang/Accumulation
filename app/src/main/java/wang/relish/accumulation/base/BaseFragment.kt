package wang.relish.accumulation.base

import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast

import wang.relish.accumulation.R
import wang.relish.accumulation.util.AppLog
import wang.relish.accumulation.util.GoActivity


/**
 * Fragment基础类
 * Created by Relish on 2016/10/27.
 */
abstract class BaseFragment : Fragment(), BaseView {

    /**
     * 布局文件ID

     * @return 布局文件ID
     */
    protected abstract fun layoutId(): Int

    /**
     * 初始化
     */
    protected abstract fun initViews(contentView: View)

    private var isLoading: Boolean = false
    private var loadingView: View? = null
    private var contentView: View? = null
    protected var noMoreDataView: View? = null
    private var contentViewAnimator: ObjectAnimator? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val resId = layoutId()
        if (resId != 0) {
            val flRoot = inflater!!.inflate(R.layout.fragment_base, container, false) as FrameLayout
            loadingView = flRoot.findViewById(R.id.tvLoading)
            noMoreDataView = flRoot.findViewById(R.id.tvNoMoreData)
            contentView = inflater.inflate(layoutId(), container, false)
            contentViewAnimator = ObjectAnimator.ofFloat(contentView, "alpha", 0f, 1f)
            contentViewAnimator!!.duration = 500
            flRoot.addView(contentView)

            initViews((contentView as View?)!!)

            return flRoot
        } else {
            return super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun goActivity(clazz: Class<*>) {
        GoActivity.Builder(clazz).build().act()
    }


    override fun showMessage(msg: Any) {
        if (msg == null) {
            AppLog.e("BaseActivity", "showMessage", "The incoming parameter is null！！！！")
            return
        }
        if (msg is Int) {
            Toast.makeText(activity, (msg as Int?)!!, Toast.LENGTH_SHORT).show()
        } else if (msg is CharSequence) {
            Toast.makeText(activity, msg as CharSequence?, Toast.LENGTH_SHORT).show()
        } else {
            AppLog.e("BaseActivity", "showMessage",
                    "The type of incoming param is " + msg.javaClass.simpleName)
        }
    }

    override fun showLoading(shouldLoading: Boolean) {
        if (shouldLoading && !isLoading) {
            // 显示加载界面
            isLoading = true
            contentView!!.visibility = View.INVISIBLE
            loadingView!!.visibility = View.VISIBLE
        } else if (!shouldLoading && isLoading) {
            //　隐藏加载界面
            isLoading = false
            loadingView!!.visibility = View.INVISIBLE
            contentViewAnimator!!.start()
            contentView!!.visibility = View.VISIBLE
        }
    }

    fun noMoreData() {
        noMoreDataView?.visibility = View.VISIBLE
    }

    fun showData() {
        noMoreDataView?.visibility = View.INVISIBLE
    }


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
}
