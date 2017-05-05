package com.qyt.accumulation.ui.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.qyt.accumulation.R;
import com.qyt.accumulation.base.BaseActivity;
import com.qyt.accumulation.entity.Goal;
import com.qyt.accumulation.entity.Record;
import com.qyt.accumulation.util.TimeUtil;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by wangxina on 2017/2/22.
 */

public class RecordActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected int layoutId() {
        return R.layout.activity_record;
    }

    private Record mRecord;

    public static void open(Context context, Record record) {
        Intent intent = new Intent(context, RecordActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("record", record);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

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
        Goal goal = mRecord.getParent();
        String goalName = goal == null ? "默认" : goal.getName();
        mToolbar.setTitle(goalName);
        mToolbar.setSubtitle(mRecord.getName());
    }

    private boolean isEditing = false;
    private View rl_start_time, rl_end_time, rl_effective;
    private ImageView iv_edit;
    private TextView tv_start_time, tv_end_time, tv_hard_time;
    private EditText et_info;
    private AppCompatRatingBar rating_bar;


    @Override
    protected void initViews(Bundle savedInstanceState) {

        rl_start_time = findViewById(R.id.rl_start_time);
        rl_end_time = findViewById(R.id.rl_end_time);
        rl_effective = findViewById(R.id.rl_effective);
        rl_start_time.setOnClickListener(this);
        rl_end_time.setOnClickListener(this);
        rl_effective.setOnClickListener(this);

        tv_start_time = (TextView) findViewById(R.id.tv_start_time);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);
        tv_hard_time = (TextView) findViewById(R.id.tv_hard_time);
        rating_bar = (AppCompatRatingBar) findViewById(R.id.rating_bar);

        iv_edit = (ImageView) findViewById(R.id.v_edit);
        iv_edit.setOnClickListener(this);

        et_info = (EditText) findViewById(R.id.et_info);


        updateTimeTime();
        et_info.setText(mRecord.getNote());
        rating_bar.setRating(mRecord.getStar());


        hideKeyboard(et_info);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.v_edit:
                if (isEditing) {
                    iv_edit.setImageResource(R.drawable.ic_edit);
                    et_info.setEnabled(false);
                    String text = et_info.getText().toString().trim();
                    mRecord.setNote(text);
                    new AsyncTask<Void,Void,Long>(){

                        @Override
                        protected Long doInBackground(Void... params) {
                            return mRecord.save();
                        }

                        @Override
                        protected void onPostExecute(Long aLong) {
                            super.onPostExecute(aLong);
                            if (aLong > 0) {
                                isEditing = false;
                            } else {
                                showMessage("修改失败");
                            }
                        }
                    }.execute();
                } else {
                    isEditing = true;
                    iv_edit.setImageResource(R.drawable.ic_save);
                    et_info.setEnabled(true);
                }
                break;
            case R.id.rl_start_time:
                Calendar cStart = getCalendar(mRecord.getStartTime());
                int sYear = cStart.get(Calendar.YEAR);
                int sMonth = cStart.get(Calendar.MONTH);
                int sDay = cStart.get(Calendar.DAY_OF_MONTH);
                int sHour = cStart.get(Calendar.HOUR_OF_DAY);
                int sMin = cStart.get(Calendar.MINUTE);
                editTime(sYear, sMonth, sDay, sHour, sMin, datetime -> {
                    mRecord.setStartTime(datetime);
                    mRecord.setUpdateTime(TimeUtil.longToDateTime(System.currentTimeMillis()));
                    new AsyncTask<Void,Void,Long>(){

                        @Override
                        protected Long doInBackground(Void... params) {
                            return mRecord.save();
                        }

                        @Override
                        protected void onPostExecute(Long aLong) {
                            super.onPostExecute(aLong);
                            if (aLong > 0) {
                                updateTimeTime();
                            } else {
                                showMessage("修改失败");
                            }
                        }
                    }.execute();
                });
                break;
            case R.id.rl_end_time:
                Calendar cEnd = getCalendar(mRecord.getEndTime());
                int eYear = cEnd.get(Calendar.YEAR);
                int eMonth = cEnd.get(Calendar.MONTH);
                int eDay = cEnd.get(Calendar.DAY_OF_MONTH);
                int eHour = cEnd.get(Calendar.HOUR_OF_DAY);
                int eMin = cEnd.get(Calendar.MINUTE);
                editTime(eYear, eMonth, eDay, eHour, eMin, datetime -> {
                    mRecord.setEndTime(datetime);
                    new AsyncTask<Void,Void,Long>(){

                        @Override
                        protected Long doInBackground(Void... params) {
                            return mRecord.save();
                        }

                        @Override
                        protected void onPostExecute(Long aLong) {
                            super.onPostExecute(aLong);
                            if (aLong > 0) {
                                updateTimeTime();
                            } else {
                                showMessage("修改失败");
                            }
                        }
                    }.execute();
                });
                break;
            case R.id.rl_effective:
                editStar();
                break;
        }
    }

    private static Calendar getCalendar(String datetime) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTime(new Date(TimeUtil.dateTimeToLong(datetime)));
        return calendar;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void editTime(int y, int m, int d, int h, int min, OnSelectedListener l) {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            afterDatePicked(year, month, dayOfMonth, h, min, l);
        }, y, m, d);
        dialog.show();
    }

    private void afterDatePicked(int y, int m, int d, int h, int min, OnSelectedListener l) {
        TimePickerDialog picker = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
            Calendar c = Calendar.getInstance();
            c.clear();
            c.set(y, m, d, hourOfDay, minute);
            String datetime = TimeUtil.longToDateTime(c.getTimeInMillis());
            l.onSelected(datetime);
        }, h, min, true);
        picker.show();
    }

    private void editStar() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_star, null);
        AppCompatRatingBar ratingBar = (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
        ratingBar.setRating(mRecord.getStar());
        new AlertDialog.Builder(this)
                .setTitle("修改评分")
                .setView(view)
                .setPositiveButton("修改", (dialog, which) -> {
                    float star = ratingBar.getRating();
                    mRecord.setStar((int) star);
                    new AsyncTask<Void, Void, Long>() {

                        @Override
                        protected Long doInBackground(Void... params) {
                            return mRecord.save();
                        }

                        @Override
                        protected void onPostExecute(Long aLong) {
                            super.onPostExecute(aLong);
                            if (aLong > 0) {
                                rating_bar.setRating(mRecord.getStar());
                            } else {
                                showMessage("修改失败");
                            }
                        }
                    }.execute();
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    private void updateTimeTime() {
        tv_start_time.setText(mRecord.getStartTime());
        tv_hard_time.setText(mRecord.getHardTime());
        tv_end_time.setText(mRecord.getEndTime());
    }

    interface OnSelectedListener {
        void onSelected(String datetime);
    }

}
