package sogou.mobile.custombar.kotlinFile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import sogou.mobile.custombar.R
import sogou.mobile.custombar.SpUtil
/**
 * Kotlin Android Extensions avoid using
 *  traditional findViewById and inner implement
 *  a view cache(HashMap) to maintain the dataBinging
 */
import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        initViews()
    }

    private fun initViews() {
        // 接口传入格式： 指定 object : interface
        kt_horizontal_bar.setOnTextSizeChangeListener(object : HorizontalSeekBar.OnTextSizeChangeListener {
            override fun onTextSizeChange(textSize: Int) {
                showTvContent.textSize = textSize.toFloat()
                SpUtil.getInstance().currentTextSize = textSize
            }
        })
    }
}
