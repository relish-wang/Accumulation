package com.qyt.accumulation.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AlertDialog
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.orhanobut.logger.Logger
import com.qyt.accumulation.App
import com.qyt.accumulation.R
import com.qyt.accumulation.base.BaseFragment
import com.qyt.accumulation.base.IOnExchangeDataListener
import com.qyt.accumulation.entity.Goal
import com.qyt.accumulation.entity.Record
import com.qyt.accumulation.ui.activity.AccumulationActivity
import com.qyt.accumulation.ui.activity.RecordActivity
import com.qyt.accumulation.ui.view.fab.MultiFloatingActionButton
import com.qyt.accumulation.ui.view.fab.TagFabLayout
import com.qyt.accumulation.util.TimeUtil
import java.util.*

/**
 * 目标
 * Created by Relish on 2016/11/10.
 */
class GoalFragment(override val activity: Activity) : BaseFragment(), ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener, AdapterView.OnItemLongClickListener, IOnExchangeDataListener, MultiFloatingActionButton.OnFabItemClickListener {


    override fun layoutId(): Int {
        return R.layout.fragment_goal
    }

    internal var mMultiFloatingActionButton: MultiFloatingActionButton? = null
    private var mListView: ExpandableListView? = null
    private var mAdapter: GoalAdapter? = null
    private var mGoals: List<Goal> = ArrayList()
    internal var tv_no_data: TextView? = null

    override fun initViews(contentView: View) {

        mMultiFloatingActionButton = contentView.findViewById(R.id.floating_button) as MultiFloatingActionButton
        mMultiFloatingActionButton?.setOnFabItemClickListener(this)
        checkFloatingItemsStyle()

        tv_no_data = contentView.findViewById(R.id.tv_no_data) as TextView

        mGoals = ArrayList<Goal>()
        mAdapter = GoalAdapter()
        mListView = contentView.findViewById(R.id.lv_goals) as ExpandableListView
        mListView!!.setGroupIndicator(null)
        mListView!!.setAdapter(mAdapter)
        mListView!!.setOnChildClickListener(this)
        mListView!!.setOnGroupClickListener(this)
        mListView!!.onItemLongClickListener = this
        checkDataShowOrHide()
    }

    private fun checkDataShowOrHide() {
        if (mGoals == null || mGoals!!.size == 0) {
            tv_no_data?.visibility = View.VISIBLE
            mListView?.visibility = View.GONE
        } else {
            tv_no_data?.visibility = View.GONE
            mListView?.visibility = View.VISIBLE
        }
    }

    private fun checkFloatingItemsStyle() {
        val text = TypedValue()
        activity.theme.resolveAttribute(R.attr.myTextColor, text, true)
        mMultiFloatingActionButton?.setTextColor(text.data)
        val background = TypedValue()
        activity.theme.resolveAttribute(R.attr.myBackground, background, true)
        mMultiFloatingActionButton?.setTagBackgroundColor(background.data)
    }

    /**
     * 点击fab后添加目标

     * @param goalName 目标名
     */
    private fun addGoal(goalName: String) {
        object : AsyncTask<Void, Void, Long>() {

            override fun doInBackground(vararg params: Void): Long? {
                val timestamp = System.currentTimeMillis()
                val goal = Goal()
                goal.mobile = App.USER!!.mobile
                goal.id = Goal.maxId + 1
                goal.name = goalName
                goal.updateTime = TimeUtil.longToDateTime(timestamp)
                goal.setTime(timestamp)
                return goal.save()
            }

            override fun onPostExecute(aVoid: Long?) {
                super.onPostExecute(aVoid)
                if (aVoid!! > -1) {
                    update()
                } else {
                    showMessage("创建目标失败")
                }
            }
        }.execute()
    }

    override fun onFabItemClick(view: TagFabLayout, pos: Int) {
        when (pos) {
            3 -> {
                //开始积累
                val intent = Intent(activity, AccumulationActivity::class.java)
                startActivity(intent)
            }
            2 -> openAddGoalDialog()
        }
    }

