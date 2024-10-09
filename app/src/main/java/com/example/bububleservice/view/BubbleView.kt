package com.example.bububleservice.view

import android.content.Context
import android.graphics.Point
import androidx.dynamicanimation.animation.SpringAnimation
import com.example.bububleservice.event.BubbleListener
import com.example.bububleservice.utils.sez
import com.example.bububleservice.view.layout.BubbleInitialization
import com.example.bububleservice.view.layout.BubbleLayout

///BubbleView is a class that extends BubbleInitialization
///It is used to create a view that is used to show the bubble
///It takes in a context, a containCompose, a forceDragging and a listener
///It has a root that is a BubbleLayout

///✨ BubbleView always display on the edge of the screen
class BubbleView(
    context: Context,
    containCompose: Boolean,
    private val forceDragging: Boolean = false,
    private val listener: BubbleListener? = null
) : BubbleInitialization(
    context = context,
    containCompose = containCompose,
    root = BubbleLayout(context)
) {
    ///✨ The following variables are used to store the point (position) of the bubble
    private val prevPoint = Point( 0 , 0)
    private val rawPointOnDown = Point( 0 , 0)
    private val newPoint = Point( 0 , 0)
    private val halfScreenWidth = sez.fullWidth / 2

    ///✨ The following variables are used to store the height and width of the bubble
    private val bubbleHeight = 0
    private val bubbleWidth = 0
    private var springAnimation: SpringAnimation? = null

    internal var mListener: BubbleListener? = null

    init {

    }

    fun snapToEdge() {
//        springAnimation?.

    }

    fun updateBubbleStatus(visible: Int) {
        root?.visibility = visible
    }
}