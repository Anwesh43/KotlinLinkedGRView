package com.anwesh.uiprojects.linkedgrview

/**
 * Created by anweshmishra on 18/06/18.
 */

import android.content.Context
import android.graphics.*
import android.view.View
import android.view.MotionEvent

class LinkedGRView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}