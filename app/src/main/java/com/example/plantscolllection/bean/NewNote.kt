package com.example.plantscolllection.bean

class NewNote {
    var id: Int? = null
    var title: String? = null
    var `val`: String? = null
    var photo: String? = null
    var noteId: Int? = null

    override fun toString(): String {
        return "Newnote(id=$id, title=$title, `val`=$`val`, photo=$photo, noteId=$noteId)"
    }


}