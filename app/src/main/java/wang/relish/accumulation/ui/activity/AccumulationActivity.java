package wang.relish.accumulation.ui.activity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import wang.relish.accumulation.R;
import wang.relish.accumulation.adapter.GoalsAdapter;
import wang.relish.accumulation.base.BaseActivity;
import wang.relish.accumulation.entity.Goal;
import wang.relish.accumulation.entity.Record;
import wang.relish.accumulation.util.TimeUtil;

/**
 * <pre>
 *     author : 王鑫
 *     e-mail : wangxin@souche.com
 *     time   : 2017/05/02
 *     desc   : 积累
 *     version: 1.0
 * </pre>
 */
public class AccumulationActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected int layoutId() {
        return R.layout.activity_accumulation;
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState, Toolbar mToolbar) {
        mToolbar.setTitle("开始积累");
    }

    private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setText(time);
        }
    };

    private boolean running = false;
    private long time;
    private TextView tv_hour, tv_min, tv_sec;
    private View ll_stop;
    private Button btn_start, btn_stop, btn_stop_and_save;
    private Record mRecord;
    private long startTime;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        tv_hour = (TextView) findViewById(R.id.tv_hour);
        tv_min = (TextView) findViewById(R.id.tv_min);
        tv_sec = (TextView) findViewById(R.id.tv_sec);

        btn_start = (Button) findViewById(R.id.btn_start);
        ll_stop = findViewById(R.id.ll_stop);
        btn_stop = (Button) findViewById(R.id.btn_stop);
        btn_stop_and_save = (Button) findViewById(R.id.btn_stop_and_save);
        btn_start.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_stop_and_save.setOnClickListener(this);

        btn_start.setVisibility(View.VISIBLE);
        ll_stop.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start:
                start();
                break;
            case R.id.btn_stop:
                running = false;
                stop();
                break;
            case R.id.btn_stop_and_save:
                running = false;
                stopAndSave();
                break;
        }
    }


    private void start() {
        running = true;
        btn_start.setVisibility(View.GONE);
        ll_stop.setVisibility(View.VISIBLE);
        mRecord = new Record();
        startTime = TimeUtil.dateTimeToLong(mRecord.getStartTime());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    time += 1;
                    handle.sendEmptyMessage(1);
                }
            }
        }).start();
    }

    private Record generateTempRecord() {
        long currentTime = System.currentTimeMillis();
        long hardTime = currentTime - startTime;
        long untitled = 1L;//Record.findMaxIdWhereGoalIdIs(0) + 1;
        String start = TimeUtil.longToDateTime(startTime);
        Record record = new Record();
        record.setGoalId(0);
        record.setId(untitled);
        record.setCreateTime(start);
        record.setStartTime(start);
        record.setName("未命名_" + untitled);
        record.setStar(1);
        record.setUpdateTime(start);
        record.setTime(hardTime);
        record.setEndTime(TimeUtil.longToDateTime(currentTime));
        return record;
    }

    /**
     * 停止积累
     */
    private void stop() {
        final Record record = generateTempRecord();
        new AlertDialog.Builder(this)
                .setTitle("保存")
                .setMessage("是否保存这次积累？")
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onSaveClick(record);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onCancelClick(record);
                    }
                })
                .create()
                .show();
    }

    /**
     * 直接保存
     *
     * @param record 记录
     */
    private void onCancelClick(final Record record) {
        new AsyncTask<Void, Void, Long>() {

            @Override
            protected Long doInBackground(Void... params) {
                return 1L;// TODO record.save();
            }

            @Override
            protected void onPostExecute(Long aLong) {
                super.onPostExecute(aLong);
                if (aLong > 0) {
                    showMessage("此积累已保存至未分类中");
                    finish();
                } else {
                    showMessage("保存失败！");
                }
            }
        }.execute();
    }

    /**
     * 保存记录
     *
     * @param record 记录
     */
    private void onSaveClick(final Record record) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_record, null);
        final EditText et_name = (EditText) view.findViewById(R.id.et_record_name);
        final AppCompatRatingBar ratingBar = (AppCompatRatingBar) view.findViewById(R.id.rating_bar);
        final EditText et_info = (EditText) view.findViewById(R.id.et_info);
        TextView tv_start_time = (TextView) view.findViewById(R.id.tv_start_time);
        TextView tv_end_time = (TextView) view.findViewById(R.id.tv_end_time);
        TextView tv_hard_time = (TextView) view.findViewById(R.id.tv_hard_time);

        ratingBar.setRating(record.getStar());
        tv_start_time.setText(record.getStartTime());
        tv_end_time.setText(record.getEndTime());
        tv_hard_time.setText("");// TODO record.getHardTime());

        new AlertDialog.Builder(this)
                .setTitle("保存")
                .setView(view)
                .setPositiveButton("存入目标", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = et_name.getText().toString().trim();
                        String note = et_info.getText().toString().trim();
                        float star = ratingBar.getRating();
                        record.setName(name);
                        record.setStar((int) star);
                        record.setNote(note);
                        onSaveIntoGoalClick(record);
                    }
                })
                .setNegativeButton("直接保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = et_name.getText().toString().trim();
                        String note = et_info.getText().toString().trim();
                        float star = ratingBar.getRating();
                        record.setName(name);
                        record.setStar((int) star);
                        record.setNote(note);
                        new AsyncTask<Void, Void, Long>() {

                            @Override
                            protected Long doInBackground(Void... params) {
                                return 1L; // TODO record.save();
                            }

                            @Override
                            protected void onPostExecute(Long aLong) {
                                super.onPostExecute(aLong);
                                if (aLong > 0) {
                                    finish();
                                } else {
                                    showMessage("保存失败！");
                                }
                            }
                        }.execute();
                    }
                })
                .create()
                .show();
    }


    /**
     * 存入目标
     *
     * @param record 记录
     */
    private void onSaveIntoGoalClick(final Record record) {
        List<Goal> goal = new ArrayList<>();// TODO Goal.findAll();
        if (goal == null || goal.size() == 0) {
            showMessage("暂无目标可存");
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_goals, null);
        final List<Goal> goals = new ArrayList<>();// TODO Goal.findAll();
        ListView lv_goals = (ListView) view.findViewById(R.id.lv_goals);
        GoalsAdapter adapter = new GoalsAdapter(goals);
        lv_goals.setAdapter(adapter);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("存入目标")
                .setView(view)
                .create();
        lv_goals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AsyncTask<Void, Void, Long>() {

                    @Override
                    protected Long doInBackground(Void... params) {
                        record.setGoalId(goals.get(position).getId());
                        record.setId(1L);// TODO Record.findMaxId() + 1);
                        return 1L;//record.save();
                    }

                    @Override
                    protected void onPostExecute(Long aLong) {
                        super.onPostExecute(aLong);
                        if (aLong > 0) {
                            finish();
                        } else {
                            showMessage("保存失败！");
                        }
                    }
                }.execute();
            }
        });
        dialog.show();
    }


    /**
     * 停止并保存
     */
    private void stopAndSave() {
        Record record = generateTempRecord();
        onSaveClick(record);
    }

    private void setText(long sec) {
        long h = sec / 60 / 60;
        long m = sec / 60 % 60;
        long s = sec % 60;
        tv_hour.setText(String.format(Locale.CHINA, "%02d", h));
        tv_min.setText(String.format(Locale.CHINA, "%02d", m));
        tv_sec.setText(String.format(Locale.CHINA, "%02d", s));
    }
}
