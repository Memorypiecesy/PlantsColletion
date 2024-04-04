package com.example.plantscolllection.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.plantscolllection.entity.HistoryRecord

@Dao
interface HistoryRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) //设置冲突：如果相同就替换策略
    suspend fun insertHistoryRecord(historyRecord: HistoryRecord) //插入HistoryRecord

    @Delete
    suspend fun deleteHistoryRecords(historyRecord: HistoryRecord) //删除HistoryRecord

    @Query("update historyrecord set createTime = :createTimeForUpdate where content = :contentForUpdate")
    suspend fun updateHistoryRecord(createTimeForUpdate:Long, contentForUpdate:String) //更新HistoryRecord

    @Query("delete from historyrecord")
    suspend fun deleteAllHistoryRecords() //删除所有HistoryRecord

    @Query("select * from historyrecord where content = :contentForSelect")
    suspend fun getHistoryRecordByContent(contentForSelect:String):HistoryRecord //根据content查询对应的HistoryRecord

    @Query("select * from historyrecord order by createTime desc")
    fun getAllHistoryRecords():LiveData<List<HistoryRecord>> //查询所有HistoryRecord

}