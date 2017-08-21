package wang.relish.accumulation.ui.view.chart

import android.graphics.Paint
import android.text.TextPaint

/**
 * 图表帮助类
 *
 *
 * Created by 鑫 on 2015/9/4.
 */
object CTHelper {

    val PADDING = 5

    val TEXT_SIZE = 16
    val TEXT_HEIGHT = getFontHeight(TEXT_SIZE.toFloat())

    val LINE_WIDTH = 4

    val SIDE = 10

    /**
     * 获取字体的行高

     * @param fontSize
     * *
     * @return
     */
    private fun getFontHeight(fontSize: Float): Int {
        val paint = Paint()
        paint.textSize = fontSize
        val fm = paint.fontMetrics
        return Math.ceil((fm.descent - fm.top).toDouble()).toInt() + 2
    }

    /**
     * 获取Y轴相邻刻度的差值（共有五个刻度）

     * @return
     */
    fun getYScaleValue(pillars: List<Pillar>?): Int {
        if (pillars == null || pillars.size == 0)
            return 0
        var max = pillars[0].value
        for (i in 1..pillars.size - 1) {
            val t = pillars[i].value
            max = Math.max(max, t)
        }
        val result = ((max / 50).toFloat() * 10).toInt()
        return if (result < 10) 10 else result
    }

    /**
     * 获取Y轴相邻刻度的差值的高度（共有5个刻度）

     * @return
     */
    fun getYScaleHeight(height: Int, xAxisHeight: Int): Int {
        return ((height - xAxisHeight) / 6).toInt()
    }

    /**
     * 获取X轴相邻刻度的差值的高度（共有7个刻度）

     * @param panelWidth
     * *
     * @return
     */
    fun getXScaleWidth(panelWidth: Int): Int {
        return (panelWidth.toFloat() / 7).toInt()
    }


    /**
     * 计算出该TextView中文字的长度(像素) 1.第一个参数是要计算的字符串 2.第二个参数是字体大小

     * @param text
     * *
     * @param size
     * *
     * @return
     */
    fun getTextWidth(text: String, size: Float): Float {
        val FontPaint = TextPaint()
        FontPaint.textSize = size
        return FontPaint.measureText(text)
    }
}
