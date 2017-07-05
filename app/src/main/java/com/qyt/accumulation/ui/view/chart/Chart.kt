package com.qyt.accumulation.ui.view.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View

import java.util.ArrayList

import com.qyt.accumulation.App
import com.qyt.accumulation.R
import com.qyt.accumulation.util.DensityUtil

/**
 * 图表
 *
 *
 * Created by 鑫 on 2015/9/4.
 */
class Chart : View {

    /**
     * 数据源
     */
    private var pillars: List<Pillar>? = null
    /**
     * 整个统计图的宽度
     */
    private var viewWidth: Int = 0
    /**
     * 整个统计图的高度
     */
    private var viewHeight: Int = 0
    /**
     * Y轴的宽度
     */
    private var yAxisWidth: Int = 0
    /**
     * 除Y轴宽度以外部分的宽度
     */
    private var panelWidth: Int = 0
    /**
     * Y轴每个刻度的高度
     */
    private var yScaleHeight: Int = 0
    /**
     * Y轴每个刻度的对应值
     */
    private var yScaleValue: Int = 0
    /**
     * X轴的高度
     */
    private var xAxisHeight: Int = 0
    /**
     * X每个轴刻度的宽度
     */
    private var xScaleWidth: Int = 0

    /**
     * 画笔
     */
    private var paint: Paint? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setWillNotDraw(false)
    }

    fun setPillars(pillars: List<Pillar>) {
        Log.d(TAG, "#setPillars")
        this.pillars = pillars
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d(TAG, "#onMeasure")
        initSizes()
        setMeasuredDimension(widthMeasureSpec, this.height)
    }

    /**
     * 初始化各种尺寸
     */
    private fun initSizes() {
        this.viewWidth = App.screenWidth
        this.viewHeight = DensityUtil.dip2px(context, 200f)
        this.yAxisWidth = CTHelper.getTextWidth(
                (CTHelper.getYScaleValue(pillars) * 5).toString(),
                CTHelper.TEXT_SIZE.toFloat()).toInt()
        +CTHelper.PADDING * 2
        +CTHelper.LINE_WIDTH
        this.panelWidth = this.width - this.yAxisWidth
        this.xScaleWidth = CTHelper.getXScaleWidth(this.panelWidth)
        this.xAxisHeight = CTHelper.TEXT_HEIGHT + CTHelper.PADDING * 2

        this.yScaleHeight = CTHelper.getYScaleHeight(this.height, this.xAxisHeight)
        this.yScaleValue = CTHelper.getYScaleValue(pillars)

        Log.d(TAG, "#width" + width)
        Log.d(TAG, "#height" + height)
        Log.d(TAG, "#yAxisWidth" + yAxisWidth)
        Log.d(TAG, "#panelWidth" + panelWidth)
        Log.d(TAG, "#xScaleWidth" + xScaleWidth)
        Log.d(TAG, "#xAxisHeight" + xAxisHeight)
        Log.d(TAG, "#yScaleHeight" + yScaleHeight)
        Log.d(TAG, "#yScaleValue" + yScaleValue)

    }

    override fun onDraw(canvas: Canvas) {
        //初始化画笔
        initPaint()
        //画出y轴
        drawYAxis(canvas)
        // 画出X轴
        canvas.drawLine(yAxisWidth.toFloat(), (this.height - this.xAxisHeight).toFloat(), this.width.toFloat(), (this.height - this.xAxisHeight).toFloat(), paint!!)
        //绘制内容
        drawPanel(canvas)
    }

    /**
     * 获得所有点的坐标

     * @param pillars
     * *
     * @return
     */
    private fun getOvals(pillars: List<Pillar>?): List<RectF>? {
        if (pillars == null) return null
        val ovals = ArrayList<RectF>()
        for (i in pillars.indices) {
            ovals.add(getOval(pillars[i], i))
        }
        return ovals
    }

    /**
     * 获得圆的坐标

     * @param index
     * *
     * @return
     */
    private fun getOval(pillar: Pillar, index: Int): RectF {

        val l = yAxisWidth.toFloat() + xScaleWidth.toFloat() / 2 + index * xScaleWidth.toFloat() - CTHelper.SIDE.toFloat() / 2
        val t = this.height.toFloat()
        -pillar.getPillarHeight(this.height, yScaleValue, xAxisHeight).toFloat()
        -CTHelper.LINE_WIDTH.toFloat() - this.xAxisHeight.toFloat() - CTHelper.SIDE.toFloat() / 2
        val r = l + CTHelper.SIDE
        val b = t + CTHelper.SIDE

        return RectF(l, t, r, b)
    }

    /**
     * 初始化画笔
     */
    private fun initPaint() {
        if (this.paint == null)
            this.paint = Paint()

        paint!!.textSize = CTHelper.TEXT_SIZE.toFloat()// 设置字体大小(像素)
        paint!!.strokeWidth = CTHelper.LINE_WIDTH.toFloat()// 设置线宽（像素）
        paint!!.color = Color.BLACK
        paint!!.isAntiAlias = true// 消除锯齿
        paint!!.style = Paint.Style.FILL// 填充矩形
    }

    /**
     * 画出y轴

     * @param canvas
     */
    private fun drawYAxis(canvas: Canvas) {

        canvas.drawLine((yAxisWidth - CTHelper.LINE_WIDTH).toFloat(), (yScaleHeight / 2).toFloat(),
                (yAxisWidth - CTHelper.LINE_WIDTH).toFloat(), (this.height - this.xAxisHeight).toFloat(), paint!!)
        canvas.drawText(
                "0",
                (yAxisWidth
                        - CTHelper.PADDING
                        - CTHelper.LINE_WIDTH
                        - CTHelper.getTextWidth("0",
                        CTHelper.TEXT_SIZE.toFloat()).toInt()).toFloat(), (this.height
                - this.xAxisHeight - CTHelper.LINE_WIDTH + CTHelper.TEXT_HEIGHT / 2 - CTHelper.LINE_WIDTH).toFloat(),
                paint!!)
        for (i in 1..5) {
            val y = this.height - this.xAxisHeight
            -CTHelper.LINE_WIDTH - yScaleHeight * i
            val text = (yScaleValue * i).toString()

            //网格线(横)
            paint!!.strokeWidth = 1f
            paint!!.color = Color.GRAY
            canvas.drawLine((yAxisWidth - CTHelper.LINE_WIDTH).toFloat(), y.toFloat(), width.toFloat(), y.toFloat(), paint!!)
            paint!!.strokeWidth = CTHelper.LINE_WIDTH.toFloat()
            paint!!.color = Color.BLACK

            canvas.drawText(
                    text,
                    (yAxisWidth
                            - CTHelper.PADDING
                            - CTHelper.LINE_WIDTH * 2
                            - CTHelper.getTextWidth(text,
                            CTHelper.TEXT_SIZE.toFloat()).toInt()).toFloat(), (y + CTHelper.TEXT_HEIGHT / 2 - CTHelper.LINE_WIDTH).toFloat(), paint!!)
        }
    }

    /**
     * 绘制面板内容

     * @param canvas
     */
    private fun drawPanel(canvas: Canvas) {
        // 绘制x轴坐标的日期
        var index = 0
        val ovals = getOvals(pillars) ?: return
        for (i in ovals.indices) {
            paint!!.strokeWidth = 1f
            paint!!.color = Color.GRAY
            canvas.drawLine(ovals[i].centerX(), (yScaleHeight / 2).toFloat(), ovals[i].centerX(), (this.height - CTHelper.LINE_WIDTH - xAxisHeight).toFloat(), paint!!)
            if (i < ovals.size - 1) {
                paint!!.color = ContextCompat.getColor(context, R.color.colorAccent)
                val w = paint!!.strokeWidth
                paint!!.strokeWidth = 10f
                canvas.drawLine(ovals[i].centerX(), ovals[i].centerY(), ovals[i + 1].centerX(), ovals[i + 1].centerY(), paint!!)
                paint!!.strokeWidth = w
            }
        }
        paint!!.strokeWidth = CTHelper.LINE_WIDTH.toFloat()
        paint!!.color = Color.BLACK
        for (pillar in pillars!!) {
            Log.d(TAG, "#pillar=" + pillar.toString())
            //日期显示位置
            val xDay = yAxisWidth + xScaleWidth / 2 + index * xScaleWidth - CTHelper.getTextWidth(pillar.day, CTHelper.TEXT_SIZE.toFloat()) / 2
            val yDay = (this.height - this.xAxisHeight + CTHelper.TEXT_HEIGHT - CTHelper.LINE_WIDTH).toFloat()
            canvas.drawText(pillar.day, xDay, yDay, paint!!)

            /**
             * 描点
             */
            val oval = ovals[index]

            val value = String.format("%.2f", pillar.value)
            val textWidth = CTHelper.getTextWidth(value, CTHelper.TEXT_SIZE.toFloat())
            canvas.drawText(value, oval.centerX() - textWidth / 2, oval.top - CTHelper.PADDING, paint!!)

            Log.d(TAG, "l=" + oval.left + " t=" + oval.top + " r=" + oval.right + " b=" + oval.bottom)


            paint!!.style = Paint.Style.FILL
            paint!!.color = Color.rgb(2, 175, 218)
            canvas.drawOval(oval, paint!!)
            paint!!.color = Color.BLACK

            index++
        }
    }

    companion object {

        private val TAG = "123456789abc"
    }
}
