package com.akame.jetpack

import android.content.Context
import android.widget.TextView
import com.akame.developkit.wigth.BaseDialog
import com.akame.jetpack.data.RepositoryManger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MyDialog(context: Context) : BaseDialog(context) {
    override fun getDialogLayoutRes(): Int = R.layout.layout_my_dialog
    override fun init() {
        val tvTest = findViewById<TextView>(R.id.tv_test)
        tvTest.setOnClickListener {
            // 模拟进行网络请求
            launch(tryBlock = {
                val data = withContext(Dispatchers.IO) {
                    RepositoryManger.service.getImageListAsync()
                }

                withContext(Dispatchers.Main) {
                    JetPackComment.executeResponse(response = data,
                        successBlock = {
                            showMsg(data.data[0].url)
                        }, errorBlock = {
                            showMsg("请求失败")
                        })
                }
            }, catchBlock = {
            })
        }


    }
}