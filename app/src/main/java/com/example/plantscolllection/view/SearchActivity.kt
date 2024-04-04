package com.example.plantscolllection.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantscolllection.R
import com.example.plantscolllection.adapter.HistoryRecordAdapter
import com.example.plantscolllection.adapter.HotBotanyAdapter
import com.example.plantscolllection.adapter.TextExpandAdapter
import com.example.plantscolllection.bean.HotBotany
import com.example.plantscolllection.databinding.ActivitySearchBinding
import com.example.plantscolllection.entity.HistoryRecord
import com.example.plantscolllection.interfaces.callback.HotBotanyCallback
import com.example.plantscolllection.interfaces.callback.TextExpandCallback
import com.example.plantscolllection.interfaces.callback.TextExpandItemOnClickCallback
import com.example.plantscolllection.utils.Data
import com.example.plantscolllection.utils.Utils
import com.example.plantscolllection.viewmodel.SearchViewModel
import com.google.android.flexbox.FlexboxLayoutManager
import kotlin.math.log

class SearchActivity : AppCompatActivity() {

    private lateinit var activitySearchBinding: ActivitySearchBinding
    private lateinit var searchPageFragmentContainer: NavHostFragment
    private lateinit var searchPageNavController: NavController
    private val textExpandAdapter by lazy { TextExpandAdapter(textExpandItemOnClickCallback) }
    private lateinit var searchViewModel:SearchViewModel

    private val textExpandCallback by lazy {
        object : TextExpandCallback {
            override fun getTextExpandListSuccess(textExpandList: List<String>) {
                textExpandAdapter.setTextExpandList(textExpandList,activitySearchBinding.searchPageEdit.text.toString()) //设置数据
                activitySearchBinding.textExpandRecycler.visibility=View.VISIBLE //让文字扩展RecyclerView显示
                Log.d(TAG, "SearchActivity处获取textExpandList集合成功->${textExpandList}")
            }

            override fun getTextExpandListFailed(errorMessage: String) {
                Log.d(TAG, "SearchActivity处获取textExpandList集合失败->${errorMessage}")
            }

        }
    }

    private val textExpandItemOnClickCallback by lazy {
        object : TextExpandItemOnClickCallback {
            override fun textExpandItemClick(searchString: String) {
                //如果点击文字扩展item的时候不在SearchResultFragment，就跳到SearchResultFragment，发送请求
                if (searchPageFragmentContainer.childFragmentManager.primaryNavigationFragment is SearchResultFragment){

                }else{
                    val bundle = Bundle()
                    bundle.putString("search_string",searchString)
                    searchPageNavController.navigate(R.id.action_historyAndHotFragment_to_searchResultFragment,bundle)
                }
                //让文字扩展RecyclerView消失
                activitySearchBinding.textExpandRecycler.visibility=View.INVISIBLE
            }
        }
    }

    companion object{
        private const val TAG = "SearchActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBar(this,false)
        activitySearchBinding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        activitySearchBinding.lifecycleOwner=this
        searchViewModel= ViewModelProvider(this)[SearchViewModel::class.java]   //初始化SearchViewModel
        searchViewModel.historyRecordAndHotBotanyForEditText.observe(this){ //观察装有HistoryRecord item点击后传给EditText的String的LiveData
            activitySearchBinding.searchPageEdit.setText(it)
        }
        searchViewModel.isTextExpandRecyclerShowed.observe(this){ //Fragment中点击item进行搜索，让文字扩展RecyclerView消失的LiveData
            if(it==false){
                activitySearchBinding.textExpandRecycler.visibility=View.INVISIBLE
            }
            Log.d(TAG, "searchViewModel处的isTextExpandRecyclerShowed值发生改变-->${it}")
        }
        //初始化NavHostFragment对象，为了后面导航
        searchPageFragmentContainer = supportFragmentManager.findFragmentById(R.id.search_page_fragment_container) as NavHostFragment
        searchPageNavController=searchPageFragmentContainer.navController
        initView()
        Log.d(TAG, "SearchActivity处的SearchViewModel->${searchViewModel}")
    }

