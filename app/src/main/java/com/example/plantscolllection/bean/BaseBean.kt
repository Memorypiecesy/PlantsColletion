package com.example.plantscolllection.bean

class BaseBean<T> {
    var code = 0
    lateinit var message: String
    var data: T?=null
    var ok = false
    override fun toString(): String {
        return "BaseBean(code=$code, message='$message', data=$data, ok=$ok)"
    }
}