package com.example.plantscolllection.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantscolllection.bean.NoteVo
import com.example.plantscolllection.bean.RequestParm
import com.example.plantscolllection.interfaces.callback.NoteCallbackContract
import com.example.plantscolllection.repository.MyNoteFragmentRepository
import kotlinx.coroutines.launch

class MyNoteFragmentViewModel:ViewModel() {

    private val myNoteFragmentRepository by lazy { MyNoteFragmentRepository() }

    companion object{
        const val TAG = "MyNoteFragmentViewModel"
    }

    fun getNotes(token:String,pageNo:Int,pageSize:Int,getNotesCallback: NoteCallbackContract.NotesCallback){
        viewModelScope.launch {
            val baseBean = myNoteFragmentRepository.getNotes(token,pageNo,pageSize)
            if(baseBean.code==200){
                baseBean.data?.let { getNotesCallback.getNotesSuccess(it) }
            }else{
                baseBean.message.let { getNotesCallback.getNotesFailed(it) }
            }
        }
    }

    //搜索笔记
    fun searchPlants(requestParm: RequestParm, getNotesCallback: NoteCallbackContract.NotesCallback){
        viewModelScope.launch {
            val baseBean = myNoteFragmentRepository.searchNotes(requestParm)
            if(baseBean.code==200){
                baseBean.data?.let { getNotesCallback.getNotesSuccess(it) }
            }else{
                baseBean.message.let { getNotesCallback.getNotesFailed(it) }
            }
        }
    }

    //删除笔记
    fun deleteNotes(token:String, noteVoidList: List<Int>, deleteNotesCallback: NoteCallbackContract.DeleteNotesCallback){
        viewModelScope.launch {
            val baseBean = myNoteFragmentRepository.deleteNotes(token,noteVoidList)
            Log.d(TAG, "MyNoteFragmentViewModel处的deleteNotes--${baseBean}")
            if(baseBean.code==200){
                baseBean.data?.let { deleteNotesCallback.deleteNotesSuccess(it) }
            }else{
                baseBean.message.let { deleteNotesCallback.deleteNotesFailed(it) }
            }
        }
    }

}