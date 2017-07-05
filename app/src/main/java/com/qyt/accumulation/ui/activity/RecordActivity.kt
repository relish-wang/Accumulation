package com.qyt.accumulation.ui.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatRatingBar
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.TimePicker

import com.qyt.accumulation.R
import com.qyt.accumulation.adapter.GoalsAdapter
import com.qyt.accumulation.base.BaseActivity
import com.qyt.accumulation.entity.Goal
import com.qyt.accumulation.entity.Record
import com.qyt.accumulation.util.TimeUtil

import java.util.Calendar
import java.util.Date

/**
 * <pre>
 * author : 王鑫
 * e-mail : wangxin@souche.com
 * time   : 2017/2/22
 * desc   :
 * version: 1.0
</pre> *
 */
class RecordActivity : BaseActivity(), View.OnClickListener {
    override fun layoutId(): Int {
        return R.layout.activity_record
    }

    private var mRecord: Record? = null

    override fun parseIntent(intent: Intent) {
        val bundle = intent.extras
        if (bundle != null) {
            mRecord = bundle.getSerializable("record") as Record
        } else {
            showMessage("bundle is null!")
            finish()
        }
    }

    private var isTitled = false

    override fun initToolbar(savedInstanceState: Bundle?, mToolbar: Toolbar?) {
        val goal = mRecord!!.parent
        isTitled = goal == null
        val goalName = if (isTitled) "未分类" else goal!!.name
        mToolbar?.title = goalName
        mToolbar?.subtitle = mRecord!!.name
    }

    private var isEditing = false
    private var rl_start_time: View? = null
    private var rl_end_time: View? = null
    private var rl_effective: View? = null
    private var iv_edit: ImageView? = null
    private var tv_start_time: TextView? = null
    private var tv_end_time: TextView? = null
    private var tv_hard_time: TextView? = null
    private var et_info: EditText? = null
    private var rating_bar: AppCompatRatingBar? = null


