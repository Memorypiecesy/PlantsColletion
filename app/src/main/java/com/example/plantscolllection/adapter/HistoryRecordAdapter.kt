package com.example.plantscolllection.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.plantscolllection.R
import com.example.plantscolllection.databinding.HistoryRecordItemBinding
import com.example.plantscolllection.entity.HistoryRecord
import com.example.plantscolllection.interfaces.callback.HistoryRecordItemOnClickCallback

class HistoryRecordAdapter(private val historyRecordItemOnClickCallback:HistoryRecordItemOnClickCallback) : RecyclerView.Adapter<HistoryRecordAdapter.HistoryRecordViewHolder>() {

    companion object{
        private const val TAG = "HistoryRecordAdapter"
    }

    private lateinit var historyRecordItemBinding: HistoryRecordItemBinding
    private var historyRecordList: MutableList<HistoryRecord> = mutableListOf()

    fun setHistoryRecordList(list:List<HistoryRecord>){
        historyRecordList.clear()
        historyRecordList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryRecordViewHolder {
        historyRecordItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.history_record_item, parent, false)
        val holder = HistoryRecordViewHolder(historyRecordItemBinding)
        holder.historyRecordItemBinding.searchPageHistoryCardView.setOnClickListener {
            historyRecordItemOnClickCallback.historyRecordItemClick(holder.historyRecordItemBinding.historyRecordText.text.toString())
        }
        return holder
    }

    override fun onBindViewHolder(holder: HistoryRecordViewHolder, position: Int) {
        //设置历史记录的文字
        holder.historyRecordItemBinding.historyRecordText.text = historyRecordList[position].content
    }

    override fun getItemCount(): Int = historyRecordList.size

    class HistoryRecordViewHolder(val historyRecordItemBinding: HistoryRecordItemBinding) : RecyclerView.ViewHolder(historyRecordItemBinding.root) {

    }

}