package com.example.plantscolllection.adapter

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.plantscolllection.R
import com.example.plantscolllection.bean.Botanysearch
import com.example.plantscolllection.bean.PlantsInfo
import com.example.plantscolllection.client.RetrofitClient
import com.example.plantscolllection.databinding.ItemBinding
import com.example.plantscolllection.databinding.ItemBotanySearchBinding
import com.example.plantscolllection.utils.DateUtil
import com.example.plantscolllection.view.PlantsDetailsActivity
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class BotanySearchAdapter(val context: Context) : RecyclerView.Adapter<BotanySearchAdapter.PlantsInfoViewHolder>() {

    companion object{
        private const val TAG = "BotanySearchAdapter"
    }

    private lateinit var itemBotanySearchBinding:ItemBotanySearchBinding
    private var mBotanySearchList: MutableList<Botanysearch> = mutableListOf()

    fun setBotanySearchList(list:List<Botanysearch>){
        mBotanySearchList.clear()
        mBotanySearchList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantsInfoViewHolder {
        itemBotanySearchBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_botany_search, parent, false)
        val holder = PlantsInfoViewHolder(itemBotanySearchBinding)
        holder.itemBotanySearchBinding.itemRelative.setOnClickListener {
            val intent = Intent(context,PlantsDetailsActivity::class.java)
            intent.putExtra("botany_id",mBotanySearchList[holder.adapterPosition].boId)
            context.startActivity(intent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: PlantsInfoViewHolder, position: Int) {
        //这里要给不同的holder(代表每一个条目)设置对象
        val botanySearch = mBotanySearchList[position]
        holder.itemBotanySearchBinding.botanySearch = botanySearch
        Picasso.get().load(RetrofitClient.BASE_URL+botanySearch.photo).into(holder.itemBotanySearchBinding.picture) //加载植物图片
//        holder.itemBinding.recognitionTimeText.text= DateUtil.noteDateShow(SimpleDateFormat("yyyy.MM.dd HH:mm").parse(plantsInfo.time).time, Date().time)
        //根据植物的物种类型来设置CardView背景颜色和TextView文字
        if (botanySearch.species==0){
            holder.itemBotanySearchBinding.speciesText.text =  "珍稀物种"
            holder.itemBotanySearchBinding.speciesCardView.setCardBackgroundColor(context.resources.getColor(R.color.RGB_76_219_154))
            Log.d(TAG, "PlantsInfoAdapter处onBindViewHolder的物种种类为-->${botanySearch.species}")
        }else if (botanySearch.species==1){
            holder.itemBotanySearchBinding.speciesText.text =  "入侵物种"
            holder.itemBotanySearchBinding.speciesCardView.setCardBackgroundColor(context.resources.getColor(R.color.RGB_255_102_102_100_percent))
            Log.d(TAG, "PlantsInfoAdapter处onBindViewHolder的物种种类为-->${botanySearch.species}")
        }else{
            holder.itemBotanySearchBinding.speciesCardView.visibility=View.INVISIBLE
        }

        Log.d(TAG, "onBindViewHolder->${position}处的note为${mBotanySearchList[position]}")
    }

    override fun getItemCount(): Int = mBotanySearchList.size

    class PlantsInfoViewHolder(val itemBotanySearchBinding: ItemBotanySearchBinding) : RecyclerView.ViewHolder(itemBotanySearchBinding.root) {

    }

}