package wang.relish.accumulation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import wang.relish.accumulation.App
import wang.relish.accumulation.R
import wang.relish.accumulation.base.BaseActivity
import wang.relish.accumulation.util.BarUtil
import wang.relish.accumulation.util.SPUtil

class SplashActivity : BaseActivity() {

    override fun layoutId(): Int {
        return R.layout.activity_splash
    }

    override fun removeParent(): Boolean {
        return true
    }

    override fun initToolbar(savedInstanceState: Bundle?, mToolbar: Toolbar?) {
        BarUtil.setStatusBarTransparent(this)
    }

    override fun initViews(savedInstanceState: Bundle?) {
        val view = findViewById(R.id.background)
        val icon = findViewById(R.id.icon) as ImageView
        val tv = findViewById(R.id.propaganda) as TextView
        val animation = AnimationUtils.loadAnimation(this, R.anim.alpha)
        view.animation = animation
        icon.animation = animation
        tv.animation = animation
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                val autoLogin = SPUtil.getBoolean("autoLogin", false)
                if (autoLogin) {
                    App.USER = SPUtil.user
                    if (App.USER != null && !App.USER!!.isEmpty) {
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }
}
