package com.qyt.accumulation.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.qyt.accumulation.R;
import com.qyt.accumulation.base.BaseActivity;
import com.qyt.accumulation.util.SPUtil;

public class SettingActivity extends BaseActivity {

    @Override
    protected int layoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {
        mToolbar.setTitle(R.string.setting);
    }

    private View rl_sync, rl_weekly_remind, rl_undo_remind, rl_goal_remind, rl_voice, rl_vibrate;
    private SwitchCompat st_sync, st_weekly_remind, st_undo_remind, st_goal_remind, st_voice, st_vibrate;


    @Override
    protected void initViews(Bundle savedInstanceState) {

        st_sync = (SwitchCompat) findViewById(R.id.st_sync);
        st_weekly_remind = (SwitchCompat) findViewById(R.id.st_weekly_remind);
        st_undo_remind = (SwitchCompat) findViewById(R.id.st_undo_remind);
        st_goal_remind = (SwitchCompat) findViewById(R.id.st_goal_remind);
        st_voice = (SwitchCompat) findViewById(R.id.st_voice);
        st_vibrate = (SwitchCompat) findViewById(R.id.st_vibrate);

        st_sync.setChecked(SPUtil.getBoolean("is_sync"));
        st_weekly_remind.setChecked(SPUtil.getBoolean("weekly_remind"));
        st_undo_remind.setChecked(SPUtil.getBoolean("undo_remind"));
        st_goal_remind.setChecked(SPUtil.getBoolean("goal_remind"));
        st_voice.setChecked(SPUtil.getBoolean("voice"));
        st_vibrate.setChecked(SPUtil.getBoolean("vibrate"));

        st_sync.setFocusable(false);
        st_weekly_remind.setFocusable(false);
        st_undo_remind.setFocusable(false);
        st_goal_remind.setFocusable(false);
        st_voice.setFocusable(false);
        st_vibrate.setFocusable(false);

        rl_sync = findViewById(R.id.rl_sync);
        rl_weekly_remind = findViewById(R.id.rl_weekly_remind);
        rl_undo_remind = findViewById(R.id.rl_undo_remind);
        rl_goal_remind = findViewById(R.id.rl_goal_remind);
        rl_voice = findViewById(R.id.rl_voice);
        rl_vibrate = findViewById(R.id.rl_vibrate);

        rl_sync.setOnClickListener(new SwitchClickListener("is_sync", st_sync));
        rl_weekly_remind.setOnClickListener(new SwitchClickListener("weekly_remind", st_weekly_remind));
        rl_undo_remind.setOnClickListener(new SwitchClickListener("undo_remind", st_undo_remind));
        rl_goal_remind.setOnClickListener(new SwitchClickListener("goal_remind", st_goal_remind));
        rl_voice.setOnClickListener(new SwitchClickListener("voice", st_voice));
        rl_vibrate.setOnClickListener(new SwitchClickListener("vibrate", st_vibrate));
    }


    private class SwitchClickListener implements View.OnClickListener {

        private String key;
        private SwitchCompat st;

        SwitchClickListener(String key, SwitchCompat st) {
            this.key = key;
            this.st = st;
        }

        @Override
        public void onClick(View v) {
            boolean checked = st.isChecked();
            st.setChecked(!checked);
            SPUtil.putBoolean(key, !checked);
        }
    }

}
