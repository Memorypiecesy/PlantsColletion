package com.example.plantscolllection.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GridView@JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    //边框四个顶点的坐标，顺时针转，左上->右上->右下->左下
    private var fourVertex = Array(4){FloatArray(2)}
    private val whitePaint = Paint()

    init {
        whitePaint.color= Color.WHITE
        whitePaint.strokeWidth=2F
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGird(canvas)
    }

    //View大小改变时回调函数
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //因为一开始四个顶点的矩形要露出来，所以要根据矩形的长和宽移动一下四个顶点的初始坐标
        //左上角点坐标
        fourVertex[0][0]=0F
        fourVertex[0][1]=0F
        //右上角点坐标
        fourVertex[1][0]=width*1.0F
        fourVertex[1][1]=0F
        //右下角点坐标
        fourVertex[2][0]=width*1.0F
        fourVertex[2][1]=height*1.0F
        //左下角点坐标
        fourVertex[3][0]=0F
        fourVertex[3][1]=height*1.0F

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
}