package com.example.plantscolllection.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class HistoryRecord {

    @PrimaryKey(autoGenerate = false)   //创建时间作为主键，不自动生成
    var createTime:Long = 0
    var content: String = ""

    override fun toString(): String {
        return "HistoryRecord(createTime=$createTime, content=$content)"
    }

}