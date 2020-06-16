package com.akame.jetpack.adapter

import com.akame.developkit.adapter.BaseAdapter
import com.akame.jetpack.R
import com.akame.jetpack.data.net.been.GirlBeen
import com.akame.jetpack.databinding.ItemGrilBinding
import java.math.BigInteger

class GirlAdapter(items: ArrayList<GirlBeen>) :
    BaseAdapter<GirlBeen, ItemGrilBinding>(items, R.layout.item_gril) {
    override fun bindItem(binding: ItemGrilBinding, item: GirlBeen) {
        binding.viewData = item
    }

}