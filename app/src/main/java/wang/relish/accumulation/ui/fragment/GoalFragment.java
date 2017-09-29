package wang.relish.accumulation.ui.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import wang.relish.accumulation.App;
import wang.relish.accumulation.R;
import wang.relish.accumulation.base.BaseFragment;
import wang.relish.accumulation.base.IOnExchangeDataListener;
import wang.relish.accumulation.entity.Goal;
import wang.relish.accumulation.entity.Record;
import wang.relish.accumulation.ui.activity.AccumulationActivity;
import wang.relish.accumulation.ui.activity.RecordActivity;
import wang.relish.accumulation.ui.view.fab.MultiFloatingActionButton;
import wang.relish.accumulation.ui.view.fab.TagFabLayout;
import wang.relish.accumulation.util.TimeUtil;

/**
 * 目标
 * Created by Relish on 2016/11/10.
 */
public class GoalFragment extends BaseFragment implements ExpandableListView.OnChildClickListener,
        ExpandableListView.OnGroupClickListener, AdapterView.OnItemLongClickListener, IOnExchangeDataListener,
        MultiFloatingActionButton.OnFabItemClickListener {


    @Override
    protected int layoutId() {
        return R.layout.fragment_goal;
    }

    MultiFloatingActionButton mMultiFloatingActionButton;
    private ExpandableListView mListView;
    private GoalAdapter mAdapter;
    private List<Goal> mGoals = new ArrayList<>();
    TextView tv_no_data;

    @Override
    protected void initViews(View contentView) {

        mMultiFloatingActionButton = (MultiFloatingActionButton) contentView.findViewById(R.id.floating_button);
        mMultiFloatingActionButton.setOnFabItemClickListener(this);
        checkFloatingItemsStyle();

        tv_no_data = (TextView) contentView.findViewById(R.id.tv_no_data);

        mGoals = new ArrayList<>();
        mAdapter = new GoalAdapter();
        mListView = (ExpandableListView) contentView.findViewById(R.id.lv_goals);
        mListView.setGroupIndicator(null);
        mListView.setAdapter(mAdapter);
        mListView.setOnChildClickListener(this);
        mListView.setOnGroupClickListener(this);
        mListView.setOnItemLongClickListener(this);
        checkDataShowOrHide();
    }

    private void checkDataShowOrHide() {
        if (mGoals == null || mGoals.size() == 0) {
            tv_no_data.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        } else {
            tv_no_data.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    private void checkFloatingItemsStyle() {
        TypedValue text = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.myTextColor, text, true);
        mMultiFloatingActionButton.setTextColor(text.data);
        TypedValue background = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.myBackground, background, true);
        mMultiFloatingActionButton.setTagBackgroundColor(background.data);
    }

    /**
     * 点击fab后添加目标
     *
     * @param goalName 目标名
     */
    private void addGoal(final String goalName) {
        new AsyncTask<Void, Void, Long>() {

            @Override
            protected Long doInBackground(Void... params) {
                long timestamp = System.currentTimeMillis();
                Goal goal = new Goal();
                goal.setId(Goal.getMaxId() + 1);
                goal.setName(goalName);
                goal.setUpdateTime(TimeUtil.longToDateTime(timestamp));
                goal.setTime(timestamp);
                return goal.save();
            }

            @Override
            protected void onPostExecute(Long aVoid) {
                super.onPostExecute(aVoid);
                if (aVoid > -1) {
                    update();
                } else {
                    showMessage("创建目标失败");
                }
            }
        }.execute();
    }

    @Override
    public void onFabItemClick(TagFabLayout view, int pos) {
        switch (pos) {
            case 3:
                //开始积累
                Intent intent = new Intent(getActivity(), AccumulationActivity.class);
                startActivity(intent);
                break;
            case 2:
                openAddGoalDialog();
                break;
        }
    }

    private void openAddGoalDialog() {
        @SuppressLint("InflateParams")
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_dialog, null);
        final EditText etGoalName = (EditText) v.findViewById(R.id.et_goal_name);
        etGoalName.setHint(R.string.goal_name);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.setView(v)
                .setTitle(R.string.add_goal)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String goalName = etGoalName.getText().toString();
                        addGoal(goalName);
                    }
                }).create();
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.3);   //高度设置为屏幕的0.3
        p.width = (int) (d.getWidth() * 0.5);    //宽度设置为屏幕的0.5
        dialog.getWindow().setAttributes(p);     //设置生效
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        RecordActivity.open(getActivity(), mGoals.get(groupPosition).getRecords().get(childPosition));
        return true;
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
            final Record record = (Record) object;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(R.array.record_long_click, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            RecordActivity.open(getActivity(), record);
                            break;
                        case 1:
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.delete)
                                    .setMessage(getString(R.string.delete_record_message, record.getName()))
                                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Record.delete(record);
                                            update();
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, null)
                                    .create().show();
                            break;
                        case 2:
                            dialog.dismiss();
                            break;
                    }
                }
            });
            builder.create().show();
            return true;
        } else if (object instanceof Goal) {
            //parent
            final Goal group = (Goal) object;
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(R.array.goal_long_click, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            View v = getActivity().getLayoutInflater().inflate(
                                    R.layout.dialog_add_dialog, null);
                            final EditText etRecordName = (EditText) v.findViewById(R.id.et_goal_name);
                            etRecordName.setHint(R.string.record_name);
                            new AlertDialog.Builder(getActivity())
                                    .setView(v)
                                    .setTitle(R.string.add_record)
                                    .setNegativeButton(R.string.cancel, null)
                                    .setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface d, int i) {
                                            String recordName = etRecordName.getText().toString();
                                            addRecord(group, recordName);
                                        }
                                    }).create().show();
                            break;
                        case 1:
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.delete)
                                    .setMessage(getString(R.string.delete_goal_message, group.getName()))
                                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Goal.deleteItAndItsRecords(group);
                                            update();
                                        }
                                    })
                                    .setNegativeButton(R.string.cancel, null)
                                    .create().show();
                            break;
                        case 2:
                            dialog.dismiss();
                            break;
                    }
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
        String start = TimeUtil.longToDateTime(System.currentTimeMillis());
        Record record = new Record();
        record.setId(Record.findMaxId() + 1);
        record.setName(recordName);
        record.setGoalId(group.getId());
        record.setUpdateTime(start);
        record.setCreateTime(start);
        record.setStartTime(start);
        record.setEndTime(start);
        record.setTime(0L);
        record.setStar(0);
        record.save();
        update();
    }

    public void update() {
        mGoals = Goal.findAll();
        mAdapter.notifyDataSetChanged();
        checkDataShowOrHide();
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

    private class GoalAdapter extends BaseExpandableListAdapter {

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
