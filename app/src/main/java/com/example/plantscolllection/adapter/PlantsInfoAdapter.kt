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
import com.example.plantscolllection.bean.PlantsInfo
import com.example.plantscolllection.client.RetrofitClient
import com.example.plantscolllection.databinding.ItemBinding
import com.example.plantscolllection.utils.DateUtil
import com.example.plantscolllection.view.PlantsDetailsActivity
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class PlantsInfoAdapter(val context: Context) : RecyclerView.Adapter<PlantsInfoAdapter.PlantsInfoViewHolder>() {

    companion object{
        private const val TAG = "PlantsInfoAdapter"
    }

    private lateinit var itemBinding:ItemBinding
    private var mPlantsInfoList: MutableList<PlantsInfo> = mutableListOf()

    fun setPlantsInfoList(list:List<PlantsInfo>){
        mPlantsInfoList.clear()
        mPlantsInfoList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantsInfoViewHolder {
        itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item, parent, false)
        val holder = PlantsInfoViewHolder(itemBinding)
        holder.itemBinding.itemRelative.setOnClickListener {
            val intent = Intent(context,PlantsDetailsActivity::class.java)
            intent.putExtra("botany_id",mPlantsInfoList[holder.adapterPosition].boId)
            context.startActivity(intent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: PlantsInfoViewHolder, position: Int) {
        //这里要给不同的holder(代表每一个条目)设置对象
        val plantsInfo = mPlantsInfoList[position]
        holder.itemBinding.plantsInfo = plantsInfo
        Picasso.get().load(RetrofitClient.BASE_URL+plantsInfo.photo).into(holder.itemBinding.picture) //加载植物图片
//        holder.itemBinding.recognitionTimeText.text= DateUtil.noteDateShow(SimpleDateFormat("yyyy.MM.dd HH:mm").parse(plantsInfo.time).time, Date().time)
        //根据植物的物种类型来设置CardView背景颜色和TextView文字
        if (plantsInfo.species==0){
            holder.itemBinding.speciesText.text =  "珍稀物种"
            holder.itemBinding.speciesCardView.setCardBackgroundColor(context.resources.getColor(R.color.RGB_76_219_154))
            Log.d(TAG, "PlantsInfoAdapter处onBindViewHolder的物种种类为-->${plantsInfo.species}")
        }else if (plantsInfo.species==1){
            holder.itemBinding.speciesText.text =  "入侵物种"
            holder.itemBinding.speciesCardView.setCardBackgroundColor(context.resources.getColor(R.color.RGB_255_102_102_100_percent))
            Log.d(TAG, "PlantsInfoAdapter处onBindViewHolder的物种种类为-->${plantsInfo.species}")
        }else{
            holder.itemBinding.speciesCardView.visibility=View.INVISIBLE
        }
        //如果是第一个item或者前一个item的时间不等于当前item的时间，让该item的时间显示，否则隐藏
        if (position == 0 || mPlantsInfoList[position - 1].time != plantsInfo.time) {
            holder.itemBinding.recognitionTimeText.visibility = View.VISIBLE
            holder.itemBinding.recognitionTimeText.text = DateUtil.noteDateShow(SimpleDateFormat("yyyy.MM.dd HH:mm").parse(plantsInfo.time).time, Date().time)
        } else {
            holder.itemBinding.recognitionTimeText.visibility = View.GONE
        }

        Log.d(TAG, "onBindViewHolder->${position}处的note为${mPlantsInfoList[position]}")
    }

    override fun getItemCount(): Int = mPlantsInfoList.size

    class PlantsInfoViewHolder(val itemBinding: ItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

    }

}