    private fun initView() {
        //配置文字扩展RecyclerView
        activitySearchBinding.textExpandRecycler.apply {
            layoutManager=LinearLayoutManager(this@SearchActivity)
            adapter=textExpandAdapter
        }
        //清除图标
        activitySearchBinding.searchPageImageClear.setOnClickListener {
            activitySearchBinding.searchPageEdit.setText("")
        }
        //"取消"文字
        activitySearchBinding.searchPageCancelText.setOnClickListener {
            finish()
        }
        //搜索框文字变化事件
        activitySearchBinding.searchPageEdit.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //搜索框中文字为空，隐藏清除图标，隐藏文字扩展RecyclerView，回到HistoryAndHotFragment；否则显示
                if (TextUtils.isEmpty(activitySearchBinding.searchPageEdit.text)){
                    activitySearchBinding.searchPageImageClear.visibility= View.INVISIBLE
                    activitySearchBinding.textExpandRecycler.visibility=View.INVISIBLE //让文字扩展RecyclerView消失
//                    textExpandAdapter.setTextExpandList(mutableListOf(),activitySearchBinding.searchPageEdit.text.toString())
                    //当文字为空的时候，如果当前Navigation活跃的是SearchResultFragment，就回到HistoryAndHotFragment
                    if(searchPageFragmentContainer.childFragmentManager.primaryNavigationFragment is SearchResultFragment){
                        searchPageNavController.popBackStack()
                    }

                }else{
                    activitySearchBinding.searchPageImageClear.visibility= View.VISIBLE
                    searchViewModel.getTextExpandList(activitySearchBinding.searchPageEdit.text.toString(),textExpandCallback) //搜索文字扩展
                    activitySearchBinding.textExpandRecycler.visibility=View.VISIBLE //让文字扩展RecyclerView显示
//                    textExpandAdapter.setTextExpandList(Data.data,activitySearchBinding.searchPageEdit.text.toString())
                    Log.d(TAG, "onTextChanged处isTextExpandRecyclerShowed的值->${searchViewModel.isTextExpandRecyclerShowed.value}")
                    if (searchViewModel.isTextExpandRecyclerShowed.value==false){
                        activitySearchBinding.textExpandRecycler.visibility=View.INVISIBLE //让文字扩展RecyclerView消失
                        Log.d(TAG, "onTextChanged处textExpandRecycler消失了")
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    //键盘某个键按下的回调
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.keyCode==KeyEvent.KEYCODE_ENTER && event.action != KeyEvent.ACTION_UP){
            Log.d(TAG, "dispatchKeyEvent处ENTER被按下...")
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            //如果软键盘显示着的话
            if (inputMethodManager.isActive) {
                //收起当前界面的键盘
                inputMethodManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
            }
            //添加历史记录
            val historyRecord = HistoryRecord()
            historyRecord.content=activitySearchBinding.searchPageEdit.text.toString()
            historyRecord.createTime=System.currentTimeMillis()
            searchViewModel.insertHistoryRecord(historyRecord)
            //按下Enter键时如果当前Navigation在SearchResultFragment，就继续发送搜索请求
            //如果当前Navigation在HistoryAndHotFragment，就导航到SearchResultFragment，再发送搜索请求
            if (searchPageFragmentContainer.childFragmentManager.primaryNavigationFragment is SearchResultFragment){
//                searchViewModel.searchPlants(activitySearchBinding.searchPageEdit.text.toString(),searchResultFragment.plantsInfoCallback)
//                searchResultFragment.getSearchResult(activitySearchBinding.searchPageEdit.text.toString())
            }else{
                val bundle = Bundle()
                bundle.putString("search_string",activitySearchBinding.searchPageEdit.text.toString())
                searchPageNavController.navigate(R.id.action_historyAndHotFragment_to_searchResultFragment,bundle)
            }
            //让文字扩展RecyclerView消失
            activitySearchBinding.textExpandRecycler.visibility=View.INVISIBLE
        }
        return super.dispatchKeyEvent(event)
    }

    //恢复的时候就要请求历史记录列表
    override fun onResume() {
        super.onResume()
    }
}