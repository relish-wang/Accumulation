package wang.relish.accumulation.util

import wang.relish.accumulation.entity.Goal
import wang.relish.accumulation.entity.Record
import wang.relish.accumulation.entity.User
import java.util.*

/**
 * 临时数据
 * Created by Relish on 2016/11/10.
 */
object Temp {

    //            goal.setId(i + 1+"");
    val goals: List<Goal>
        get() {
            val r = Random()
            val random = r.nextInt(30) + 3
            val goals = ArrayList<Goal>()
            for (i in 0..random - 1) {
                val goal = Goal()
                goal.name = "Goal" + (i + 1)
                goal.records = records
                goals.add(goal)
            }
            return goals

        }

    val records: List<Record>
        get() {
            val r = Random()
            val random = r.nextInt(5) + 3
            val now = System.currentTimeMillis()
            val records = ArrayList<Record>()
            for (i in 0..random - 1) {
                val t = now + (if (r.nextInt(1) > 0) 1 else -1) * (now % (r.nextInt((now / 12338911).toInt()) + 12338911))
                val record = Record()
                record.id = i.plus(1).toLong()
                record.name = "Record" + (i + 1)
                record.star = i % 5 + 1
                record.createTime = TimeUtil.longToDateTime(t)
                record.note = "You don't know!"
                record.time = (1000 * (r.nextInt(3600) + 3600)).toLong()
                records.add(record)
            }
            return records
        }

    fun initDemoData() {
        val userId: Long = 1
        val goal1Id: Long = 1
        val goal2Id: Long = 2
        val goal3Id: Long = 3
        val user = User()
        user.name = "Alice"
        user.password = "123"
        user.mobile = "13588888888"
        user.photo = "/storage/emulated/0/Download/timg.jpg"

        val goal1 = Goal()
        goal1.id = goal1Id
        goal1.name = "红楼梦"
        goal1.updateTime = "2017-04-30 15:07"
        goal1.mobile = user.mobile
        goal1.records = records
        goal1.save()


    }

    fun initHLMRecord(): List<Record> {

        val record = Record()
        record.goalId = 1
        record.name = "序"
        record.startTime = "2017-04-01 10:02"
        record.endTime = "2017-04-01 13:24"
        val et = TimeUtil.dateTimeToLong(record.endTime!!)
        val st = TimeUtil.dateTimeToLong(record.startTime!!)
        record.time = et - st
        record.updateTime = record.endTime
        record.note = "序，写的很棒！"
        record.save()

        val record2 = Record()
        record2.goalId = 1
        record2.name = "第一张"
        record2.startTime = "2017-04-02 09:21"
        record2.endTime = "2017-04-02 10:42"
        val et2 = TimeUtil.dateTimeToLong(record2.endTime!!)
        val st2 = TimeUtil.dateTimeToLong(record2.startTime!!)
        record2.time = et2 - st2
        record2.updateTime = record2.endTime
        record2.note = "第一章内容跌宕起伏引人入胜"
        record2.save()

        return object : ArrayList<Record>() {
            init {
                add(record)
                add(record2)
            }
        }
    }

}
