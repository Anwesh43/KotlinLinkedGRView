package com.anwesh.uiprojects.linkedgrview

/**
 * Created by anweshmishra on 18/06/18.
 */

import android.content.Context
import android.graphics.*
import android.view.View
import android.view.MotionEvent

val GR_NODES : Int = 5

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

    data class State(var prevScale : Float = 0f, var dir : Float = 0f, var j : Int = 0) {

        val scales : Array<Float> = arrayOf(0f, 0f, 0f)

        fun update(stopcb : (Float) -> Unit) {
            scales[j] += 0.1f * dir
            if (Math.abs(scales[j] - prevScale) > 1) {
                scales[j] = prevScale + dir
                j += dir.toInt()
                if (j == scales.size || j == -1) {
                    j -= dir.toInt()
                    dir = 0f
                    prevScale = scales[j]
                    stopcb(prevScale)
                }
            }
        }

        fun startUpdating(startcb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                startcb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class GRNode(var i : Int, val state : State = State()) {

        private var next : GRNode? = null

        private var prev : GRNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < GR_NODES - 1) {
                next = GRNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            val w : Float = canvas.width.toFloat()
            val h : Float = canvas.height.toFloat()
            val gap : Float = w / GR_NODES
            val rIndex : Int = i % 2
            val gIndex : Int = (i + 1) % 2
            val getScale : (Int) -> Float = {index -> index + (1 - 2 * index) * state.scales[1]}
            val getColorPart : (Int) -> Int = {index -> (255 * getScale(index)).toInt()}
            paint.color = Color.rgb(getColorPart(rIndex), getColorPart(gIndex), 0)
            canvas.save()
            canvas.translate(gap * i + gap/10 + gap * state.scales[0], h/2)
            canvas.drawRoundRect(RectF(-gap/10, -gap/3, gap/10, gap/3), gap/10, gap/10, paint)
            canvas.restore()
        }

        fun update(stopcb : (Float) -> Unit) {
            state.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            state.startUpdating(startcb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : GRNode {
            var curr : GRNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LinkedGR(var i : Int) {

        private var curr : GRNode = GRNode(0)

        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(stopcb : (Float) -> Unit) {
            curr.update(stopcb)
        }

        fun startUpdating(startcb : () -> Unit) {
            curr.startUpdating(startcb)
        }
    }

    data class Renderer(var view : LinkedGRView) {

        private val animator : Animator = Animator(view)

        private val linkedGR : LinkedGR = LinkedGR(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            linkedGR.draw(canvas, paint)
            animator.animate {
                linkedGR.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            linkedGR.startUpdating {
                animator.start()
            }
        }
    }
}