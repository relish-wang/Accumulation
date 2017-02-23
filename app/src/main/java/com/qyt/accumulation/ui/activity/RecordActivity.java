package com.qyt.accumulation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.qyt.accumulation.R;
import com.qyt.accumulation.base.BaseActivity;
import com.qyt.accumulation.entity.Record;

/**
 * Created by wangxina on 2017/2/22.
 */

public class RecordActivity extends BaseActivity {
    @Override
    protected int layoutId() {
        return R.layout.activity_record;
    }

    private Record mRecord;

    @Override
    protected void parseIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mRecord = (Record) bundle.getSerializable("record");
        } else {
            showMessage("bundle is null!");
            finish();
        }
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {
        mToolbar.setTitle(mRecord.getName());
        mToolbar.setSubtitle(mRecord.getParent().getName());
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {

    }
}
