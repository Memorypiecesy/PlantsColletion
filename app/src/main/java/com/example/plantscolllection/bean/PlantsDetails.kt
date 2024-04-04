package com.example.plantscolllection.bean

class PlantsDetails {

    var name:String?=null   //中文名
    var id:Int=0
    var species:Int=0   //种类
    var latinName:String?=null  //拉丁名
    var alias:String?=null  //别名
    var door:String?=null   //门
    var family:String?=null //科
    var className:String?=null  //纲
    var category:String?=null   //属
    var eye:String?=null    //目
    var seed:String?=null   //种
    var growingEnv:String?=null //生长环境
    var disRange:String?=null   //分布范围
    var chiefValue:String?=null //主要价值
    var morChar:String?=null    //形态特征
    var photo:List<String> = listOf()
    var viewCount:Int=0
    var likeCount:Int=0
    var searchCount:Int=0
    override fun toString(): String {
        return "PlantsDetails(name=$name, id=$id, species=$species, latinName=$latinName, alias=$alias, door=$door, family=$family, className=$className, category=$category, eye=$eye, seed=$seed, growingEnv=$growingEnv, disRange=$disRange, chiefValue=$chiefValue, morChar=$morChar, photo=$photo, viewCount=$viewCount, likeCount=$likeCount, searchCount=$searchCount)"
    }


}