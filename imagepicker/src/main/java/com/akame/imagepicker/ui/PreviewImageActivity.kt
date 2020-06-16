package com.akame.imagepicker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.akame.developkit.EventSimple
import com.akame.imagepicker.EventCode
import com.akame.imagepicker.R
import com.akame.imagepicker.adapter.PreviewAdapter
import com.akame.imagepicker.been.ImageBeen
import kotlinx.android.synthetic.main.activity_preview_image.*
import org.greenrobot.eventbus.EventBus

class PreviewImageActivity : AppCompatActivity() {
    private lateinit var imageList: ArrayList<ImageBeen>
    private lateinit var selectList: ArrayList<ImageBeen>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_image)
        imageList = intent.getSerializableExtra("imageList") as ArrayList<ImageBeen>
        selectList = intent.getSerializableExtra("selectList") as ArrayList<ImageBeen>
        val currentIndex = intent.getIntExtra("currentIndex", 0)

        vpContent.adapter = PreviewAdapter(imageList)
        vpContent.setCurrentItem(currentIndex, false)
        iv_back.setOnClickListener { finish() }
        setSelectSize()
        changeSelectStatue(currentIndex)

        vpContent.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                changeSelectStatue(position)
            }

        })

        ivSelect.setOnClickListener {
            val position = vpContent.currentItem
            val imageBeen = imageList[position]
            if (selectList.indexOf(imageBeen) != -1) {
                selectList.remove(imageBeen)
                setSelectSize()
            } else {
                selectList.add(imageBeen)
            }
            changeSelectStatue(position)
            setSelectSize()
        }

        tvConfirm.setOnClickListener {
            finish()
        }
    }

    fun changeSelectStatue(position: Int) {
        val index = selectList.indexOf(imageList[position])
        ivSelect.setImageResource(if (index == -1) R.mipmap.ic_un_select else R.mipmap.ic_select)
        if (selectList.size == 9 && index == -1) {
            ivSelect.alpha = 0.5f
            ivSelect.isEnabled = false
        } else {
            ivSelect.alpha = 1f
            ivSelect.isEnabled = true
        }
    }

    private fun setSelectSize() {
        tvConfirm.text = "完成(${selectList.size})"
    }

    override fun onDestroy() {
        // eventBus 刷新选择容器
        EventBus.getDefault().post(EventSimple(EventCode.UPDATE_SELECT_LIST, selectList))
        super.onDestroy()
    }


}
