package com.example.bubService.view

import android.content.Context
import com.example.bubService.utils.applyFlowKeyboardBubbleViewLayoutParams
import com.example.bubService.utils.sez
import com.example.bubService.view.layout.BubbleInitialization
import com.example.bubService.view.layout.BubbleLayout

///FlowKeyboardBubbleView is a class that extends BubbleInitialization
///It is used to create a view that is used to show the keyboard
///It takes in a context and a containCompose
///It has a root that is a BubbleLayout

///✨ FlowKeyboardBubbleView always display above the keyboard
class FlowKeyboardBubbleView(
    context: Context,
    containCompose: Boolean = false,
): BubbleInitialization(
    context = context,
    containCompose = containCompose,
    root = BubbleLayout(context)
) {
    init {
        layoutParams?.applyFlowKeyboardBubbleViewLayoutParams()
        layoutParams?.apply {
            this.x = 0
            this.y = sez.fullHeight - (root?.height ?: 0)
        }
        update()
    }


}

