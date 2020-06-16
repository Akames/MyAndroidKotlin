package com.akame.imagepicker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akame.developkit.image.ImageCornerType
import com.akame.developkit.image.ImageLoader
import com.akame.imagepicker.R
import com.akame.imagepicker.been.MenuBeen

class SelectPopWindowAdapter(private var menuData: ArrayList<MenuBeen>) :
    RecyclerView.Adapter<SelectPopWindowAdapter.ViewHolder>() {
    lateinit var oldSelect: ImageView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_select_menu, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = menuData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        ImageLoader.with(holder.ivAlbum.context)
            .round(4, ImageCornerType.ALL)
            .centerCrop()
            .url(menuData[position].album)
            .show(holder.ivAlbum)
        holder.tvSize.text = menuData[position].size
        holder.tvTitle.text = menuData[position].title
        holder.itemView.setOnClickListener {
            itemClickListener(position)
            oldSelect.visibility = View.GONE
            holder.ivSelect.visibility = View.VISIBLE
            oldSelect = holder.ivSelect
        }
        if (position == 0) {
            holder.ivSelect.visibility = View.VISIBLE
            oldSelect = holder.ivSelect
        } else {
            holder.ivSelect.visibility = View.GONE
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAlbum = itemView.findViewById<ImageView>(R.id.iv_album)
        val tvTitle = itemView.findViewById<TextView>(R.id.tv_title)
        val tvSize = itemView.findViewById<TextView>(R.id.tv_size)
        val ivSelect = itemView.findViewById<ImageView>(R.id.iv_select)
    }

    var itemClickListener: (Int) -> Unit = {}
}