package com.example.plantscolllection.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.plantscolllection.R
import com.example.plantscolllection.bean.BotanyRes
import com.example.plantscolllection.client.RetrofitClient
import com.example.plantscolllection.databinding.IdentificationResultItemBinding
import com.squareup.picasso.Picasso

class IdentificationResultAdapter : RecyclerView.Adapter<IdentificationResultAdapter.IdentificationResultViewHolder>() {

    companion object{
        private const val TAG = "IdentificationResultA"
    }

    private lateinit var identificationResultItemBinding: IdentificationResultItemBinding
    private var botanyResList: MutableList<BotanyRes> = mutableListOf()

    fun setBotanyResList(list:List<BotanyRes>){
        botanyResList.clear()
        botanyResList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdentificationResultViewHolder {
        identificationResultItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.identification_result_item, parent, false)
        return IdentificationResultViewHolder(identificationResultItemBinding)
    }

    override fun onBindViewHolder(holder: IdentificationResultViewHolder, position: Int) {
        //这里要给不同的holder(代表每一个条目)设置对象
        val botanyRes = botanyResList[position]
        holder.identificationResultItemBinding.similarityText.text=botanyRes.score //相似度
        holder.identificationResultItemBinding.botanyRes=botanyRes
        Log.d(TAG, "照片的路径-->${RetrofitClient.BASE_URL+botanyRes.photo}")

    }

    override fun getItemCount(): Int = botanyResList.size

    class IdentificationResultViewHolder(val identificationResultItemBinding: IdentificationResultItemBinding) : RecyclerView.ViewHolder(identificationResultItemBinding.root) {

    }

}