package com.example.plantscolllection.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantscolllection.R
import com.example.plantscolllection.adapter.BotanySearchAdapter
import com.example.plantscolllection.bean.Botanysearch
import com.example.plantscolllection.bean.RequestParm
import com.example.plantscolllection.databinding.FragmentSearchResultBinding
import com.example.plantscolllection.interfaces.callback.BotanySearchCallback
import com.example.plantscolllection.viewmodel.SearchViewModel

class SearchResultFragment : Fragment() {

    private lateinit var fragmentSearchResultBinding: FragmentSearchResultBinding
    private lateinit var searchViewModel:SearchViewModel
    private val mBotanySearchAdapter by lazy { BotanySearchAdapter(requireActivity()) }
    private var pageNoThis = 0
    private var pageSizeThis = 10

    private val botanySearchCallback by lazy {
        object : BotanySearchCallback {

            override fun getBotanySearchSuccess(botanySearchList: List<Botanysearch>) {
                if (botanySearchList.isEmpty()){
                    fragmentSearchResultBinding.apply {
                        searchResultRecycler.visibility=View.INVISIBLE //隐藏搜索结果RecyclerView
                        findNoPicture.visibility=View.VISIBLE //显示"搜索不到植物的图片"
                    }
                }else{
                    fragmentSearchResultBinding.apply {
                        findNoPicture.visibility=View.INVISIBLE //隐藏"搜索不到植物的图片"
                        fragmentSearchResultBinding.searchResultRecycler.visibility=View.VISIBLE //显示搜索结果RecyclerView
                    }
                    mBotanySearchAdapter.setBotanySearchList(botanySearchList)
                }
                Log.d(TAG, "SearchResultFragment处获取BotanySearch成功->${botanySearchList}")
            }

            override fun getBotanySearchFailed(errorMessage: String) {
                Log.d(TAG, "SearchResultFragment处获取BotanySearch失败->${errorMessage}")
            }


        }
    }

    companion object{
        private const val TAG = "SearchResultFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "SearchResultFragment的onCreate方法被调用...")
        searchViewModel=ViewModelProvider(requireActivity())[SearchViewModel::class.java]
        searchViewModel.isTextExpandRecyclerShowed.value=false //让文字扩展RecyclerView消失
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "SearchResultFragment的onCreateView方法被调用...")
        fragmentSearchResultBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_result, container, false)
        return fragmentSearchResultBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //配置搜索结果RecyclerView
        fragmentSearchResultBinding.searchResultRecycler.apply {
            layoutManager= LinearLayoutManager(requireActivity())
            adapter=mBotanySearchAdapter
        }
        //获得SearchActivity发过来的搜索文字，进行搜索请求
        arguments?.let {bundle->
            bundle.getString("search_string")?.let {searchStringThis->
                getSearchResult(searchStringThis)
            }
        }
        Log.d(TAG, "SearchResultFragment的onViewCreated方法被调用...")
    }
    //查询搜索结果的方法
    private fun getSearchResult(searchStringThis:String){
        val requestParm = RequestParm()
        requestParm.apply {
            pageSize=pageSizeThis
            pageNo=pageNoThis
            searchString=searchStringThis
        }
        searchViewModel.searchPlants(requestParm,botanySearchCallback)
    }

    override fun onResume() {
        Log.d(TAG, "SearchResultFragment的onResume方法被调用...")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "SearchResultFragment的onPause方法被调用...")
        super.onPause()
    }

    override fun onStop() {
        Log.d(TAG, "SearchResultFragment的onStop方法被调用...")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "SearchResultFragment的onDestroy方法被调用...")
        super.onDestroy()
    }
}