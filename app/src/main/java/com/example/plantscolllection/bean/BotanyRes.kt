package com.example.plantscolllection.bean

class BotanyRes {
    val name: String? = null
    val latinName: String? = null
    val photo: String? = null
    val score: String? = null
    val botanyId: Int? = null

    override fun toString(): String {
        return "BotanyRes{name:${name},latinName:${latinName},photo:${photo},sim:${score},botanyId:${botanyId}}"
    }
}