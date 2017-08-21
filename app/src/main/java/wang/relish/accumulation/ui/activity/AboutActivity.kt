package wang.relish.accumulation.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import wang.relish.accumulation.App
import wang.relish.accumulation.R
import wang.relish.accumulation.base.BaseActivity
import wang.relish.accumulation.util.ScoreUtils

/**
 * 关于页
 * Created by Relish on 2016/11/7.
 */
class AboutActivity : BaseActivity() {
    override fun layoutId(): Int {
        return R.layout.activity_about
    }

    override fun initToolbar(savedInstanceState: Bundle?, mToolbar: Toolbar?) {
        mToolbar?.setTitle(R.string.about)
    }

    @SuppressLint("SetTextI18n")
    override fun initViews(savedInstanceState: Bundle?) {
        val tvVersion = findViewById(R.id.tv_version) as TextView
        tvVersion.text = "v" + App.getVersionName(this)
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.rl_blog -> goBrowser("http://relish.wang")

            R.id.rl_github -> goBrowser("https://github.com/relish-wang/")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_about, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.feedback -> {
                val data = Intent(Intent.ACTION_SENDTO)
                data.data = Uri.parse("mailto:relish-wang@gmail.com")
                data.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject, getString(R.string.app_name)))
                data.putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback_text, getString(R.string.app_name)))
                startActivity(data)
            }
            R.id.comment -> {
                val markets = ScoreUtils.InstalledAPPs(activity)
                if (markets.size >= 0 && markets.contains(App.GOOGLE_PLAY)) {
                    ScoreUtils.launchAppDetail(activity.packageName, App.GOOGLE_PLAY)
                } else {
                    showMessage(R.string.no_google_play)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
