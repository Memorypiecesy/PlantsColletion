package com.example.plantscolllection.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.plantscolllection.dao.HistoryRecordDao
import com.example.plantscolllection.entity.HistoryRecord

@Database(entities = [HistoryRecord::class], version = 2, exportSchema = false)
abstract class HistoryRecordDatabase:RoomDatabase() {
    abstract fun getHistoryRecordDao():HistoryRecordDao  //Dao 对象

    companion object{
        private var instance:HistoryRecordDatabase?=null

        fun getInstance(context:Context):HistoryRecordDatabase{
            //对象锁
            return instance?: synchronized(this) {
                //上下文                          数据库名字
                Room.databaseBuilder(context,HistoryRecordDatabase::class.java,"history_record_database")
                    .build().also { instance=it }
            }
        }
    }
}