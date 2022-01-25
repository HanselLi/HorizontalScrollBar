package sogou.mobile.custombar.kotlinFile

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import sogou.mobile.custombar.R
import sogou.mobile.custombar.SpUtil

/**
 * test submodule
 * Created by hansel on 2018/11/13.
 * 画一个自带刻度的可滑动小球(仿QQ浏览器的设置中的字体大小的调整View)
 * make a difference
 */
@Deprecated("此例子是Java convert to Kotlin 自动转化的，仅参考，不是很智能")
class HorizontalScrollBall @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var linePaint: Paint? = null
    private var mRadius: Int = 0
    private var fillPaint: Paint? = null
    // 当前的圆心横坐标
    private var mXPosition: Int = 0
    // 横坐标的最大值
    private var maxXPosition: Int = 0
    // 横坐标的最小值
    private var minXPosition: Int = 0
    // 俩个刻度线的中点的横坐标
    private var mFirstCentralXPoint: Int = 0
    private var mSecondCentralXPoint: Int = 0
    private var mThirdCentralXPoint: Int = 0
    private var mFourthCentralXPoint: Int = 0
    private var mFifthCentralXPoint: Int = 0
    // action_up的 X 横坐标
    private var mStopX: Int = 0
    // 6 个字体大小刻度的横坐标
    private var mOneTextSizeXPosition: Int = 0
    private var mTwoTextSizeXPosition: Int = 0
    private var mThreeTextSizeXPosition: Int = 0
    private var mFourTextSizeXPosition: Int = 0
    private var mFiveTextSizeXPosition: Int = 0
    private var mSixTextSizeXPosition: Int = 0
    // 字体大小调整接口
    private var listener: OnTextSizeChangeListener? = null

    /**
     * 根据上次保存的字体大小，去更新小球的位置
     */
    private val circleInitPositionX: Int
        get() {
            val currentTextSize = SpUtil.getInstance().currentTextSize
            when (currentTextSize) {
                15 -> return mOneTextSizeXPosition
                17 -> return mTwoTextSizeXPosition
                19 -> return mThreeTextSizeXPosition
                21 -> return mFourTextSizeXPosition
                23 -> return mFiveTextSizeXPosition
                25 -> return mSixTextSizeXPosition
                else -> return mThreeTextSizeXPosition
            }
        }

    private//        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
    //        fontMetrics.
    val scaleUnderTextYCoordinate: Int
        get() = measuredHeight / 2 + 6 * 18

    /**
     * 将整个View的宽度等分为6 份
     * 其中刻度线占5份，剩余的一份等分为二，
     * 作为刻度线的左右边距
     */
    private val perLength: Int
        get() = measuredWidth / 6

    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorizontalScrollBall)
            mRadius = typedArray.getInteger(R.styleable.HorizontalScrollBall_radius, 2)
            /** save default set radius for comparision with Method{@link# changeCircleRadius} */
            SpUtil.getInstance().currentRadius = mRadius
            Log.d(TAG, "radius is : $mRadius")
            typedArray.recycle()
        }
        init()
    }


    private fun init() {
        // 画圆圈的黑色外描线
        circlePaint.color = Color.BLACK
        circlePaint.isDither = true
        circlePaint.style = Paint.Style.STROKE
        circlePaint.strokeWidth = 2f
        // 圆圈填充为白色
        fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        fillPaint!!.style = Paint.Style.FILL
        fillPaint!!.color = Color.WHITE
        fillPaint!!.isAntiAlias = true
        fillPaint!!.isDither = true
        // 画一刻度直线
        linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint!!.style = Paint.Style.STROKE
        linePaint!!.isDither = true
        linePaint!!.strokeWidth = 3f
        linePaint!!.color = Color.BLACK
        // 画字体
        textPaint.strokeWidth = 5f
        textPaint.textSize = dp2px(15).toFloat()
        textPaint.isDither = true
        textPaint.color = Color.BLACK
        textPaint.textAlign = Paint.Align.CENTER
        Log.d(TAG, "init finish === ")
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        Log.d(TAG, " onFinishInflate =========")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(heightMeasureSpec), View.MeasureSpec.EXACTLY))

        maxXPosition = 5 * perLength + perLength / 2
        minXPosition = perLength / 2
        mXPosition = circleInitPositionX
        /** init five central point between two text size 's X coordinate position  */
        mFirstCentralXPoint = perLength
        mSecondCentralXPoint = 2 * perLength
        mThirdCentralXPoint = 3 * perLength
        mFourthCentralXPoint = 4 * perLength
        mFifthCentralXPoint = 5 * perLength
        //  six text size X coordinate position
        mOneTextSizeXPosition = perLength / 2
        mTwoTextSizeXPosition = perLength + perLength / 2
        mThreeTextSizeXPosition = 2 * perLength + perLength / 2
        mFourTextSizeXPosition = 3 * perLength + perLength / 2
        mFiveTextSizeXPosition = 4 * perLength + perLength / 2
        mSixTextSizeXPosition = 5 * perLength + perLength / 2
        // ------------------------------------------------------------------
        Log.d(TAG, "on Measure ======  max : " + maxXPosition + " min : " +
                minXPosition + " xPosition : " + mXPosition)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas) { // TODO: 2018/11/13 此处的坐标相对于自己
        super.onDraw(canvas)
        Log.d(TAG, "onDraw start $perLength")
        canvas.drawLine((perLength / 2).toFloat(), (measuredHeight / 2).toFloat(),
                (measuredWidth - perLength / 2).toFloat(),
                (measuredHeight / 2).toFloat(), linePaint!!)
        for (i in 0..5) {
            canvas.drawLines(getPts(i), linePaint!!)
        }
        // 画对应的字体
        drawScaleUnderText(canvas)
        //        canvas.save();
        //        canvas.restore();

        // draw white circle and black stroke line
        canvas.drawCircle(mXPosition.toFloat(), (measuredHeight / 2).toFloat(), dp2px(mRadius).toFloat(), circlePaint)
        canvas.drawCircle(mXPosition.toFloat(), (measuredHeight / 2).toFloat(), dp2px(mRadius).toFloat(), fillPaint!!)
    }

    private fun drawScaleUnderText(canvas: Canvas) {
        canvas.drawText(TEXT_SMALLER, mOneTextSizeXPosition.toFloat(), scaleUnderTextYCoordinate.toFloat(),
                textPaint)
        canvas.drawText(TEXT_SMALL, mTwoTextSizeXPosition.toFloat(), scaleUnderTextYCoordinate.toFloat(),
                textPaint)
        canvas.drawText(TEXT_DEFAULT, mThreeTextSizeXPosition.toFloat(), scaleUnderTextYCoordinate.toFloat(),
                textPaint)
        canvas.drawText(TEXT_BIG, mFourTextSizeXPosition.toFloat(), scaleUnderTextYCoordinate.toFloat(),
                textPaint)
        canvas.drawText(TEXT_BIGGER, mFiveTextSizeXPosition.toFloat(), scaleUnderTextYCoordinate.toFloat(),
                textPaint)
        canvas.drawText(TEXT_BIGGEST, mSixTextSizeXPosition.toFloat(), scaleUnderTextYCoordinate.toFloat(),
                textPaint)
    }

    /**
     * 设置当前的滑动的X坐标为圆心，且滑动范围控制在左右俩个边界
     */
    fun setCircleXPosition(xPosition: Int) {
        mXPosition = Math.min(Math.max(minXPosition, xPosition), maxXPosition)
        Log.d(TAG, " X position : $mXPosition min X : $minXPosition max X : $maxXPosition")
        invalidate()
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val mStartX = event.x.toInt()
                expandCircleRadius(true)
                Log.d(TAG, " mStartX : $mStartX")
            }
            MotionEvent.ACTION_MOVE -> {
                val mMoveX = event.x.toInt()
                // 根据mMoveX的坐标更改小球的位置
                setCircleXPosition(mMoveX)
                Log.d(TAG, "move X  is :  --------------------- $mMoveX")
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL// 在父BounceView的竖向滑动的时候会拦截子view的事件，要重置小球位置
            -> {
                mStopX = event.x.toInt()
                resetCirclePositionX(mStopX)
                expandCircleRadius(false)
            }
            else -> {
            }
        }
        return true
    }

    /**
     * 根据当前的横坐标的值跟刻度之间的中点做对比
     * 进而调整小球的位置
     */
    private fun resetCirclePositionX(currentX: Int) {
        var currentX = currentX
        // 滑动X距离大于最大值，reset为maxXPosition
        if (currentX > maxXPosition) {
            currentX = maxXPosition
        }
        if (currentX <= minXPosition || currentX > minXPosition && currentX <= mFirstCentralXPoint) {
            setCircleXPosition(minXPosition)
            setShowTextSize(mWebTextSizeZoom50)
        } else if (currentX > mFirstCentralXPoint && currentX <= mTwoTextSizeXPosition || currentX > mTwoTextSizeXPosition && currentX <= mSecondCentralXPoint) {
            setCircleXPosition(mTwoTextSizeXPosition)
            setShowTextSize(mWebTextSizeZoom75)
        } else if (currentX > mSecondCentralXPoint && currentX <= mThreeTextSizeXPosition || currentX > mThreeTextSizeXPosition && currentX < mThirdCentralXPoint) {
            setCircleXPosition(mThreeTextSizeXPosition)
            setShowTextSize(mWebTextSizeZoom100)
        } else if (currentX > mThirdCentralXPoint && currentX <= mFourTextSizeXPosition || currentX > mFourTextSizeXPosition && currentX <= mFourthCentralXPoint) {
            setCircleXPosition(mFourTextSizeXPosition)
            setShowTextSize(mWebTextSizeZoom125)
        } else if (currentX > mFourthCentralXPoint && currentX <= mFiveTextSizeXPosition || currentX > mFiveTextSizeXPosition && currentX <= mFifthCentralXPoint) {
            setCircleXPosition(mFiveTextSizeXPosition)
            setShowTextSize(mWebTextSizeZoom150)
        } else if (currentX > mFifthCentralXPoint && currentX <= mSixTextSizeXPosition) {
            setCircleXPosition(mSixTextSizeXPosition)
            setShowTextSize(mWebTextSizeZoom175)
        }
    }

    interface OnTextSizeChangeListener {
        fun onTextSizeChange(textSize: Int)
    }

    fun setOnTextSizeChangeListener(textSizeChangeListener: OnTextSizeChangeListener) {
        this.listener = textSizeChangeListener
    }

    private fun setShowTextSize(textSize: Int) {
        if (listener != null) {
            listener!!.onTextSizeChange(textSize)
        }
    }

    /**
     * 点击ActionDown触发后扩大小球的半径
     * ActionUp后恢复半径
     *
     * @param isExpand True expand Circle
     */
    private fun expandCircleRadius(isExpand: Boolean) {
        val defaultRadius = SpUtil.getInstance().currentRadius
        if (isExpand) {
            mRadius = (defaultRadius * 1.2).toInt()
        } else {
            mRadius = defaultRadius
        }
        invalidate()
    }

    /**
     * 画刻度线使用，提供6个line的坐标
     */
    private fun getPts(index: Int): FloatArray {
        val perLength = perLength
        val x = if (index == 5) index * perLength + perLength / 2 else index * perLength + perLength / 2 + 4
        return floatArrayOf(x.toFloat(), (measuredHeight / 2 - 40).toFloat(), x.toFloat(), (measuredHeight / 2).toFloat())
    }


    private fun dp2px(dp: Int): Int {
        return (resources.displayMetrics.density * dp + 0.5f).toInt()
    }

    companion object {

        private val TAG = "hansel"
        // 网页字体大小的 6 个 档
        var mWebTextSizeZoom50 = 15
        var mWebTextSizeZoom75 = 17
        var mWebTextSizeZoom100 = 19
        var mWebTextSizeZoom125 = 21
        var mWebTextSizeZoom150 = 23
        var mWebTextSizeZoom175 = 25
        // 刻度字体
        private val TEXT_SMALLER = "较小"
        private val TEXT_SMALL = "小"
        private val TEXT_DEFAULT = "默认"
        private val TEXT_BIG = "大"
        private val TEXT_BIGGER = "较大"
        private val TEXT_BIGGEST = "超大"
    }

}// because we 'd like to use The Third Constructor So Use this
