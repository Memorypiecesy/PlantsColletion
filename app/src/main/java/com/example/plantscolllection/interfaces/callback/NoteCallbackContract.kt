package com.example.plantscolllection.interfaces.callback

import com.example.plantscolllection.bean.NoteVo

interface NoteCallbackContract {
    //NoteDetails添加笔记回调接口
    interface AddNoteCallback {
        fun addNoteSuccess(noteId: Int)
        fun addNoteFailed(errorMessage: String)
    }
    //MyNoteFragment中获得多个笔记回调接口
    interface NotesCallback {
        fun getNotesSuccess(noteVoList: List<NoteVo>)
        fun getNotesFailed(errorMessage: String)
    }
    //MyNoteFragment中删除笔记回调接口
    interface DeleteNotesCallback {
        fun deleteNotesSuccess(deleteSuccess: String)
        fun deleteNotesFailed(errorMessage: String)
    }
    //NoteDetails补充笔记回调接口
    interface SupplementNoteCallback {
        fun supplementNoteSuccess(success: String)
        fun supplementNoteFailed(errorMessage: String)
    }
    //NoteDetails中获得单个笔记回调接口
    interface NoteCallback {
        fun getNoteSuccess(noteVo: NoteVo)
        fun getNoteFailed(errorMessage: String)
    }
}