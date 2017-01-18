package cn.studyjams.s1.contest.accumulation.ui.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.studyjams.s1.contest.accumulation.App;
import cn.studyjams.s1.contest.accumulation.R;
import cn.studyjams.s1.contest.accumulation.base.BaseFragment;
import cn.studyjams.s1.contest.accumulation.entity.Goal;
import cn.studyjams.s1.contest.accumulation.entity.Record;

/**
 * 目标
 * Created by Relish on 2016/11/10.
 */
public class GoalFragment extends BaseFragment {

    @Override
    protected int layoutId() {
        return R.layout.fragment_goal;
    }

    private ExpandableListView mListView;
    private GoalAdapter mAdapter;
    private List<Goal> mGoals;

    @Override
    protected void initViews(View contentView) {
        mGoals = new ArrayList<>();//Temp.getGoals();//// TODO: 2016/11/10 临时数据
        mAdapter = new GoalAdapter();
        mListView = (ExpandableListView) contentView.findViewById(R.id.lv_goals);
        mListView.setGroupIndicator(null);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoals == null || mGoals.size() == 0) {
            mGoals = Goal.findAll();
            mAdapter.notifyDataSetChanged();
        }
    }

    class GoalAdapter extends BaseExpandableListAdapter  {

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            VHGroup holder;
            if(convertView==null){
                convertView = LayoutInflater.from(App.CONTEXT).inflate(R.layout.item_ex_goal,parent,false);
                holder = new VHGroup();
                convertView.setTag(holder);
            }else{
                holder = (VHGroup) convertView.getTag();
            }
            Goal goal = mGoals.get(groupPosition);
            holder.ivExpand = (ImageView) convertView.findViewById(R.id.iv_expand);
            holder.tvGoalName = (TextView) convertView.findViewById(R.id.tv_goal_name);
            holder.tvUpdateTime = (TextView) convertView.findViewById(R.id.tv_update_time);

            holder.ivExpand.setImageResource(isExpanded ? R.drawable.arrow_expand : R.drawable.arrow_collapse);
            holder.tvGoalName.setText(goal.getName());
            holder.tvUpdateTime.setText(goal.getUpdateTime());
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            VHChild holder;
            if(convertView==null){
                convertView = LayoutInflater.from(App.CONTEXT).inflate(R.layout.item_ex_record,parent,false);
                holder = new VHChild();
                convertView.setTag(holder);
            }else{
                holder = (VHChild) convertView.getTag();
            }
            Record record = mGoals.get(groupPosition).getRecords().get(childPosition);
            holder.tvRecordName = (TextView) convertView.findViewById(R.id.tv_record_name);
            holder.tvUpdateTime = (TextView) convertView.findViewById(R.id.tv_update_time);

            holder.tvRecordName.setText(record.getName());
            holder.tvUpdateTime.setText(record.getUpdateTime());
            return convertView;
        }

        class VHGroup {
            ImageView ivExpand;
            TextView tvGoalName;
            TextView tvUpdateTime;
        }

        class VHChild {
            TextView tvRecordName;
            TextView tvUpdateTime;
        }

        @Override
        public int getGroupCount() {
            return mGoals != null ? mGoals.size() : 0;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            if (mGoals != null) {
                if (groupPosition < mGoals.size()) {
                    if (mGoals.get(groupPosition) != null) {
                        if (mGoals.get(groupPosition).getRecords() != null) {
                            return mGoals.get(groupPosition).getRecords().size();
                        } else {
                            return 0;
                        }
                    } else {
                        return 0;
                    }
                } else {
                    Logger.e("IndexOutBoundsException");
                    return 0;
                }
            } else {
                return 0;
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            if (mGoals != null) {
                if (groupPosition < mGoals.size()) {
                    return mGoals.get(groupPosition);
                } else {
                    Logger.e("IndexOutBoundsException");
                    return 0;
                }
            } else {
                return null;
            }
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            if (mGoals != null) {
                if (groupPosition < mGoals.size()) {
                    Goal goal = mGoals.get(groupPosition);
                    if (goal != null) {
                        List<Record> children = goal.getRecords();
                        if (children != null) {
                            if (childPosition < children.size()) {
                                return children.get(childPosition);
                            } else {
                                Logger.e("IndexOutBoundsException");
                                return null;
                            }
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
                } else {
                    Logger.e("IndexOutBoundsException");
                    return 0;
                }
            } else {
                return null;
            }
        }

        @Override
        public long getGroupId(int groupPosition) {
            if (mGoals != null) {
                if (groupPosition < mGoals.size()) {
                    Goal goal = mGoals.get(groupPosition);
                    if (goal != null) {
                        return goal.getId();
                    } else {
                        return 0;
                    }
                } else {
                    Logger.e("IndexOutBoundsException");
                    return 0;
                }
            } else {
                return 0;
            }
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            if (mGoals != null) {
                if (groupPosition < mGoals.size()) {
                    Goal goal = mGoals.get(groupPosition);
                    if (goal != null) {
                        List<Record> children = goal.getRecords();
                        if (children != null) {
                            if (childPosition < children.size()) {
                                Record record = children.get(childPosition);
                                if (record != null) {
                                    return record.getId();
                                } else {
                                    return 0;
                                }
                            } else {
                                Logger.e("IndexOutBoundsException");
                                return 0;
                            }
                        } else {
                            return 0;
                        }
                    } else {
                        return 0;
                    }
                } else {
                    Logger.e("IndexOutBoundsException");
                    return 0;
                }
            } else {
                return 0;
            }
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
