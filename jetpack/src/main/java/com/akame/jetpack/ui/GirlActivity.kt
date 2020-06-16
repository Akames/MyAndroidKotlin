package com.akame.jetpack.ui

import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.akame.developkit.setOnClickListen
import com.akame.developkit.showLog
import com.akame.developkit.showMsg
import com.akame.jetpack.R
import com.akame.jetpack.adapter.GirlAdapter
import com.akame.jetpack.base.BaseBindingActivity
import com.akame.jetpack.databinding.ActivityGirlBinding
import com.akame.jetpack.viewmodule.GirlModule
import kotlinx.android.synthetic.main.activity_jet_pack.*
import kotlinx.android.synthetic.main.layout_base_title_bar.*

class GirlActivity : BaseBindingActivity<ActivityGirlBinding, GirlModule>() {
    private val adapter: GirlAdapter by lazy {
        GirlAdapter(viewModel.items.value!!)
    }

    override fun getViewModule(): Class<GirlModule> = GirlModule::class.java

    override fun getLayoutResource(): Int = R.layout.activity_girl

    override fun init() {
        iv_back.setOnClickListen {
            finish()
        }

        tv_bar_title.text = "妹子图"
        val layoutManager = StaggeredGridLayoutManager(1, RecyclerView.VERTICAL)
//        val layoutManager =  GridLayoutManager(this,2,RecyclerView.VERTICAL,false)
        rv_content.layoutManager = layoutManager
        rv_content.adapter = adapter
        viewModel.getData()
        viewModel.items.observe(this, Observer {
            adapter.addItems(it)
        })


        rv_content.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (rv_content.computeVerticalScrollExtent() + rv_content.computeVerticalScrollOffset() >= rv_content.computeVerticalScrollRange()) {
                    viewModel.getData()
                }
            }
        })
    }


}
