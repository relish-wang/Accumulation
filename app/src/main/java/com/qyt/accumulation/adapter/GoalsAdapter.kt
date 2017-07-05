package com.qyt.accumulation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import com.qyt.accumulation.R
import com.qyt.accumulation.entity.Goal

import java.util.ArrayList

/**
 * <pre>
 * author : 王鑫
 * e-mail : wangxin@souche.com
 * time   : 2017/05/04
 * desc   :
 * version: 1.0
</pre> *
 */
class GoalsAdapter(goals: List<Goal>) : BaseAdapter() {


    internal var goals: List<Goal> = ArrayList()

    init {
        this.goals = goals
    }

    override fun getCount(): Int {
        return goals.size
    }

    override fun getItem(position: Int): Goal {
        return goals[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        val holder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.context).inflate(R.layout.item_goal, parent, false)
            holder = ViewHolder(convertView)
            convertView!!.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        holder.tv_name.text = goals[position].name
        return convertView
    }

    private inner class ViewHolder internal constructor(view: View) {
        internal var tv_name: TextView

        init {
            tv_name = view.findViewById(R.id.tv_name) as TextView
        }
    }
}