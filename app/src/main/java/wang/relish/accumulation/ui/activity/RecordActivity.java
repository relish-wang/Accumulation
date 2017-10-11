package wang.relish.accumulation.ui.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import wang.relish.accumulation.App;
import wang.relish.accumulation.R;
import wang.relish.accumulation.adapter.GoalsAdapter;
import wang.relish.accumulation.base.BaseActivity;
import wang.relish.accumulation.entity.Goal;
import wang.relish.accumulation.entity.Record;
import wang.relish.accumulation.greendao.DaoSession;
import wang.relish.accumulation.greendao.RecordDao;
import wang.relish.accumulation.util.TimeUtil;

/**
 * <pre>
 *     author : 王鑫
 *     e-mail : wangxin@souche.com
 *     time   : 2017/2/22
 *     desc   :
 *     version: 1.0
 * </pre>
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

    private boolean isTitled = false;

    @Override
    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {
        Goal goal = App.getParent(mRecord);
        isTitled = goal == null || goal.getId() == App.UNTITLED_GOAL_ID;
        String goalName = isTitled ? "未分类" : goal.getName();
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
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            App.getDaosession().getRecordDao().save(mRecord);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            isEditing = false;
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
                editTime(sYear, sMonth, sDay, sHour, sMin,
                        new OnSelectedListener() {
                            @Override
                            public void onSelected(String datetime) {
                                mRecord.setStartTime(datetime);
                                mRecord.setUpdateTime(TimeUtil.longToDateTime(System.currentTimeMillis()));
                                new AsyncTask<Void, Void, Void>() {

                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        App.getDaosession().getRecordDao().update(mRecord);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);
                                        updateTimeTime();
                                    }
                                }.execute();
                            }
                        });
                break;
            case R.id.rl_end_time:
                Calendar cEnd = getCalendar(mRecord.getEndTime());
                int eYear = cEnd.get(Calendar.YEAR);
                int eMonth = cEnd.get(Calendar.MONTH);
                int eDay = cEnd.get(Calendar.DAY_OF_MONTH);
                int eHour = cEnd.get(Calendar.HOUR_OF_DAY);
                int eMin = cEnd.get(Calendar.MINUTE);
                editTime(eYear, eMonth, eDay, eHour, eMin, new OnSelectedListener() {
                    @Override
                    public void onSelected(String datetime) {
                        mRecord.setEndTime(datetime);
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... params) {
                                App.getDaosession().getRecordDao().update(mRecord);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                updateTimeTime();
                            }
                        }.execute();
                    }
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
    private void editTime(final int y, final int m, final int d, final int h, final int min, final OnSelectedListener l) {
        final DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                afterDatePicked(year, month, dayOfMonth, h, min, l);
            }
        }, y, m, d);
        dialog.show();
    }

    private void afterDatePicked(final int y, final int m, final int d, final int h, final int min, final OnSelectedListener l) {
        final TimePickerDialog picker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                Calendar c = Calendar.getInstance();
                c.clear();
                c.set(y, m, d, hourOfDay, minute);
                String datetime = TimeUtil.longToDateTime(c.getTimeInMillis());
                l.onSelected(datetime);
            }
        }, h, min, true);
        picker.show();
    }

    private void editStar() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_star, null);
        final AppCompatRatingBar ratingBar = (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
        ratingBar.setRating(mRecord.getStar());
        new AlertDialog.Builder(this)
                .setTitle("修改评分")
                .setView(view)
                .setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        float star = ratingBar.getRating();
                        mRecord.setStar((int) star);
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... params) {
                                App.getDaosession().getRecordDao().save(mRecord);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                rating_bar.setRating(mRecord.getStar());
                            }
                        }.execute();
                    }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isTitled) {
            getMenuInflater().inflate(R.menu.record, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_title_it:
                onSaveIntoGoalClick(mRecord);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 存入目标
     *
     * @param record 记录
     */
    private void onSaveIntoGoalClick(final Record record) {
        final List<Goal> goals = App.findAllGoalsWithoutUntitled();
        if (goals == null || goals.size() == 0) {
            showMessage("暂无目标可存");
            return;
        }
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.dialog_goals, null);
        ListView lv_goals = (ListView) view.findViewById(R.id.lv_goals);
        GoalsAdapter adapter = new GoalsAdapter(goals);
        lv_goals.setAdapter(adapter);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("存入目标")
                .setView(view)
                .create();
        lv_goals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view1, final int position, long id) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        final long oldId = record.getId();
                        record.setGoalId(goals.get(position).getId());
                        record.setUpdateTime(TimeUtil.getNowTime());
                        final DaoSession daosession = App.getDaosession();
                        daosession.runInTx(new Runnable() {
                            @Override
                            public void run() {
                                RecordDao recordDao = daosession.getRecordDao();
                                recordDao.save(record);
                                recordDao.deleteByKey(oldId);//根据主键删除
                            }
                        });
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Goal goal = App.getParent(record);
                        mToolbar.setTitle(goal.getName());
                        dialog.dismiss();
                    }
                }.execute();
            }
        });
        dialog.show();
    }

    interface OnSelectedListener {
        void onSelected(String datetime);
    }

}
