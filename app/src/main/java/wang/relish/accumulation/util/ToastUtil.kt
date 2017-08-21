package wang.relish.accumulation.util

import android.annotation.TargetApi
import android.app.Application
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import java.util.*

/**
 * @author zhouqian
 * *
 * @since 20160920
 */
object ToastUtil {

    val DEFAULT_FACTORY: Factory<ToastInfo> = object : Factory<ToastInfo> {
        override fun newToastInfo(): ToastInfo {
            return ToastInfo(this)
        }

        @TargetApi(Build.VERSION_CODES.CUPCAKE)
        override fun onCreateView(toastInfo: ToastInfo): View? {
            val text = toastInfo.text()
            if (TextUtils.isEmpty(text) && toastInfo.iconResId() == 0) return null

            val textView = TextView(sApp)
            textView.text = text
            textView.setTextColor(Color.WHITE)

            if (toastInfo.iconResId() == 0) {
                val padding = ToastInfo.dpToPx(15f)
                textView.setPadding(padding, padding, padding, padding)
            } else {
                val padding = ToastInfo.dpToPx(20f)
                textView.setPadding(padding, padding, padding, padding)
                textView.compoundDrawablePadding = ToastUtil.ToastInfo.dpToPx(15f)
                textView.setCompoundDrawablesWithIntrinsicBounds(0, toastInfo.iconResId(), 0, 0)
            }

            if (toastInfo.backgroundResId() == 0) {
                textView.setBackgroundColor(Color.parseColor("#C3000000"))
            } else {
                textView.setBackgroundResource(toastInfo.backgroundResId())
            }

            // if duration is not set manually, auto adjust duration depending on the text length
            if (toastInfo.duration() <= 0) {
                if (text.length > 50) {
                    toastInfo.duration(5000)
                } else if (text.length > 25) {
                    toastInfo.duration(3500)
                } else {
                    toastInfo.duration(2000)
                }
            }

            // if icon is set, let the Toast shown at the center of screen
            if (toastInfo.iconResId() != 0) {
                toastInfo.gravity(Gravity.CENTER, 0)
            }
            return textView
        }
    }
    private val HANDLER = Handler(Looper.getMainLooper())

    interface Factory<T : ToastInfo> {
        fun newToastInfo(): T

        fun onCreateView(toastInfo: T): View?
    }

    interface OnStageListener {
        fun onShow(toastDelegate: ToastDelegate)

        fun onDismiss(toastDelegate: ToastDelegate)
    }

    private var sApp: Application? = null
    private var sFactory: Factory<out ToastInfo> = DEFAULT_FACTORY

    fun init(app: Application) {
        sApp = app
    }

    fun setDefaultFactory(factory: Factory<out ToastInfo>?) {
        sFactory = factory ?: DEFAULT_FACTORY
    }

    fun <T : ToastInfo> create(cs: CharSequence): T {
        @Suppress("UNCHECKED_CAST")
        return sFactory.newToastInfo().text(cs) as T
    }

    fun show(stringResId: Int) {
        show(sApp!!.getString(stringResId))
    }

    fun show(stringResId: Int, iconResId: Int) {
        show(sApp!!.getString(stringResId), iconResId)
    }

    fun enqueue(stringResId: Int) {
        enqueue(sApp!!.getString(stringResId))
    }

    fun enqueue(stringResId: Int, iconResId: Int) {
        enqueue(sApp!!.getString(stringResId), iconResId)
    }


    fun show(cs: CharSequence) {
        sFactory.newToastInfo().text(cs).show()
    }

    fun show(cs: CharSequence, iconResId: Int) {
        sFactory.newToastInfo().text(cs).iconResId(iconResId).show()
    }

    fun enqueue(cs: CharSequence) {
        sFactory.newToastInfo().text(cs).enqueue()
    }

    fun enqueue(cs: CharSequence, iconResId: Int) {
        sFactory.newToastInfo().text(cs).iconResId(iconResId).enqueue()
    }


    open class ToastInfo(protected val mFactory: Factory<ToastInfo>) {
        protected var mText: CharSequence = null!!
        protected var mBackgroundResId = 0
        protected var mIconResId = 0
        protected var mExtra: MutableMap<String, Any>? = null

        fun text(cs: CharSequence): ToastInfo {
            mText = cs
            return this
        }

        fun text(): CharSequence {
            return mText
        }

