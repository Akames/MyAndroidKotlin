package com.akame.developkit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<T, B : ViewDataBinding>(
    var items: ArrayList<T>,
    var layoutRes: Int
) :
    RecyclerView.Adapter<BaseAdapter<T, B>.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding = DataBindingUtil.inflate<B>(layoutInflater, layoutRes, p0, false)
        val viewHolder = ViewHolder(binding)
        viewHolder.itemView.setOnClickListener { itemClickListener(viewHolder.adapterPosition) }
        return viewHolder
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bind(items[p0.adapterPosition])
    }

    inner class ViewHolder(val binding: B) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T) {
            bindItem(binding, item)
            binding.executePendingBindings()
        }
    }

    abstract fun bindItem(binding: B, item: T)

    var itemClickListener: (Int) -> Unit = {}

    /**
     * 替换
     */
    fun replaceItems(items: ArrayList<T>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    /**
     * 添加
     */
    fun addItems(items: ArrayList<T>) {
        val startItems = this.itemCount
        this.items.addAll(items)
        notifyItemRangeInserted(startItems,items.size)
    }

    /**
     * 插入
     */
    fun insertItem(t: T) {
        this.items.add(t)
        notifyItemRangeInserted(0, items.size)
    }

    /**
     * 删除
     */
    fun deleteItem(index: Int) {
        if (index < 0 || index >= items.size) {
            return
        }
        this.items.removeAt(index)
        notifyItemRemoved(index)
//        notifyItemRangeRemoved(index, items.size)
    }
}