    private fun openAddGoalDialog() {
        @SuppressLint("InflateParams")
        val v = activity.layoutInflater.inflate(R.layout.dialog_add_dialog, null)
        val etGoalName = v.findViewById(R.id.et_goal_name) as EditText
        etGoalName.setHint(R.string.goal_name)
        val builder = AlertDialog.Builder(activity)
        val dialog = builder.setView(v)
                .setTitle(R.string.add_goal)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ensure) { dialog, which ->
                    val goalName = etGoalName.text.toString()
                    addGoal(goalName)
                }.create()
        val m = activity.windowManager
        val d = m.defaultDisplay  //为获取屏幕宽、高
        val p = dialog.window!!.attributes  //获取对话框当前的参数值
        p.height = (d.height * 0.3).toInt()   //高度设置为屏幕的0.3
        p.width = (d.width * 0.5).toInt()    //宽度设置为屏幕的0.5
        dialog.window!!.attributes = p     //设置生效
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    override fun onChildClick(parent: ExpandableListView, v: View,
                              groupPosition: Int, childPosition: Int, id: Long): Boolean {
        RecordActivity.open(activity, mGoals!![groupPosition].records!![childPosition])
        return true
    }

    override fun onGroupClick(parent: ExpandableListView, v: View,
                              groupPosition: Int, id: Long): Boolean {
        return false
    }

    override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        val `object` = view.getTag(R.id.tv_goal_name)

