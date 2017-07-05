package com.qyt.accumulation.ui.view.fab

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView

/**
 * 在TabTagLayout中需要一个能设置Text的CardView
 * 所以继承CardView实现一个TagView

 */
class TagView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : CardView(context, attrs, defStyleAttr) {
    private val mTextView: TextView = TextView(context)

    init {
        mTextView.setSingleLine(true)
    }

    protected fun setTextSize(size: Float) {
        mTextView.textSize = size
    }

    fun setTextColor(color: Int) {
        mTextView.setTextColor(color)
    }


    fun setTagText(text: String) {
        mTextView.text = text
        addTag()
    }

    private fun addTag() {
        val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER)
        val l = dp2px(8)
        val t = dp2px(8)
        val r = dp2px(8)
        val b = dp2px(8)
        layoutParams.setMargins(l, t, r, b)
        //addView会引起所有View的layout
        addView(mTextView, layoutParams)
    }

    private fun dp2px(value: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics).toInt()
    }

}
