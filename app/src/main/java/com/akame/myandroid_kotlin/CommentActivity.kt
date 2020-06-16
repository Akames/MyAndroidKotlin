package com.akame.myandroid_kotlin

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.akame.developkit.util.ScreenUtil
import kotlinx.android.synthetic.main.activity_comment.*

class CommentActivity : AppCompatActivity() {
   private val dialogFragment = CommentButtonFragment()
    private  lateinit var dialogSheet :MyBottomSheetDialog
    private val test = Test()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        dialogSheet =  MyBottomSheetDialog(this)

        var view: View = layoutInflater.inflate(R.layout.item_layout_comment, null).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ScreenUtil.getScreenHeight() / 3 * 2
            )
        }
        bOpen.setOnClickListener {
//            dialogFragment.showNow(supportFragmentManager,"dialog")
//            dialogSheet.show()
            test.show(supportFragmentManager,"dailog")
        }
    }


}
