package cn.studyjams.s1.contest.accumulation.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.studyjams.s1.contest.accumulation.R;
import cn.studyjams.s1.contest.accumulation.base.BaseFragment;
import cn.studyjams.s1.contest.accumulation.entity.Goal;
import cn.studyjams.s1.contest.accumulation.util.Temp;

/**
 * 目标
 * Created by Relish on 2016/11/10.
 */
public class GoalFragment extends BaseFragment {

    @Override
    protected int layoutId() {
        return R.layout.fragment_goal;
    }

    RecyclerView rv;
    GoalAdapter mAdapter;
    List<Goal> goals;

    @Override
    protected void initViews(View contentView) {
        rv = (RecyclerView) contentView.findViewById(R.id.recyclerView);
        goals = Temp.getGoals();//// TODO: 2016/11/10 临时数据
        mAdapter = new GoalAdapter(R.layout.rv_item_goal, goals);
        rv.setAdapter(mAdapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    class GoalAdapter extends BaseQuickAdapter<Goal> {

        public GoalAdapter(int layoutResId, List<Goal> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder baseViewHolder, Goal goal) {
            baseViewHolder.setText(R.id.tvGoalName, goal.getName());
        }
    }
}
