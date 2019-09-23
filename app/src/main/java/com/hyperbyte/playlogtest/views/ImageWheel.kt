package com.hyperbyte.playlogtest.views

import android.content.Context
import android.graphics.PointF
import android.os.Handler
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import com.hyperbyte.playlogtest.R
import com.hyperbyte.playlogtest.utils.MathHelper
import kotlinx.android.synthetic.main.image_wheel.view.*

import java.util.Random

class ImageWheel(context: Context, attributeSet: AttributeSet) : RelativeLayout(context, attributeSet),
    View.OnTouchListener {

    companion object {
        const val CLICK_LOCK_TIME_MS = 300
    }

    private var mListener: OnImageWheelEventListener? = null

    private var mImageWheelButton: Button? = null
    private var mImageWheel: ImageView? = null

    private var mPressDownTime: Long = 0
    private var mPreviousAngle = 0
    private var mNewAngle = 0

    interface OnImageWheelEventListener {
        fun onTap()
        fun onPositionChanged(angle: Int)
    }

    fun setImageWheelEventListener(eventListener: OnImageWheelEventListener) {
        this.mListener = eventListener
    }

    init {
        View.inflate(context, R.layout.image_wheel, this)
        this.mImageWheel = image_wheel_image
        this.mImageWheelButton = image_wheel_button
        this.mListener = null
        mImageWheelButton!!.setOnTouchListener(this)
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            // Use timestamps to prevent accidental touches to ensure smoother animation/dragging
            MotionEvent.ACTION_DOWN -> mPressDownTime = System.currentTimeMillis()

            MotionEvent.ACTION_UP -> if (System.currentTimeMillis() - mPressDownTime < CLICK_LOCK_TIME_MS) {
                wheelButtonTapped(view)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (System.currentTimeMillis() - mPressDownTime >= CLICK_LOCK_TIME_MS) {
                    moveToPosition(
                        view,
                        MathHelper.getAngle(MathHelper.getCenterOfView(mImageWheel!!), PointF(event.rawX, event.rawY))
                    )
                }
            }

            else -> return false
        }

        return true
    }

    private fun wheelButtonTapped(view: View) {
        val rand = Random()
        val newAngle = rand.nextInt(360)

        mNewAngle = mPreviousAngle

        // animate the button moving around the wheel
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (newAngle != mPreviousAngle) {
                    mNewAngle += 1
                    moveToPosition(view, mNewAngle.toDouble())
                    handler.postDelayed(this, 20)
                }
            }
        }
        handler.post(runnable)

        mListener?.onTap()
    }

    private fun moveToPosition(view: View, angle: Double) {
        val coordinates = MathHelper.getCoordinates(mImageWheel!!, angle - 90)
        val newAngle = angle.toInt() % 360

        view.y = coordinates.y - view.height / 2
        view.x = coordinates.x - view.width / 2

        mImageWheelButton?.text = newAngle.toString()
        mPreviousAngle = newAngle

        mListener?.onPositionChanged(newAngle)
    }
}