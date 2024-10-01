package com.example.bububleservice.view

import android.content.Context
import android.widget.LinearLayout
import com.example.bububleservice.view.layout.BubbleInitialization

///CLoseBubbleView is a class that extends BubbleInitialization
///It is used to create a view that is used to close the bubble
///It takes in a context and a distanceToClose
///It has a root that is a LinearLayout

///✨ CloseBubbleView always display in the bottom of the screen
/// This is used to close the bubble
class CloseBubbleView(
    context: Context,
    private val distanceToClose: Int = 100
) : BubbleInitialization(
    context = context,
    root = LinearLayout(context)
) {
}