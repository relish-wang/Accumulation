package com.qyt.accumulation.ui.view.chart;

public class Pillar {

    private String day;
    private double value;

    public Pillar(String day, double value) {
        this.day = day;
        this.value = value;
    }

    /**
     * 获取pillar的显示高度
     * @param height
     * @param yScaleValue
     * @param xAxisHeight
     * @return
     */
    public int getPillarHeight(int height ,int yScaleValue, int xAxisHeight) {
        return (int) (((double) this.value / ((double) yScaleValue * 5)) * ((double) (CTHelper
                .getYScaleHeight(height, xAxisHeight) * 5)));
    }

    // get set 有的 没的
    public String getDay() {
        return day;
    }


    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value + " on " + day;
    }
}
