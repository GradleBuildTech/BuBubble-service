package com.example.bubService.service

import android.content.Context
import android.graphics.Point
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import com.example.bubService.event.BubbleListener


/**
 * BuBubbleBuilder is a class that takes in a context
 * It is used to build the bubble
 * It is used to build the bubble
 * Example:
 * ```kotlin
 * val bubble = BuBubbleBuilder(context).bubbleViewCompose {
 *      BubbleViewContent()
 *      }
 *      .closeBubbleViewCompose { CloseBubbleViewContent()}
 *      ```
 */
class BuBubbleBuilder(
    private val context: Context
) {
    ///✨ Config data of bubbleView
    internal var bubbleView: View? = null
    internal var bubbleComposeView: ComposeView? = null

    ///📝  Position display of bubble
    internal var startPoint = Point(0, 0)

    ///📝  Animation of bubble
    internal var isAnimateToEdgeEnabled = true

    ///📝  Config user behavior of bubble
    internal var listener: BubbleListener? = null

    ///📝  Config dragging of bubble
    internal var forceDragging: Boolean = true

    ///📝  Config draggable of bubble, default is true, if false, the bubble will not be draggable
    internal var isBubbleDraggable: Boolean = true

    ///✨ Config data of closeBubbleView
    internal var closeView: View? = null
    internal var closeComposeView: ComposeView? = null

    ///📝 Distance to close the bubble
    internal var distanceToClose: Int = 100

    ///✨ Config data of expandBubbleView
    internal var expandBubbleView: ComposeView? = null

    ///✨ Config data of flowKeyboardBubbleView
    internal var flowKeyboardBubbleView: ComposeView? = null

    fun bubbleView(view: View): BuBubbleBuilder {
        this.bubbleView = view
        return this
    }

    /// Handle builder pattern
    fun bubbleViewCompose(content: @Composable () -> Unit): BuBubbleBuilder {
        this.bubbleComposeView = ComposeView(context).apply {
            setContent(content)
        }
        return this
    }

    fun closeBubbleViewCompose(content: @Composable () -> Unit): BuBubbleBuilder {
        this.closeComposeView = ComposeView(context).apply {
            setContent(content)
        }
        return this
    }

    fun closeView(view: View): BuBubbleBuilder {
        this.closeView = view
        return this
    }

    fun expandBubbleViewCompose(content: @Composable () -> Unit): BuBubbleBuilder {
        this.expandBubbleView = ComposeView(context).apply {
            setContent(content)
        }
        return this
    }


    fun flowKeyboardBubbleViewCompose(content: @Composable () -> Unit): BuBubbleBuilder {
        this.flowKeyboardBubbleView = ComposeView(context).apply {
            setContent(content)
        }
        return this
    }

    fun bubbleStartPoint(point: Point): BuBubbleBuilder {
        this.startPoint = point
        return this
    }

    fun bubbleListener(listener: BubbleListener): BuBubbleBuilder {
        this.listener = listener
        return this
    }

    fun bubbleForceDragging(forceDragging: Boolean): BuBubbleBuilder {
        this.forceDragging = forceDragging
        return this
    }

    fun bubbleDraggable(isBubbleDraggable: Boolean): BuBubbleBuilder {
        this.isBubbleDraggable = isBubbleDraggable
        return this
    }

    fun bubbleDistanceToClose(distanceToClose: Int): BuBubbleBuilder {
        this.distanceToClose = distanceToClose
        return this
    }

    fun bubbleAnimateToEdgeEnabled(isAnimateToEdgeEnabled: Boolean): BuBubbleBuilder {
        this.isAnimateToEdgeEnabled = isAnimateToEdgeEnabled
        return this
    }
}