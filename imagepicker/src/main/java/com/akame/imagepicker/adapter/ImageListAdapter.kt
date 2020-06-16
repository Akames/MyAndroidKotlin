package com.akame.imagepicker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akame.developkit.image.ImageLoader
import com.akame.developkit.util.ScreenUtil
import com.akame.imagepicker.R
import com.akame.imagepicker.been.ImageBeen

class ImageListAdapter(
    private var listBeen: ArrayList<ImageBeen>,
    val isMany: Boolean
) : // isMany 表示是否为多选模式
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEAD = 0x11
        private const val TYPE_CONTENT = 0x12
        private const val MAX_NUM = 9 //设置多选最多不能超过9
    }

    private var isMax = false // 是否选择了是最大值

    private var itemClickListener: ItemClickListener? = null
    private lateinit var selectListBeen: ArrayList<ImageBeen>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View = if (viewType == TYPE_HEAD) {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_camera_layout, parent, false)
        } else {
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_image_layout, parent, false)
        }
        val layoutParams = view.layoutParams
        layoutParams.height = (ScreenUtil.getScreenWidth()) / 3
        view.layoutParams = layoutParams
        return if (viewType == TYPE_HEAD) HeadViewHolder(view) else ContentViewHolder(view)
    }

    override fun getItemCount(): Int = 1 + listBeen.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeadViewHolder -> {
                holder.itemView.setOnClickListener {
                    itemClickListener?.onHeadClick()
                }
                if (isMany && isMax) {
                    holder.vShadow.visibility = View.VISIBLE
                    holder.itemView.isEnabled = false
                } else {
                    holder.vShadow.visibility = View.GONE
                    holder.itemView.isEnabled = true
                }
            }

            is ContentViewHolder -> {
                val been = listBeen[position - 1]
                ImageLoader.with(holder.imageView.context)
                    .centerCrop()
                    .url(been.path)
                    .show(holder.imageView)
                holder.itemView.setOnClickListener {
                    itemClickListener?.onItemClick(position - 1, listBeen[position - 1].path)
                }

                if (isMany) {
                    holder.ivSelect.setOnClickListener {
                        val selectIndex = selectListBeen.indexOf(been)
                        if (selectIndex == -1) {
                            //说明这个图片没有被选择过
                            been.position = position
                            addSelectList(been)
                            notifyItemChanged(position, "Akame")
                        } else {
                            //说明这个图片被添加过
                            removeSelectList(been)
                            notifyItemChanged(position, "Akame")
                        }

                        if (isMax && selectListBeen.size < MAX_NUM || !isMax && selectListBeen.size == MAX_NUM) {
                            notifyDataSetChanged()
                        }
                        isMax = selectListBeen.size == MAX_NUM
                        itemClickListener?.onSelectClick(selectListBeen.size)
                    }

                    val selectIndex = selectListBeen.indexOf(been)
                    if (selectIndex != -1) {
                        selectListBeen[selectIndex].position = position //更新已经保存的图片的位置，因为位置可能是之前的
                        holder.ivSelect.visibility = View.VISIBLE
                        holder.ivSelect.isClickable = true
                        holder.tvCount.visibility = View.VISIBLE
                        holder.tvCount.text = (selectIndex + 1).toString()
                        holder.vShadow.visibility = View.GONE
                    } else {
                        holder.tvCount.visibility = View.GONE
                        if (isMax) {
                            holder.ivSelect.visibility = View.GONE
                            holder.ivSelect.isClickable = false
                            holder.vShadow.visibility = View.VISIBLE
                        } else {
                            holder.ivSelect.visibility = View.VISIBLE
                            holder.ivSelect.isClickable = true
                            holder.vShadow.visibility = View.GONE
                        }
                    }

                }
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            if (holder is ContentViewHolder) {
                val been = listBeen[position - 1]
                val selectIndex = selectListBeen.indexOf(been)
                if (selectIndex != -1) {
                    holder.tvCount.visibility = View.VISIBLE
                    holder.tvCount.text = (selectIndex + 1).toString()
                } else {
                    holder.tvCount.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_HEAD
        } else {
            TYPE_CONTENT
        }
    }

    inner class HeadViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val vShadow = itemView.findViewById<View>(R.id.vShadow)
    }

    inner class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.iv_image)
        val ivSelect = itemView.findViewById<ImageView>(R.id.iv_select)
        val tvCount = itemView.findViewById<TextView>(R.id.tv_count)
        val vShadow = itemView.findViewById<View>(R.id.vShadow)

        init {
            ivSelect.visibility = if (isMany) View.VISIBLE else View.GONE
            ivSelect.isClickable = isMany
        }
    }

    interface ItemClickListener {
        fun onHeadClick()
        fun onItemClick(pos: Int, imagePath: String)
        fun onSelectClick(selectCount: Int)
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    private fun addSelectList(imageBeen: ImageBeen) {
        selectListBeen.add(imageBeen)
    }

    /**
     * 删除并更新选中图片的位置
     */
    private fun removeSelectList(imageBeen: ImageBeen) {
        val removeIndex = selectListBeen.indexOf(imageBeen) //找到要删除图片的位置
        selectListBeen.remove(imageBeen)  //将图片从选择列表中删除 在删除之后的下标都会向前移动
        selectListBeen.forEachIndexed { index, been ->
            //如果当前下标大于或者等于删除下标 则需要更新 因为向前移动了一位 所以有等于情况
            if (index >= removeIndex) {
                notifyItemChanged(been.position, "Akame")
            }
        }
    }

    fun getSelectList(): ArrayList<ImageBeen> = selectListBeen

    fun setSelectList(list: ArrayList<ImageBeen>) {
        this@ImageListAdapter.selectListBeen = list
        isMax = selectListBeen.size == MAX_NUM
    }
}