package com.akame.myandroid_kotlin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sliding.*

class SlidingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sliding)
        lv_data.adapter = TestAdapter()
    }

    class TestAdapter : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view: View? = null
            if (view == null) {
                view = LayoutInflater.from(parent?.context)
                    .inflate(R.layout.item_rv_content, parent, false)
            }
            return view!!
        }

        override fun getItem(position: Int): Any = position

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getCount(): Int = 30

    }
}
