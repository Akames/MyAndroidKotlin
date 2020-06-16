package com.akame.developkit.image.choice

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.akame.developkit.util.FileUtil
import com.akame.developkit.util.SystemUtil
import java.io.File

class ChoiceImageManger(
    val activity: AppCompatActivity,
    val choiceCallBack: ChoiceImageCallBack,
    private val cropWith: Int = 200,
    private val cropHeight: Int = 200
) {

    //拍照存储的图片
    val cameraFile: File by lazy {
        FileUtil.createFile(FileUtil.getCachePath() + "/takeCamera.jpg")
    }

    //最终选择的图片
    val choiceImageFile: File by lazy {
        FileUtil.createFile(FileUtil.getCachePath() + "/ChoiceImage.jpg")
    }

    val fileProvider: String by lazy {
        SystemUtil.getPackName() + ".fileprovider"
    }


    companion object {
        const val IMAGE_ALBUM = 0X11

        const val IMAGE_CAMERA = 0X12

        const val IMAGE_CROP = 0x13
    }


    /**
     * 打开系统相册
     */
    fun takeSystemAlbum(fragment: ChoiceImageFragment) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        fragment.startActivityForResult(intent, IMAGE_ALBUM)
    }


    /**
     * 打开系统相机
     */
    fun takeCamera(fragment: ChoiceImageFragment) {
        val fileUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                activity,
                fileProvider,
                cameraFile
            )
        } else {
            Uri.fromFile(cameraFile)
        }
        val intentCamera = Intent()
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intentCamera.action = MediaStore.ACTION_IMAGE_CAPTURE
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        fragment.startActivityForResult(intentCamera, IMAGE_CAMERA)
    }


    /**
     * 剪切图片 设置固定高宽裁切
     */
    fun cropImageUri(fragment: ChoiceImageFragment, fileUri: Uri) {
        val intent = Intent("com.android.onCamera.action.CROP")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intent.setDataAndType(fileUri, "image/*")
        intent.putExtra("crop", "true")// 设置裁剪
        intent.putExtra("circleCrop", "true") //设置裁剪类型为圆形
        intent.putExtra("aspectX", 1) // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputX", cropWith)// outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputY", cropHeight)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(choiceImageFile))//将剪切的图片保存到目标Uri中
        intent.putExtra("return-data", false)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("noFaceDetection", true)
        fragment.startActivityForResult(intent, IMAGE_CROP)
    }

    fun onSystemAlbum() {
        val choiceImageFragment = ChoiceImageFragment()
        choiceImageFragment.choice(this, IMAGE_ALBUM)
        activity.supportFragmentManager.beginTransaction()
            .add(choiceImageFragment, activity::class.java.simpleName).commit()
    }

    fun onCamera() {
        val choiceImageFragment = ChoiceImageFragment()
        choiceImageFragment.choice(this, IMAGE_CAMERA)
        activity.supportFragmentManager.beginTransaction()
            .add(choiceImageFragment, activity::class.java.simpleName).commit()
    }

    interface ChoiceImageCallBack {
        fun choiceImage(file: File)
    }


}