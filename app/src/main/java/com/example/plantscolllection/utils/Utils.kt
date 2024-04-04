package com.example.plantscolllection.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.plantscolllection.R

object Utils {

    fun setStatusBar(appCompatActivity: AppCompatActivity, isLayoutExtend:Boolean){

        //拿到当前活动的DecorView
        val decorView = appCompatActivity.window.decorView //拿到当前活动的DecorView

        //第一个参数表示活动的布局会显示在状态栏上面，第二个参数表示当状态栏的背景为浅色系时，状态栏变为深色系
        //一定要用"|"来写两个值，如果分开用两行代码写，则后面设置的会覆盖前面的，不能实现同时一起的效果
        if (isLayoutExtend){
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }else{
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        appCompatActivity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //注意要清除 FLAG_TRANSLUCENT_STATUS flag
        appCompatActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        //setStatusBarColor()方法将状态栏设置为透明色
        appCompatActivity.window.statusBarColor = Color.TRANSPARENT
    }

    //第一个参数是上下文，第二个参数是Dialog的style资源的id，第三个参数是要放进Dialog的View的layoutId，第四个参数是提示的文字
    fun getNoticeDialog(context: Context, themeResId: Int, inflatedViewId: Int, noticeText:String): Dialog {
        val dialog = Dialog(context,themeResId)
        val view = LayoutInflater.from(context).inflate(inflatedViewId,null)
        view.findViewById<TextView>(R.id.notice_dialog_text).apply { text=noticeText } //修改TextView的文字
        dialog.setContentView(view)
        //设置对话框的大小
        view.minimumHeight = (ScreenSizeUtils.getInstance(context).screenHeight * 0.23f).toInt()
        val dialogWindow = dialog.window;
        val lp = dialogWindow?.attributes
        lp?.apply {
            width = (ScreenSizeUtils.getInstance(context).screenWidth * 0.5f).toInt()
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.CENTER;
        }
        dialogWindow?.attributes = lp
        return dialog
    }


}