package com.example.bububleservice.view

import android.content.Context
import com.example.bububleservice.event.BubbleListener
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
}