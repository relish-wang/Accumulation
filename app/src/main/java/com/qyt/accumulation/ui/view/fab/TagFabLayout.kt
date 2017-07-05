package com.qyt.accumulation.ui.view.fab

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

import com.qyt.accumulation.R


/**
 * 该ViewGroup是点击Tab后展开的条目
 * 组合一个TagView和一个FloatingActionButton
 * 默认Tag在左，button在右
 */
class TagFabLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet = null!!, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {
    private var mTagText: String? = null
    private var mTagView: TagView? = null

    private var mOnTagClickListener: (() -> Unit?)? = null
    private var mOnFabClickListener: (()->Unit?)? = null

    interface OnTagClickListener {
        fun onTagClick()
    }

    interface OnFabClickListener {
        fun onFabClick()
    }

    fun setOnTagClickListener(onTagClickListener: () -> Unit) {
        mOnTagClickListener = onTagClickListener
    }

    fun setOnFabClickListener(onFabClickListener: () -> Unit) {
        mOnFabClickListener = onFabClickListener
    }

    init {
        getAttributes(context, attrs)
        settingTagView(context)
    }

    private fun getAttributes(context: Context, attributeSet: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.FabTagLayout)
        mTagText = typedArray.getString(R.styleable.FabTagLayout_tagText)
        typedArray.recycle()
    }

    private fun settingTagView(context: Context) {
        mTagView = TagView(context)
        mTagView!!.setTagText(mTagText!!)
        addView(mTagView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var width = 0
        var height = 0

        val count = childCount
        for (i in 0..count - 1) {

            val view = getChildAt(i)

            measureChild(view, widthMeasureSpec, heightMeasureSpec)

            width += view.measuredWidth
            height = Math.max(height, view.measuredHeight)
        }

        width += dp2px(8 + 8 + 8)
        height += dp2px(8 + 8)

        //直接将该ViewGroup设定为wrap_content的
        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        //为子View布局
        val tagView = getChildAt(0)
        val fabView = getChildAt(1)

        val tagWidth = tagView.measuredWidth
        val tagHeight = tagView.measuredHeight

        val fabWidth = fabView.measuredWidth
        val fabHeight = fabView.measuredHeight

        val tl = dp2px(8)
        val tt = (measuredHeight - tagHeight) / 2
        val tr = tl + tagWidth
        val tb = tt + tagHeight

        val fl = tr + dp2px(8)
        val ft = (measuredHeight - fabHeight) / 2
        val fr = fl + fabWidth
        val fb = ft + fabHeight

        fabView.layout(fl, ft, fr, fb)
        tagView.layout(tl, tt, tr, tb)
        bindEvents(tagView, fabView)
    }

    private fun bindEvents(tagView: View, fabView: View) {
        tagView.setOnClickListener {
            if (mOnTagClickListener != null) {
                mOnTagClickListener!!.invoke()
            }
        }

        fabView.setOnClickListener {
            if (mOnFabClickListener != null) {
                mOnFabClickListener!!.invoke()
            }
        }
    }

    override fun setBackgroundColor(color: Int) {
        mTagView!!.setBackgroundColor(color)
    }

    fun setTextColor(color: Int) {
        mTagView!!.setTextColor(color)
    }

    private fun dp2px(value: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics).toInt()

    }
}