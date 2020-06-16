package com.akame.jetpack.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.akame.jetpack.R
import com.akame.jetpack.adapter.ViewPage2Adapter
import com.akame.jetpack.showMsg
import kotlinx.android.synthetic.main.activity_view_page2.*

class ViewPage2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_page2)

        vpContent.orientation = ViewPager2.ORIENTATION_VERTICAL
        vpContent.adapter = ViewPage2Adapter(arrayListOf("1", "2", "3"))

        vpContent.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) { super.onPageSelected(position)
                showMsg("滑动到了第${position}页")
            }
        })
    }
}
