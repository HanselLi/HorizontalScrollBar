package sogou.mobile.custombar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView showTvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

    }

    private void initViews() {
        showTvContent = (TextView) findViewById(R.id.showTvContent);
        final TextView textTip = (TextView) findViewById(R.id.textTip);
        HorizontalScrollBall scrollBall = (HorizontalScrollBall) findViewById(R.id.scrollBall);
        scrollBall.setOnTextSizeChangeListener(new HorizontalScrollBall.OnTextSizeChangeListener() {
            @Override
            public void onTextSizeChange(int textSize) {
                Log.d("hansel","textSize is : " + textSize);
                showTvContent.setTextSize(textSize);
                SpUtil.getInstance().setCurrentTextSize(textSize);
                if (textSize == HorizontalScrollBall.mWebTextSizeZoom50){
                    textTip.setVisibility(View.VISIBLE);
                    textTip.setText(R.string.text_small_tip);
                }else if (textSize >= HorizontalScrollBall.mWebTextSizeZoom150){
                    textTip.setVisibility(View.VISIBLE);
                    textTip.setText(R.string.text_big_tip);
                }else {
                    textTip.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        refreshShowTvTextSize();
    }

    private void refreshShowTvTextSize() {
        Log.d("hansel"," get stored text size : " + SpUtil.getInstance().getCurrentTextSize());
        showTvContent.setTextSize(SpUtil.getInstance().getCurrentTextSize());
    }
}
