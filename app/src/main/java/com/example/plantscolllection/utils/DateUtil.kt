package com.example.plantscolllection.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    //HomePage的Note上方显示的时间
    fun homePageNoteAboveDateShow(oldDate: Long, newDate: Long): String {
//        Log.d("NewNoteFragment", "oldDate->${oldDate}")
//        Log.d("NewNoteFragment", "newDate->${newDate}")
        //现在所处年份
        val currentYear = SimpleDateFormat("yyyy").format(Date(newDate)).toInt()
        //当前年份1月1日00:00:00时刻代表的Long值
        val currentYearTimeInLong = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("${currentYear}-01-01 00:00:00").time
        //当天00:00:00时刻所代表的字符串
        val currentDayInString = "${SimpleDateFormat("yyyy-MM-dd").format(newDate)} 00:00:00"
        //当天00:00:00时刻所代表的Long值
        val currentDayInLong = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentDayInString).time
        //昨天00:00:00时刻所代表的Long值
        val theDayBeforeYesterdayTimeInLong = currentDayInLong-86400000
        return if (oldDate < currentYearTimeInLong) { //旧的日期已是一年前或是更久，返回格式为"yyyy年MM月dd日"
            dateFormat(SimpleDateFormat("yyyy年MM月dd日").format(Date(oldDate)))
        }else if(oldDate < theDayBeforeYesterdayTimeInLong){ //旧的日期若是一年内，返回格式为"MM月dd日 HH:mm"
            "${dateFormat(SimpleDateFormat("MM月dd日").format(Date(oldDate)))} ${SimpleDateFormat("HH:mm").format(Date(oldDate))}"
        }else if(oldDate < currentDayInLong){ //旧的日期若是昨天，返回格式为"昨天 HH:mm"
            "昨天 ${SimpleDateFormat("HH:mm").format(Date(oldDate))}"
        }else{ //旧的日期若是今天，返回格式为"今天 HH:mm"
            "今天 ${SimpleDateFormat("HH:mm").format(Date(oldDate))}"
        }
    }

    //Note显示的时间
    fun noteDateShow(oldDate: Long, newDate: Long): String {
//        Log.d("NewNoteFragment", "oldDate->${oldDate}")
//        Log.d("NewNoteFragment", "newDate->${newDate}")
        //现在所处年份
        val currentYear = SimpleDateFormat("yyyy").format(Date(newDate)).toInt()
        //当前年份1月1日00:00:00时刻代表的Long值
        val currentYearTimeInLong = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("${currentYear}-01-01 00:00:00").time
        //当天00:00:00时刻所代表的字符串
        val currentDayInString = "${SimpleDateFormat("yyyy-MM-dd").format(newDate)} 00:00:00"
        //当天00:00:00时刻所代表的Long值
        val currentDayInLong = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(currentDayInString).time
        //昨天00:00:00时刻所代表的Long值
        val theDayBeforeYesterdayTimeInLong = currentDayInLong-86400000
        return if (oldDate < currentYearTimeInLong) { //旧的日期已是一年前或是更久，返回格式为"yyyy年MM月dd日"
            dateFormat(SimpleDateFormat("yyyy年MM月dd日").format(Date(oldDate)))
        }else if(oldDate < theDayBeforeYesterdayTimeInLong){ //旧的日期若是一年内，返回格式为"MM月dd日"
            dateFormat(SimpleDateFormat("MM月dd日").format(Date(oldDate)))
        }else if(oldDate < currentDayInLong){ //旧的日期若是昨天，返回格式为"昨天 HH:mm"
            "昨天 ${SimpleDateFormat("HH:mm").format(Date(oldDate))}"
        }else{ //旧的日期若是今天，返回格式为"今天 HH:mm"
            "今天 ${SimpleDateFormat("HH:mm").format(Date(oldDate))}"
        }
    }

    private fun dateFormat(dateToBeFormatted: String): String {
        val stringList = dateToBeFormatted.split('年','月','日')
//        会多出一个空字符串
//        Log.d("date", "大小-->${stringList.size}")
//        stringList.forEach { string ->
//            Log.d("date", string)
//        }
        return if (stringList.size==3){
            "${stringList[0].toInt()}月${stringList[1].toInt()}日"
        }else{
            "${stringList[0]}年${stringList[1].toInt()}月${stringList[2].toInt()}日"
        }
    }





}