package com.hyperbyte.playlogtest.utils

import android.graphics.PointF
import android.view.View

class MathHelper {

    companion object {
        fun getCoordinates(view: View, angle: Double): PointF {
            val center = getCenterOfView(view)
            val radius = getRadiusOfView(view)

            return PointF(
                (center.x + radius * Math.cos(Math.toRadians(angle))).toFloat(),
                (center.y + radius * Math.sin(Math.toRadians(angle))).toFloat()
            )
        }

        fun getAngle(center: PointF, point: PointF): Double {
            val angle = -Math.atan2((point.x - center.x).toDouble(), (point.y - center.y).toDouble()) * 180 / Math.PI
            return angle + 180
        }

        fun getCenterOfView(view: View): PointF {
            val centerX = view.width.toFloat() / 2 + view.x
            val centerY = view.height.toFloat() / 2 + view.y
            return PointF(centerX, centerY)
        }

        fun getRadiusOfView(view: View): Float {
            return view.width.toFloat() / 2
        }
    }

}