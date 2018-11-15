package sogou.mobile.custombar;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;


/**
 * 反弹效果嵌套
 * @author hansel
 *
 */
public class BounceView extends ScrollView
{

    private boolean isCalled ;

    private Callback mCallback;

    /**
     * 包含的View
     */
    private View mBounceView;
    /**
     * 存储正常时的位置
     */
    private Rect mRect = new Rect();

    /**
     * y坐标
     */
    private int y;

    private boolean isFirst = true;

    private GestureDetector mGestureDetector;

    private float mBounceY;

    private boolean isBounceing = false;

    private int mVelocityY;

    private final static int mAnimatorDiff = 20;

    private final static int mAnimatorTime = 400;

    private final static int SCROLL_VELOCITY = 3000;

    private boolean mShowStickAnimator = true;
    private DecelerateInterpolator mDecelerateInterpolator;
    private static final int TIME_RESET_POS_MS = 400;
    //是否需要显示下滑到底部的动画
    private boolean mIsNeedShowScrollBottomAnimation = true;

    public BounceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    public BounceView(Context context) {
        super(context);
    }

    public BounceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    /***
     * 根据 XML 生成视图工作完成.该函数在生成视图的最后调用，在所有子视图添加完之后. 即使子类覆盖了 onFinishInflate
     * 方法，也应该调用父类的方法，使该方法得以执行.
     */
    @Override
    protected void onFinishInflate()
    {
        MyOnGestureListener gestureListener = new MyOnGestureListener();
        mDecelerateInterpolator = new DecelerateInterpolator(2.5f);
        mGestureDetector = new GestureDetector(getContext(), gestureListener);

        if (getChildCount() > 0) {
            mBounceView = getChildAt(0);
        }
        setOverScrollMode(OVER_SCROLL_NEVER);
        super.onFinishInflate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev){
        try {
            if (mBounceView != null && isBounceAction()){
                commonOnTouch(ev);
            }
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            return false;
        }
    }

    private void commonOnTouch(MotionEvent ev){
        Log.d("hansel","bounce view onTouch ======================= ");
        int action = ev.getAction();
        int cy = (int) ev.getY();
        float YMoveDiff = Math.abs(mStartY-ev.getY());
        switch (action)
        {
        case MotionEvent.ACTION_DOWN:
            break;
            /**
             * 跟随手指移动
             */
        case MotionEvent.ACTION_MOVE:
            if(!isMoving(YMoveDiff)){
                isFirst = true;
                break;
            }

            int dy = cy - y;
            if (isFirst)
            {
                dy = 0;
                isFirst = false;
            }
            y = cy;
            if (isBounceNeedMove()){
                isBounceing = true;
                if (mRect.isEmpty())
                {
                    /**
                     * 记录移动前的位置
                     */
                    mRect.set(mBounceView.getLeft(), mBounceView.getTop(),
                            mBounceView.getRight(), mBounceView.getBottom());
                }
                mBounceView.layout(mBounceView.getLeft(), mBounceView.getTop() + 1 * dy / 3,
                        mBounceView.getRight(), mBounceView.getBottom() + 1 * dy / 3);

                if (shouldCallBack(dy)){
                    if (mCallback != null)
                    {
                        if(!isCalled)
                        {
                            isCalled = true ;
                            resetPosition();
                            mCallback.callback();
                        }
                    }
                }
            }

            break;
            /**
             * 反弹回去
             */
        case MotionEvent.ACTION_UP:
            if (!mRect.isEmpty() && isMoving(YMoveDiff)){
                resetPosition();
            }
            isBounceing = false;
            break;

        }
    }

