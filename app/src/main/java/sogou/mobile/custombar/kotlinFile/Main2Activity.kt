package sogou.mobile.custombar.kotlinFile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import sogou.mobile.custombar.HorizontalScrollBall
import sogou.mobile.custombar.R
import sogou.mobile.custombar.SpUtil

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        initViews()
    }

    private lateinit var horizontalSeekBar: HorizontalSeekBar
    private lateinit var showTv: TextView
    private fun initViews() {
        showTv = findViewById<TextView>(R.id.showTvContent)
        horizontalSeekBar = findViewById<HorizontalSeekBar>(R.id.kt_horizontal_bar)
        // 接口传入格式： 指定 object : interface
        horizontalSeekBar.setOnTextSizeChangeListener(object : HorizontalSeekBar.OnTextSizeChangeListener {
            override fun onTextSizeChange(textSize: Int) {
                showTv.textSize = textSize.toFloat()
                SpUtil.getInstance().currentTextSize = textSize
            }
        })
    }
}
