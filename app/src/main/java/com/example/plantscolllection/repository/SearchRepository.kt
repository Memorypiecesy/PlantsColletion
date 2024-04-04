package com.example.plantscolllection.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.plantscolllection.bean.*
import com.example.plantscolllection.client.RetrofitClient
import com.example.plantscolllection.dao.HistoryRecordDao
import com.example.plantscolllection.database.HistoryRecordDatabase
import com.example.plantscolllection.entity.HistoryRecord

class SearchRepository(context: Context) {

    private val historyRecordDatabase:HistoryRecordDatabase
    private val historyRecordDao :HistoryRecordDao

    companion object{
        const val TAG = "SearchRepository"
    }

    init {
        historyRecordDatabase=HistoryRecordDatabase.getInstance(context)
        historyRecordDao=historyRecordDatabase.getHistoryRecordDao()
    }

    suspend fun getHotBotany():BaseBean<List<HotBotany>>{
        return RetrofitClient.retrofitApi.getHotBotany()
    }

    suspend fun getTextExpandList(searchText:String):BaseBean<List<String>>{
        return RetrofitClient.retrofitApi.getTextExpandList(searchText)
    }
    //搜索植物（搜索页）
    suspend fun searchPlants(requestParm: RequestParm):BaseBean<List<Botanysearch>>{
        return RetrofitClient.retrofitApi.searchPlants(requestParm)
    }

    //插入HistoryRecord
    suspend fun insertHistoryRecord(historyRecord: HistoryRecord) {
        historyRecordDao.insertHistoryRecord(historyRecord)
    }
    //更新HistoryRecord
    suspend fun updateHistoryRecord(historyRecord: HistoryRecord) {
        historyRecordDao.updateHistoryRecord(historyRecord.createTime,historyRecord.content)
        Log.d(TAG, "SearchRepository处updateHistoryRecord被调用，HistoryRecord对象-->${historyRecord}")
    }
    //删除所有HistoryRecord
    suspend fun deleteAllHistoryRecords() {
        historyRecordDao.deleteAllHistoryRecords()
    }
    //根据content查询对应的HistoryRecord
    suspend fun getHistoryRecordByContent(contentForSelect:String):HistoryRecord {
        return historyRecordDao.getHistoryRecordByContent(contentForSelect)
    }
    //查询所有的HistoryRecord
    fun getAllHistoryRecords(): LiveData<List<HistoryRecord>>{
        return historyRecordDao.getAllHistoryRecords()
    }

}