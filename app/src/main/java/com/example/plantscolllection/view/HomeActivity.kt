package com.example.plantscolllection.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.plantscolllection.R
import com.example.plantscolllection.databinding.ActivityHomeBinding
import com.example.plantscolllection.utils.ScreenSizeUtils
import com.example.plantscolllection.utils.Utils
import com.example.plantscolllection.viewmodel.HomeActivityViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var activityHomeBinding: ActivityHomeBinding
    private lateinit var nearByPageFragmentContainer: NavHostFragment
    private val homeActivityViewModel by viewModels<HomeActivityViewModel>()
    private val deletingNoteDialog by lazy { Utils.getNoticeDialog(this,R.style.NoticeDialogStyle,R.layout.notice_dialog,"删除笔记中") }
    private val deleteNotesWarnDialog by lazy { getDeleteNotesWarnDialog(this,R.style.DeleteNoteDialog,R.layout.delete_note_warn) }
    private val deleteSingleNoteWarnDialog by lazy { getDeleteSingleNoteWarnDialog(this,R.style.DeleteNoteDialog,R.layout.delete_note_warn) }

    companion object{
        const val TAG = "HomeActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityHomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        activityHomeBinding.lifecycleOwner = this
        activityHomeBinding.bottomNavigation.itemIconTintList=null
        nearByPageFragmentContainer = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        activityHomeBinding.bottomNavigation.setupWithNavController(nearByPageFragmentContainer.navController)
        //底部RelativeLayout动画显示/消失
        homeActivityViewModel.isBottomRelativeShowedLiveData.observe(this){
            if (it) {
                showBottomRelative()
                //禁用BottomNavigation的item的点击事件
                activityHomeBinding.bottomNavigation.menu.getItem(0).isEnabled = false
                activityHomeBinding.bottomNavigation.menu.getItem(1).isEnabled = false
                activityHomeBinding.bottomNavigation.menu.getItem(2).isEnabled = false
                activityHomeBinding.bottomNavigation.menu.getItem(3).isEnabled = false
            } else {
                hideBottomRelative()
                //启用BottomNavigation的item的点击事件
                activityHomeBinding.bottomNavigation.menu.getItem(0).isEnabled = true
                activityHomeBinding.bottomNavigation.menu.getItem(1).isEnabled = true
                activityHomeBinding.bottomNavigation.menu.getItem(2).isEnabled = true
                activityHomeBinding.bottomNavigation.menu.getItem(3).isEnabled = true
            }
        }
        //删除按钮状态变化
        homeActivityViewModel.isDeleteButtonEnabled.observe(this){
            if (it){
                activityHomeBinding.deleteButton.apply {
                    isEnabled=true
                    setBackgroundResource(R.drawable.delete_button_round_corner_red)
                    setTextColor(Color.rgb(255,255,255))
                }
            }else{
                activityHomeBinding.deleteButton.apply {
                    isEnabled=false
                    setBackgroundResource(R.drawable.delete_button_round_corner_grey)
                    setTextColor(Color.rgb(158,158,158))
                }
            }
        }
        //全选圆圈状态改变
        homeActivityViewModel.isAllSelectedImageSelectedForActivity.observe(this){
            activityHomeBinding.allSelectedImage.isSelected = it
            Log.d(TAG, "全选按钮处变量的值-->${it}")
        }
        //隐藏"删除笔记中"弹窗的LiveData观察
        homeActivityViewModel.hideDeletingNoteDialog.observe(this){
            if (it) deletingNoteDialog.dismiss()
        }
        //"删除单个笔记警告"弹窗的LiveData观察
        homeActivityViewModel.showDeleteSingleNoteWarnDialog.observe(this){
            if (it) deleteSingleNoteWarnDialog.show()
        }
        initView()
        Utils.setStatusBar(this,true)
    }

    private fun initView(){
        //禁用中间item的点击事件
        activityHomeBinding.bottomNavigation.menu.getItem(2).isEnabled = false
        activityHomeBinding.cameraImage.setOnClickListener {
            val intent = Intent(this,CameraActivity::class.java)
            intent.putExtra("page",0)   //0代表从拍照图标进入拍照页
            startActivity(intent)
        }
        //全选圆圈点击事件
        activityHomeBinding.allSelectedImage.setOnClickListener {
            //如果全选圆圈被选中，设置为未选中，通过LiveData更新Fragment中item全选；否则相反
            if (activityHomeBinding.allSelectedImage.isSelected){
                activityHomeBinding.allSelectedImage.isSelected=false
                homeActivityViewModel.isAllSelectedImageSelectedForFragment.value=false
                homeActivityViewModel.isDeleteButtonEnabled.value=false //删除按钮变灰
            }else{
                activityHomeBinding.allSelectedImage.isSelected=true
                homeActivityViewModel.isAllSelectedImageSelectedForFragment.value=true
                homeActivityViewModel.isDeleteButtonEnabled.value=true //删除按钮变红
            }
        }
        //删除按钮点击事件
        activityHomeBinding.deleteButton.setOnClickListener {
            if (activityHomeBinding.deleteButton.isEnabled){//如果能被点击，弹出弹窗警告用户是否要删除
                deleteNotesWarnDialog.show()
            }
        }
    }
    //键盘某个键按下的回调
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.keyCode== KeyEvent.KEYCODE_ENTER && event.action != KeyEvent.ACTION_UP){
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            //如果软键盘显示着的话
            if (inputMethodManager.isActive) {
                //收起当前界面的键盘
                inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            }
            //如果当前Navigation在NearByFragment
            if (nearByPageFragmentContainer.childFragmentManager.primaryNavigationFragment is NearByFragment){
                val nayByFragment = nearByPageFragmentContainer.childFragmentManager.primaryNavigationFragment as NearByFragment
                nayByFragment.onKeyEnter()
            }
            //如果当前Navigation在MyNoteFragment
            if (nearByPageFragmentContainer.childFragmentManager.primaryNavigationFragment is MyNoteFragment){
                val myNoteFragment = nearByPageFragmentContainer.childFragmentManager.primaryNavigationFragment as MyNoteFragment
                myNoteFragment.onKeyEnter()
            }
        }
        return super.dispatchKeyEvent(event)
    }

    //弹出删除多个笔记警告弹窗
    private fun getDeleteNotesWarnDialog(context: Context, themeResId: Int, inflatedViewId: Int):Dialog{
        val dialog = Dialog(context,themeResId)
        val view = LayoutInflater.from(context).inflate(inflatedViewId,null)
        view.findViewById<TextView>(R.id.think_again_text).setOnClickListener { //"我再想想"TextView点击事件
            dialog.dismiss()
        }
        view.findViewById<TextView>(R.id.confirm_text).setOnClickListener { //"确认"TextView点击事件
            dialog.dismiss() //警告弹窗消失
            deletingNoteDialog.show() //删除笔记中弹窗出现
            homeActivityViewModel.isDeleteNotes.value=true //更新LiveData的值，通知Fragment删除多个item
        }
        dialog.setContentView(view)

        //设置对话框的大小
        view.minimumHeight = (ScreenSizeUtils.getInstance(context).screenHeight * 0.23f).toInt()
        val dialogWindow = dialog.window
        val lp = dialogWindow?.attributes
        lp?.apply {
            width = 762
            height = 480
            gravity = Gravity.CENTER
        }
        dialogWindow?.attributes = lp
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    //得到删除单个笔记警告弹窗
    private fun getDeleteSingleNoteWarnDialog(context: Context, themeResId: Int, inflatedViewId: Int):Dialog{
        val dialog = Dialog(context,themeResId)
        val view = LayoutInflater.from(context).inflate(inflatedViewId,null)
        view.findViewById<TextView>(R.id.think_again_text).setOnClickListener { //"我再想想"TextView点击事件
            dialog.dismiss()
        }
        view.findViewById<TextView>(R.id.confirm_text).setOnClickListener { //"确认"TextView点击事件
            dialog.dismiss() //警告弹窗消失
            deletingNoteDialog.show() //删除笔记中弹窗出现
            homeActivityViewModel.isDeleteNotes.value=false //更新LiveData的值，通知Fragment删除单个item
        }
        dialog.setContentView(view)

        //设置对话框的大小
        view.minimumHeight = (ScreenSizeUtils.getInstance(context).screenHeight * 0.23f).toInt()
        val dialogWindow = dialog.window
        val lp = dialogWindow?.attributes
        lp?.apply {
            width = 762
            height = 480
            gravity = Gravity.CENTER
        }
        dialogWindow?.attributes = lp
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    //显示底部布局
    private fun showBottomRelative() {
        activityHomeBinding.bottomRelative.visibility= View.VISIBLE

        //创建平移动画对象
        val startTranslateAnim = TranslateAnimation(0F, 0F, activityHomeBinding.bottomRelative.height*1.0F, 0F)
        startTranslateAnim.duration = 500

        //底部布局开始动画
        activityHomeBinding.bottomRelative.startAnimation(startTranslateAnim);

    }

    //隐藏底部布局
    private fun hideBottomRelative() {

        //让"已取消收藏"图标显示
        activityHomeBinding.bottomRelative.visibility= View.VISIBLE

        //创建平移动画对象
        val endTranslateAnim = TranslateAnimation(0F, 0F, 0F,activityHomeBinding.bottomRelative.height*1.0F )
        endTranslateAnim.duration = 500

        //底部布局开始动画
        activityHomeBinding.bottomRelative.startAnimation(endTranslateAnim)
        //给动画设置监听
        endTranslateAnim.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                //动画结束时隐藏布局
                activityHomeBinding.bottomRelative.visibility= View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })

    }
}