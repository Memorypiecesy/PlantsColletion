package com.example.plantscolllection.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.plantscolllection.R
import com.example.plantscolllection.bean.Classify
import com.example.plantscolllection.databinding.ItemClassifyBinding

class ClassifyAdapter(private val classifyList:List<Classify>) : RecyclerView.Adapter<ClassifyAdapter.ClassifyViewHolder>() {

    companion object{
        private const val TAG = "HistoryRecordAdapter"
    }

    private lateinit var itemClassifyBinding:ItemClassifyBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassifyViewHolder {
        itemClassifyBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_classify, parent, false)
        val holder = ClassifyViewHolder(itemClassifyBinding)
        holder.itemClassifyBinding.textRelative.setOnClickListener {
            onItemClickListener.onItemClick(holder.itemClassifyBinding.nameText.text.toString())
        }
        return holder
    }

    override fun onBindViewHolder(holder: ClassifyViewHolder, position: Int) {
        val classify = classifyList[position]
        //如果是第一个item或者前一个item的index不等于当前item的index，让该item的index显示，否则隐藏
        if (position == 0 || classifyList[position - 1].index?.substring(0,1) != classify.index?.substring(0,1)) {
            holder.itemClassifyBinding.indexText.visibility = View.VISIBLE
            holder.itemClassifyBinding.indexText.text = classify.index?.substring(0,1)
        } else {
            holder.itemClassifyBinding.indexText.visibility = View.GONE
        }
        holder.itemClassifyBinding.nameText.text = classify.name

    }

    override fun getItemCount(): Int = classifyList.size

    class ClassifyViewHolder(val itemClassifyBinding: ItemClassifyBinding) : RecyclerView.ViewHolder(itemClassifyBinding.root) {

    }
    //暴露Item点击事件的接口，把点击的item的name属性暴露出去就好
    interface OnItemClickListener{
        fun onItemClick(nameText:String)
    }
    //定义接口变量
    private lateinit var onItemClickListener: OnItemClickListener

    //注册Listener
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.onItemClickListener=onItemClickListener
    }

}