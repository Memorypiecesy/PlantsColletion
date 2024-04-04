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
import com.example.plantscolllection.utils.DateUtil
import com.example.plantscolllection.view.NoteDetailsActivity
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter(val context: Context) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    companion object{
        private const val TAG = "NoteAdapter"
    }

    private lateinit var itemNoteBinding: ItemNoteBinding
    private var mNoteVoList: MutableList<NoteVo> = mutableListOf()
    private val MODE_CHECK = 0
    private val MODE_EDIT = 1
    private var mEditMode = -1

    fun setNoteList(list:List<NoteVo>){
        mNoteVoList.clear()
        mNoteVoList.addAll(list)
        notifyDataSetChanged()
    }

    //Activity往Adapter里添加数据的方法，isAdd为true代表追加数据。
    fun notifyAdapter(noteVoList:MutableList<NoteVo>, isAdd:Boolean){
        if (isAdd){
            mNoteVoList.addAll(noteVoList)
        }else{
            mNoteVoList=noteVoList
        }
        notifyDataSetChanged()
    }

    //暴露给外部的设置编辑模式的代码
    fun setEditMode(editMode:Int){
        if (editMode==MODE_EDIT){//如果要改成编辑模式，则所有noteVo的isLeftShowed要改成false
            for (noteVo in mNoteVoList){
                noteVo.isLeftShowed=false
            }
        }else{
            for (noteVo in mNoteVoList){
                noteVo.isLeftShowed=true
            }
        }
        mEditMode=editMode
        notifyDataSetChanged()
    }

    //让RecycleView认为每个item都是不同的，就不会出现状态错乱
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        itemNoteBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_note, parent, false)
        val holder = NoteViewHolder(itemNoteBinding)
//        holder.itemNoteBinding.itemNoteCard.setOnClickListener {
//            val intent = Intent(context,NoteDetailsActivity::class.java)
//            intent.putExtra("note_id",mNoteVoList[holder.adapterPosition].id)
//            intent.putExtra("activity_type",2) //代表点击了MyNoteFragment的item跳过去的
//            context.startActivity(intent)
//        }
        holder.itemNoteBinding.itemNoteSlide.setOnClickListener{
            if (mEditMode==MODE_EDIT){//只有处于编辑模式下，才能被点击
                onItemClickListener.onItemClick(holder.adapterPosition)
            }
        }
        holder.itemNoteBinding.deleteText.setOnClickListener {
            onDeleteButtonClickListener.onDeleteButtonClick(holder.adapterPosition)
        }
        return holder
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val noteVo = mNoteVoList[position]
        Picasso.get().load(RetrofitClient.BASE_URL+noteVo.photo).into(holder.itemNoteBinding.picture  ) //加载植物图片
        holder.itemNoteBinding.noteVo=noteVo
//        holder.itemNoteBinding.timeText.text= DateUtil.noteDateShow(SimpleDateFormat("yyyy.MM.dd HH:mm").parse(noteVo.time).time, Date().time)
        if (noteVo.isSelected){ //根据isSelected变量的值判断item有没有被选中，设置对应的图片
            holder.itemNoteBinding.circleImage.setImageResource(R.drawable.selected_circle)
            Log.d(TAG, "onBindViewHolder处noteVo的isSelected状态-->${noteVo.isSelected}")
        }else{
            holder.itemNoteBinding.circleImage.setImageResource(R.drawable.not_selected_circle)
            Log.d(TAG, "onBindViewHolder处noteVo的isSelected状态-->${noteVo.isSelected}")
        }
        if (mEditMode==MODE_EDIT){//处于编辑模式下
            Log.d(TAG, "onBindViewHolder处position位置修改前的noteVo的isLeftShowed状态-->${noteVo.isLeftShowed}")
            if (!noteVo.isLeftShowed){ //左边布局状态如果没显示，就动画右滑
                holder.itemNoteBinding.itemNoteSlide.swipe(0,0,-holder.itemNoteBinding.circleLinear.width,0,500)
                noteVo.isLeftShowed=true //左边布局的状态修改为已经显示
                holder.itemNoteBinding.itemNoteSlide.isSwipeable=false //item不可左滑
            }
            Log.d(TAG, "onBindViewHolder处position位置修改后的noteVo的isLeftShowed状态-->${noteVo.isLeftShowed}")
        }else if (mEditMode==MODE_CHECK){//处于显示模式下
            if (noteVo.isLeftShowed){ //左边布局状态如果已经显示，就动画左滑
                holder.itemNoteBinding.itemNoteSlide.swipe(-holder.itemNoteBinding.circleLinear.width,0,holder.itemNoteBinding.circleLinear.width,0,500)
                noteVo.isLeftShowed=false //左边布局的状态修改为没显示
                holder.itemNoteBinding.itemNoteSlide.isSwipeable=true //item可以左滑。
            }
        }
    }

    override fun getItemCount(): Int = mNoteVoList.size

    class NoteViewHolder(val itemNoteBinding: ItemNoteBinding) : RecyclerView.ViewHolder(itemNoteBinding.root) {

    }

    //暴露Item点击事件的接口，把position传出去
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
    //定义接口变量
    private lateinit var onItemClickListener:OnItemClickListener

    //注册Listener
    fun setOnItemClickListener(onItemClickListener:OnItemClickListener){
        this.onItemClickListener=onItemClickListener
    }

    //暴露删除按钮点击事件的接口，把position传出去
    interface OnDeleteButtonClickListener{
        fun onDeleteButtonClick(position: Int)
    }
    //定义接口变量
    private lateinit var onDeleteButtonClickListener:OnDeleteButtonClickListener

    //注册Listener
    fun setOnDeleteButtonClickListener(onDeleteButtonClickListener:OnDeleteButtonClickListener){
        this.onDeleteButtonClickListener=onDeleteButtonClickListener
    }

}