package com.example.plantscolllection.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.plantscolllection.bean.PlantsInfo
import com.example.plantscolllection.bean.RequestParm
import com.example.plantscolllection.entity.HistoryRecord
import com.example.plantscolllection.interfaces.callback.BotanySearchCallback
import com.example.plantscolllection.interfaces.callback.HotBotanyCallback
import com.example.plantscolllection.interfaces.callback.PlantsInfoCallback
import com.example.plantscolllection.interfaces.callback.TextExpandCallback
import com.example.plantscolllection.repository.SearchRepository
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val searchRepository by lazy { SearchRepository(application.applicationContext) }
    private lateinit var historyRecordLiveData: LiveData<List<HistoryRecord>>
    val historyRecordAndHotBotanyForEditText by lazy { MutableLiveData<String>() }
    val isTextExpandRecyclerShowed by lazy { MutableLiveData<Boolean>() }

    companion object{
        const val TAG = "SearchViewModel"
    }

    init {
        viewModelScope.launch {
            //如果数据库返回的结果为null，就更新LiveData内容为空的List；否则更新为返回结果
            historyRecordLiveData = searchRepository.getAllHistoryRecords()
            Log.d(TAG, "historyRecordLiveData的值-->${historyRecordLiveData.value}")
        }
    }

    //获得热门搜索列表
    fun getHotBotany(hotBotanyCallback: HotBotanyCallback) {
        viewModelScope.launch {
            val baseBean = searchRepository.getHotBotany()
            if (baseBean.code == 200) {
                baseBean.data?.let { hotBotanyCallback.getHotBotanySuccess(it) }
            } else {
                baseBean.message.let { hotBotanyCallback.getHotBotanyFailed(it) }
            }
        }
    }

    //获得文字扩展列表
    fun getTextExpandList(searchText: String, textExpandCallback: TextExpandCallback) {
        viewModelScope.launch {
            val baseBean = searchRepository.getTextExpandList(searchText)
            if (baseBean.code == 200) {
                baseBean.data?.let { textExpandCallback.getTextExpandListSuccess(it) }
            } else {
                baseBean.message.let { textExpandCallback.getTextExpandListFailed(it) }
            }
        }
    }

    //插入HistoryRecord
    fun insertHistoryRecord(historyRecord: HistoryRecord) {
        viewModelScope.launch {
            //先根据内容查找数据库，看有没有记录，有的话更新该HistoryRecord的createTime；没的话创建一个新的HistoryRecord对象插入
            historyRecord.content.let {content->
                val result = searchRepository.getHistoryRecordByContent(content)
                if (result==null){
                    searchRepository.insertHistoryRecord(historyRecord)
                }else{
                    result.createTime=System.currentTimeMillis()
                    searchRepository.updateHistoryRecord(result)
                }
            }
        }
    }

    //更新HistoryRecord
    suspend fun updateHistoryRecord(historyRecord: HistoryRecord) {
        viewModelScope.launch {
            searchRepository.insertHistoryRecord(historyRecord)
        }
    }

    //删除所有HistoryRecord
    fun deleteAllHistoryRecords() {
        viewModelScope.launch {
            searchRepository.deleteAllHistoryRecords()
        }
    }

    //根据content查询对应的HistoryRecord
    suspend fun getHistoryRecordByContent(contentForSelect: String) {
        viewModelScope.launch {
            val historyRecord = searchRepository.getHistoryRecordByContent(contentForSelect)
            Log.d(TAG, "SearchViewModel处getHistoryRecordByContent方法返回的数据->${historyRecord}")
        }
    }

    //查询所有的HistoryRecord
    fun getAllHistoryRecords(): LiveData<List<HistoryRecord>>{
        return historyRecordLiveData
    }

    //搜索植物（搜索页）
    fun searchPlants(requestParm: RequestParm,botanySearchCallback: BotanySearchCallback){
        viewModelScope.launch {
            val baseBean = searchRepository.searchPlants(requestParm)
            if(baseBean.code==200){
                baseBean.data?.let { botanySearchCallback.getBotanySearchSuccess(it) }
            }else{
                baseBean.message.let { botanySearchCallback.getBotanySearchFailed(it) }
            }
        }
    }

}