    override fun initViews(savedInstanceState: Bundle?) {
        rl_start_time = findViewById(R.id.rl_start_time)
        rl_end_time = findViewById(R.id.rl_end_time)
        rl_effective = findViewById(R.id.rl_effective)
        rl_start_time!!.setOnClickListener(this)
        rl_end_time!!.setOnClickListener(this)
        rl_effective!!.setOnClickListener(this)

        tv_start_time = findViewById(R.id.tv_start_time) as TextView
        tv_end_time = findViewById(R.id.tv_end_time) as TextView
        tv_hard_time = findViewById(R.id.tv_hard_time) as TextView
        rating_bar = findViewById(R.id.rating_bar) as AppCompatRatingBar

        iv_edit = findViewById(R.id.v_edit) as ImageView
        iv_edit!!.setOnClickListener(this)

        et_info = findViewById(R.id.et_info) as EditText


        updateTimeTime()
        et_info!!.setText(mRecord!!.note)
        rating_bar!!.rating = mRecord!!.star!!.toFloat()


        hideKeyboard(et_info!!)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onClick(v: View) {
        when (v.id) {
            R.id.v_edit -> if (isEditing) {
                iv_edit!!.setImageResource(R.drawable.ic_edit)
                et_info!!.isEnabled = false
                val text = et_info!!.text.toString().trim { it <= ' ' }
                mRecord!!.note = text
                object : AsyncTask<Void, Void, Long>() {

                    override fun doInBackground(vararg params: Void): Long? {
                        return mRecord!!.save()
                    }

                    override fun onPostExecute(aLong: Long?) {
                        super.onPostExecute(aLong)
                        if (aLong != null) {
                            if (aLong > 0) {
                                isEditing = false
                            } else {
                                showMessage("修改失败")
                            }
                        }
                    }
                }.execute()
            } else {
                isEditing = true
                iv_edit!!.setImageResource(R.drawable.ic_save)
                et_info!!.isEnabled = true
            }
            R.id.rl_start_time -> {
                val cStart = getCalendar(mRecord!!.startTime!!)
                val sYear = cStart.get(Calendar.YEAR)
                val sMonth = cStart.get(Calendar.MONTH)
                val sDay = cStart.get(Calendar.DAY_OF_MONTH)
                val sHour = cStart.get(Calendar.HOUR_OF_DAY)
                val sMin = cStart.get(Calendar.MINUTE)
                editTime(sYear, sMonth, sDay, sHour, sMin, object : OnSelectedListener {
                    override fun onSelected(datetime: String) {
                        mRecord!!.startTime = datetime
                        mRecord!!.updateTime = TimeUtil.longToDateTime(System.currentTimeMillis())
                        object : AsyncTask<Void, Void, Long>() {

                            override fun doInBackground(vararg params: Void): Long? {
                                return mRecord!!.save()
                            }

                            override fun onPostExecute(aLong: Long?) {
                                super.onPostExecute(aLong)
                                if (aLong != null) {
                                    if (aLong > 0) {
                                        updateTimeTime()
                                    } else {
                                        showMessage("修改失败")
                                    }
                                }
                            }
                        }.execute()
                    }
                })
            }
            R.id.rl_end_time -> {
                val cEnd = getCalendar(mRecord!!.endTime!!)
                val eYear = cEnd.get(Calendar.YEAR)
                val eMonth = cEnd.get(Calendar.MONTH)
                val eDay = cEnd.get(Calendar.DAY_OF_MONTH)
                val eHour = cEnd.get(Calendar.HOUR_OF_DAY)
                val eMin = cEnd.get(Calendar.MINUTE)
                editTime(eYear, eMonth, eDay, eHour, eMin, object : OnSelectedListener {
                    override fun onSelected(datetime: String) {
                        mRecord!!.endTime = datetime
                        object : AsyncTask<Void, Void, Long>() {

                            override fun doInBackground(vararg params: Void): Long? {
                                return mRecord!!.save()
                            }

                            override fun onPostExecute(aLong: Long?) {
                                super.onPostExecute(aLong)
                                if (aLong != null) {
                                    if (aLong > 0) {
                                        updateTimeTime()
                                    } else {
                                        showMessage("修改失败")
                                    }
                                }
                            }
                        }.execute()
                    }
                })
            }
            R.id.rl_effective -> editStar()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun editTime(y: Int, m: Int, d: Int, h: Int, min: Int, l: OnSelectedListener) {
        val dialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth -> afterDatePicked(year, month, dayOfMonth, h, min, l) }, y, m, d)
        dialog.show()
    }

    private fun afterDatePicked(y: Int, m: Int, d: Int, h: Int, min: Int, l: OnSelectedListener) {
        val picker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            val c = Calendar.getInstance()
            c.clear()
            c.set(y, m, d, hourOfDay, minute)
            val datetime = TimeUtil.longToDateTime(c.timeInMillis)
            l.onSelected(datetime)
        }, h, min, true)
        picker.show()
    }

    private fun editStar() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_star, null)
        val ratingBar = view.findViewById(R.id.rating_bar) as AppCompatRatingBar
        ratingBar.rating = mRecord!!.star!!.toFloat()
        AlertDialog.Builder(this)
                .setTitle("修改评分")
                .setView(view)
                .setPositiveButton("修改") { dialog, which ->
                    val star = ratingBar.rating
                    mRecord!!.star = star.toInt()
                    object : AsyncTask<Void, Void, Long>() {

                        override fun doInBackground(vararg params: Void): Long? {
                            return mRecord!!.save()
                        }

                        override fun onPostExecute(aLong: Long?) {
                            super.onPostExecute(aLong)
                            if (aLong != null) {
                                if (aLong > 0) {
                                    rating_bar!!.rating = mRecord!!.star!!.toFloat()
                                } else {
                                    showMessage("修改失败")
                                }
                            }
                        }
                    }.execute()
                }
                .setNegativeButton("取消", null)
                .create()
                .show()
    }

    private fun updateTimeTime() {
        tv_start_time!!.text = mRecord!!.startTime
        tv_hard_time!!.text = mRecord!!.hardTime
        tv_end_time!!.text = mRecord!!.endTime
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (isTitled) {
            menuInflater.inflate(R.menu.record, menu)
            return true
        } else {
            return super.onCreateOptionsMenu(menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_title_it -> {
                onSaveIntoGoalClick(mRecord!!)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 存入目标

     * @param record 记录
     */
    private fun onSaveIntoGoalClick(record: Record) {
        val goal = Goal.findAll()
        if (goal == null || goal.size == 0) {
            showMessage("暂无目标可存")
            return
        }
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_goals, null)
        val goals = Goal.findAll()
        val lv_goals = view.findViewById(R.id.lv_goals) as ListView
        val adapter = GoalsAdapter(goals)
        lv_goals.adapter = adapter
        val dialog = AlertDialog.Builder(this)
                .setTitle("存入目标")
                .setView(view)
                .create()
        lv_goals.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            object : AsyncTask<Void, Void, Long>() {

                override fun doInBackground(vararg params: Void): Long? {
                    val oldId = record.id
                    record.goalId = goals[position].id
                    record.id = Record.findMaxId() + 1
                    record.updateTime = TimeUtil.nowTime
                    return record.save() + Record.remove(oldId)
                }

                override fun onPostExecute(aLong: Long?) {
                    super.onPostExecute(aLong)
                    if (aLong != null) {
                        if (aLong > 1) {
                            val goal = Goal.findById(mRecord!!.goalId)
                            mToolbar?.title = goal?.name
                            dialog.dismiss()
                        } else {
                            showMessage("保存失败！")
                        }
                    }
                }
            }.execute()
        }
        dialog.show()
    }

    internal interface OnSelectedListener {
        fun onSelected(datetime: String)
    }

    companion object {

        fun open(context: Context, record: Record) {
            val intent = Intent(context, RecordActivity::class.java)
            val bundle = Bundle()
            bundle.putSerializable("record", record)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }

        private fun getCalendar(datetime: String): Calendar {
            val calendar = Calendar.getInstance()
            calendar.clear()
            calendar.time = Date(TimeUtil.dateTimeToLong(datetime))
            return calendar
        }
    }

}
