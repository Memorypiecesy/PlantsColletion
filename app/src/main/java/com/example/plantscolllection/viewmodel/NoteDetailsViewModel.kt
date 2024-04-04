package com.example.plantscolllection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantscolllection.bean.NewNote
import com.example.plantscolllection.bean.NoteVo
import com.example.plantscolllection.interfaces.callback.NoteCallbackContract
import com.example.plantscolllection.interfaces.callback.PlantsDetailsCallback
import com.example.plantscolllection.repository.NoteDetailsRepository
import kotlinx.coroutines.launch

class NoteDetailsViewModel : ViewModel() {

    private val noteDetailsRepository by lazy { NoteDetailsRepository() }

    companion object {
        private const val TAG = "PlantsDetailsViewModel"
    }

    fun getPlantsDetails(id: Int, plantsDetailsCallback: PlantsDetailsCallback) {
        viewModelScope.launch {
            val baseBean = noteDetailsRepository.getPlantsDetails(id)
            if (baseBean.code == 200) {
                baseBean.data?.let { plantsDetailsCallback.getPlantsDetailsSuccess(it) }
            } else {
                baseBean.message.let { plantsDetailsCallback.getPlantsDetailsFailed(it) }
            }
        }
    }

    fun addNote(token: String, noteVo: NoteVo, addNoteCallback: NoteCallbackContract.AddNoteCallback) {
        viewModelScope.launch {
            val baseBean = noteDetailsRepository.addNote(token, noteVo)
            if (baseBean.code == 200) {
                baseBean.data?.let { addNoteCallback.addNoteSuccess(it) }
            } else {
                baseBean.message.let { addNoteCallback.addNoteFailed(it) }
            }
        }
    }

    //补充笔记
    fun supplementNote(newNote: NewNote, noteId: Int,supplementNoteCallback: NoteCallbackContract.SupplementNoteCallback){
        viewModelScope.launch {
            val baseBean = noteDetailsRepository.supplementNote(newNote, noteId)
            if (baseBean.code == 200) {
                baseBean.data?.let { supplementNoteCallback.supplementNoteSuccess(it) }
            } else {
                baseBean.message.let { supplementNoteCallback.supplementNoteFailed(it) }
            }
        }
    }

    //获取单个笔记
    fun getNote(token: String, noteId:Int,getNoteCallback:NoteCallbackContract.NoteCallback){
        viewModelScope.launch {
            val baseBean = noteDetailsRepository.getNote(token, noteId)
            if (baseBean.code == 200) {
                baseBean.data?.let { getNoteCallback.getNoteSuccess(it) }
            } else {
                baseBean.message.let { getNoteCallback.getNoteFailed(it) }
            }
        }
    }


}