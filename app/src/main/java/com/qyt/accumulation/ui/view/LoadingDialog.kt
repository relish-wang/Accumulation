package com.qyt.accumulation.ui.view

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView

import com.qyt.accumulation.R

/**
 * 正在加载对话框（获取焦点，只能通过主动调用dismiss()方法消失）

 * @author qiuy qiuy@servyou.com.cn
 * *
 * @version 1.0 2015年8月26日
 */
class LoadingDialog : DialogFragment() {

    internal var tvLoading: TextView? = null
    internal var v: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog.window!!.decorView.setBackgroundColor(Color.TRANSPARENT)
        //        setCancelable(false);
        v = inflater!!.inflate(R.layout.dialog_loading, container, false)
        tvLoading = v?.findViewById(R.id.tv_loading) as TextView
        tvLoading!!.visibility = View.VISIBLE
        val bundle = arguments
        if (bundle != null) {
            val msg = bundle.getString("msg")
            if (!TextUtils.isEmpty(msg)) {
                tvLoading!!.text = msg
            } else {
                tvLoading!!.setText(R.string.loading)
            }
        } else {
            tvLoading!!.setText(R.string.loading)
        }
        return v
    }

    fun setText(msg: String?) {
        var msg = msg
        if (msg == null) {
            msg = getString(R.string.loading)
        }
        if (tvLoading == null) {
            tvLoading = v?.findViewById(R.id.tv_loading) as TextView
        }
        tvLoading!!.text = msg
    }

    val isShowing: Boolean
        get() = isAdded

    fun onShow(str: String) {
        setText(str)
    }

    fun onShow() {
        setText(getString(R.string.loading))
    }

    companion object {

        fun getInstance(msg: String): LoadingDialog {
            val dialog = LoadingDialog()
            val bundle = Bundle()
            bundle.putString("msg", msg)
            dialog.arguments = bundle
            return dialog
        }
    }
}
