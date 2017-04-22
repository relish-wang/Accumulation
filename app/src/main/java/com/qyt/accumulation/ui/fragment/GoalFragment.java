package com.qyt.accumulation.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qyt.accumulation.App;
import com.qyt.accumulation.R;
import com.qyt.accumulation.base.BaseFragment;
import com.qyt.accumulation.base.IOnExchangeDataListener;
import com.qyt.accumulation.entity.Goal;
import com.qyt.accumulation.entity.Record;
import com.qyt.accumulation.ui.activity.RecordActivity;
import com.qyt.accumulation.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 目标
 * Created by Relish on 2016/11/10.
 */
public class GoalFragment extends BaseFragment implements ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener, AdapterView.OnItemLongClickListener, IOnExchangeDataListener {


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
        mListView.setOnChildClickListener(this);
        mListView.setOnGroupClickListener(this);
        mListView.setOnItemLongClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoals == null || mGoals.size() == 0) {
            mGoals = Goal.findAll();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        Record record = mGoals.get(groupPosition).getRecords().get(childPosition);
        Intent intent = new Intent(getActivity(), RecordActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("record", record);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
        return false;
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v,
                                int groupPosition, long id) {
        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Object object = view.getTag(R.id.tv_goal_name);

        if (object instanceof Record) {
            //child
            Record record = (Record) object;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(R.array.record_long_click, (dialog, which) -> {
                switch (which) {
                    case 0:
                        Intent intent = new Intent(getActivity(), RecordActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("record", record);
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                        break;
                    case 1:
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.delete)
                                .setMessage(getString(R.string.delete_record_message, record.getName()))
                                .setPositiveButton(R.string.delete, (dialog1, which1) -> {
                                    Record.delete(record);
                                    update();
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .create().show();
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
            });
            builder.create().show();
            return true;
        } else if (object instanceof Goal) {
            //parent
            Goal group = (Goal) object;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(R.array.goal_long_click, (dialog, which) -> {
                switch (which) {
                    case 0:
                        View v = getActivity().getLayoutInflater().inflate(
                                R.layout.dialog_add_dialog, null);
                        EditText etRecordName = (EditText) v.findViewById(R.id.et_goal_name);
                        etRecordName.setHint(R.string.record_name);
                        new AlertDialog.Builder(getActivity())
                                .setView(v)
                                .setTitle(R.string.add_record)
                                .setNegativeButton(R.string.cancel, null)
                                .setPositiveButton(R.string.ensure, (DialogInterface d, int i) -> {
                                    String recordName = etRecordName.getText().toString();
                                    addRecord(group, recordName);
                                }).create().show();
                        break;
                    case 1:
                        new AlertDialog.Builder(getActivity())
                                .setTitle(R.string.delete)
                                .setMessage(getString(R.string.delete_goal_message, group.getName()))
                                .setPositiveButton(R.string.delete, (dialog1, which1) -> {
                                    Goal.deleteItAndItsRecords(group);
                                    update();
                                })
                                .setNegativeButton(R.string.cancel, null)
                                .create().show();
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
            });
            builder.create().show();
            return true;
        } else {
            //never occur.
            return false;
        }
    }

    private void addRecord(Goal group, String recordName) {
        Record record = new Record();
        record.setId(Record.findMaxId() + 1);
        record.setName(recordName);
        record.setGoalId(group.getId());
        record.setUpdateTime(TimeUtil.longToDateTime(System.currentTimeMillis()));
        record.setCreateTime(TimeUtil.longToDateTime(System.currentTimeMillis()));
        record.setTime(0L);
        record.setStar(0);
        record.save();
        update();
    }

    public void update() {
        mGoals = Goal.findAll();
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public Object onGetValueByKey(String key) {
        return null;
    }

    @Override
    public void onSendMessage(Object object) {
        if (object instanceof Boolean) {
            if ((Boolean) object) {
                update();
            }
        }
    }

    class GoalAdapter extends BaseExpandableListAdapter {

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            VHGroup holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(App.CONTEXT).inflate(
                        R.layout.item_ex_goal, parent, false);
                holder = new VHGroup();
                convertView.setTag(holder);
            } else {
                holder = (VHGroup) convertView.getTag();
            }
            convertView.setTag(R.id.tv_goal_name, mGoals.get(groupPosition));
            Goal goal = mGoals.get(groupPosition);
            holder.ivExpand = (ImageView) convertView.findViewById(R.id.iv_expand);
            holder.tvGoalName = (TextView) convertView.findViewById(R.id.tv_goal_name);
            holder.tvUpdateTime = (TextView) convertView.findViewById(R.id.tv_update_time);

            holder.ivExpand.setImageResource(isExpanded ?
                    R.drawable.arrow_expand : R.drawable.arrow_collapse);
            holder.tvGoalName.setText(goal.getName());
            holder.tvUpdateTime.setText(goal.getUpdateTime());
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            VHChild holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(App.CONTEXT).inflate(
                        R.layout.item_ex_record, parent, false);
                holder = new VHChild();
                convertView.setTag(holder);
            } else {
                holder = (VHChild) convertView.getTag();
            }
            convertView.setTag(R.id.tv_goal_name, mGoals.get(groupPosition).getRecords().get(childPosition));
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
            return true;
        }
    }
}
