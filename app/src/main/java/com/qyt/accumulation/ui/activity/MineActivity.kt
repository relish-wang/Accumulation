package com.qyt.accumulation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.qyt.accumulation.App
import com.qyt.accumulation.R
import com.qyt.accumulation.base.BaseActivity
import com.qyt.accumulation.entity.User
import com.qyt.accumulation.util.SPUtil

/**
 * 个人中心
 * Created by Relish on 2016/11/13.
 */
class MineActivity : BaseActivity(), View.OnClickListener {
    override fun layoutId(): Int {
        return R.layout.activity_mine
    }

    override fun initToolbar(savedInstanceState: Bundle?, mToolbar: Toolbar?) {
        mToolbar?.setTitle(R.string.mine)
    }

    private var mUser: User? = null

    private var ivHead: ImageView? = null
    private var tvName: TextView? = null
    private var tvMobile: TextView? = null
    private var tvEmail: TextView? = null

    override fun initViews(savedInstanceState: Bundle?) {
        mUser = SPUtil.user
        if (mUser!!.isEmpty) {
            finish()
        }
        ivHead = findViewById(R.id.iv_head) as ImageView
        tvName = findViewById(R.id.tv_name) as TextView
        tvMobile = findViewById(R.id.tv_mobile) as TextView
        tvEmail = findViewById(R.id.tv_email) as TextView

        Glide.with(this)
                .load(mUser!!.photo)
                .centerCrop()
                .placeholder(R.mipmap.icon)
                .crossFade()
                .into(ivHead!!)
        tvName!!.text = checkNull(mUser!!.name!!)
        tvMobile!!.text = checkNull(mUser!!.mobile!!)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.rl_head -> {
            }
            R.id.rl_name -> {
            }
            R.id.rl_mobile -> {
            }
            R.id.rl_email -> {
            }
            R.id.btn_logout -> {
                App.exitApp()
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("logout", true)
                startActivity(intent)
                SPUtil.putBoolean("autoLogin", false)
            }
        }
    }

    private fun checkNull(txt: String): String {
        if (TextUtils.isEmpty(txt) || txt.equals("null", ignoreCase = true))
            return getString(R.string.unsetting)
        return txt
    }
}
