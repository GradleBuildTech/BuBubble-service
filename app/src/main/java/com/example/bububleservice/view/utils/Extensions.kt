package com.example.bububleservice.view.utils

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.provider.Settings
import android.view.ViewTreeObserver

////💥 Remove navigation action

private var exclusionRect: MutableList<Rect> = ArrayList()

/// getXYOnScreen returns the x and y coordinates of the view on the screen
internal fun View.getXYOnScreen(): Pair<Int, Int> {
    val arr = IntArray(2)
    this.getLocationOnScreen(arr)
    return Pair(arr[0], arr[1])
}

///  camDrawOverlays checks if the app has permission to draw overlays
internal fun Context.canDrawOverlays(): Boolean {
    return Settings.canDrawOverlays(this)
}

//💥 Remove navigation action
/// updateGestureExclusion updates the gesture exclusion rect for the view
internal fun View.updateGestureExclusion() {
//    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return
//    val screenSize= sez.fullSize
//
//    exclusionRect.clear()
//    val rect = Rect(0, 0, this.width, screenSize.height)
//    exclusionRect.add(rect)
//
//    this.systemGestureExclusionRects = exclusionRect
}

/// afterMeasured calls the afterMeasuredWork after the view is measured
inline fun View.afterMeasured(crossinline afterMeasuredWork: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            /// measuredWidth is the width of the view
            if (measuredWidth > 0 && measuredHeight > 0) {
                /// removeOnGlobalLayoutListener removes the listener from the view
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                afterMeasuredWork()
            }
        }
    })
}