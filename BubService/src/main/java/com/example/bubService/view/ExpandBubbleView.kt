package com.example.bubService.view

import android.content.Context
import android.view.View
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.example.bubService.animation.AnimationEvent
import com.example.bubService.animation.SpringAnimationHelper
import com.example.bubService.event.BubbleListener
import com.example.bubService.utils.afterMeasured
import com.example.bubService.utils.applyExpandBubbleViewLayoutParams
import com.example.bubService.utils.sez
import com.example.bubService.view.layout.BubbleInitialization
import com.example.bubService.view.layout.BubbleLayout

/**
 * ExpandBubbleView is a class that represents a bubble view that can be expanded and collapsed.
 * It extends the BubbleInitialization class and provides methods to open and close the bubble view.
 * It also provides a spring animation to animate the opening and closing of the bubble view.
 * Example usage:
 * ```kotlin
 *  val expandBubbleView = ExpandBubbleView(context)
 *  expandBubbleView.onOpen() // To open the bubble view
 *  expandBubbleView.onClose() // To close the bubble view
 *  @param closeComplete: (() -> Unit)? = null // Optional callback when the bubble view is closed
 *  *  expandBubbleView.onClose {
 *    // Do something when the bubble view is closed
 *    }
 * ```
 *  @param containCompose: Boolean = false // Optional parameter to determine if the bubble view should contain a Compose view
 */
class ExpandBubbleView(
    context: Context,
    containCompose: Boolean = false,
    dragToClose: Boolean = false,
): BubbleInitialization(
    context = context,
    containCompose = containCompose,
    root = BubbleLayout(context)
) {
    /// The root view of the bubble
    private var bubbleHeight = 0
    private var isInitial = false

    /// The spring animation that is used to animate the bubble
    private var springAnimation: SpringAnimation? = null

    private var mListener: BubbleListener = ExpandBubbleListener(lBubble = this)


    init {
        layoutParams?.applyExpandBubbleViewLayoutParams()
        root?.visibility = View.INVISIBLE
        root?.afterMeasured {
            bubbleHeight = root?.height ?: 0
            layoutParams.apply {
                this?.x = 0
                this?.y = sez.safeHeight - bubbleHeight
            }
            update()
            root?.visibility = View.VISIBLE
            openCloseAnimation(
                startY = sez.fullHeight,
                endY = sez.safeHeight - bubbleHeight
            )
            isInitial = true
        }
    }

    /// Show the bubble view
    fun onOpen() {
        if(springAnimation != null) return
        if(!isInitial) show()
        updateVisibility(isShown = false)
        layoutParams?.apply {
            this.x = 0
            this.y = sez.fullHeight
        }
        update()
        updateVisibility(isShown = true)
        show()
        openCloseAnimation(
            startY = sez.fullHeight,
            endY = sez.safeHeight - bubbleHeight
        )
    }

    /// Close the bubble view
    fun onClose(closeComplete: (() -> Unit)? = null) {
        if(springAnimation != null) return
        remove()
        closeComplete?.invoke()
    }

    /**
     * ✨ This function is used to animate the opening and closing of the bubble view.
     * @param startY: Int - The starting Y position of the bubble view.
     * @param endY: Int - The ending Y position of the bubble view.
     * @param onComplete: (() -> Unit)? - An optional callback that is invoked when the animation is complete.
     * */
    private fun openCloseAnimation(
        startY: Int,
        endY: Int,
        onComplete: (() -> Unit)? = null
    ) {
        springAnimation?.cancel()
        springAnimation = null

        springAnimation = SpringAnimationHelper.startSpring(
            startPosition = startY.toFloat(),
            finalPosition = endY.toFloat(),
            stiffness = SpringForce.STIFFNESS_LOW,
            damping = SpringForce.DAMPING_RATIO_NO_BOUNCY,
            event = object : AnimationEvent{
                override fun onAnimationUpdate(positionX: Float) {
                    try {
                        layoutParams?.y = positionX.toInt()
                        update()
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                }

                override fun onAnimationEnd() {
                    springAnimation = null
                    onComplete?.invoke()
                }
            }
        )
    }

    private inner class ExpandBubbleListener(
        private val lBubble: ExpandBubbleView
    ) : BubbleListener {
        override fun onFingerDown(x: Float, y: Float) {
            // Handle finger down event
        }

        override fun onFingerMove(x: Float, y: Float) {
            // Handle finger move event
        }

        override fun onFingerUp(x: Float, y: Float) {
        }
    }
}