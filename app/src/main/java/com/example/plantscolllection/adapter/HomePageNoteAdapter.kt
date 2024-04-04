package com.example.plantscolllection.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.plantscolllection.R
import com.example.plantscolllection.bean.NoteVo
import com.example.plantscolllection.client.RetrofitClient
import com.example.plantscolllection.databinding.ItemNoteBinding
import com.example.plantscolllection.databinding.ItemNoteInHomePageBinding
import com.example.plantscolllection.utils.DateUtil
import com.example.plantscolllection.view.NoteDetailsActivity
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class HomePageNoteAdapter(val context: Context) : RecyclerView.Adapter<HomePageNoteAdapter.HomePageNoteViewHolder>() {

    companion object{
        private const val TAG = "NoteAdapter"
    }

    private lateinit var itemNoteInHomePageBinding: ItemNoteInHomePageBinding
    private var homePageNoteVoList: MutableList<NoteVo> = mutableListOf()

    fun setHomePageNoteList(list:List<NoteVo>){
        homePageNoteVoList.clear()
        homePageNoteVoList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePageNoteViewHolder {
        itemNoteInHomePageBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_note_in_home_page, parent, false)
        val holder = HomePageNoteViewHolder(itemNoteInHomePageBinding)
        holder.itemNoteInHomePageBinding.itemNoteCard.setOnClickListener {
            val intent = Intent(context,NoteDetailsActivity::class.java)
            intent.putExtra("note_id",homePageNoteVoList[holder.adapterPosition].id)
            context.startActivity(intent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: HomePageNoteViewHolder, position: Int) {
        val noteVo = homePageNoteVoList[position]
        Picasso.get().load(RetrofitClient.BASE_URL+noteVo.photo).into(holder.itemNoteInHomePageBinding.picture  ) //加载植物图片
        holder.itemNoteInHomePageBinding.noteVo=noteVo
        holder.itemNoteInHomePageBinding.spotPlantsTimeText.text=DateUtil.homePageNoteAboveDateShow(SimpleDateFormat("yyyy.MM.dd HH:mm").parse(noteVo.time).time,Date().time)
        holder.itemNoteInHomePageBinding.spotPlantsPlaceText.text="在${noteVo.place}发现了${noteVo.name}"
        holder.itemNoteInHomePageBinding.timeText.text=DateUtil.noteDateShow(SimpleDateFormat("yyyy.MM.dd HH:mm").parse(noteVo.time).time,Date().time)
        Log.d(TAG, "HomePageNoteAdapter的onBindViewHolder方法处得到的时间-->${DateUtil.noteDateShow(SimpleDateFormat("yyyy.MM.dd HH:mm").parse(noteVo.time).time,Date().time)}")
    }

    override fun getItemCount(): Int = homePageNoteVoList.size

    class HomePageNoteViewHolder(val itemNoteInHomePageBinding: ItemNoteInHomePageBinding) : RecyclerView.ViewHolder(itemNoteInHomePageBinding.root) {

    }

}