    /**
     * 设置是否显示下滑到底部的反弹动画
     * @param isNeedShowScrollBottomAnmiation
     */
    public void setIsNeedShowScrollBottomAnmiation(boolean isNeedShowScrollBottomAnmiation) {
        this.mIsNeedShowScrollBottomAnimation = isNeedShowScrollBottomAnmiation;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if(/*mVelocityY > 2000 &&*/ oldt > 20 && !isBounceing && isBounceAction() && mShowStickAnimator){
            int offset = mBounceView.getMeasuredHeight() - getHeight();
            if(t == 0){
                scrollTopAnimation();
                mVelocityY = 0;
            }else if(t == offset && mIsNeedShowScrollBottomAnimation){
                scrollBottomAnimation();
                mVelocityY = 0;
            }
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    private void scrollTopAnimation(){
        int animatorDiff = getAnimatorDiff();
        int animatorTime = getAnimatorTime(animatorDiff);
        Animation animation = new TranslateAnimation(0, 0,animatorDiff,0);
        animation.setDuration(animatorTime);
        animation.setInterpolator(mDecelerateInterpolator);
        animation.setFillBefore(true);
        mBounceView.startAnimation(animation);
    }

    private void scrollBottomAnimation(){
        int animatorDiff = getAnimatorDiff();
        int animatorTime = getAnimatorTime(animatorDiff);
        Animation animation = new TranslateAnimation(0, 0, -animatorDiff,0);
        animation.setDuration(animatorTime);
        animation.setInterpolator(mDecelerateInterpolator);
        animation.setFillAfter(true);
        mBounceView.startAnimation(animation);
    }


    @Override
    public void fling(int velocityY) {
        mVelocityY = (int)(1.2f* Math.abs(velocityY));
        super.fling((int)(1.2f*velocityY));
    }
    private boolean isMoving(float diff){
        if(diff > 150){
            return true;
        }
        return false;
    }
    /**
     * 当从上往下，移动距离达到一半时，回调接口
     *
     * @return
     */
    private boolean shouldCallBack(int dy){
        if (dy > 0 && mBounceView.getTop() > getHeight() / 2) {
            return true;
        }
        return false;
    }

    private void resetPosition()
    {
        Animation animation = new TranslateAnimation(0, 0, mBounceView.getTop(),
                mRect.top);
        animation.setDuration(TIME_RESET_POS_MS);
        animation.setFillAfter(true);
        animation.setInterpolator(mDecelerateInterpolator);
        mBounceView.startAnimation(animation);
        mBounceView.layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
        mRect.setEmpty();
        mBounceY = 0;
        isFirst = true;
        isCalled = false ;
    }
//    private void resetState(){
//        mBounceY = 0;
//        mScrollY = 0;
//        isFirst = true;
//        isCalled = false ;
//    }
    /***
     * 是否需要移动布局 inner.getMeasuredHeight():获取的是控件的总高度
     *
     * getHeight()：获取的是屏幕的高度
     *
     * @return
     */
    public boolean isBounceNeedMove()
    {
        int offset = mBounceView.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        // 0是顶部，后面那个是底部
        if (scrollY == 0 || scrollY == offset){
            return true;
        }
        return false;
    }

    public void setCallBack(Callback callback)
    {
        mCallback = callback;
    }

    interface Callback
    {
        void callback();
    }
    public void setBounceView(View view){
        if(mBounceView == null){
            this.mBounceView = view;
        }
    }
    private float mStartY;
    class MyOnGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            float y = e2.getY() - mStartY;
            mBounceY = Math.abs(y);
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                return false;
            }
            if(mBounceView.getMeasuredHeight() <= getHeight()){
                return true;
            }
            return false;
        }

    }

    private int getAnimatorDiff(){
        if(mVelocityY > 10000){
            return mAnimatorDiff * 4;
        }
        return (mVelocityY/SCROLL_VELOCITY + 1)*mAnimatorDiff;
    }

    private int getAnimatorTime(int diff){
        int num = diff/mAnimatorDiff;
        switch (num) {
        case 1:
            return mAnimatorTime;
        case 4:
            return mAnimatorTime + 100;
        default:
            return num * (mAnimatorTime/2);
        }
    }

    public boolean isBounceState(){
        return isBounceing;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isBounceAction()){
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                mStartY = ev.getY();
            }

            boolean isGesure = mGestureDetector.onTouchEvent(ev);
            if(isGesure){
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public boolean isBounceAction(){
        return !isLowVersion();
    }

    public static boolean isLowVersion(){
        if(Build.VERSION.SDK_INT < 11){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 取消滚动停止添加动画效果
     */
    public void stopStickAnimator(){
        mShowStickAnimator = false;
    }
}
