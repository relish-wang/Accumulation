package com.qyt.accumulation.ui.fragment;
//123
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qyt.accumulation.App;
import com.qyt.accumulation.R;
import com.qyt.accumulation.base.BaseFragment;
import com.qyt.accumulation.entity.Goal;
import com.qyt.accumulation.entity.Record;
import com.qyt.accumulation.ui.view.chart.Chart;
import com.qyt.accumulation.ui.view.chart.Pillar;
import com.qyt.accumulation.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Relish on 2016/11/13.
 */

public class ChartFragment extends BaseFragment {

    @Override
    protected int layoutId() {
        return R.layout.fragment_chart;
    }

    TextView tvGoalName;
    TextView tvHour;
    TextView tvMinute;
    Chart chart;
    RecyclerView rvRecords;
    Goal mGoal;
    List<Record> mRecords;
    ChildAdapter adapter;

    public static ChartFragment getInstance(Goal goal) {
        ChartFragment fragment = new ChartFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("goal", goal);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initViews(View contentView) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mGoal = (Goal) bundle.getSerializable("goal");
        }
        tvGoalName = (TextView) contentView.findViewById(R.id.tvGoalName);
        tvHour = (TextView) contentView.findViewById(R.id.tvHour);
        tvMinute = (TextView) contentView.findViewById(R.id.tvMinute);

        tvGoalName.setText(mGoal.getName());
        tvHour.setText(String.valueOf(mGoal.getHardHour()));
        tvMinute.setText(String.valueOf(mGoal.getHardMinute()));

        chart = (Chart) contentView.findViewById(R.id.chart);
        chart.setPillars(getPillars(mGoal));
        rvRecords = (RecyclerView) contentView.findViewById(R.id.rvRecords);
        mRecords = mGoal == null ? new ArrayList<>() : mGoal.getRecords();
        adapter = new ChildAdapter(R.layout.rv_item_record, mRecords);
        rvRecords.setAdapter(adapter);
        rvRecords.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        //测试数据
        mRecords = mGoal.getRecords();
        adapter.setNewData(mRecords);
        chart.setPillars(getPillarsTemp(mRecords));
    }

    private static List<Pillar> getPillarsTemp(List<Record> records) {
        List<Pillar> pillars = new ArrayList<>();
        String[] day = App.CONTEXT.getResources().getStringArray(R.array.week);
        long[] values = {0, 0, 0, 0, 0, 0, 0};
        for (Record r : records) {
            values[TimeUtil.dayForWeek(r.getCreateTime())] = r.getTime();
        }
        for (int i = 0; i < 7; i++) {
            pillars.add(new Pillar(day[i], values[i]));
        }
        return pillars;
    }

    private static List<Pillar> getPillars(Goal goal) {
        List<Pillar> pillars = new ArrayList<>();
        String[] day = App.CONTEXT.getResources().getStringArray(R.array.week);
        long[] values = {0, 0, 0, 0, 0, 0, 0};
        List<Record> records = goal.getRecords();
        for (Record r : records) {
            values[TimeUtil.dayForWeek(r.getCreateTime())] = r.getTime();
        }
        for (int i = 0; i < 7; i++) {
            pillars.add(new Pillar(day[i], values[i]));
        }
        return pillars;
    }

    private class ChildAdapter extends BaseQuickAdapter<Record> {

        ChildAdapter(int layoutResId, List<Record> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, Record record) {
            holder.setText(R.id.tvRecordName, record.getName());
            holder.setText(R.id.tvHardTime, mGoal.getHardTime());
        }
    }
}
