package com.example.plantscolllection.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantscolllection.R
import com.example.plantscolllection.adapter.NoteAdapter
import com.example.plantscolllection.bean.NoteVo
import com.example.plantscolllection.bean.RequestParm
import com.example.plantscolllection.databinding.FragmentMyNoteBinding
import com.example.plantscolllection.interfaces.callback.NoteCallbackContract
import com.example.plantscolllection.interfaces.listener.FragmentOnKeyListener
import com.example.plantscolllection.utils.Data
import com.example.plantscolllection.viewmodel.HomeActivityViewModel
import com.example.plantscolllection.viewmodel.MyNoteFragmentViewModel

class MyNoteFragment : Fragment(), FragmentOnKeyListener {

    private lateinit var fragmentMyNoteBinding: FragmentMyNoteBinding
    private val noteAdapter by lazy { NoteAdapter(requireActivity()) }
    private val myNoteFragmentViewModel by viewModels<MyNoteFragmentViewModel>()
    private val sp by lazy { requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE) }
    private var pageNoThis = 0
    private var pageSizeThis = 10
    private val MODE_CHECK = 0
    private val MODE_EDIT = 1
    private var mEditMode = MODE_CHECK
    private lateinit var homeActivityViewModel: HomeActivityViewModel
    private val mNoteVoList by lazy { Data.noteVoList }
    private var index = 0 //记录选中的item的个数
    private var deleteSingleNotePosition = -1 //左滑删除的单个笔记的位置
    private var deleteNoteType = -1 //删除笔记的类型。1代表删除单个笔记，2代表删除多个笔记

    //Adapter中item点击的回调接口
    private val onItemClickListener = object :NoteAdapter.OnItemClickListener{

        override fun onItemClick(position: Int) {
            val noteVo = mNoteVoList[position]
            if (!noteVo.isSelected){//如果点击的noteVo处于未选中状态
                noteVo.isSelected=true //变为未选中状态
                index++ //选中的item的个数++
                if (index == mNoteVoList.size){//如果选中了所有的item，就通知Activity更新底部布局中的"全选"圆圈为选中状态
                    homeActivityViewModel.isAllSelectedImageSelectedForActivity.value=true
                }
            }else{ //如果点击的noteVo处于选中状态
                noteVo.isSelected=false //变为未选中状态
                index-- //选中的item的个数--
                homeActivityViewModel.isAllSelectedImageSelectedForActivity.value=false //通知Activity的"全选"圆圈未选中状态
            }
            //如果选中的item个数为0，通知Activity更新底部布局中"删除"按钮，让它变灰，不能被点击；否则能点击
            homeActivityViewModel.isDeleteButtonEnabled.value = index!=0
            noteAdapter.notifyDataSetChanged()
            Log.d(TAG, "onItemClick处index的值-->${index}")
            Log.d(TAG, "onItemClick处mNoteVoList的容量-->${mNoteVoList.size}")
        }

    }

    //Adapter中删除按钮点击的回调接口
    private val onDeleteButtonClickListener = object :NoteAdapter.OnDeleteButtonClickListener{

        override fun onDeleteButtonClick(position: Int) {
            homeActivityViewModel.showDeleteSingleNoteWarnDialog.value=true //通知Activity弹出"删除笔记警告"弹窗。
            deleteSingleNotePosition=position //记录点击位置
            Log.d(TAG, "onDeleteButtonClick方法处记录的要删除的item的位置-->${deleteSingleNotePosition}")
            Log.d(TAG, "onDeleteButtonClick方法处打印的item的List集合-->${mNoteVoList}")
        }

    }
    //获取笔记回调接口
    private val getNotesCallback by lazy {
        object : NoteCallbackContract.NotesCallback {

            override fun getNotesSuccess(noteVoList: List<NoteVo>) {
                Log.d(TAG, "MyNoteFragment处获取笔记列表成功->${noteVoList}")
                noteAdapter.setNoteList(noteVoList)
            }

            override fun getNotesFailed(errorMessage: String) {
                Log.d(TAG, "MyNoteFragment处获取笔记列表失败->${errorMessage}")
            }

        }
    }

    ///删除笔记回调接口
    private val deleteNoteCallback by lazy {
        object : NoteCallbackContract.DeleteNotesCallback {

            override fun deleteNotesSuccess(deleteSuccess: String) {
                Log.d(TAG, "MyNoteFragment处删除笔记成功->${deleteSuccess}")
                homeActivityViewModel.hideDeletingNoteDialog.value=true //隐藏"删除笔记中"弹窗
                if (deleteNoteType==1){//如果删除单个笔记
                    noteAdapter.notifyItemRemoved(deleteSingleNotePosition) //更新Adapter在删除位置的item
                }else{
                    noteAdapter.notifyDataSetChanged()
                    homeActivityViewModel.isBottomRelativeShowedLiveData.value=false //隐藏Activity底部布局
                    noteAdapter.setEditMode(MODE_CHECK) //更新为查看模式
                    fragmentMyNoteBinding.manageAndCancelText.text = "管理" //更改右上角的文字
                }
//                Toast.makeText(requireActivity(),"删除笔记成功",Toast.LENGTH_SHORT).show()
            }

            override fun deleteNotesFailed(errorMessage: String) {
                Log.d(TAG, "MyNoteFragment处删除笔记列表失败->${errorMessage}")
                homeActivityViewModel.hideDeletingNoteDialog.value=true //隐藏"删除笔记中"弹窗
//                Toast.makeText(requireActivity(),"删除笔记失败",Toast.LENGTH_SHORT).show()
            }

        }
    }

    companion object {
        private const val TAG = "MyNoteFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        activity?.let {
            it.window.statusBarColor = Color.WHITE
        }
        fragmentMyNoteBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_my_note,container,false)
        return fragmentMyNoteBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeActivityViewModel= ViewModelProvider(requireActivity())[HomeActivityViewModel::class.java]
        initView()
        //Activity中全选圆圈状态的观察
        homeActivityViewModel.isAllSelectedImageSelectedForFragment.observe(requireActivity()){
            if (it){ //如果Activity中全选圆圈为选中，设置数据List中所有的noteVo的isSelected为true，index变为数据List的size
                index=mNoteVoList.size
                for (noteVo in mNoteVoList){
                    noteVo.isSelected=true
                }
            }else{//如果Activity中全选圆圈为未选中，设置数据List中所有的noteVo的isSelected为false，index变为0
                index=0
                for (noteVo in mNoteVoList){
                    noteVo.isSelected=false
                }
            }
            noteAdapter.notifyDataSetChanged() //刷新noteAdapter
        }
        //是否删除笔记的LiveData观察
        homeActivityViewModel.isDeleteNotes.observe(requireActivity()){
            if (it){ //如果LiveData的值为true，说明需要删除多个笔记
                //遍历数据List，如果bean的isSelected为true，就删除该位置的item，把id放进一个List中
                val noteIdList = mutableListOf<Int>()
                //不能遍历的时候同时删除，所以开一个List存放一下要删除的NoteVo
                val noteVoList = mutableListOf<NoteVo>()
                for (noteVo in mNoteVoList){
                    if (noteVo.isSelected){
                        noteVoList.add(noteVo)
                        noteIdList.add(noteVo.id)
                    }
                }
                deleteNoteType=2
                mNoteVoList.removeAll(noteVoList)
//                noteAdapter.notifyDataSetChanged()
//                homeActivityViewModel.hideDeletingNoteDialog.value=true //隐藏"删除笔记中"弹窗
//                homeActivityViewModel.isBottomRelativeShowedLiveData.value=false //隐藏Activity底部布局
//                noteAdapter.setEditMode(MODE_CHECK) //更新为查看模式
//                fragmentMyNoteBinding.manageAndCancelText.text = "管理" //更改右上角的文字
//                Toast.makeText(requireActivity(),"删除笔记成功",Toast.LENGTH_SHORT).show()
                Log.d(TAG, "删除的idList-->${noteIdList}")
                sp.getString("token","no this key")?.let {token->
                    myNoteFragmentViewModel.deleteNotes(token,noteIdList.toList(),deleteNoteCallback)
                }
            }else{
                Log.d(TAG, "要删除的笔记id-->${mNoteVoList[deleteSingleNotePosition].id}")
                Toast.makeText(requireActivity(),"要删除的笔记id-->${mNoteVoList[deleteSingleNotePosition].id}",Toast.LENGTH_SHORT).show()
                val noteIdList = mutableListOf<Int>() //记录要删除的笔记的id
                noteIdList.add(mNoteVoList[deleteSingleNotePosition].id)
                mNoteVoList.removeAt(deleteSingleNotePosition) //删除对应位置处的item
                deleteNoteType=1
                sp.getString("token","no this key")?.let {token->
                    myNoteFragmentViewModel.deleteNotes(token,noteIdList.toList(),deleteNoteCallback)
                }

            }
        }
        //获取笔记
        sp.getString("token","no this key")?.let {token->
            myNoteFragmentViewModel.getNotes(token,pageNoThis,pageSizeThis,getNotesCallback)
        }
    }

    private fun initView() {
        noteAdapter.apply {
            notifyAdapter(mNoteVoList,false) //给Adapter设置数据
            setOnItemClickListener(onItemClickListener)
            setOnDeleteButtonClickListener(onDeleteButtonClickListener)
        }
        //配置RecyclerView
        fragmentMyNoteBinding.myNotePageRecycler.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(requireActivity())
        }
        //"管理/取消"文字点击事件
        fragmentMyNoteBinding.manageAndCancelText.setOnClickListener{
            mEditMode = if (mEditMode==MODE_EDIT) MODE_CHECK else MODE_EDIT //把模式取反
            if (mEditMode == MODE_EDIT) { //如果当前是编辑模式
                fragmentMyNoteBinding.manageAndCancelText.text = "取消"
                homeActivityViewModel.isBottomRelativeShowedLiveData.value=true //改变LiveData的值，Activity观察到了就会显示布局
                homeActivityViewModel.isAllSelectedImageSelectedForFragment.value=false //每次切到编辑模式，让所有item的image变为未选中状态
            } else { //如果当前是查看模式
                fragmentMyNoteBinding.manageAndCancelText.text = "管理"
                homeActivityViewModel.isBottomRelativeShowedLiveData.value=false //改变LiveData的值，Activity观察到了就会隐藏布局
            }

            noteAdapter.setEditMode(mEditMode)
        }
    }
    //当用户按下的Enter键搜索笔记
    override fun onKeyEnter() {
        if (TextUtils.isEmpty(fragmentMyNoteBinding.notePageEdit.text)){
            Toast.makeText(requireActivity(),"搜索内容不能为空！",Toast.LENGTH_SHORT).show()
        }else{
            val requestParm = RequestParm().apply {
                pageNo=pageNoThis
                pageSize=pageSizeThis
                searchString=fragmentMyNoteBinding.notePageEdit.text.toString()
            }
            myNoteFragmentViewModel.searchPlants(requestParm,getNotesCallback)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (homeActivityViewModel.isBottomRelativeShowedLiveData.value==true){
            homeActivityViewModel.isBottomRelativeShowedLiveData.value=false //去其他页面时让底部弹窗消失
        }
        fragmentMyNoteBinding.notePageEdit.setText("")
    }

}