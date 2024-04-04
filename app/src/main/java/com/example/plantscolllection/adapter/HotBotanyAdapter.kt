package com.example.plantscolllection.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.plantscolllection.R
import com.example.plantscolllection.bean.HotBotany
import com.example.plantscolllection.databinding.HotBotanyItemBinding
import com.example.plantscolllection.interfaces.callback.HotBotanyItemOnClickCallback

class HotBotanyAdapter(private val hotBotanyItemOnClickCallback: HotBotanyItemOnClickCallback) : RecyclerView.Adapter<HotBotanyAdapter.HotBotanyViewHolder>() {

    companion object{
        private const val TAG = "HotBotanyAdapter"
    }

    private lateinit var hotBotanyItemBinding: HotBotanyItemBinding
    private var hotBotanyList: MutableList<HotBotany> = mutableListOf()

    fun setHotBotanyList(list:List<HotBotany>){
        hotBotanyList.clear()
        hotBotanyList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotBotanyViewHolder {
        hotBotanyItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.hot_botany_item, parent, false)
        val holder = HotBotanyViewHolder(hotBotanyItemBinding)
        holder.hotBotanyItemBinding.hotBotanyNameText.setOnClickListener { //热门搜索植物名字点击后，通过回调接口把名字传过去
            hotBotanyItemOnClickCallback.hotBotanyItemClick(holder.hotBotanyItemBinding.hotBotanyNameText.text.toString())
        }
        return holder
    }

    override fun onBindViewHolder(holder: HotBotanyViewHolder, position: Int) {
        //这里要给不同的holder(代表每一个条目)设置对象
        val hotBotany = hotBotanyList[position]
        holder.hotBotanyItemBinding.hotBotany = hotBotany
        holder.hotBotanyItemBinding.sequenceText.text = "${position+1}"
    }

    override fun getItemCount(): Int = hotBotanyList.size

    class HotBotanyViewHolder(val hotBotanyItemBinding: HotBotanyItemBinding) : RecyclerView.ViewHolder(hotBotanyItemBinding.root) {

    }

}