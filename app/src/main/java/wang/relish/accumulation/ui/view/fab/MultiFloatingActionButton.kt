package wang.relish.accumulation.ui.view.fab

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator

import wang.relish.accumulation.R


class MultiFloatingActionButton @JvmOverloads constructor(context: Context, attrs: AttributeSet = null!!, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {

    //该View包含的默认子View
    private var mBackgroundView: View? = null
    private var mFloatingActionButton: FloatingActionButton? = null

    //该View的属性
    private var mBackgroundColor: Int = 0
    private var mFabIcon: Drawable? = null
    private var mFabColor: ColorStateList? = null
    private var mAnimationDuration: Int = 0
    private var mAnimationMode: Int = 0
    private var mPosition: Int = 0

    //主Fab是否被点开
    var buttonState: Boolean = false
        private set

    private var mOnFabItemClickListener: OnFabItemClickListener? = null

    interface OnFabItemClickListener {
        fun onFabItemClick(view: TagFabLayout, pos: Int)
    }

    fun setOnFabItemClickListener(onFabItemClickListener: OnFabItemClickListener) {
        mOnFabItemClickListener = onFabItemClickListener
    }

    init {
        //获取属性值
        getAttributes(context, attrs)
        //添加一个背景View和一个FloatingActionButton
        setBaseViews(context)
    }

    private fun getAttributes(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MultiFloatingActionButton)


        mBackgroundColor = typedArray.getColor(
                R.styleable.MultiFloatingActionButton_backgroundColor, Color.TRANSPARENT)
        mFabIcon = typedArray.getDrawable(R.styleable.MultiFloatingActionButton_switchFabIcon)
        mFabColor = typedArray.getColorStateList(R.styleable.MultiFloatingActionButton_switchFabColor)
        mAnimationDuration = typedArray.getInt(R.styleable.MultiFloatingActionButton_animationDuration, 150)
        mAnimationMode = typedArray.getInt(R.styleable.MultiFloatingActionButton_animationMode, ANIM_SCALE)
        mPosition = typedArray.getInt(R.styleable.MultiFloatingActionButton_position, POS_RIGHT_BOTTOM)
        typedArray.recycle()
    }

    private fun setBaseViews(context: Context) {
        mBackgroundView = View(context)
        mBackgroundView!!.setBackgroundColor(mBackgroundColor)
        mBackgroundView!!.alpha = 0f
        addView(mBackgroundView)

        mFloatingActionButton = FloatingActionButton(context)
        mFloatingActionButton!!.backgroundTintList = mFabColor
        mFloatingActionButton!!.setImageDrawable(mFabIcon)
        addView(mFloatingActionButton)
    }

    //什么事也没做
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val count = childCount
        for (i in 0..count - 1) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec)
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (changed) {
            //布局背景和主Fab
            layoutFloatingActionButton()
            layoutBackgroundView()
            layoutItems()
            Log.e("最外层View的宽高是", measuredWidth.toString() + "*" + measuredHeight)

        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var intercepted = false
        val x = ev.x.toInt()
        val y = ev.y.toInt()
        if (buttonState) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (judgeIfTouchBackground(x, y)) {
                        intercepted = true
                    }
                    intercepted = false
                }
                MotionEvent.ACTION_MOVE -> intercepted = false
                MotionEvent.ACTION_UP -> intercepted = false
            }
        }
        return intercepted
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (buttonState) {
            closeMenu()
            changeBackground()
            rotateFloatingButton()
            changeStatus()
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun judgeIfTouchBackground(x: Int, y: Int): Boolean {
        val a = Rect()
        val b = Rect()
        a.set(0, 0, width, height - getChildAt(childCount - 1).top)
        b.set(0, getChildAt(childCount - 1).top, getChildAt(childCount - 1).left, height)
        if (a.contains(x, y) || b.contains(x, y)) {
            return true
        }
        return false
    }


    private fun layoutFloatingActionButton() {
        val width = mFloatingActionButton!!.measuredWidth
        val height = mFloatingActionButton!!.measuredHeight

        var fl = 0
        var ft = 0
        var fr = 0
        var fb = 0

        when (mPosition) {
            POS_LEFT_BOTTOM, POS_RIGHT_BOTTOM -> {
                fl = measuredWidth - width - dp2px(8)
                ft = measuredHeight - height - dp2px(8)
                fr = fl + width
                fb = ft + height
            }
        }

        mFloatingActionButton!!.layout(fl, ft, fr, fb)
        bindFloatingEvent()

    }

    private fun layoutBackgroundView() {
        mBackgroundView!!.layout(0, 0, measuredWidth, measuredHeight)
        //bindBackgroundEvent();
    }

    private fun layoutItems() {
        val count = childCount
        for (i in 2..count - 1) {
            val child = getChildAt(i) as TagFabLayout
            child.visibility = View.INVISIBLE

            val width = child.measuredWidth
            val height = child.measuredHeight
            Log.e("子View的宽高是", width.toString() + "*" + height)

            val fabHeight = mFloatingActionButton!!.measuredHeight

            var cl = 0
            var ct = 0

            when (mPosition) {
                POS_LEFT_BOTTOM, POS_RIGHT_BOTTOM -> {
                    cl = measuredWidth - width - dp2px(8)
                    ct = measuredHeight - fabHeight - (i - 1) * height - dp2px(8)
                }
            }
            Log.e("子View的坐标是", cl.toString() + "+" + ct)
            child.layout(cl, ct, cl + width, ct + height)
            bindMenuEvents(child, i)
            prepareAnim(child)
        }
    }

    private fun bindFloatingEvent() {
        mFloatingActionButton!!.setOnClickListener {
            rotateFloatingButton()
            changeBackground()
            changeStatus()
            if (buttonState) {
                openMenu()
            } else {
                closeMenu()
            }
        }
    }

    private fun bindMenuEvents(child: TagFabLayout, pos: Int) {
        child.setOnTagClickListener {
            rotateFloatingButton()
            changeBackground()
            changeStatus()
            closeMenu()
            if (mOnFabItemClickListener != null) {
                mOnFabItemClickListener!!.onFabItemClick(child, pos)
            }
        }

        child.setOnFabClickListener {
            rotateFloatingButton()
            changeBackground()
            changeStatus()
            closeMenu()
            if (mOnFabItemClickListener != null) {
                mOnFabItemClickListener!!.onFabItemClick(child, pos)
            }
        }
    }

    private fun prepareAnim(child: TagFabLayout) {
        when (mAnimationMode) {
            ANIM_BOUNCE -> child.translationY = 50f
            ANIM_SCALE -> {
                child.scaleX = 0f
                child.scaleY = 0f
            }
        }
    }


    private fun rotateFloatingButton() {
        val animator = if (buttonState)
            ObjectAnimator.ofFloat(mFloatingActionButton, "rotation", 135f, 0f)
        else
            ObjectAnimator.ofFloat(mFloatingActionButton, "rotation", 0f, -135f)
        animator.duration = 150
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    private fun changeBackground() {
        val animator = if (buttonState)
            ObjectAnimator.ofFloat(mBackgroundView, "alpha", 0.9f, 0f)
        else
            ObjectAnimator.ofFloat(mBackgroundView, "alpha", 0f, 0.9f)
        animator.duration = 150
        animator.interpolator = LinearInterpolator()
        animator.start()
    }

    private fun changeStatus() {
        buttonState = !buttonState
    }

    private fun openMenu() {
        when (mAnimationMode) {
            ANIM_BOUNCE -> bounceToShow()
            ANIM_SCALE -> scaleToShow()
        }
    }

    private fun bounceToShow() {
        for (i in 2..childCount - 1) {
            val view = getChildAt(i)
            view.visibility = View.VISIBLE

            val trans = ObjectAnimator.ofFloat(view, "translationY", 50f, 0f)
            val show = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
            val set = AnimatorSet()
            set.play(trans).with(show)
            set.duration = mAnimationDuration.toLong()
            set.interpolator = BounceInterpolator()
            set.start()

        }
    }

    private fun scaleToShow() {
        for (i in 2..childCount - 1) {
            val view = getChildAt(i)
            view.visibility = View.VISIBLE
            view.alpha = 0f
            val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
            val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f)
            val alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
            val set = AnimatorSet()
            set.playTogether(scaleX, scaleY, alpha)
            set.duration = mAnimationDuration.toLong()
            set.start()
        }
    }


    private fun closeMenu() {
        for (i in 2..childCount - 1) {
            val view = getChildAt(i)
            val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
            alpha.duration = mAnimationDuration.toLong()
            alpha.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })
            alpha.start()
        }
    }

    private fun dp2px(value: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), resources.displayMetrics).toInt()

    }

    fun setAnimationDuration(duration: Int) {
        mAnimationDuration = duration
    }

    fun setFabColor(color: ColorStateList) {
        mFloatingActionButton!!.backgroundTintList = color
    }

    fun setTagBackgroundColor(color: Int) {
        for (i in 2..childCount - 1) {
            val tagFabLayout = getChildAt(i) as TagFabLayout
            tagFabLayout.setBackgroundColor(color)
        }
    }

    fun setTextColor(color: Int) {
        for (i in 2..childCount - 1) {
            val tagFabLayout = getChildAt(i) as TagFabLayout
            tagFabLayout.setTextColor(color)
        }
    }

    override fun setBackgroundColor(color: Int) {
        mBackgroundColor = color
        mBackgroundView!!.setBackgroundColor(color)
    }

    fun setFabIcon(icon: Drawable) {
        mFloatingActionButton!!.setImageDrawable(icon)
    }

    companion object {

        val POS_LEFT_BOTTOM = 0
        val POS_RIGHT_BOTTOM = 1

        val ANIM_FADE = 0
        val ANIM_SCALE = 1
        val ANIM_BOUNCE = 2
    }
}