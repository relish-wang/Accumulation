package com.qyt.accumulation.ui.activity

import android.os.Bundle
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.view.View

import com.qyt.accumulation.R
import com.qyt.accumulation.base.BaseActivity
import com.qyt.accumulation.util.SPUtil

class SettingActivity : BaseActivity() {

    override fun layoutId(): Int {
        return R.layout.activity_setting
    }

    override fun initToolbar(savedInstanceState: Bundle?, mToolbar: Toolbar?) {
        mToolbar?.setTitle(R.string.setting)
    }

    private var rl_sync: View? = null
    private var rl_weekly_remind: View? = null
    private var rl_undo_remind: View? = null
    private var rl_goal_remind: View? = null
    private var rl_voice: View? = null
    private var rl_vibrate: View? = null
    private var st_sync: SwitchCompat? = null
    private var st_weekly_remind: SwitchCompat? = null
    private var st_undo_remind: SwitchCompat? = null
    private var st_goal_remind: SwitchCompat? = null
    private var st_voice: SwitchCompat? = null
    private var st_vibrate: SwitchCompat? = null


    override fun initViews(savedInstanceState: Bundle?) {

        st_sync = findViewById(R.id.st_sync) as SwitchCompat
        st_weekly_remind = findViewById(R.id.st_weekly_remind) as SwitchCompat
        st_undo_remind = findViewById(R.id.st_undo_remind) as SwitchCompat
        st_goal_remind = findViewById(R.id.st_goal_remind) as SwitchCompat
        st_voice = findViewById(R.id.st_voice) as SwitchCompat
        st_vibrate = findViewById(R.id.st_vibrate) as SwitchCompat

        st_sync!!.isChecked = SPUtil.getBoolean("is_sync")
        st_weekly_remind!!.isChecked = SPUtil.getBoolean("weekly_remind")
        st_undo_remind!!.isChecked = SPUtil.getBoolean("undo_remind")
        st_goal_remind!!.isChecked = SPUtil.getBoolean("goal_remind")
        st_voice!!.isChecked = SPUtil.getBoolean("voice")
        st_vibrate!!.isChecked = SPUtil.getBoolean("vibrate")

        st_sync!!.isFocusable = false
        st_weekly_remind!!.isFocusable = false
        st_undo_remind!!.isFocusable = false
        st_goal_remind!!.isFocusable = false
        st_voice!!.isFocusable = false
        st_vibrate!!.isFocusable = false

        rl_sync = findViewById(R.id.rl_sync)
        rl_weekly_remind = findViewById(R.id.rl_weekly_remind)
        rl_undo_remind = findViewById(R.id.rl_undo_remind)
        rl_goal_remind = findViewById(R.id.rl_goal_remind)
        rl_voice = findViewById(R.id.rl_voice)
        rl_vibrate = findViewById(R.id.rl_vibrate)

        rl_sync!!.setOnClickListener(SwitchClickListener("is_sync", st_sync!!))
        rl_weekly_remind!!.setOnClickListener(SwitchClickListener("weekly_remind", st_weekly_remind!!))
        rl_undo_remind!!.setOnClickListener(SwitchClickListener("undo_remind", st_undo_remind!!))
        rl_goal_remind!!.setOnClickListener(SwitchClickListener("goal_remind", st_goal_remind!!))
        rl_voice!!.setOnClickListener(SwitchClickListener("voice", st_voice!!))
        rl_vibrate!!.setOnClickListener(SwitchClickListener("vibrate", st_vibrate!!))
    }


    private inner class SwitchClickListener internal constructor(private val key: String, private val st: SwitchCompat) : View.OnClickListener {

        override fun onClick(v: View) {
            val checked = st.isChecked
            st.isChecked = !checked
            SPUtil.putBoolean(key, !checked)
        }
    }

}
