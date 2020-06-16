package com.akame.myandroid_kotlin

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Resources
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.akame.developkit.*
import com.akame.developkit.util.CompressImageUtils
import com.akame.developkit.util.FileUtil
import com.akame.imagepicker.EventCode
import com.akame.imagepicker.been.ImageBeen
import com.akame.imagepicker.ui.ImageListActivity
import com.akame.jetpack.ui.GirlActivity
import com.akame.jetpack.ui.NavigationActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.PrintStream

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private var selectList = arrayListOf<ImageBeen>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvJetPack.setOnClickListener {
            startActivity(

//                newIndexIntent(ImageListActivity::class.java).putExtra(
//                    "isMany",
//                    true
//                ).putExtra("selectList", selectList)
                newIndexIntent(ImageListActivity::class.java)
//                newIndexIntent(JetPackActivity::class.java)
            )
//            startActivity(newIndexIntent(WebTestActivity::class.java))
        }

        EventBus.getDefault().register(this)

        tvVideo.setOnClickListener { startActivity(newIndexIntent(VideoActivity::class.java)) }

        tvConment.setOnClickListener { startActivity(newIndexIntent(CommentActivity::class.java)) }

        tvVpContent.setOnClickListener {
            startActivity(newIndexIntent(SlidingActivity::class.java))
        }

        tvRecycler.setOnClickListener { startActivity(newIndexIntent(RecyclerActivity::class.java)) }
//        testWirite("123")

        tvNav.setOnClickListener { startActivity(newIndexIntent(NavigationActivity::class.java)) }

        tv_girl.setOnClickListener { startActivity(newIndexIntent(GirlActivity::class.java)) }

        tv_bitmap.setOnClickListener{ startActivity(newIndexIntent(BitmapActivity::class.java))}

        tv_my_view.setOnClickListen { startActivity(newIndexIntent(MyViewActivity::class.java)) }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(eventSimple: EventSimple<Any>) {
        when (eventSimple.code) {
            EventCode.CROP_IMAGE_CODE -> {
                val path = eventSimple.data as String
                showMsg(path)
            }

            EventCode.SELECT_IMAGE_CODE -> {
                GlobalScope.launch(Dispatchers.IO) {
                    selectList = eventSimple.data as ArrayList<ImageBeen>
                    val filePath = selectList[0].path
                    val file = File(FileUtil.getCachePath(), "/雅俗共赏.jpg")
                    if (file.exists()) {
                        file.delete()
                    }
                    CompressImageUtils.samplingToSize(filePath, 500, file.absolutePath)
                    launch(Dispatchers.Main) { showMsg("压缩后图片地址为${file.absoluteFile}") }
                }
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
        cancel()
    }


    fun testWirite(data: String) {
        val file = File(Environment.getExternalStorageDirectory().path, "test.txt")
        if (!file.exists()) {
            file.createNewFile()
        }

        launch {
            withContext(Dispatchers.IO) {
                /* val fos = FileOutputStream(file)
                 fos.write(data.toByteArray())
                 fos.close()*/
                while (true) {
                    delay(3000)
                    val ps = PrintStream(FileOutputStream(file))
                    val fileData = testRead(file)
                    showLog("---->> $fileData")
                    ps.println(fileData)
                    ps.append(data)
                    showLog("写入成功")
                }
            }

        }
    }

    override fun onPause() {
        super.onPause()
    }

    fun testRead(file: File): String {
        val fis = FileInputStream(file)
        val size = fis.available()
        val buffer = ByteArray(size)
        fis.read(buffer)
        fis.close()
        return String(buffer, Charsets.UTF_8)
    }

    val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            //服务断开
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            //服务绑定成功
            val binder = Book.Stub.asInterface(service)
            Log.e("tag", "--------" + binder.name())
        }
    }

    fun startService(){
        val intent = Intent()
        intent.component = ComponentName("com.akame.myandroid_kotlin","com.akame.myandroid_kotlin.MyService")
        bindService(intent,connection, Context.BIND_AUTO_CREATE)
    }

    override fun getResources(): Resources {
        return super.getResources()
    }




}