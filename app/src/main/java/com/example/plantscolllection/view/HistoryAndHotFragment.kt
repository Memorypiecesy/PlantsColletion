package com.example.plantscolllection.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantscolllection.R
import com.example.plantscolllection.adapter.HistoryRecordAdapter
import com.example.plantscolllection.adapter.HotBotanyAdapter
import com.example.plantscolllection.bean.HotBotany
import com.example.plantscolllection.databinding.FragmentHistoryAndHotBinding
import com.example.plantscolllection.interfaces.callback.HistoryRecordItemOnClickCallback
import com.example.plantscolllection.interfaces.callback.HotBotanyCallback
import com.example.plantscolllection.interfaces.callback.HotBotanyItemOnClickCallback
import com.example.plantscolllection.viewmodel.SearchViewModel
import com.google.android.flexbox.FlexboxLayoutManager

class HistoryAndHotFragment : Fragment() {

    private lateinit var fragmentHistoryAndHotBinding: FragmentHistoryAndHotBinding
    private val hotBotanyAdapter by lazy { HotBotanyAdapter(hotBotanyItemOnClickCallback) }
    private val historyRecordAdapter by lazy { HistoryRecordAdapter(historyRecordItemOnClickCallback) }
    private lateinit var searchViewModel:SearchViewModel
    //获得热门搜索植物的回调接口
    private val hotBotanyCallback by lazy {
        object : HotBotanyCallback {
            override fun getHotBotanySuccess(hotBotanyResList: List<HotBotany>) {
                hotBotanyAdapter.setHotBotanyList(hotBotanyResList)
                Log.d(TAG, "HistoryAndHotFragment处获取hotBotany集合成功->${hotBotanyResList}")
            }

            override fun getHotBotanyFailed(errorMessage: String) {
                Log.d(TAG, "HistoryAndHotFragment处获取hotBotany集合失败->${errorMessage}")
            }
        }
    }
    //历史记录item点击后的回调：导航到SearchResultFragment，通过ViewModel的LiveData通知SearchActivity的EditText更改内容，让文字扩展RecyclerView消失
    private val historyRecordItemOnClickCallback by lazy {
        object : HistoryRecordItemOnClickCallback {
            override fun historyRecordItemClick(searchString: String) {
                searchViewModel.isTextExpandRecyclerShowed.value=false
                val bundle = Bundle()
                bundle.putString("search_string",searchString)
                findNavController().navigate(R.id.action_historyAndHotFragment_to_searchResultFragment,bundle)
                searchViewModel.historyRecordAndHotBotanyForEditText.value=searchString
            }
        }
    }

    //点击热门搜索植物Item后的回调接口：导航到SearchResultFragment，通过ViewModel的LiveData通知SearchActivity的EditText更改内容，让文字扩展RecyclerView消失
    private val hotBotanyItemOnClickCallback by lazy {
        object : HotBotanyItemOnClickCallback {
            override fun hotBotanyItemClick(searchString: String) {
                searchViewModel.isTextExpandRecyclerShowed.value=false
                val bundle = Bundle()
                bundle.putString("search_string",searchString)
                findNavController().navigate(R.id.action_historyAndHotFragment_to_searchResultFragment,bundle)
                searchViewModel.historyRecordAndHotBotanyForEditText.value=searchString
            }
        }
    }

    companion object{
        private const val TAG = "HistoryAndHotFragment"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchViewModel = ViewModelProvider(requireActivity())[SearchViewModel::class.java]  //和SearchActivity的ViewModel同一个
        //对SearchViewModel含有历史记录的LiveData进行观察
        searchViewModel.getAllHistoryRecords().observe(this){
            historyRecordAdapter.setHistoryRecordList(it)
            Log.d(TAG, "HistoryAndHotFragment处检测到SearchViewModel的historyRecordLiveData更新->${it}")
        }
        Log.d(TAG, "HistoryAndHotFragment的onCreate方法被调用...")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "HistoryAndHotFragment的onCreateView方法被调用...")
        fragmentHistoryAndHotBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_history_and_hot, container, false)
        return fragmentHistoryAndHotBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        //请求热门搜索的植物
        searchViewModel.getHotBotany(hotBotanyCallback)
        Log.d(TAG, "HistoryAndHotFragment的onViewCreated方法被调用...")
    }

    private fun initView() {
        //配置热门搜索RecyclerView
        fragmentHistoryAndHotBinding.hotBotanyRecycler.apply {
            layoutManager= LinearLayoutManager(requireActivity())
            adapter=hotBotanyAdapter
        }
        //配置历史记录RecyclerView
        fragmentHistoryAndHotBinding.historyRecordRecycler.apply {
            layoutManager=FlexboxLayoutManager(requireActivity())
            adapter=historyRecordAdapter
        }
        //删除icon的点击事件
        fragmentHistoryAndHotBinding.deleteImage.setOnClickListener {
            //显示"确认删除"的对话框
            AlertDialog.Builder(requireActivity()).apply {
                setTitle("确定要删除全部历史记录吗？")
//                setMessage("这些可能很重要")
                setPositiveButton("确定"){_,_->
                    searchViewModel.deleteAllHistoryRecords()
                }
                setNegativeButton("取消"){_,_->
                }.show()
            }
        }
    }

}