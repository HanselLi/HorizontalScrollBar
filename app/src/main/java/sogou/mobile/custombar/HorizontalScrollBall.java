package sogou.mobile.custombar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by hansel on 2018/11/13.
 * 画一个自带刻度的可滑动小球(仿QQ浏览器的设置中的字体大小的调整View)
 */

public class HorizontalScrollBall extends View {

    private static final String TAG = "hansel";
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint linePaint;
    private int mRadius;
    private Paint fillPaint;
    // 当前的圆心横坐标
    private int mXPosition;
    // 横坐标的最大值
    private int maxXPosition;
    // 横坐标的最小值
    private int minXPosition;
    // 俩个刻度线的中点的横坐标
    private int mFirstCentralXPoint;
    private int mSecondCentralXPoint;
    private int mThirdCentralXPoint;
    private int mFourthCentralXPoint;
    private int mFifthCentralXPoint;
    // action_up的 X 横坐标
    private int mStopX;
    // 6 个字体大小刻度的横坐标
    private int mOneTextSizeXPosition;
    private int mTwoTextSizeXPosition;
    private int mThreeTextSizeXPosition;
    private int mFourTextSizeXPosition;
    private int mFiveTextSizeXPosition;
    private int mSixTextSizeXPosition;
    // 字体大小调整接口
    private OnTextSizeChangeListener listener;
    // 网页字体大小的 6 个 档
    public static int mWebTextSizeZoom50 = 15;
    public static int mWebTextSizeZoom75 = 17;
    public static int mWebTextSizeZoom100 = 19;
    public static int mWebTextSizeZoom125 = 21;
    public static int mWebTextSizeZoom150 = 23;
    public static int mWebTextSizeZoom175 = 25;
    // 刻度字体
    private static final String TEXT_SMALLER = "较小";
    private static final String TEXT_SMALL = "小";
    private static final String TEXT_DEFAULT = "默认";
    private static final String TEXT_BIG = "大";
    private static final String TEXT_BIGGER = "较大";
    private static final String TEXT_BIGGEST = "超大";

    public HorizontalScrollBall(Context context) {
        this(context, null);
    }

