package com.akame.jetpack.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.akame.developkit.newIndexIntent
import com.akame.developkit.util.StatusLayoutManger
import com.akame.jetpack.MyDialog
import com.akame.jetpack.R
import kotlinx.android.synthetic.main.activity_statue_test.*

class StatueTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statue_test)
        val statueUtil = StatusLayoutManger.Builder()
            .context(this)
            .defaultView(fl_bg)
            .emptyViewId(R.layout.layout_test_empty)
            .errorViewId(R.layout.layout_test_err, object : StatusLayoutManger.ErrorViewBuilder {
                override fun errorView(view: View) {
                    val tvError = view.findViewById<TextView>(R.id.tv_error)
                    tvError.setOnClickListener { startActivity(newIndexIntent(ScrollingActivity::class.java)) }
                }
            })
            .builder()
        val myDialog = MyDialog(this)
        btn1.setOnClickListener {
            statueUtil.showDefaultView()
            myDialog.show()
        }

        btn2.setOnClickListener { statueUtil.showErrorView() }

        btn3.setOnClickListener { statueUtil.showEmptyView() }
    }
}
