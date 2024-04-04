package com.example.plantscolllection.utils

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import kotlin.math.abs

class CropImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    companion object{
        const val TAG = "CropImageView"
    }

    private val whitePaint = Paint()
    //边框四个顶点的坐标，顺时针转，左上->右上->右下->左下
    private var fourVertex = Array(4){FloatArray(2)}
    //上一次手滑到的坐标
    private var lastX = 0F
    private var lastY = 0F
    //矩形的长和宽
    private val rectLength = 28F
    private val rectWidth = 8F
    //裁剪框所能达到的最小宽度和高度
    private val minWidth = 100F
    private val minHeight = 200F
    //四个顶点感应的范围
    private val touchArea = 20
    //如果手指按下的时候靠近四个顶点，记录按下的时候靠近的顶点的编号
    private var nearVertexNo = 0
    //手指移动的时候的模式：在框里面移动、在框外面移动、靠近四个顶点移动（调整裁剪框）
    private var MODE = 0
    private val MODE_INSIDE = 1
    private val MODE_OUTSIZE = 2
    private val MODE_NEARVERTEX = 3
    //遮罩颜色
    private val maskColor = Color.argb(100, 0, 0, 0)
    //清除遮罩清除的画笔
    private val eraser = Paint(Paint.ANTI_ALIAS_FLAG)
    //绘制完我们指定的src图片后的bitmap（带有第0层背景+第一层我们设置的图片）
    private lateinit var bitmap: Bitmap
    //控制遮罩层和裁剪框、网格线、矩形是否绘制的枚举类
    private var show = Background.SHOW


    init {
//        setLayerType(View.LAYER_TYPE_SOFTWARE,null)
        whitePaint.color=Color.WHITE
        whitePaint.strokeWidth=2F
        eraser.xfermode=PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    }

    override fun onDraw(canvas: Canvas) {
        //第一次绘制图片的时候获取对应的bitmap（如果bitmap变量没初始化才初始化）
        if (!::bitmap.isInitialized){
            //获取跟CropImageView大小相等的新的bitmap（可看成一张白布）
            bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
            //借助新的Canvas（可看成画板）把View画到上面新建的bitmap中
            val canvasTemp = Canvas(bitmap)
            super.onDraw(canvasTemp)
            Log.d(TAG, "onDraw-->bitmap初始化")
            Log.d(TAG, "onDraw处Bitmap的宽->${bitmap.width};Bitmap的高->${bitmap.height}")
        }
        super.onDraw(canvas)
        if (show==Background.SHOW){
            //新建layer和绘制图像
            val layerId = canvas.saveLayer(0F,0F,width*1.0F,height*1.0F,null)
            canvas.drawColor(maskColor) //画半透明黑色遮罩
            clearRect(canvas)
            //让新建layer与原来canvas进行Alpha混合
            canvas.restoreToCount(layerId)
            drawGird(canvas)
            drawBorder(canvas)
            drawRect(canvas)
        }
    }

    //清除裁剪框范围内的遮罩
    private fun clearRect(canvas: Canvas) {
        canvas.drawRect(fourVertex[0][0],fourVertex[0][1],fourVertex[2][0],fourVertex[2][1],eraser)
    }

    //画网格线
    private fun drawGird(canvas: Canvas){
        //竖1
        canvas.drawLine(fourVertex[0][0]+(fourVertex[1][0]-fourVertex[0][0])/3,fourVertex[0][1],fourVertex[0][0]+(fourVertex[1][0]-fourVertex[0][0])/3, fourVertex[3][1],whitePaint)
        //竖2
        canvas.drawLine(fourVertex[0][0]+(fourVertex[1][0]-fourVertex[0][0])*2/3,fourVertex[0][1],fourVertex[0][0]+(fourVertex[1][0]-fourVertex[0][0])*2/3, fourVertex[3][1],whitePaint)
        //横1
        canvas.drawLine(fourVertex[0][0],fourVertex[0][1]+(fourVertex[3][1]-fourVertex[0][1])/8,fourVertex[1][0], fourVertex[0][1]+(fourVertex[3][1]-fourVertex[0][1])/8,whitePaint)
        //横2
        canvas.drawLine(fourVertex[0][0],fourVertex[0][1]+(fourVertex[3][1]-fourVertex[0][1])/2,fourVertex[1][0], fourVertex[0][1]+(fourVertex[3][1]-fourVertex[0][1])/2,whitePaint)
        //横3
        canvas.drawLine(fourVertex[0][0],fourVertex[0][1]+(fourVertex[3][1]-fourVertex[0][1])*7/8,fourVertex[1][0], fourVertex[0][1]+(fourVertex[3][1]-fourVertex[0][1])*7/8,whitePaint)

    }

    //画边框
    private fun drawBorder(canvas: Canvas){
        //上边的边框：左上角->右上角
        canvas.drawLine(fourVertex[0][0],fourVertex[0][1],fourVertex[1][0],fourVertex[1][1],whitePaint)
        //右边的边框：右上角->右下角
        canvas.drawLine(fourVertex[1][0],fourVertex[1][1],fourVertex[2][0],fourVertex[2][1],whitePaint)
        //下边的边框：右下角->左下角
        canvas.drawLine(fourVertex[2][0],fourVertex[2][1],fourVertex[3][0],fourVertex[3][1],whitePaint)
        //左边的边框：左下角->左上角
        canvas.drawLine(fourVertex[3][0],fourVertex[3][1],fourVertex[0][0],fourVertex[0][1],whitePaint)
    }

    //画四个顶点的矩形
    private fun drawRect(canvas: Canvas){
        //左上
        canvas.drawRect(fourVertex[0][0]-rectWidth,fourVertex[0][1]-rectWidth,fourVertex[0][0],fourVertex[0][1]+(rectLength-rectWidth),whitePaint) //竖着的矩形
        canvas.drawRect(fourVertex[0][0]-rectWidth,fourVertex[0][1]-rectWidth,fourVertex[0][0]+(rectLength-rectWidth),fourVertex[0][1],whitePaint)  //横着的矩形
        //右上
        canvas.drawRect(fourVertex[1][0]+rectWidth,fourVertex[1][1]-rectWidth,fourVertex[1][0],fourVertex[1][1]+rectLength-rectWidth,whitePaint) //竖着的矩形
        canvas.drawRect(fourVertex[1][0]+rectWidth,fourVertex[1][1]-rectWidth,fourVertex[1][0]-(rectLength-rectWidth),fourVertex[1][1],whitePaint)  //横着的矩形
        //右下
        canvas.drawRect(fourVertex[2][0]+rectWidth,fourVertex[2][1]+rectWidth,fourVertex[2][0],fourVertex[2][1]-(rectLength-rectWidth),whitePaint) //竖着的矩形
        canvas.drawRect(fourVertex[2][0]+rectWidth,fourVertex[2][1]+rectWidth,fourVertex[2][0]-(rectLength-rectWidth),fourVertex[2][1],whitePaint)  //横着的矩形
//      //左下
        canvas.drawRect(fourVertex[3][0]-rectWidth,fourVertex[3][1]+rectWidth,fourVertex[3][0],fourVertex[3][1]-(rectLength-rectWidth),whitePaint) //竖着的矩形
        canvas.drawRect(fourVertex[3][0]-rectWidth,fourVertex[3][1]+rectWidth,fourVertex[3][0]+(rectLength-rectWidth),fourVertex[3][1],whitePaint)  //横着的矩形
    }

    //View大小改变时回调函数
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setVertexDefaultPosition()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            //手指按下的时候记录按下的坐标，根据坐标更新模式
            MotionEvent.ACTION_DOWN->{
                lastX=event.x
                lastY=event.y
                updateMode()
//                Log.d(TAG, "按下了，坐标->(${event.x},${event.y})")
            }
            MotionEvent.ACTION_MOVE->{
                //本次移动的坐标和上次记录的坐标相减求增量
                val increaseX = event.x-lastX
                val increaseY = event.y-lastY
//                Log.d(TAG, "移动中，坐标-->(${event.x},${event.y})")
                when(MODE){
                    //在里面移动，即整体移动框
                    MODE_INSIDE->{
                        //让四个顶点坐标加上该增量
                        updateFourVertex(increaseX,increaseY)
                        postInvalidate()
                        lastX=event.x
                        lastY=event.y
                    }
                    //靠近四个顶点移动，就缩放裁剪框
                    MODE_NEARVERTEX->{
                        //让对应顶点加上增量
                        zoom(increaseX,increaseY)
                        postInvalidate()
                        lastX=event.x
                        lastY=event.y
                    }

                }
            }
        }
