package com.akame.jetpack.ui

import android.Manifest
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akame.developkit.image.choice.ChoiceImageManger
import com.akame.developkit.permission.PermissionManger
import com.akame.developkit.showMsg
import com.akame.jetpack.R
import com.akame.jetpack.adapter.ImageAdapter
import com.akame.jetpack.base.BaseBindingActivity
import com.akame.jetpack.databinding.ActivityJetPackBinding
import com.akame.jetpack.viewmodule.JetPackModule
import kotlinx.android.synthetic.main.activity_jet_pack.*
import kotlinx.coroutines.*
import java.io.File


class JetPackActivity : BaseBindingActivity<ActivityJetPackBinding, JetPackModule>() {
    private lateinit var adapter: ImageAdapter
    private lateinit var choiceImageManger: ChoiceImageManger
    override fun getViewModule(): Class<JetPackModule> {
        return JetPackModule::class.java
    }

    override fun getLayoutResource(): Int = R.layout.activity_jet_pack

    override fun init() {
        createStatue(rv_content)
        dataBinding.jetPack = viewModel
//        viewModel.getTitle()
        rv_content.layoutManager = GridLayoutManager(
            this,
            2,
            RecyclerView.VERTICAL,
            false
        )
//        rv_content.layoutManager = LinearLayoutManager(this)
        adapter = ImageAdapter(viewModel.items.value!!)
        rv_content.adapter = adapter
        lifecycle.addObserver(viewModel)
        viewModel.items.observe(this, Observer { adapter.replaceItems(it!!) })
        viewModel.error.observe(this, Observer { showErrorView() })
        viewModel.netWorkStatue.observe(this, Observer {
            when (it) {
                0 -> {
                    showLoadingView()
                }

                1 -> {
                    Log.e("Tag", "----数据访问完成----")
                    showDefaultView()
                }
            }
        })
        choiceImageManger = ChoiceImageManger(this, object : ChoiceImageManger.ChoiceImageCallBack {
            override fun choiceImage(file: File) {
                showMsg("你选择的图片是----->>" + file.absoluteFile)
            }
        })
//        dataBinding.data = "你好 你非常好"

        /*GlobalScope.launch {
            val result = withContext(Dispatchers.IO) {
                Log.e("Tag", "-------->>    GlobalScope.launch")
                " GlobalScope.launch"
            }

            withContext(Dispatchers.Main) {
                showMsg(result)
            }
        }*/
//        removeItem()
        rv_content.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                super.onDraw(c, parent, state)
                c.drawLine(0f, 0f, parent.width.toFloat(), 0f, Paint().apply {
                    this.color = Color.GREEN
                    this.strokeWidth = 10f
                })
            }
        })

        rv_content.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL).apply {
        })
    }

    override fun initListener() {
        adapter.itemClickListener = {
            viewModel.title.value = "数据改变了吗"
            showMsg("你选择的图片是第${it}张")
           /* startActivity(
                newIndexIntent(ImageActivity::class.java).putExtra(
                    "url",
                    viewModel.items.value?.get(it)?.url
                )
            )*/


            PermissionManger().with(this)
                .request(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    )
                )
                .callBack(object : PermissionManger.PermissionCallBack {
                    override fun fail(permissions: ArrayList<String>) {
                        showMsg("权限被拒绝： ---->>" + permissions[0])
                    }

                    override fun success() {
                        if (it % 2 == 0) {
                            choiceImageManger.onSystemAlbum()
                        } else {
                            choiceImageManger.onCamera()
                        }
                    }
                })
        }

    }

    fun removeItem() {
        GlobalScope.launch(Dispatchers.IO) {
            delay(2000)
            withContext(Dispatchers.Main) {
                adapter.deleteItem(0)
            }
        }
    }


}