    public HorizontalScrollBall(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);// because we 'd like to use The Third Constructor So Use this
    }

    public HorizontalScrollBall(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorizontalScrollBall);
            mRadius = typedArray.getInteger(R.styleable.HorizontalScrollBall_radius, 2);
            /** save default set radius for comparision with Method{@link# changeCircleRadius}*/
            SpUtil.getInstance().setCurrentRadius(mRadius);
            Log.d(TAG, "radius is : " + mRadius);
            typedArray.recycle();
        }
        init();
    }


    private void init() {
        // 画圆圈的黑色外描线
        circlePaint.setColor(Color.BLACK);
        circlePaint.setDither(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(2);
        // 圆圈填充为白色
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);
        fillPaint.setAntiAlias(true);
        fillPaint.setDither(true);
        // 画一刻度直线
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setDither(true);
        linePaint.setStrokeWidth(3);
        linePaint.setColor(Color.BLACK);
        // 画字体
        textPaint.setStrokeWidth(5);
        textPaint.setTextSize(dp2px(15));
        textPaint.setDither(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        Log.d(TAG, "init finish === ");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, " onFinishInflate =========");
    }

    /**
     * 根据上次保存的字体大小，去更新小球的位置
     */
    private int getCircleInitPositionX() {
        int currentTextSize = SpUtil.getInstance().getCurrentTextSize();
        switch (currentTextSize) {
            case 15:
                return mOneTextSizeXPosition;
            case 17:
                return mTwoTextSizeXPosition;
            case 19:
                return mThreeTextSizeXPosition;
            case 21:
                return mFourTextSizeXPosition;
            case 23:
                return mFiveTextSizeXPosition;
            case 25:
                return mSixTextSizeXPosition;
            default:
                return mThreeTextSizeXPosition;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY));

        maxXPosition = 5 * getPerLength() + getPerLength() / 2;
        minXPosition = getPerLength() / 2;
        mXPosition = getCircleInitPositionX();
        /** init five central point between two text size 's X coordinate position */
        mFirstCentralXPoint = getPerLength();
        mSecondCentralXPoint = 2 * getPerLength();
        mThirdCentralXPoint = 3 * getPerLength();
        mFourthCentralXPoint = 4 * getPerLength();
        mFifthCentralXPoint = 5 * getPerLength();
        //  six text size X coordinate position
        mOneTextSizeXPosition = getPerLength() / 2;
        mTwoTextSizeXPosition = getPerLength() + getPerLength() / 2;
        mThreeTextSizeXPosition = 2 * getPerLength() + getPerLength() / 2;
        mFourTextSizeXPosition = 3 * getPerLength() + getPerLength() / 2;
        mFiveTextSizeXPosition = 4 * getPerLength() + getPerLength() / 2;
        mSixTextSizeXPosition = 5 * getPerLength() + getPerLength() / 2;
        // ------------------------------------------------------------------
        Log.d(TAG, "on Measure ======  max : " + maxXPosition + " min : " +
                minXPosition + " xPosition : " + mXPosition);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) { // TODO: 2018/11/13 此处的坐标相对于自己
        super.onDraw(canvas);
        Log.d(TAG, "onDraw start " + getPerLength());
        canvas.drawLine(getPerLength() / 2, getMeasuredHeight() / 2,
                (getMeasuredWidth() - getPerLength() / 2),
                getMeasuredHeight() / 2, linePaint);
        for (int i = 0; i < 6; i++) {
            canvas.drawLines(getPts(i), linePaint);
        }
        // 画对应的字体
        drawScaleUnderText(canvas);
//        canvas.save();
//        canvas.restore();

        // draw white circle and black stroke line
        canvas.drawCircle(mXPosition, getMeasuredHeight() / 2, dp2px(mRadius), circlePaint);
        canvas.drawCircle(mXPosition, getMeasuredHeight() / 2, dp2px(mRadius), fillPaint);
    }

    private void drawScaleUnderText(Canvas canvas) {
        canvas.drawText(TEXT_SMALLER, mOneTextSizeXPosition, getScaleUnderTextYCoordinate(),
                textPaint);
        canvas.drawText(TEXT_SMALL, mTwoTextSizeXPosition, getScaleUnderTextYCoordinate(),
                textPaint);
        canvas.drawText(TEXT_DEFAULT, mThreeTextSizeXPosition, getScaleUnderTextYCoordinate(),
                textPaint);
        canvas.drawText(TEXT_BIG, mFourTextSizeXPosition, getScaleUnderTextYCoordinate(),
                textPaint);
        canvas.drawText(TEXT_BIGGER, mFiveTextSizeXPosition, getScaleUnderTextYCoordinate(),
                textPaint);
        canvas.drawText(TEXT_BIGGEST, mSixTextSizeXPosition, getScaleUnderTextYCoordinate(),
                textPaint);
    }

    private int getScaleUnderTextYCoordinate() {
//        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
//        fontMetrics.
        return (getMeasuredHeight() / 2 + 6 * 18);
    }

    /**
     * 设置当前的滑动的X坐标为圆心，且滑动范围控制在左右俩个边界
     */
    public void setCircleXPosition(int xPosition) {
        mXPosition = Math.min(Math.max(minXPosition, xPosition), maxXPosition);
        Log.d(TAG, " X position : " + mXPosition + " min X : " + minXPosition + " max X : " + maxXPosition);
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int mStartX = (int) event.getX();
                expandCircleRadius(true);
                Log.d(TAG, " mStartX : " + mStartX);
                break;
            case MotionEvent.ACTION_MOVE:
                int mMoveX = (int) event.getX();
                // 根据mMoveX的坐标更改小球的位置
                setCircleXPosition(mMoveX);
                Log.d(TAG, "move X  is :  --------------------- " + mMoveX);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:// 在父BounceView的竖向滑动的时候会拦截子view的事件，要重置小球位置
                mStopX = (int) event.getX();
                resetCirclePositionX(mStopX);
                expandCircleRadius(false);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 根据当前的横坐标的值跟刻度之间的中点做对比
     * 进而调整小球的位置
     */
    private void resetCirclePositionX(int currentX) {
        // 滑动X距离大于最大值，reset为maxXPosition
        if (currentX > maxXPosition) {
            currentX = maxXPosition;
        }
        if (currentX <= minXPosition || (currentX > minXPosition && currentX <= mFirstCentralXPoint)) {
            setCircleXPosition(minXPosition);
            setShowTextSize(mWebTextSizeZoom50);
        } else if ((currentX > mFirstCentralXPoint && currentX <= mTwoTextSizeXPosition) ||
                (currentX > mTwoTextSizeXPosition && currentX <= mSecondCentralXPoint)) {
            setCircleXPosition(mTwoTextSizeXPosition);
            setShowTextSize(mWebTextSizeZoom75);
        } else if ((currentX > mSecondCentralXPoint && currentX <= mThreeTextSizeXPosition) ||
                (currentX > mThreeTextSizeXPosition && currentX < mThirdCentralXPoint)) {
            setCircleXPosition(mThreeTextSizeXPosition);
            setShowTextSize(mWebTextSizeZoom100);
        } else if ((currentX > mThirdCentralXPoint && currentX <= mFourTextSizeXPosition) ||
                (currentX > mFourTextSizeXPosition && currentX <= mFourthCentralXPoint)) {
            setCircleXPosition(mFourTextSizeXPosition);
            setShowTextSize(mWebTextSizeZoom125);
        } else if ((currentX > mFourthCentralXPoint && currentX <= mFiveTextSizeXPosition) ||
                (currentX > mFiveTextSizeXPosition && currentX <= mFifthCentralXPoint)) {
            setCircleXPosition(mFiveTextSizeXPosition);
            setShowTextSize(mWebTextSizeZoom150);
        } else if (currentX > mFifthCentralXPoint && currentX <= mSixTextSizeXPosition) {
            setCircleXPosition(mSixTextSizeXPosition);
            setShowTextSize(mWebTextSizeZoom175);
        }
    }

    public interface OnTextSizeChangeListener {
        void onTextSizeChange(int textSize);
    }

    public void setOnTextSizeChangeListener(OnTextSizeChangeListener textSizeChangeListener) {
        this.listener = textSizeChangeListener;
    }

    private void setShowTextSize(int textSize) {
        if (listener != null) {
            listener.onTextSizeChange(textSize);
        }
    }

    /**
     * 点击ActionDown触发后扩大小球的半径
     * ActionUp后恢复半径
     *
     * @param isExpand True expand Circle
     */
    private void expandCircleRadius(boolean isExpand) {
        int defaultRadius = SpUtil.getInstance().getCurrentRadius();
        if (isExpand) {
            mRadius = (int) (defaultRadius * 1.2);
        } else {
            mRadius = defaultRadius;
        }
        invalidate();
    }

    /**
     * 画刻度线使用，提供6个line的坐标
     */
    private float[] getPts(int index) {
        int perLength = getPerLength();
        int x = index == 5 ? index * perLength + perLength / 2 : index * perLength + perLength / 2 + 4;
        return new float[]{x, getMeasuredHeight() / 2 - 40, x, getMeasuredHeight() / 2};
    }

    /**
     * 将整个View的宽度等分为6 份
     * 其中刻度线占5份，剩余的一份等分为二，
     * 作为刻度线的左右边距
     */
    private int getPerLength() {
        return getMeasuredWidth() / 6;
    }


    private int dp2px(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

}
