package com.qyt.accumulation.ui.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import com.qyt.accumulation.App;
import com.qyt.accumulation.util.DensityUtil;

/**
 * 图表
 * <p/>
 * Created by 鑫 on 2015/9/4.
 */
public class Chart extends View {

    private static final String TAG = "123456789abc";

    /**
     * 数据源
     */
    private List<Pillar> pillars;
    /**
     * 整个统计图的宽度
     */
    private int width;
    /**
     * 整个统计图的高度
     */
    private int height;
    /**
     * Y轴的宽度
     */
    private int yAxisWidth;
    /**
     * 除Y轴宽度以外部分的宽度
     */
    private int panelWidth;
    /**
     * Y轴每个刻度的高度
     */
    private int yScaleHeight;
    /**
     * Y轴每个刻度的对应值
     */
    private int yScaleValue;
    /**
     * X轴的高度
     */
    private int xAxisHeight;
    /**
     * X每个轴刻度的宽度
     */
    private int xScaleWidth;

    /**
     * 画笔
     */
    private Paint paint;

    public Chart(Context context) {
        super(context);
    }

    public Chart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Chart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    public void setPillars(List<Pillar> pillars) {
        Log.d(TAG, "#setPillars");
        this.pillars = pillars;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.d(TAG, "#onMeasure");
        initSizes();
        setMeasuredDimension(widthMeasureSpec, this.height);
    }

    /**
     * 初始化各种尺寸
     */
    private void initSizes() {
        this.width = App.screenWidth;
        this.height = DensityUtil.dip2px(getContext(), 200);
        this.yAxisWidth = (int) CTHelper.getTextWidth(
                String.valueOf(CTHelper.getYScaleValue(pillars) * 5),
                CTHelper.TEXT_SIZE)
                + CTHelper.PADDING * 2
                + CTHelper.LINE_WIDTH;
        this.panelWidth = this.width - this.yAxisWidth;
        this.xScaleWidth = CTHelper.getXScaleWidth(this.panelWidth);
        this.xAxisHeight = CTHelper.TEXT_HEIGHT + CTHelper.PADDING * 2;

        this.yScaleHeight = CTHelper.getYScaleHeight(this.height, this.xAxisHeight);
        this.yScaleValue = CTHelper.getYScaleValue(pillars);

        Log.d(TAG, "#width" + width);
        Log.d(TAG, "#height" + height);
        Log.d(TAG, "#yAxisWidth" + yAxisWidth);
        Log.d(TAG, "#panelWidth" + panelWidth);
        Log.d(TAG, "#xScaleWidth" + xScaleWidth);
        Log.d(TAG, "#xAxisHeight" + xAxisHeight);
        Log.d(TAG, "#yScaleHeight" + yScaleHeight);
        Log.d(TAG, "#yScaleValue" + yScaleValue);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //初始化画笔
        initPaint();
        //画出y轴
        drawYAxis(canvas);
        // 画出X轴
        canvas.drawLine(yAxisWidth, this.height - this.xAxisHeight, this.width, this.height - this.xAxisHeight, paint);
        //绘制内容
        drawPanel(canvas);
    }

    /**
     * 获得所有点的坐标
     *
     * @param pillars
     * @return
     */
    private List<RectF> getOvals(List<Pillar> pillars) {
        if(pillars==null)return null;
        List<RectF> ovals = new ArrayList<>();
        for (int i = 0; i < pillars.size(); i++) {
            ovals.add(getOval(pillars.get(i), i));
        }
        return ovals;
    }

