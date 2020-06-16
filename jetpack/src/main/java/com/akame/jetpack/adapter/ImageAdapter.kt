package com.akame.jetpack.adapter

import android.app.Activity
import com.akame.developkit.adapter.BaseAdapter
import com.akame.jetpack.R
import com.akame.jetpack.data.net.been.ImageViewBeen
import com.akame.jetpack.databinding.ItemImageBinding

class ImageAdapter(items: ArrayList<ImageViewBeen>) :
    BaseAdapter<ImageViewBeen, ItemImageBinding>(items, R.layout.item_image) {
    override fun bindItem(binding: ItemImageBinding, item: ImageViewBeen) {
        binding.imageData = item
        binding.ivImg.context is Activity
        /*ImageLoader.with(binding.ivImg.context)
            .border(1,Color.BLACK,4)
            .url(item.url)
            .show(binding.ivImg)*/
    }

}