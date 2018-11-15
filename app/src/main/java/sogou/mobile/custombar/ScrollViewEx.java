package sogou.mobile.custombar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class ScrollViewEx extends ScrollView {

    private ScrollViewExListener mListener;
    private boolean mIsScrollToBottom=false;

    public ScrollViewEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (this.mListener != null) {
            this.mListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }

    public void setScrollExListener(ScrollViewExListener listener) {
        this.mListener = listener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if(mIsScrollToBottom){
            int count = getChildCount();
            if (count > 0) {
                View view = getChildAt(count - 1);
                int distance = view.getBottom() - getHeight();
                scrollTo(0, distance);
            }
        }
    }

    public void initScrollToBottom(boolean option){
        mIsScrollToBottom=option;
    }

    public static interface ScrollViewExListener {
        void onScrollChanged(ScrollViewEx scrollView, int x, int y, int oldx,
                int oldy);
    }
}
