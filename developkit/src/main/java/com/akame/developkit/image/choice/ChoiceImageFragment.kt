package com.akame.developkit.image.choice

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.akame.developkit.showLog
import com.akame.developkit.showMsg

class ChoiceImageFragment : Fragment() {
    private lateinit var choiceImageManger: ChoiceImageManger
    private var choiceType: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (choiceType == ChoiceImageManger.IMAGE_ALBUM) {
            choiceImageManger.takeSystemAlbum(this)
        } else if (choiceType == ChoiceImageManger.IMAGE_CAMERA) {
            choiceImageManger.takeCamera(this)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    fun choice(choiceImageManger: ChoiceImageManger, choiceType: Int) {
        this.choiceImageManger = choiceImageManger
        this.choiceType = choiceType
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode!=RESULT_OK){
            return
        }
        var newUri: Uri
        when (requestCode) {
            ChoiceImageManger.IMAGE_ALBUM -> {
                if (data != null && data.data != null) {
                    newUri = Uri.parse(data.data!!.toString())
                    showLog("------>> "+ data.data!!.toString())
                    choiceImageManger.cropImageUri(this, newUri)
                } else {
                    showMsg("选择图片地址异常")
                }
            }

            ChoiceImageManger.IMAGE_CAMERA -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) run {
                    newUri = FileProvider.getUriForFile(
                        context!!,
                        choiceImageManger.fileProvider,
                        choiceImageManger.cameraFile
                    )
                } else run { newUri = Uri.fromFile(choiceImageManger.cameraFile) }
                choiceImageManger.cropImageUri(this, newUri)
            }

            ChoiceImageManger.IMAGE_CROP -> {
                choiceImageManger.choiceCallBack.choiceImage(choiceImageManger.choiceImageFile)
            }
        }
    }


}