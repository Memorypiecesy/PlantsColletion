package com.example.plantscolllection.repository

import com.example.plantscolllection.bean.BaseBean
import com.example.plantscolllection.bean.NoteVo
import com.example.plantscolllection.bean.RequestParm
import com.example.plantscolllection.client.RetrofitClient

class MyNoteFragmentRepository {

    suspend fun getNotes(token:String,pageNo:Int,pageSize:Int): BaseBean<List<NoteVo>> {
        return RetrofitClient.retrofitApi.getNotes(token,pageNo,pageSize)
    }

    //搜索笔记
    suspend fun searchNotes(requestParm: RequestParm):BaseBean<List<NoteVo>>{
        return RetrofitClient.retrofitApi.searchNotes(requestParm)
    }

    //删除笔记
    suspend fun deleteNotes(token:String, noteVoidList: List<Int>):BaseBean<String>{
        return RetrofitClient.retrofitApi.deleteNotes(token,noteVoidList)
    }


}