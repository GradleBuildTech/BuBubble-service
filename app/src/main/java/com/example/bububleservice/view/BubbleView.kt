package com.example.bububleservice.view

import android.content.Context
import com.example.bububleservice.event.BubbleListener
import com.example.bububleservice.view.layout.BubbleInitialization
import com.example.bububleservice.view.layout.BubbleLayout

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