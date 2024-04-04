package com.example.plantscolllection.bean

class User {
    var id: Int = 0
    var name: String? = null
    var photo: String? = null
    var password: String? = null
    var self: String? = null
    var phone: String? = null
    var noteVoCount: Int = 0
    var botanyCount: Int = 0
    override fun toString(): String {
        return "User(id=$id, name=$name, photo=$photo, password=$password, self=$self, phone=$phone, noteVoCount=$noteVoCount, botanyCount=$botanyCount)"
    }


}