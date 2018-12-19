package sogou.mobile.custombar.kotlinFile

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import sogou.mobile.custombar.HorizontalScrollBall
import sogou.mobile.custombar.R
import sogou.mobile.custombar.SpUtil

/**
 *  horizontal seekBar kotlin style
 *  using kotlin is very neat
 */
class HorizontalSeekBar @JvmOverloads constructor(// JvmLoad 注解直接设置默认值，方便
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    // 或者使用java的互相调用构造器的方式
    /*constructor(context: Context?) : this(context,null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.HorizontalScrollBall)
        radius = attributes?.getInteger(R.styleable.HorizontalScrollBall_radius, 8)
        Log.d(TAG, " current radius is : $radius")
        attributes?.recycle()
        initPaint()
    }*/

    // static 常量
    companion object {
        // 编译期常量 TAG
        const val TAG = "liyang"
        // 网页字体大小的 6 个 档
        const val mWebTextSizeZoom50 = 15
        const val mWebTextSizeZoom75 = 17
        const val mWebTextSizeZoom100 = 19
        const val mWebTextSizeZoom125 = 21
        const val mWebTextSizeZoom150 = 23
        const val mWebTextSizeZoom175 = 25
        // 刻度字体
        private const val TEXT_SMALLER = "较小"
        private const val TEXT_SMALL = "小"
        private const val TEXT_DEFAULT = "默认"
        private const val TEXT_BIG = "大"
        private const val TEXT_BIGGER = "较大"
        private const val TEXT_BIGGEST = "超大"
    }

    var radius: Int?
    private lateinit var mLinePaint: Paint
    private lateinit var mCirclePaint: Paint// 必须用lateInit 不然编译器不通过
    private lateinit var mFillPaint: Paint
    // 初始化6 个刻度的横坐标
    private var mFirstXPoint: Int = 0
    private var mSecondXPoint: Int = 0
    private var mThirdXPoint: Int = 0
    private var mFourthXPoint: Int = 0
    private var mFifthXPoint: Int = 0
    private var mSixthXPoint: Int = 0
    // 俩个刻度线的中点的横坐标
    private var mFirstCentralXPoint: Int = 0
    private var mSecondCentralXPoint: Int = 0
    private var mThirdCentralXPoint: Int = 0
    private var mFourthCentralXPoint: Int = 0
    private var mFifthCentralXPoint: Int = 0
    // current init ball position according to saved textSize
    private var mCurrentXPoint: Int = 0

    // kotlin 初始化
    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.HorizontalScrollBall)
        radius = attributes?.getInteger(R.styleable.HorizontalScrollBall_radius, 8)
        Log.d(TAG, " current radius is : $radius")
        attributes?.recycle()
        initPaint()
    }


    private lateinit var mTextPaint: Paint

    /** 初始化画笔*/
    private fun initPaint() {
        mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLinePaint.strokeWidth = 3f // 赋值后自动变为set函数
        mLinePaint.color = Color.BLACK
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.isDither = true
        // circle paint
        mCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mCirclePaint.color = Color.BLACK
        mCirclePaint.strokeWidth = 2f
        mCirclePaint.style = Paint.Style.STROKE
        // fill paint
        mFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mFillPaint.style = Paint.Style.FILL
        mFillPaint.color = Color.WHITE
        // text paint
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.color = Color.BLACK
        mTextPaint.textSize = dp2px(15)
        mTextPaint.textAlign = Paint.Align.CENTER
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.EXACTLY))
        mFirstXPoint = getPerLength() / 2
        mSecondXPoint = getPerLength() + getPerLength() / 2
        mThirdXPoint = getPerLength() * 2 + getPerLength() / 2
        mFourthXPoint = getPerLength() * 3 + getPerLength() / 2
        mFifthXPoint = getPerLength() * 4 + getPerLength() / 2
        mSixthXPoint = getPerLength() * 5 + getPerLength() / 2
        // 刻度之间的中点横坐标
        mFirstCentralXPoint = getPerLength()
        mSecondCentralXPoint = 2 * getPerLength()
        mThirdCentralXPoint = 3 * getPerLength()
        mFourthCentralXPoint = 4 * getPerLength()
        mFifthCentralXPoint = 5 * getPerLength()

        // 当前的小球位置（根据上次设定的字体大小确定）
        mCurrentXPoint = getCurrentXPositionWithTextSize(SpUtil.getInstance().currentTextSize)
        Log.d(TAG, "mCurrent X Position : $mCurrentXPoint + measure height : $measuredHeight")
    }


    private fun getCurrentXPositionWithTextSize(textSize: Int): Int {
        return when (textSize) {
            mWebTextSizeZoom50 -> mFirstXPoint
            mWebTextSizeZoom75 -> mSecondXPoint
            mWebTextSizeZoom100 -> mThirdXPoint
            mWebTextSizeZoom125 -> mFourthXPoint
            mWebTextSizeZoom150 -> mFifthXPoint
            mWebTextSizeZoom175 -> mSixthXPoint
            else -> mThirdXPoint // 默认返回第三个坐标
        }
    }

    /** 整个View的宽等分为 6 份*/
    private fun getPerLength(): Int {
        return measuredWidth / 6
    }

    /** 画刻度线使用，获取刻度的线的俩个点坐标，返回一个数组*/
    private fun getPts(index: Int): FloatArray {
        val x: Float = if (index == 5) ((index * getPerLength() + getPerLength() / 2).toFloat()) else ((index * getPerLength() + getPerLength() / 2 + 4).toFloat())
        return floatArrayOf(x, (measuredHeight / 2 - 40).toFloat(), x, (measuredHeight / 2).toFloat())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // draw horizontal line
        canvas?.drawLine(mFirstXPoint.toFloat(), (measuredHeight / 2).toFloat(), mSixthXPoint.toFloat(), (measuredHeight / 2).toFloat(), mLinePaint)
        for (i in 0..5) {
            // draw scale line 刻度
            canvas?.drawLines(getPts(i), mLinePaint)
        }
        // 画小球
        canvas?.drawCircle(mCurrentXPoint.toFloat(), (measuredHeight / 2).toFloat(), dp2px(radius!!), mCirclePaint)
        // 画填充
        canvas?.drawCircle(mCurrentXPoint.toFloat(), (measuredHeight / 2).toFloat(), dp2px(radius!!), mFillPaint)
        // 画文字
        drawTextUnderScale(canvas)
    }

    /**
     * var <propertyName>[: <PropertyType>] [= <property_initializer>]
    [<getter>]
    [<setter>]
     *  getter and setter
     */
    private val textHeight: Float // 注：直接使用赋值 = 直接从初始器获取，此时的measureHeight = 0 ,还未执行onMeasure
        get() = (measuredHeight / 2 + 6 * 18).toFloat()

    /**
     * draw text
     */
    private fun drawTextUnderScale(canvas: Canvas?) {
        canvas?.drawText(TEXT_SMALLER, mFirstXPoint.toFloat(), textHeight, mTextPaint)
        canvas?.drawText(TEXT_SMALL, mSecondXPoint.toFloat(), textHeight, mTextPaint)
        canvas?.drawText(TEXT_DEFAULT, mThirdXPoint.toFloat(), textHeight, mTextPaint)
        canvas?.drawText(TEXT_BIG, mFourthXPoint.toFloat(), textHeight, mTextPaint)
        canvas?.drawText(TEXT_BIGGER, mFifthXPoint.toFloat(), textHeight, mTextPaint)
        canvas?.drawText(TEXT_BIGGEST, mSixthXPoint.toFloat(), textHeight, mTextPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                // expand the circle radius
                changeCircleRadius(true)
            }
            MotionEvent.ACTION_MOVE -> {
                setSeekBallPosition(event.x)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL
            -> {
                dealCircleBallPositionWhenUp(event.x.toInt())
                setSeekBallPosition(mCurrentXPoint.toFloat())
                changeCircleRadius(false)
            }
            else -> {

            }
        }
        return true
    }

    /**
     * @param isExpand true 增大小球的半径
     */
    private fun changeCircleRadius(isExpand: Boolean) {
        val currentRadius = SpUtil.getInstance().currentRadius
        if (isExpand) {
            radius = currentRadius.times(1.2).toInt()
        } else {
            radius = currentRadius
        }
        Log.d(TAG, " expand radius is : $radius")
        invalidate()
    }

    /** deal circle ball position when action up kotlin style is very clean and effective*/
    private fun dealCircleBallPositionWhenUp(xPositionUp: Int) {
        var tempXPosition: Int = xPositionUp
        if (tempXPosition < mFirstXPoint) tempXPosition = mFirstXPoint
        else if (tempXPosition > mSixthXPoint) tempXPosition = mSixthXPoint
        mCurrentXPoint = when (tempXPosition) {
            in mFirstXPoint..mFirstCentralXPoint -> mFirstXPoint
            in mFirstCentralXPoint..mSecondXPoint, in mSecondXPoint..mSecondCentralXPoint -> mSecondXPoint
            in mSecondCentralXPoint..mThirdXPoint, in mThirdXPoint..mThirdCentralXPoint -> mThirdXPoint
            in mThirdCentralXPoint..mFourthXPoint, in mFourthXPoint..mFourthCentralXPoint -> mFourthXPoint
            in mFourthCentralXPoint..mFifthXPoint, in mFifthXPoint..mFifthCentralXPoint -> mFifthXPoint
            in mFifthCentralXPoint..mSixthXPoint -> mSixthXPoint
            else -> mThirdXPoint
        }
        setShowTextSize(mCurrentXPoint)
    }

    /** 根据 x position 去重绘小球的横坐标位置*/
    private fun setSeekBallPosition(xPosition: Float) {
        mCurrentXPoint = Math.min(mSixthXPoint.toFloat(), Math.max(mFirstXPoint.toFloat(), xPosition)).toInt()
        Log.d(TAG, " current coordinate X is : $mCurrentXPoint  max : $mSixthXPoint min : $mFirstXPoint")
        invalidate()
    }

    private fun dp2px(dp: Int): Float {
        return (resources.displayMetrics.density * dp + 0.4f)
    }

    // interface to provide current textSize---------------------------------------------------------------------
    public interface OnTextSizeChangeListener {
        fun onTextSizeChange(textSize: Int)
    }

    private lateinit var listener: OnTextSizeChangeListener

    fun setOnTextSizeChangeListener(textSizeChangeListener: OnTextSizeChangeListener) {
        this.listener = textSizeChangeListener
    }

    private fun setShowTextSize(postion: Int) {
        var textSize: Int = when (postion) {
            mFirstXPoint -> mWebTextSizeZoom50
            mSecondXPoint -> mWebTextSizeZoom75
            mThirdXPoint -> mWebTextSizeZoom100
            mFourthXPoint -> mWebTextSizeZoom125
            mFifthXPoint -> mWebTextSizeZoom150
            mSixthXPoint -> mWebTextSizeZoom175
            else -> {
                mWebTextSizeZoom100
            }
        }
        listener.onTextSizeChange(textSize)
    }
}