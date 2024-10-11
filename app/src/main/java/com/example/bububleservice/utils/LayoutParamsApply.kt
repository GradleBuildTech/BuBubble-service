package com.example.bububleservice.utils

import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.view.Gravity
import android.view.WindowManager

///This function is used to apply the layout parameters to the close bubble view
fun WindowManager.LayoutParams.applyCloseBubbleViewLayoutParams() {
    apply {
        ///Set the flags
        /// * FLAG_NOT_FOCUSABLE: This window won't ever get key input focus, so the user can not send key or other button events to it.
        /// * FLAG_LAYOUT_NO_LIMITS: Window flag: special flag to let windows be displayed on top of the status bar.
        /// * FLAG_WATCH_OUTSIDE_TOUCH: Window flag: as long as this window is visible to the user, keep the device's screen turned on and bright.
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH

        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT

        ///Set the gravity
        /// * Gravity.TOP: Push object to the top of its container, not changing its size.
        /// * Gravity.START: Push object to the beginning of its container, not changing its size.
        gravity = Gravity.TOP or Gravity.START
        format = PixelFormat.TRANSLUCENT

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            type =  WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    }
}

///This function is used to apply the layout parameters to the bubble view
fun WindowManager.LayoutParams.applyBubbleViewLayoutParams(startPoint: Point) {
    apply {
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT

        gravity = Gravity.TOP or Gravity.START
        format = PixelFormat.TRANSLUCENT

        x = startPoint.x
        y = startPoint.y

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    }
}

///This function is used to apply the layout parameters to the expand bubble view
fun WindowManager.LayoutParams.applyExpandBubbleViewLayoutParams() {
    apply {
        flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS

        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.WRAP_CONTENT

        gravity = Gravity.TOP or Gravity.START
        format = PixelFormat.TRANSLUCENT

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    }
}

///This function is used to apply the layout parameters to the flow keyboard bubble view
fun WindowManager.LayoutParams.applyFlowKeyboardBubbleViewLayoutParams() {
    apply {
        flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND

        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.WRAP_CONTENT

        gravity = Gravity.TOP or Gravity.START
        format = PixelFormat.TRANSLUCENT
        dimAmount = 0.5f       // default = 0.5f

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    }
}

