package com.qyt.accumulation.ui.view.chart

class Pillar(// get set 有的 没的
        val day: String, val value: Double) {

    /**
     * 获取pillar的显示高度
     * @param height
     * *
     * @param yScaleValue
     * *
     * @param xAxisHeight
     * *
     * @return
     */
    fun getPillarHeight(height: Int, yScaleValue: Int, xAxisHeight: Int): Int {
        return (this.value.toDouble() / (yScaleValue.toDouble() * 5) * (CTHelper
                .getYScaleHeight(height, xAxisHeight) * 5).toDouble()).toInt()
    }

    override fun toString(): String {
        return value.toString() + " on " + day
    }
}
