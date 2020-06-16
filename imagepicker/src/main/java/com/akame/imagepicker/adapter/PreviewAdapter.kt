package com.akame.imagepicker.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.akame.developkit.disPlay
import com.akame.imagepicker.been.ImageBeen
import com.akame.imagepicker.wigth.PreviewImageView

class PreviewAdapter( private val imageList:ArrayList<ImageBeen>) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun getCount(): Int = imageList.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = PreviewImageView(context = container.context)
        imageView.disPlay(imageList[position].path)
        container.addView(imageView)
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
       container.removeView(`object` as View)
    }

}