package com.example.bubService.view.layout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.LinearLayout

/// BubbleLayout is a custom LinearLayout that is used to display the bubble view.
internal open class BubbleLayout(
    /// The context of the application.
    context: Context,
    /// The attribute set of the view.
    /// The attribute set is a collection of attributes that are passed to the view.
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    /// A function that is used to determine if the child event should be ignored.
    internal var ignoreChildEvent: (MotionEvent) -> Boolean = { false }

    /// A function that is used to handle the touch event.
    internal var doOnTouchEvent: (MotionEvent) -> Unit = {}

    /// A function that is used to dispatch the key event.
    private var onDispatchKeyEvent: ((KeyEvent) -> Boolean?)? = null

    /// A function that is used to dispatch the key event.
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (onDispatchKeyEvent != null && event != null) {
            return onDispatchKeyEvent!!(event) ?: super.dispatchKeyEvent(event)
        }
        return super.dispatchKeyEvent(event)
    }

    /// A function that is used to intercept the touch event.
    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        val b = event?.let{
            doOnTouchEvent(it)
            ignoreChildEvent(it)
        }
        return b ?: onInterceptTouchEvent(event)
    }

    /// A function that is used to handle the touch event.
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let(doOnTouchEvent)
        return super.onTouchEvent(event)
    }
}