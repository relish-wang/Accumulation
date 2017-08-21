package wang.relish.accumulation.ui.fragment

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import wang.relish.accumulation.R
import wang.relish.accumulation.base.BaseFragment
import wang.relish.accumulation.entity.Record
import wang.relish.accumulation.ui.activity.RecordActivity
import java.util.*

/**
 * <pre>
 * author : 王鑫
 * e-mail : wangxin@souche.com
 * time   : 2017/05/04
 * desc   :
 * version: 1.0
</pre> *
 */
class UntitledFragment(override val activity: Activity) : BaseFragment(), AdapterView.OnItemClickListener {
    override fun layoutId(): Int {
        return R.layout.fragment_untitled
    }

    private var lv_records: ListView? = null
    private var mAdapter: RecordAdapter? = null
    private var records: List<Record>? = ArrayList()
    private var tv_no_data: TextView? = null

    override fun initViews(contentView: View) {
        lv_records = contentView.findViewById(R.id.lv_records) as ListView
        tv_no_data = contentView.findViewById(R.id.tv_no_data) as TextView
        records = Record.findAllUntitled()
        mAdapter = RecordAdapter()
        lv_records!!.adapter = mAdapter
        lv_records!!.onItemClickListener = this
    }

    override fun onResume() {
        super.onResume()
        update()
    }

    private fun update() {
        records = Record.findAllUntitled()
        mAdapter!!.notifyDataSetChanged()
        if (records == null || records!!.size == 0) {
            tv_no_data!!.visibility = View.VISIBLE
            lv_records!!.visibility = View.GONE
        } else {
            tv_no_data!!.visibility = View.GONE
            lv_records!!.visibility = View.VISIBLE
        }
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        RecordActivity.open(activity, records!![position])
    }

    private inner class RecordAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return records!!.size
        }

        override fun getItem(position: Int): Record {
            return records!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            val holder: ViewHolder
            if (convertView == null) {
                convertView = LayoutInflater.from(activity).inflate(R.layout.item_record, parent, false)
                holder = ViewHolder(convertView)
                convertView!!.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            val record = records!![position]
            holder.tv_name.text = record.name
            holder.tv_start_time.text = record.startTime
            return convertView
        }

        internal inner class ViewHolder(v: View) {
            var tv_name: TextView
            var tv_start_time: TextView

            init {
                tv_name = v.findViewById(R.id.tv_name) as TextView
                tv_start_time = v.findViewById(R.id.tv_create_time) as TextView
            }
        }
    }
}
