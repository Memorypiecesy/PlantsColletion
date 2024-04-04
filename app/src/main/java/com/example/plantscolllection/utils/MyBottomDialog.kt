package com.example.plantscolllection.utils

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import com.google.android.material.bottomsheet.BottomSheetDialog

class MyBottomDialog@JvmOverloads constructor(
    context: Context, theme:Int=0
) : BottomSheetDialog(context,theme), GestureDetector.OnGestureListener {

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {

    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return true
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (distanceY < 0) {//下滑
            dismiss()
        }
        return false
    }

    override fun onLongPress(e: MotionEvent) {

    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }


}