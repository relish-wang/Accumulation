package cn.studyjams.s1.contest.accumulation.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import cn.studyjams.s1.contest.accumulation.R;
import cn.studyjams.s1.contest.accumulation.base.BaseActivity;

/**
 * Created by Relish on 2016/11/7.
 */

public class AboutActivity extends BaseActivity {
    @Override
    protected int layoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {
        mToolbar.setTitle(R.string.about);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }
}