        if (`object` is Record) {
            //child
            val record = `object`
            val builder = AlertDialog.Builder(activity)
            builder.setItems(R.array.record_long_click
            ) { dialog, which ->
                when (which) {
                    0 -> RecordActivity.open(activity, record)
                    1 -> AlertDialog.Builder(activity)
                            .setTitle(R.string.delete)
                            .setMessage(getString(R.string.delete_record_message, record.name))
                            .setPositiveButton(R.string.delete) { dialog, which ->
                                Record.delete(record)
                                update()
                            }
                            .setNegativeButton(R.string.cancel, null)
                            .create().show()
                    2 -> dialog.dismiss()
                }
            }
            builder.create().show()
            return true
        } else if (`object` is Goal) {
            //parent
            val group = `object`
            val builder = AlertDialog.Builder(activity)
            builder.setItems(R.array.goal_long_click) { dialog, which ->
                when (which) {
                    0 -> {
                        val v = activity.layoutInflater.inflate(
                                R.layout.dialog_add_dialog, null)
                        val etRecordName = v.findViewById(R.id.et_goal_name) as EditText
                        etRecordName.setHint(R.string.record_name)
                        AlertDialog.Builder(activity)
                                .setView(v)
                                .setTitle(R.string.add_record)
                                .setNegativeButton(R.string.cancel, null)
                                .setPositiveButton(R.string.ensure) { dialog, which ->
                                    val recordName = etRecordName.text.toString()
                                    addRecord(group, recordName)
                                }.create().show()
                    }
                    1 -> AlertDialog.Builder(activity)
                            .setTitle(R.string.delete)
                            .setMessage(getString(R.string.delete_goal_message, group.name))
                            .setPositiveButton(R.string.delete) { dialog, which ->
                                Goal.deleteItAndItsRecords(group)
                                update()
                            }
                            .setNegativeButton(R.string.cancel, null)
                            .create().show()
                    2 -> dialog.dismiss()
                }
            }
            builder.create().show()
            return true
        } else {
            //never occur.
            return false
        }
    }

    private fun addRecord(group: Goal, recordName: String) {
        val start = TimeUtil.longToDateTime(System.currentTimeMillis())
        val record = Record()
        record.id = Record.findMaxId() + 1
        record.name = recordName
        record.goalId = group.id
        record.updateTime = start
        record.createTime = start
        record.startTime = start
        record.endTime = start
        record.time = 0L
        record.star = 0
        record.save()
        update()
    }

    fun update() {
        mGoals = Goal.findAll()
        mAdapter!!.notifyDataSetChanged()
        checkDataShowOrHide()
    }


    override fun onGetValueByKey(key: String): Any? {
        return null
    }

    override fun onSendMessage(`object`: Any) {
        if (`object` is Boolean) {
            if (`object`) {
                update()
            }
        }
    }

    private inner class GoalAdapter : BaseExpandableListAdapter() {

        override fun getGroupView(groupPosition: Int, isExpanded: Boolean,
                                  convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val holder: VHGroup
            if (convertView == null) {
                convertView = LayoutInflater.from(App.CONTEXT).inflate(
                        R.layout.item_ex_goal, parent, false)
                holder = VHGroup()
                convertView!!.tag = holder
            } else {
                holder = convertView.tag as VHGroup
            }
            convertView.setTag(R.id.tv_goal_name, mGoals!![groupPosition])
            val goal = mGoals!![groupPosition]
            holder.ivExpand = convertView.findViewById(R.id.iv_expand) as ImageView
            holder.tvGoalName = convertView.findViewById(R.id.tv_goal_name) as TextView
            holder.tvUpdateTime = convertView.findViewById(R.id.tv_update_time) as TextView

            holder.ivExpand!!.setImageResource(if (isExpanded)
                R.drawable.arrow_expand
            else
                R.drawable.arrow_collapse)
            holder.tvGoalName!!.text = goal.name
            holder.tvUpdateTime!!.text = goal.updateTime
            return convertView
        }

        override fun getChildView(groupPosition: Int, childPosition: Int,
                                  isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val holder: VHChild
            if (convertView == null) {
                convertView = LayoutInflater.from(App.CONTEXT).inflate(
                        R.layout.item_ex_record, parent, false)
                holder = VHChild()
                convertView!!.tag = holder
            } else {
                holder = convertView.tag as VHChild
            }
            convertView.setTag(R.id.tv_goal_name, mGoals!![groupPosition].records!![childPosition])
            val record = mGoals!![groupPosition].records!![childPosition]
            holder.tvRecordName = convertView.findViewById(R.id.tv_record_name) as TextView
            holder.tvUpdateTime = convertView.findViewById(R.id.tv_update_time) as TextView

            holder.tvRecordName!!.text = record.name
            holder.tvUpdateTime!!.text = record.updateTime
            return convertView
        }

        internal inner class VHGroup {
            var ivExpand: ImageView? = null
            var tvGoalName: TextView? = null
            var tvUpdateTime: TextView? = null
        }

        internal inner class VHChild {
            var tvRecordName: TextView? = null
            var tvUpdateTime: TextView? = null
        }

        override fun getGroupCount(): Int {
            return if (mGoals != null) mGoals!!.size else 0
        }

        override fun getChildrenCount(groupPosition: Int): Int {
            if (mGoals != null) {
                if (groupPosition < mGoals!!.size) {
                    if (mGoals!![groupPosition] != null) {
                        if (mGoals!![groupPosition].records != null) {
                            return mGoals!![groupPosition].records!!.size
                        } else {
                            return 0
                        }
                    } else {
                        return 0
                    }
                } else {
                    Logger.e("IndexOutBoundsException")
                    return 0
                }
            } else {
                return 0
            }
        }

        override fun getGroup(groupPosition: Int): Any? {
            if (mGoals != null) {
                if (groupPosition < mGoals!!.size) {
                    return mGoals!![groupPosition]
                } else {
                    Logger.e("IndexOutBoundsException")
                    return 0
                }
            } else {
                return null
            }
        }

        override fun getChild(groupPosition: Int, childPosition: Int): Any? {
            if (mGoals != null) {
                if (groupPosition < mGoals!!.size) {
                    val goal = mGoals!![groupPosition]
                    if (goal != null) {
                        val children = goal.records
                        if (children != null) {
                            if (childPosition < children.size) {
                                return children[childPosition]
                            } else {
                                Logger.e("IndexOutBoundsException")
                                return null
                            }
                        } else {
                            return null
                        }
                    } else {
                        return null
                    }
                } else {
                    Logger.e("IndexOutBoundsException")
                    return 0
                }
            } else {
                return null
            }
        }

        override fun getGroupId(groupPosition: Int): Long {
            if (mGoals != null) {
                if (groupPosition < mGoals!!.size) {
                    val goal = mGoals!![groupPosition]
                    if (goal != null) {
                        return goal.id
                    } else {
                        return 0
                    }
                } else {
                    Logger.e("IndexOutBoundsException")
                    return 0
                }
            } else {
                return 0
            }
        }

        override fun getChildId(groupPosition: Int, childPosition: Int): Long {
            if (mGoals != null) {
                if (groupPosition < mGoals!!.size) {
                    val goal = mGoals!![groupPosition]
                    if (goal != null) {
                        val children = goal.records
                        if (children != null) {
                            if (childPosition < children.size) {
                                val record = children[childPosition]
                                if (record != null) {
                                    return record.id
                                } else {
                                    return 0
                                }
                            } else {
                                Logger.e("IndexOutBoundsException")
                                return 0
                            }
                        } else {
                            return 0
                        }
                    } else {
                        return 0
                    }
                } else {
                    Logger.e("IndexOutBoundsException")
                    return 0
                }
            } else {
                return 0
            }
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
            return true
        }
    }
}
