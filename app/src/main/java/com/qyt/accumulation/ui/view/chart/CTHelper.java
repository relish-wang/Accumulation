package com.qyt.accumulation.ui.view.chart;

import android.graphics.Paint;
import android.text.TextPaint;

import java.util.List;

/**
 * 图表帮助类
 * <p/>
 * Created by 鑫 on 2015/9/4.
 */
public class CTHelper {

    public static final int PADDING = 5;

    public static final int TEXT_SIZE = 12;
    public static final int TEXT_HEIGHT = getFontHeight(TEXT_SIZE);

    public static final int LINE_WIDTH = 2;

    public static final int SIDE = 10;

    /**
     * 获取字体的行高
     *
     * @param fontSize
     * @return
     */
    private static int getFontHeight(float fontSize) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        Paint.FontMetrics fm = paint.getFontMetrics();
        return (int) Math.ceil(fm.descent - fm.top) + 2;
    }

    /**
     * 获取Y轴相邻刻度的差值（共有五个刻度）
     *
     * @return
     */
    public static int getYScaleValue(List<Pillar> pillars) {
        if (pillars == null||pillars.size() == 0 )
            return 0;
        double max = pillars.get(0).getValue();
        for (int i = 1; i < pillars.size(); i++) {
            double t = pillars.get(i).getValue();
            max = Math.max(max, t);
        }
        int result = (int) ((int) (max / 50) * 10);
        return result < 10 ? 10 : result;
    }

    /**
     * 获取Y轴相邻刻度的差值的高度（共有5个刻度）
     *
     * @return
     */
    public static int getYScaleHeight(int height,int xAxisHeight) {
        return (int) ((height - xAxisHeight) / 6);
    }

    /**
     * 获取X轴相邻刻度的差值的高度（共有7个刻度）
     *
     * @param panelWidth
     * @return
     */
    public static int getXScaleWidth(int panelWidth) {
        return (int) ((float)panelWidth / 7);
    }


    /**
     * 计算出该TextView中文字的长度(像素) 1.第一个参数是要计算的字符串 2.第二个参数是字体大小
     *
     * @param text
     * @param size
     * @return
     */
    public static float getTextWidth(String text, float size) {
        TextPaint FontPaint = new TextPaint();
        FontPaint.setTextSize(size);
        return FontPaint.measureText(text);
    }
}