        fun backgroundResId(resId: Int): ToastInfo {
            mBackgroundResId = resId
            return this
        }

        fun backgroundResId(): Int {
            return mBackgroundResId
        }

        fun iconResId(resId: Int): ToastInfo {
            mIconResId = resId
            return this
        }

        fun iconResId(): Int {
            return mIconResId
        }

        fun extra(key: String, value: Any): ToastInfo {
            if (mExtra == null) mExtra = HashMap<String, Any>()
            mExtra!!.put(key, value)
            return this
        }

        fun extra(key: String): Any? {
            return if (mExtra == null) null else mExtra!![key]
        }


        protected var mDuration = 0
        protected var mGravity = Gravity.BOTTOM
        protected var mOffsetY = 200

        fun duration(duration: Int): ToastInfo {
            mDuration = duration
            return this
        }

        fun duration(): Int {
            return mDuration
        }

        fun gravity(gravity: Int, offsetY: Int): ToastInfo {
            mGravity = gravity
            mOffsetY = offsetY
            return this
        }

        fun gravity(): Int {
            return mGravity
        }

        fun offsetY(): Int {
            return mOffsetY
        }


        var mOnStageListener: OnStageListener? = null

        fun onStageListener(onStageListener: OnStageListener): ToastInfo {
            mOnStageListener = onStageListener
            return this
        }

        fun onStageListener(): OnStageListener? {
            return mOnStageListener
        }

        fun build(): ToastDelegate? {
            val view = mFactory.onCreateView(this@ToastInfo)
            return if (view == null) null else ToastDelegate(view, this)
        }

        fun show() {
            val toastDelegate = build()
            toastDelegate?.show()
        }

        fun enqueue() {
            val toastDelegate = build()
            toastDelegate?.enqueue()
        }

        companion object {

            fun dpToPx(dp: Float): Int {
                val scale = sApp!!.resources.displayMetrics.density
                return (dp * scale + 0.5f).toInt()
            }
        }
    }

    class ToastDelegate(view: View, val toastInfo: ToastInfo) {

        private val mUpdateRunnable = Runnable { update() }
        private val mEnqueueRunnable = Runnable { enqueue() }

        private val mDuration: Int
        var toast: Toast? = null
            private set
        private var mStartTimestamp: Long = 0

        init {
            val toast = Toast(sApp)
            toast.view = view
            toast.setGravity(toastInfo.gravity(), 0, toastInfo.offsetY())
            toast.duration = Toast.LENGTH_LONG

            this.toast = toast
            mDuration = if (toastInfo.duration() <= 0) 2000 else toastInfo.duration()
        }

        val isShowing: Boolean
            get() = toast != null && mStartTimestamp > 0

        private fun update() {
            if (mStartTimestamp.equals(0)) {
                mStartTimestamp = currentTimestamp()
            }

            if (currentTimestamp() - mStartTimestamp < mDuration) {
                toast!!.show()
                HANDLER.postDelayed(mUpdateRunnable, sCheckInterval.toLong())
            } else {
                cancel()
            }
        }

        fun show() {
            if (sLastToastDelegate != null) sLastToastDelegate!!.cancel()

            sLastToastDelegate = this
            update()
            if (toastInfo.mOnStageListener != null) {
                toastInfo.mOnStageListener!!.onShow(this)
            }
        }

        fun enqueue() {
            if (sLastToastDelegate == null) {
                show()
                return
            }

            val timeRemaining = sLastToastDelegate!!.mStartTimestamp + sLastToastDelegate!!.mDuration - currentTimestamp()
            if (timeRemaining <= 0) {
                sLastToastDelegate!!.cancel()
                show()
                return
            }

            HANDLER.postDelayed(mEnqueueRunnable, timeRemaining)
        }

        fun cancel() {
            if (!isShowing) return

            toast!!.cancel()
            toast = null
            HANDLER.removeCallbacks(mUpdateRunnable)
            HANDLER.removeCallbacks(mEnqueueRunnable)
            if (toastInfo.mOnStageListener != null) {
                toastInfo.mOnStageListener!!.onDismiss(this)
            }
        }

        companion object {

            var sCheckInterval = 335

            private var sLastToastDelegate: ToastDelegate? = null// memory leak 28 bytes, forget it!!

            private fun currentTimestamp(): Long {
                return SystemClock.uptimeMillis()
            }
        }
    }
}
