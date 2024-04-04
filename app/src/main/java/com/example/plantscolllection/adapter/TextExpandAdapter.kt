package com.example.plantscolllection.adapter

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.plantscolllection.R
import com.example.plantscolllection.bean.HotBotany
import com.example.plantscolllection.databinding.HotBotanyItemBinding
import com.example.plantscolllection.databinding.TextExpandItemBinding
import com.example.plantscolllection.interfaces.callback.TextExpandItemOnClickCallback

class TextExpandAdapter(private val textExpandItemOnClickCallback: TextExpandItemOnClickCallback) : RecyclerView.Adapter<TextExpandAdapter.TextExpandViewHolder>() {

    companion object{
        private const val TAG = "HotBotanyAdapter"
    }

    private lateinit var textExpandItemBinding: TextExpandItemBinding
    private var textExpandList: MutableList<String> = mutableListOf()
    private var highLightWords = ""

    fun setTextExpandList(list:List<String>, highLightWordsForAssign:String){
        highLightWords=highLightWordsForAssign
        textExpandList.clear()
        textExpandList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextExpandViewHolder {
        textExpandItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.text_expand_item, parent, false)
        val holder = TextExpandViewHolder(textExpandItemBinding)
        //点击文字扩展Item的时候，调用回调接口的方法进行文字搜索
        holder.textExpandItemBinding.textExpandRelative.setOnClickListener {
            textExpandItemOnClickCallback.textExpandItemClick(holder.textExpandItemBinding.textExpand.text.toString())
        }
        return holder
    }

    override fun onBindViewHolder(holder: TextExpandViewHolder, position: Int) {
        //这里要给文字扩展返回的列表的每一个String的关键字高亮
        val textExpandString = textExpandList[position]
        val spannable = SpannableStringBuilder(textExpandString)
        val index = textExpandString.indexOf(highLightWords) //查找要高亮的词在每一个String中的位置
        Log.d(TAG, "TextExpandAdapter处高亮的词->${highLightWords}")
        if (index!=-1){
            Log.d(TAG, "TextExpandAdapter处高亮的词index->${index}")
            spannable.setSpan(ForegroundColorSpan(Color.parseColor("#FF4CDB9A")),index,index+highLightWords.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            holder.textExpandItemBinding.textExpand.text=spannable
        }else{
            holder.textExpandItemBinding.textExpand.text=textExpandString
        }
    }

    override fun getItemCount(): Int = textExpandList.size

    class TextExpandViewHolder(val textExpandItemBinding: TextExpandItemBinding) : RecyclerView.ViewHolder(textExpandItemBinding.root) {

    }

}