//        Log.d(TAG, "模式类型->${MODE}")
        return true
    }


    //根据手指滑动后的X、Y坐标和上一次的X、Y坐标来更新四个点的坐标
    private fun updateFourVertex(increaseX:Float, increaseY:Float) {
        //先让四个顶点加增量
        for (i in 0..3){
            fourVertex[i][0]+=increaseX
            fourVertex[i][1]+=increaseY
        }
        //看看有没有顶点坐标越界，有一个越界，就回滚到之前没加增量前的状态
        for (i in 0..3){
            if(!isVertexCoordinateIllegal(fourVertex[i][0],fourVertex[i][1])){
                for (j in 0..3){
                    fourVertex[j][0]-=increaseX
                    fourVertex[j][1]-=increaseY
                }
                return
            }
        }
    }
    //判断顶点坐标是否合法（没越界）
    private fun isVertexCoordinateIllegal(x:Float,y:Float):Boolean{
//        return x>=0&&x<=width&&y>=0&&y<=height
        return if(x>=0&&x<=width&&y>=0&&y<=height){
            true
        }else{
//            Log.d(TAG, "isVertexCoordinateIllegal->坐标不合法")
            false
        }
    }

    //判断裁剪框是否已经达到允许达到的最小面积，要根据移动不同顶点拉伸裁剪框作单独处理
    private fun isReachMinArea(vertexNo:Int):Boolean{
        when(vertexNo){
            0->{//左上：靠(右上X-左上X)和(左下Y-左上Y)
                return if((fourVertex[1][0]-fourVertex[0][0]<minWidth)||(fourVertex[3][1]-fourVertex[0][1]<minHeight)){
//                    Log.d(TAG, "isReachMinArea-->达到最小面积了")
                    true
                }else{
                    false
                }
            }
            1->{//右上：靠(右上X-左上X)和(右下Y-右上Y)
                return if((fourVertex[1][0]-fourVertex[0][0]<minWidth)||(fourVertex[2][1]-fourVertex[1][1]<minHeight)){
//                    Log.d(TAG, "isReachMinArea-->达到最小面积了")
                    true
                }else{
                    false
                }
            }
            2->{//右下：靠(右下X-左下X)和(右下Y-右上Y)
                return if((fourVertex[2][0]-fourVertex[3][0]<minWidth)||(fourVertex[2][1]-fourVertex[1][1]<minHeight)){
//                    Log.d(TAG, "isReachMinArea-->达到最小面积了")
                    true
                }else{
                    false
                }
            }
            3->{//左下：靠(右下X-左下X)和(左下Y-左上Y)
                return if((fourVertex[2][0]-fourVertex[3][0]<minWidth)||(fourVertex[3][1]-fourVertex[0][1]<minHeight)){
//                    Log.d(TAG, "isReachMinArea-->达到最小面积了")
                    true
                }else{
                    false
                }
            }
        }
        return false
    }

    //根据手指按下时候的X、Y坐标更新模式
    private fun updateMode(){
        MODE = if (nearVertex()<4){
            MODE_NEARVERTEX
        }else if(lastX>=fourVertex[0][0]&&lastX<=fourVertex[1][0]&&lastY>=fourVertex[0][1]&&lastY<=fourVertex[3][1]){
            MODE_INSIDE
        }else{
            MODE_OUTSIZE
        }
    }
    //手指按下的时候是否靠近四个顶点，如果靠近返回靠近的顶点的编号
    private fun nearVertex(): Int {
        if ( abs(lastX - fourVertex[0][0]) <= touchArea && abs(lastY - fourVertex[0][1]) <= touchArea ) {//左上顶点
            nearVertexNo = 0
            return 0
        }
        if ( abs(lastX - fourVertex[1][0]) <= touchArea && abs(lastY - fourVertex[1][1]) <= touchArea ) {//右上顶点
            nearVertexNo = 1
            return 1
        }
        if ( abs(lastX - fourVertex[2][0]) <= touchArea && abs(lastY - fourVertex[2][1]) <= touchArea ) {//右下顶点
            nearVertexNo = 2
            return 2
        }
        if ( abs(lastX - fourVertex[3][0]) <= touchArea && abs(lastY - fourVertex[3][1]) <= touchArea ) {//左下顶点
            nearVertexNo = 3
            return 3
        }
        nearVertexNo = 100
        return 100
    }

    //点击顶点附近时缩放裁剪框的逻辑
    private fun zoom(increaseX:Float, increaseY:Float) {
        //先判断哪个顶点附近
        when(nearVertexNo){
            //如果缩放后的坐标合法，才正常加对应增量；如果不合法，将已经加了增量的变量退回原来状态，return
            0->{//左上顶点动：左上顶点X、Y都加对应增量；右上顶点Y加对应增量；左下顶点X加对应增量
                fourVertex[0][0]+=increaseX
                fourVertex[0][1]+=increaseY
                if (!isVertexCoordinateIllegal(fourVertex[0][0],fourVertex[0][1])||isReachMinArea(0)){
                    fourVertex[0][0]-=increaseX
                    fourVertex[0][1]-=increaseY
                    return
                }
                fourVertex[1][1]+=increaseY
                fourVertex[3][0]+=increaseX
            }
            1->{//右上顶点动：右上顶点X、Y都加对应增量；右下顶点X加对应增量；左上顶点Y加对应增量
                fourVertex[1][0]+=increaseX
                fourVertex[1][1]+=increaseY
                if (!isVertexCoordinateIllegal(fourVertex[1][0],fourVertex[1][1])||isReachMinArea(1)){
                    fourVertex[1][0]-=increaseX
                    fourVertex[1][1]-=increaseY
                    return
                }
                fourVertex[2][0]+=increaseX
                fourVertex[0][1]+=increaseY
            }
            2->{//右下顶点动：右下顶点X、Y都加对应增量；左下顶点Y加对应增量；右上顶点X加对应增量
                fourVertex[2][0]+=increaseX
                fourVertex[2][1]+=increaseY
                if (!isVertexCoordinateIllegal(fourVertex[2][0],fourVertex[2][1])||isReachMinArea(2)){
                    fourVertex[2][0]-=increaseX
                    fourVertex[2][1]-=increaseY
                    return
                }
                fourVertex[3][1]+=increaseY
                fourVertex[1][0]+=increaseX
            }
            3->{//左下顶点动：左下顶点X、Y都加对应增量；左上顶点X加对应增量；右下顶点Y加对应增量
                fourVertex[3][0]+=increaseX
                fourVertex[3][1]+=increaseY
                if (!isVertexCoordinateIllegal(fourVertex[3][0],fourVertex[3][1])||isReachMinArea(3)){
                    fourVertex[3][0]-=increaseX
                    fourVertex[3][1]-=increaseY
                    return
                }
                fourVertex[0][0]+=increaseX
                fourVertex[2][1]+=increaseY
            }
        }
    }

    //返回裁剪框范围内对应的Bitmap
    fun cropBitmap():Bitmap{
        //根据裁剪框的大小裁剪第一次初始化时得到的bitmap，然后返回
        return Bitmap.createBitmap(bitmap,fourVertex[0][0].toInt(),fourVertex[0][1].toInt(),(fourVertex[1][0]-fourVertex[0][0]).toInt(),(fourVertex[3][1]-fourVertex[0][1]).toInt())
    }

    //显示遮罩层和裁剪框、网格线、矩形等
    fun showBackground(){
        show=Background.SHOW
        postInvalidate()
    }

    //隐藏遮罩层和裁剪框、网格线、矩形等
    fun hideBackground(){
        show=Background.NOT_SHOW
        postInvalidate()
    }

    //控制遮罩层和裁剪框、网格线、矩形是否绘制的枚举类
    enum class Background{
        SHOW,
        NOT_SHOW
    }

    //将四个顶点恢复默认位置的方法
    fun setVertexDefaultPosition(){
        //因为一开始四个顶点的矩形要露出来，所以要根据矩形的长和宽移动一下四个顶点的初始坐标
        //左上角点坐标
        fourVertex[0][0]=0F+rectWidth
        fourVertex[0][1]=0F+rectWidth
        //右上角点坐标
        fourVertex[1][0]=width*1.0F-rectWidth
        fourVertex[1][1]=0F+rectWidth
        //右下角点坐标
        fourVertex[2][0]=width*1.0F-rectWidth
        fourVertex[2][1]=height*1.0F-rectWidth
        //左下角点坐标
        fourVertex[3][0]=0F+rectWidth
        fourVertex[3][1]=height*1.0F-rectWidth
    }

}