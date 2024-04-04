package com.example.plantscolllection.utils

import com.example.plantscolllection.bean.Classify
import com.example.plantscolllection.bean.NoteVo

object Data {

    val data = mutableListOf<String>("哈哈哈哈","afafafafa","afafafafgagag","极大几大","法发噶","啊打发啊","发嘎嘎爱国","啊法发噶",)

    //门
    private val doorList = mutableListOf(
        Classify("ZAO LEI ZHI WU MEN","藻类植物门"),
        Classify("DI YI ZHI WU MEN","地衣植物门"),
        Classify("TAI XIAN ZHI WU MEN","苔藓植物门"),
        Classify("JUE LEI ZHI WU MEN","蕨类植物门"),
        Classify("LUO ZI ZHI WU MEN","裸子植物门"),
        Classify("BEI ZI ZHI WU MEN","被子植物门"),
    )
    //纲
    private val classList = mutableListOf(
        Classify("TAI GANG","苔纲"),
        Classify("XIAN GANG","藓纲"),
        Classify("JIAO TAI GANG","角苔纲"),
        Classify("YIN XING GANG","银杏纲"),
        Classify("SONG BAI GANG","松柏纲"),
        Classify("SU TIE GANG","苏铁纲"),
        Classify("MAI MA TENG GANG","买麻藤纲"),
        Classify("HONG DOU SHAN GANG","红豆杉纲"),
        Classify("DAN ZI YE ZHI WU GANG","单子叶植物纲")
    )
    //目
    private val eyeList = mutableListOf(
        Classify("MA HUANG MU","麻黄目"),
        Classify("MAI MA TENG MU","买麻藤目"),

        )
    //科
    private val familyList = mutableListOf(
        Classify("MA HUANG KE","麻黄科"),
        Classify("MAI MA TENG KE","买麻藤科"),
    )
    //属
    private val categoryList = mutableListOf(
        Classify("MA HUANG SHU","麻黄目属"),
        Classify("MAI MA TENG SHU","买麻藤属")
    )
    //种
    private val seedList = mutableListOf(
        Classify("BI BAO MAI MA TENG","闭苞买麻藤"),
        Classify("HAI NAN MAI MA TENG","海南买麻藤"),
        Classify("LUO FU MAI MA TENG","罗浮买麻藤"),
        Classify("XIAO YE MAI MA TENG","小叶买麻藤"),
        Classify("XI BING MAI MA TENG","细柄买麻藤"),
        Classify("DA ZI MAI MA TENG","大子买麻藤"),
        Classify("CHUI ZI MAI MA TENG","垂子买麻藤"),
    )

    val noteVoList = mutableListOf(
        NoteVo().apply {
            title="笔记1"
            name="菊花"
        },
        NoteVo().apply {
            title="笔记2"
            name="西兰花"
        },
        NoteVo().apply {
            title="笔记3"
            name="西洋菜"
        },
        NoteVo().apply {
            title="笔记4"
            name="玫瑰"
        },
        NoteVo().apply {
            title="笔记5"
            name="蓝菊"
        },
        NoteVo().apply {
            title="笔记6"
            name="喇叭花"
        },
        NoteVo().apply {
            title="笔记7"
            name="红花"
        },
        NoteVo().apply {
            title="笔记8"
            name="蓝花"
        },
        NoteVo().apply {
            title="笔记9"
            name="紫花"
        },
        NoteVo().apply {
            title="笔记10"
            name="橙花"
        },
        )

    //根据当前第几个Fragment返回对应的ClassifyAdapter的数据
    fun getData(position:Int):MutableList<Classify>{
        return when(position){
            0-> doorList
            1-> classList
            2-> eyeList
            3-> familyList
            4-> categoryList
            else-> seedList
        }
    }

    //根据当前第几个Fragment返回对应的面包屑BreadAdapter的最后一项数据
    fun getTag(position: Int):String{
        return when(position){
            0-> "门类选择"
            1-> "纲类选择"
            2-> "目类选择"
            3-> "科类选择"
            4-> "属类选择"
            else-> "种类选择"
        }
    }


}