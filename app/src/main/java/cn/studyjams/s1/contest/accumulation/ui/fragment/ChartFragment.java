package cn.studyjams.s1.contest.accumulation.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.studyjams.s1.contest.accumulation.R;
import cn.studyjams.s1.contest.accumulation.base.BaseFragment;
import cn.studyjams.s1.contest.accumulation.entity.Goal;
import cn.studyjams.s1.contest.accumulation.entity.Record;
import cn.studyjams.s1.contest.accumulation.util.Temp;
import cn.studyjams.s1.contest.accumulation.util.TimeUtil;
import cn.studyjams.s1.contest.accumulation.ui.view.chart.Chart;
import cn.studyjams.s1.contest.accumulation.ui.view.chart.Pillar;

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
        //// TODO: 2016/11/13 获取数据 
        if (bundle != null) {
            mGoal = (Goal) bundle.getSerializable("goal");
        }
        tvGoalName = (TextView) contentView.findViewById(R.id.tvGoalName);
        tvHour = (TextView) contentView.findViewById(R.id.tvHour);
        tvMinute = (TextView) contentView.findViewById(R.id.tvMinute);
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
        //// TODO: 2016/11/13 获取records，更新列表
        //测试数据
        mRecords = Temp.getRecords();
        adapter.setNewData(mRecords);
        chart.setPillars(getPillarsTemp(mRecords));
    }

    private static List<Pillar> getPillarsTemp(List<Record> records) {
        List<Pillar> pillars = new ArrayList<>();
        String[] day = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
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
        String[] day = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
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

    class ChildAdapter extends BaseQuickAdapter<Record> {

        public ChildAdapter(int layoutResId, List<Record> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder holder, Record record) {
            holder.setText(R.id.tvRecordName, record.getName());
            holder.setText(R.id.tvHardTime, mGoal.getHardTime());
        }
    }
}
