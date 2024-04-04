package com.example.plantscolllection.utils

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
import android.widget.OverScroller
import com.example.plantscolllection.R


class SlideMenuLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val STATE_CLOSED = 0 //关闭状态
    private val STATE_OPEN = 1 //打开状态
    private val STATE_MOVING_LEFT = 2 //左滑将要打开状态
    private val STATE_MOVING_RIGHT = 3 //右滑将要关闭状态
    private var currentState = 0 //记录状态的变量
    private var downX = 0F //按下时候的X坐标
    private var downY = 0F //按下时候的Y坐标
    private var lastX = 0F //滑动时候上一次的X坐标
    private var lastY = 0F //按下时候上一次的Y坐标
    private var mRightId = -1 //右边隐藏菜单的id
    private var menuWidth = 0 //右边隐藏菜单的总长度
    private var mScaledTouchSlop = 0 //滑动距离
    private lateinit var rightMenuView:View //右边隐藏菜单对象
    private var mScroller: OverScroller
    var isSwipeable = true //是否能滑动的变量

    companion object {
        const val TAG = "SlideMenuLayout"
    }
    //获取右边菜单id
    init {
        mScroller=OverScroller(context)
        val configuration = ViewConfiguration.get(context)
        mScaledTouchSlop=configuration.scaledTouchSlop
        //获取右边菜单id
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideMenuLayout)
        mRightId = typedArray.getResourceId(R.styleable.SlideMenuLayout_right_id, -1)
        typedArray.recycle() //回收资源
    }
    //这一段代码是让右边隐藏菜单有内容的代码（应该是测量起了作用）
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec,heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        //如果右边菜单id不为-1，说明存在，就得到右边菜单的对象
        if (mRightId != -1) {
            rightMenuView = findViewById(mRightId);
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        menuWidth=rightMenuView.measuredWidth
        super.onLayout(changed, l, t, r, b)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isSwipeable) return super.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                lastX = event.x
                lastY = event.y
                Log.d(TAG, "ACTION_DOWN");
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = (event.x - lastX).toInt() //x偏移量
                val dy = (event.x - lastY).toInt() //y偏移量
                //如果Y轴偏移量大于X轴偏移量 不再滑动
//                if (abs(dy)> abs(dx)) return false
                if (dx > 0){//父View向右滑动，子View向左滑动
                    Log.d(TAG, "向右滑动")
                    currentState=STATE_MOVING_RIGHT //更新状态
                    //左边缘检测：如果scrollX>0，说明View在一开始的位置往右动了，需要回到原点
                    if (scrollX<0){
                        return false
                    }
                }else{//View向左滑动，内容向右滑动
                    currentState=STATE_MOVING_LEFT //更新状态
                    //右边缘检测，如果scrollX>=menuWidth，父View不能再往右滑了
                    if(scrollX>=menuWidth){
                        return false
                    }
                }
                //往反方向滑，这样跟手指滑的方向跟子View滑动的方向一致
                scrollBy(-dx, 0)
                Log.d(TAG, "ACTION_MOVE")
                Log.d(TAG, "偏移量-->${dx}")
                Log.d(TAG, "已经滑动了的X距离-->${scrollX}")
                //记录上一次滑动的x、y坐标，下一次滑动的时候要做比较
                lastX = event.x
                lastY = event.y
            }
            MotionEvent.ACTION_UP->{
                Log.d(TAG, "ACTION_UP")
                //手指抬起的时候判断状态
                if (currentState==STATE_MOVING_LEFT){//左滑状态
                    //左滑打开
                    mScroller.startScroll(scrollX, 0, menuWidth - scrollX, 0, 300)
                    invalidate()
                }else if(currentState==STATE_MOVING_RIGHT||currentState==STATE_OPEN){
                    //右滑关闭
                    smoothToCloseMenu()
                }
            }
        }
        Log.d(TAG, "currentState的值-->${currentState}")
        return super.onTouchEvent(event)
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()){
            scrollTo(mScroller.currX,mScroller.currY)
            postInvalidate()
        }
        if (isMenuOpen()){
            currentState=STATE_OPEN
        }else if(isMenuClosed()){
            currentState=STATE_CLOSED
        }
    }
    //判断menu是否打开
    private fun isMenuOpen():Boolean{
        return scrollX>=menuWidth
    }
    //判断menu是否关闭
    private fun isMenuClosed():Boolean{
        return scrollX<=0
    }
    //关闭菜单
    private fun smoothToCloseMenu(){
        mScroller.startScroll(scrollX,0,-scrollX,0,300)
        invalidate()
    }
    //暴露给外部的滑动方法，决定露出左边的布局与否
    fun swipe(startX:Int,startY:Int,dx:Int,dy:Int,duration:Int){
        mScroller.startScroll(startX,startY,dx,dy,duration)
        invalidate()
    }

}