package com.akame.myandroid_kotlin

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_recycler.*
import java.io.File


class RecyclerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler)
        rv_content.layoutManager = StaggeredGridLayoutManager(2, RecyclerView.HORIZONTAL)
        rv_content.adapter = MyAdapter()

        tv_data.text = """
           缓存 ${FileSizeUtil.getFileOrFilesSize(cacheDir.absolutePath, FileSizeUtil.SIZETYPE_MB)}MB
           SD 缓存 ${FileSizeUtil.getFileOrFilesSize(externalCacheDir.path, FileSizeUtil.SIZETYPE_MB)}
           
           缓存数据 ${FileSizeUtil.getFileOrFilesSize(filesDir.path, FileSizeUtil.SIZETYPE_MB)}MB
           SD 缓存数据 ${FileSizeUtil.getFileOrFilesSize(
            getExternalFilesDir(null).path,
            FileSizeUtil.SIZETYPE_MB
        )}
        """.trimIndent()
    }

    class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        private var datas = arrayOf(1, 2, 3, 4, 5, 6)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_recycler, parent, false)
            val layoutParameter = view.layoutParams
            layoutParameter.width = getScreenWidth(view.context) / 3
            layoutParameter.height = layoutParameter.width
            view.layoutParams = layoutParameter
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = datas.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.tvData.text = datas[position].toString()
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvData = itemView.findViewById<TextView>(R.id.tv_data)
        }

        fun getScreenWidth(context: Context): Int {
            val dm = DisplayMetrics()
            val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(dm)
            return dm.widthPixels
        }
    }


    private fun getFolderSize(file: File): Float {
        var size = 0f
        try {
            val fileList = file.listFiles()
            for (i in fileList.indices) {
                size =
                    if (fileList[i].isDirectory) size + getFolderSize(fileList[i]) else size + fileList[i].length()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    private fun getCacheSize(size: Float): Float { // TODO:设置数据显示
        var size_show = 0f
        Math.round(size / 1024.0f / 1024 * 100).toFloat() / 100 // (这里的100就是2位小数点,如果要其它位,如4位,这里两个100改成10000)
        if (size_show == 0f) size_show = if (size == 0f) 0f else 0.01f
        return size_show
    }

}
