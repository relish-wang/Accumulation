package wang.relish.accumulation.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import wang.relish.accumulation.App
import wang.relish.accumulation.R
import wang.relish.accumulation.base.BaseFragment
import wang.relish.accumulation.entity.Goal
import wang.relish.accumulation.entity.Record
import wang.relish.accumulation.ui.view.chart.Chart
import wang.relish.accumulation.ui.view.chart.Pillar
import wang.relish.accumulation.util.TimeUtil
import java.util.*

/**
 * Created by Relish on 2016/11/13.
 */

class ChartFragment(override val activity: Activity) : BaseFragment() {

    override fun layoutId(): Int {
        return R.layout.fragment_chart
    }

    internal var tvGoalName: TextView? = null
    internal var tvHour: TextView? = null
    internal var tvMinute: TextView? = null
    internal var chart: Chart? = null
    internal var rvRecords: RecyclerView? = null
    internal var mGoal: Goal? = null
    internal var mRecords: List<Record>? = null
    internal var adapter: ChildAdapter? = null

    override fun initViews(contentView: View) {
        val bundle = arguments
        if (bundle != null) {
            mGoal = bundle.getSerializable("goal") as Goal
        }
        tvGoalName = contentView.findViewById(R.id.tvGoalName) as TextView
        tvHour = contentView.findViewById(R.id.tvHour) as TextView
        tvMinute = contentView.findViewById(R.id.tvMinute) as TextView

        tvGoalName?.text = mGoal!!.name
        tvHour?.text = mGoal!!.hardHour.toString()
        tvMinute?.text = mGoal!!.hardMinute.toString()

        chart = contentView.findViewById(R.id.chart) as Chart
        chart?.setPillars(getPillars(mGoal!!))
        rvRecords = contentView.findViewById(R.id.rvRecords) as RecyclerView
        mRecords = if (mGoal == null) ArrayList<Record>() else mGoal!!.getRecords()
        adapter = ChildAdapter(R.layout.rv_item_record, mRecords!!)
        rvRecords?.adapter = adapter
        rvRecords?.layoutManager = LinearLayoutManager(activity)
    }

    override fun onResume() {
        super.onResume()
        //测试数据
        mRecords = mGoal!!.getRecords()
        adapter?.setNewData(mRecords)
        chart?.setPillars(getPillarsTemp(mRecords!!))
    }

    inner class ChildAdapter internal constructor(layoutResId: Int, data: List<Record>) : BaseQuickAdapter<Record>(layoutResId, data) {

        override fun convert(holder: BaseViewHolder, record: Record) {
            holder.setText(R.id.tvRecordName, record.name)
            holder.setText(R.id.tvHardTime, record.hardTime)
        }
    }

    companion object {

        fun getInstance(goal: Goal): ChartFragment {
            val fragment = ChartFragment(Activity())
            val bundle = Bundle()
            bundle.putSerializable("goal", goal)
            fragment.arguments = bundle
            return fragment
        }

        private fun getPillarsTemp(records: List<Record>): List<Pillar> {
            val pillars = ArrayList<Pillar>()
            val day = App.CONTEXT!!.resources.getStringArray(R.array.week)
            val values = longArrayOf(0, 0, 0, 0, 0, 0, 0)
            for (r in records) {
                values[TimeUtil.dayForWeek(r.startTime as String)] = r.time!! / 1000 / 60
            }
            for (i in 0..6) {
                pillars.add(Pillar(day[i], values[i].toDouble()))
            }
            return pillars
        }

        private fun getPillars(goal: Goal): List<Pillar> {
            val pillars = ArrayList<Pillar>()
            val day = App.CONTEXT!!.resources.getStringArray(R.array.week)
            val values = longArrayOf(0, 0, 0, 0, 0, 0, 0)
            val records = goal.getRecords()
            for (r in records!!) {
                values[TimeUtil.dayForWeek(r.startTime as String)] = r.time!! / 1000 / 60
            }
            for (i in 0..6) {
                pillars.add(Pillar(day[i], values[i].toDouble()))
            }
            return pillars
        }
    }
}
