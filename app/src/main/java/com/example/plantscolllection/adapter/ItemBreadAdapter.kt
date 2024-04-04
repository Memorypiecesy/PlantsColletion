package com.example.plantscolllection.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.plantscolllection.R
import com.example.plantscolllection.databinding.ItemBreadBinding

class ItemBreadAdapter(private val stringList: List<String>) : RecyclerView.Adapter<ItemBreadAdapter.ItemBreadAdapter>() {

    companion object{
        private const val TAG = "HistoryRecordAdapter"
    }

    private lateinit var itemBreadBinding: ItemBreadBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemBreadAdapter {
        itemBreadBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_bread, parent, false)
        val holder = ItemBreadAdapter(itemBreadBinding)
        //给BreadText添加点击事件
        holder.itemBreadBinding.breadText.setOnClickListener {
            onItemClickListener.onItemClick(holder.adapterPosition,holder.itemBreadBinding.breadText.text.toString())
        }
        return holder
    }

    override fun onBindViewHolder(holder: ItemBreadAdapter, position: Int) {
        //设置面包屑的文字，最后一个图标和文字要变成绿色，其他为灰色
        holder.itemBreadBinding.breadText.text = stringList[position]
        if (position==itemCount-1){
            holder.itemBreadBinding.breadText.setTextColor(Color.rgb(76,219,154))
            holder.itemBreadBinding.breadImage.setImageResource(R.drawable.right_arrow_for_bread_green)
        }else{
            holder.itemBreadBinding.breadText.setTextColor(Color.rgb(77,77,77))
            holder.itemBreadBinding.breadImage.setImageResource(R.drawable.right_arrow_for_bread_grey)
        }

    }

    override fun getItemCount(): Int = stringList.size

    class ItemBreadAdapter(val itemBreadBinding: ItemBreadBinding) : RecyclerView.ViewHolder(itemBreadBinding.root) {

    }
    //暴露Item点击事件的接口
    interface OnItemClickListener{
        fun onItemClick(position:Int, breadText:String)
    }
    //定义接口变量
    private lateinit var onItemClickListener: OnItemClickListener

    //注册Listener
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.onItemClickListener=onItemClickListener
    }

}