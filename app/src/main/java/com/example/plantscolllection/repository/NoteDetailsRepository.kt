package com.example.plantscolllection.repository

import com.example.plantscolllection.bean.BaseBean
import com.example.plantscolllection.bean.NewNote
import com.example.plantscolllection.bean.NoteVo
import com.example.plantscolllection.bean.PlantsDetails
import com.example.plantscolllection.client.RetrofitClient

class NoteDetailsRepository {

    suspend fun getPlantsDetails(id:Int): BaseBean<PlantsDetails> {
        return RetrofitClient.retrofitApi.getPlantsDetails(id)
    }

    suspend fun addNote(token:String,noteVo: NoteVo): BaseBean<Int> {
        return RetrofitClient.retrofitApi.addNote(token,noteVo)
    }
    //补充笔记
    suspend fun supplementNote(newNote: NewNote, noteId:Int): BaseBean<String> {
        return RetrofitClient.retrofitApi.supplementNote(newNote,noteId)
    }

    //获取单个笔记
    suspend fun getNote(token: String, noteId:Int): BaseBean<NoteVo>{
        return RetrofitClient.retrofitApi.getNote(token,noteId)
    }

}