    /**
     * 获得圆的坐标
     *
     * @param index
     * @return
     */
    private RectF getOval(Pillar pillar, int index) {

        float l = yAxisWidth + (float) xScaleWidth / 2 + index * (float) xScaleWidth - (float) CTHelper.SIDE / 2;
        float t = this.height
                - (float) pillar.getPillarHeight(this.height, yScaleValue, xAxisHeight)
                - CTHelper.LINE_WIDTH - this.xAxisHeight - (float) CTHelper.SIDE / 2;
        float r = l + CTHelper.SIDE;
        float b = t + CTHelper.SIDE;

        return new RectF(l, t, r, b);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        if (this.paint == null)
            this.paint = new Paint();

        paint.setTextSize(CTHelper.TEXT_SIZE);// 设置字体大小(像素)
        paint.setStrokeWidth(CTHelper.LINE_WIDTH);// 设置线宽（像素）
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);// 消除锯齿
        paint.setStyle(Paint.Style.FILL);// 填充矩形
    }

    /**
     * 画出y轴
     *
     * @param canvas
     */
    private void drawYAxis(Canvas canvas) {

        canvas.drawLine(yAxisWidth - CTHelper.LINE_WIDTH, yScaleHeight / 2,
                yAxisWidth - CTHelper.LINE_WIDTH, this.height
                        - this.xAxisHeight, paint);
        canvas.drawText(
                "0",
                yAxisWidth
                        - CTHelper.PADDING
                        - CTHelper.LINE_WIDTH
                        - (int) CTHelper.getTextWidth("0",
                        CTHelper.TEXT_SIZE), this.height
                        - this.xAxisHeight - CTHelper.LINE_WIDTH
                        + CTHelper.TEXT_HEIGHT / 2 - CTHelper.LINE_WIDTH,
                paint);
        for (int i = 1; i < 6; i++) {
            int y = this.height - this.xAxisHeight
                    - CTHelper.LINE_WIDTH - yScaleHeight * i;
            String text = String.valueOf(yScaleValue * i);

            //网格线(横)
            paint.setStrokeWidth(1);
            paint.setColor(Color.GRAY);
            canvas.drawLine(yAxisWidth - CTHelper.LINE_WIDTH, y, width, y, paint);
            paint.setStrokeWidth(CTHelper.LINE_WIDTH);
            paint.setColor(Color.BLACK);

            canvas.drawText(
                    text,
                    yAxisWidth
                            - CTHelper.PADDING
                            - CTHelper.LINE_WIDTH
                            * 2
                            - (int) CTHelper.getTextWidth(text,
                            CTHelper.TEXT_SIZE), y
                            + CTHelper.TEXT_HEIGHT / 2
                            - CTHelper.LINE_WIDTH, paint);
        }
    }

    /**
     * 绘制面板内容
     *
     * @param canvas
     */
    private void drawPanel(Canvas canvas) {
        // 绘制x轴坐标的日期
        int index = 0;
        List<RectF> ovals = getOvals(pillars);
        if(ovals==null)return;
        for (int i = 0; i < ovals.size(); i++) {
            paint.setStrokeWidth(1);
            paint.setColor(Color.GRAY);
            canvas.drawLine(ovals.get(i).centerX(), yScaleHeight / 2, ovals.get(i).centerX(), this.height - CTHelper.LINE_WIDTH - xAxisHeight, paint);
            if (i < ovals.size() - 1) {
                paint.setColor(Color.GREEN);
                canvas.drawLine(ovals.get(i).centerX(), ovals.get(i).centerY(), ovals.get(i + 1).centerX(), ovals.get(i + 1).centerY(), paint);
            }
        }
        paint.setStrokeWidth(CTHelper.LINE_WIDTH);
        paint.setColor(Color.BLACK);
        for (Pillar pillar : pillars) {
            Log.d(TAG, "#pillar=" + pillar.toString());
            //日期显示位置
            float xDay = yAxisWidth + xScaleWidth / 2 + index * xScaleWidth -
                    CTHelper.getTextWidth(pillar.getDay(), CTHelper.TEXT_SIZE) / 2;
            float yDay = this.height - this.xAxisHeight
                    + CTHelper.TEXT_HEIGHT - CTHelper.LINE_WIDTH;
            canvas.drawText(pillar.getDay(), xDay, yDay, paint);

            /**
             * 描点
             */
            RectF oval = ovals.get(index);

            String value = String.format("%.2f", pillar.getValue());
            float textWidth = CTHelper.getTextWidth(value, CTHelper.TEXT_SIZE);
            canvas.drawText(value, oval.centerX() - textWidth / 2, oval.top - CTHelper.PADDING, paint);

            Log.d(TAG, "l=" + oval.left + " t=" + oval.top + " r=" + oval.right + " b=" + oval.bottom);


            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(2, 175, 218));
            canvas.drawOval(oval, paint);
            paint.setColor(Color.BLACK);

            index++;
        }
    }
}
