package com.example.bububleservice.view

import android.content.Context
import android.view.View
import com.example.bububleservice.utils.applyExpandBubbleViewLayoutParams
import com.example.bububleservice.view.layout.BubbleInitialization
import com.example.bububleservice.view.layout.BubbleLayout

///ExpandBubbleView is a class that extends BubbleInitialization
///It is used to create a view that is used to expand the bubble
///It takes in a context and a containCompose
///It has a root that is a BubbleLayout

///✨ ExpandBubbleView always display on the edge of the screen
class ExpandBubbleView(
    context: Context,
    containCompose: Boolean = false,
): BubbleInitialization(
    context = context,
    containCompose = containCompose,
    root = BubbleLayout(context)
) {
    init {
        layoutParams?.applyExpandBubbleViewLayoutParams()
    }

}