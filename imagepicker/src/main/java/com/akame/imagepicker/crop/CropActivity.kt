package com.akame.imagepicker.crop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.akame.developkit.EventSimple
import com.akame.developkit.util.FileUtil
import com.akame.imagepicker.EventCode
import com.akame.imagepicker.R
import kotlinx.android.synthetic.main.activity_crop.*
import org.greenrobot.eventbus.EventBus

class CropActivity : AppCompatActivity() {
    private lateinit var imagePath: String
    private var isCamera = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)
        iv_back.setOnClickListener { finish() }

        imagePath = intent.getStringExtra("imagePath")
        isCamera = intent.getBooleanExtra("isCamera", false)
        iv_image.setImageResource(imagePath)

        tv_confirm.setOnClickListener {
            val cropPath = iv_image.corpImage(cropView.getFrameRectF())
            EventBus.getDefault().post(
                EventSimple(
                    EventCode.CROP_IMAGE_CODE,
                    cropPath
                )
            )
            setResult(RESULT_OK)
            finish()
        }
    }


    override fun onDestroy() {
        if (isCamera) {
            //如果是照相机拍的图片 在裁剪完成后 删除原图，给用户节省空间
            FileUtil.deleteFile(imagePath)
        }
        super.onDestroy()
    }
}
