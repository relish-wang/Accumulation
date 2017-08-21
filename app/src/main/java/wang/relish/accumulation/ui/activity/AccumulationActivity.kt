package wang.relish.accumulation.ui.activity

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatRatingBar
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import wang.relish.accumulation.R
import wang.relish.accumulation.adapter.GoalsAdapter
import wang.relish.accumulation.base.BaseActivity
import wang.relish.accumulation.entity.Goal
import wang.relish.accumulation.entity.Record
import wang.relish.accumulation.util.TimeUtil
import java.util.*

/**
 * <pre>
 * author : 王鑫
 * e-mail : wangxin@souche.com
 * time   : 2017/05/02
 * desc   : 积累
 * version: 1.0
</pre> *
 */
class AccumulationActivity : BaseActivity(), View.OnClickListener {
    override fun layoutId(): Int {
        return R.layout.activity_accumulation
    }

    override fun initToolbar(savedInstanceState: Bundle?, mToolbar: Toolbar?) {
        mToolbar?.title = "开始积累"
    }

    private val handle = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            setText(time)
        }
    }

    private var running = false
    private var time: Long = 0
    private var tv_hour: TextView? = null
    private var tv_min: TextView? = null
    private var tv_sec: TextView? = null
    private var ll_stop: View? = null
    private var btn_start: Button? = null
    private var btn_stop: Button? = null
    private var btn_stop_and_save: Button? = null
    private var mRecord: Record? = null
    private var startTime: Long = 0

    override fun initViews(savedInstanceState: Bundle?) {
        tv_hour = findViewById(R.id.tv_hour) as TextView
        tv_min = findViewById(R.id.tv_min) as TextView
        tv_sec = findViewById(R.id.tv_sec) as TextView

        btn_start = findViewById(R.id.btn_start) as Button
        ll_stop = findViewById(R.id.ll_stop)
        btn_stop = findViewById(R.id.btn_stop) as Button
        btn_stop_and_save = findViewById(R.id.btn_stop_and_save) as Button
        btn_start!!.setOnClickListener(this)
        btn_stop!!.setOnClickListener(this)
        btn_stop_and_save!!.setOnClickListener(this)

        btn_start!!.visibility = View.VISIBLE
        ll_stop!!.visibility = View.GONE
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_start -> start()
            R.id.btn_stop -> {
                running = false
                stop()
            }
            R.id.btn_stop_and_save -> {
                running = false
                stopAndSave()
            }
        }
    }


    private fun start() {
        running = true
        btn_start!!.visibility = View.GONE
        ll_stop!!.visibility = View.VISIBLE
        mRecord = Record()
        startTime = TimeUtil.dateTimeToLong(mRecord!!.startTime as String)
        Thread {
            while (running) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                time += 1
                handle.sendEmptyMessage(1)
            }
        }.start()
    }

    private fun generateTempRecord(): Record {
        val currentTime = System.currentTimeMillis()
        val hardTime = currentTime - startTime
        val untitled = Record.findMaxIdWhereGoalIdIs(0) + 1
        val start = TimeUtil.longToDateTime(startTime)
        val record = Record()
        record.goalId = 0
        record.id = untitled
        record.createTime = start
        record.startTime = start
        record.name = "未命名_" + untitled
        record.star = 1
        record.updateTime = start
        record.time = hardTime
        record.endTime = TimeUtil.longToDateTime(currentTime)
        return record
    }

    /**
     * 停止积累
     */
    private fun stop() {
        val record = generateTempRecord()
        AlertDialog.Builder(this)
                .setTitle("保存")
                .setMessage("是否保存这次积累？")
                .setPositiveButton("保存") { dialog, which -> onSaveClick(record) }
                .setNegativeButton("取消") { dialog, which -> onCancelClick(record) }
                .create()
                .show()
    }

    /**
     * 直接保存

     * @param record 记录
     */
    private fun onCancelClick(record: Record) {
        object : AsyncTask<Void, Void, Long>() {

            override fun doInBackground(vararg params: Void): Long? {
                return record.save()
            }

            override fun onPostExecute(aLong: Long?) {
                super.onPostExecute(aLong)
                if (aLong != null) {
                    if (aLong > 0) {
                        showMessage("此积累已保存至未分类中")
                        finish()
                    } else {
                        showMessage("保存失败！")
                    }
                }
            }
        }.execute()
    }

    /**
     * 保存记录

     * @param record 记录
     */
    private fun onSaveClick(record: Record) {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_record, null)
        val et_name = view.findViewById(R.id.et_record_name) as EditText
        val ratingBar = view.findViewById(R.id.rating_bar) as AppCompatRatingBar
        val et_info = view.findViewById(R.id.et_info) as EditText
        val tv_start_time = view.findViewById(R.id.tv_start_time) as TextView
        val tv_end_time = view.findViewById(R.id.tv_end_time) as TextView
        val tv_hard_time = view.findViewById(R.id.tv_hard_time) as TextView

        ratingBar.rating = record.star!!.toFloat()
        tv_start_time.text = record.startTime
        tv_end_time.text = record.endTime
        tv_hard_time.text = record.hardTime

        AlertDialog.Builder(this)
                .setTitle("保存")
                .setView(view)
                .setPositiveButton("存入目标") { _, _ ->
                    val name = et_name.text.toString().trim { it <= ' ' }
                    val note = et_info.text.toString().trim { it <= ' ' }
                    val star = ratingBar.rating
                    record.name = name
                    record.star = star.toInt()
                    record.note = note
                    onSaveIntoGoalClick(record)
                }
                .setNegativeButton("直接保存") { dialog, which ->
                    val name = et_name.text.toString().trim { it <= ' ' }
                    val note = et_info.text.toString().trim { it <= ' ' }
                    val star = ratingBar.rating
                    record.name = name
                    record.star = star.toInt()
                    record.note = note
                    object : AsyncTask<Void, Void, Long>() {

                        override fun doInBackground(vararg params: Void): Long? {
                            return record.save()
                        }

                        override fun onPostExecute(aLong: Long?) {
                            super.onPostExecute(aLong)
                            if (aLong != null) {
                                if (aLong > 0) {
                                    finish()
                                } else {
                                    showMessage("保存失败！")
                                }
                            }
                        }
                    }.execute()
                }
                .create()
                .show()
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
        lv_goals.setOnItemClickListener { parent, view1, position, id ->
            object : AsyncTask<Void, Void, Long>() {

                override fun doInBackground(vararg params: Void): Long? {
                    record.goalId = goals[position].id
                    record.id = Record.findMaxId() + 1
                    return record.save()
                }

                override fun onPostExecute(aLong: Long?) {
                    super.onPostExecute(aLong)
                    if (aLong != null) {
                        if (aLong > 0) {
                            finish()
                        } else {
                            showMessage("保存失败！")
                        }
                    }
                }
            }.execute()
        }
        dialog.show()
    }


    /**
     * 停止并保存
     */
    private fun stopAndSave() {
        val record = generateTempRecord()
        onSaveClick(record)
    }

    private fun setText(sec: Long) {
        val h = sec / 60 / 60
        val m = sec / 60 % 60
        val s = sec % 60
        tv_hour!!.text = String.format(Locale.CHINA, "%02d", h)
        tv_min!!.text = String.format(Locale.CHINA, "%02d", m)
        tv_sec!!.text = String.format(Locale.CHINA, "%02d", s)
    }
}
