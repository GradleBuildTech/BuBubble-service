package com.example.bubService.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.view.MotionEvent
import android.view.View
import androidx.dynamicanimation.animation.SpringAnimation
import com.example.bubService.animation.SpringAnimationHelper
import com.example.bubService.event.BubbleListener
import com.example.bubService.utils.BubbleEdgeSide
import com.example.bubService.utils.afterMeasured
import com.example.bubService.utils.applyBubbleViewLayoutParams
import com.example.bubService.utils.getXYOnScreen
import com.example.bubService.utils.sez
import com.example.bubService.utils.updateGestureExclusion
import com.example.bubService.view.layout.BubbleInitialization
import com.example.bubService.view.layout.BubbleLayout
import kotlin.math.abs

/**
 * BubbleView is a class that extends BubbleInitialization
 * It is used to create a view that is used to show the bubble
 * It takes in a context and a containCompose
 * It has a root that is a BubbleLayout
 * @param context The context of the application
 * @param containCompose The compose view that is used to show the bubble
 * @param forceDragging The boolean value that is used to determine if the bubble should be dragged
 * @param listener The listener that is used to handle the bubble events
 */
class BubbleView(
    context: Context,
    containCompose: Boolean,
    startPoint: Point,
    private val forceDragging: Boolean = false,
    private val listener: BubbleListener? = null
) : BubbleInitialization(
    context = context,
    containCompose = containCompose,
    root = BubbleLayout(context)
) {
    ///✨ The following variables are used to store the point (position) of the bubble
    private val prevPoint = Point(0, 0)
    private val rawPointOnDown = Point(0, 0)
    private val newPoint = Point(0, 0)
    private val halfScreenWidth = sez.fullWidth / 2

    ///✨ The following variables are used to store the height and width of the bubble
    private var bubbleHeight = 0
    private var bubbleWidth = 0
    private var springAnimation: SpringAnimation? = null

    private var ignoreClick: Boolean = false

    ///✨ The following variables are used to store the listener of the bubble, by following MotionEvent
    internal var mListener: BubbleListener? = null

    ///✨ The following variables are used to store the position of the bubble
    /// If don't want to drag,drop the bubble, set isDraggable to false
    private var isDraggable: Boolean = true

    /// Getter value
    fun isSmallBubble(): Boolean {
        if (this.root == null) return false
        return this.root!!.height < bubbleHeight
    }

    fun isExpandWidth(): Boolean {
        if (this.root == null) return false
        return this.root!!.width > bubbleWidth
    }


    init {
        layoutParams?.applyBubbleViewLayoutParams(startPoint)
        customTouch()
    }

    ///✨ Handle move, animation and position  of bubble


    ///Handle new position of bubble
    fun setPosition(x: Int, y: Int) {
        newPoint.x = x
        newPoint.y = y
    }

    fun updateByNewPosition(
        inVisibleBefore: Boolean = false,
        resetPositionY: Boolean = true
    ) {
        if (inVisibleBefore) {
            root?.visibility = View.INVISIBLE
        }
        layoutParams?.x = newPoint.x
        if (resetPositionY) {
            layoutParams?.y = newPoint.y
        }
        update()
    }

    /// snapToEdge is a function that is used to snap the bubble to the edge
    fun snapToEdge(animationComplete: (BubbleEdgeSide) -> Unit) {
        springAnimation?.cancel()
        springAnimation = null

        val bubbleWidth = root?.width
        val iconX = root?.getXYOnScreen()?.first

        if (bubbleWidth == null || iconX == null) return

        val isOnLeftSide = (iconX + bubbleWidth / 2) < halfScreenWidth
        val startX: Int = iconX
        val endX: Int = if (isOnLeftSide) 0 else (sez.fullWidth - bubbleWidth)

        springAnimation = SpringAnimationHelper.startSpring(
            startPosition = startX.toFloat(),
            finalPosition = endX.toFloat(),
            event = object : com.example.bubService.animation.AnimationEvent {
                override fun onAnimationUpdate(positionX: Float) {
                    try {
                        layoutParams?.x = positionX.toInt()
                        update()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun onAnimationEnd() {
                    springAnimation = null
                    animationComplete(if (isOnLeftSide) BubbleEdgeSide.LEFT else BubbleEdgeSide.RIGHT)
                }
            }
        )
    }

    /// animateTo is a function that is used to animate the bubble to a specific position
    fun animateTo(positionX: Int, positionY: Int, animationComplete: () -> Unit) {
        springAnimation?.cancel()
        springAnimation = null

        val startX: Int = root?.getXYOnScreen()?.first ?: 0
        val startY: Int = root?.getXYOnScreen()?.second ?: 0

        springAnimation = SpringAnimationHelper.animateToPosition(
            startPositionX = startX.toFloat(),
            startPositionY = startY.toFloat(),
            finalPositionX = positionX.toFloat(),
            finalPositionY = positionY.toFloat(),
            event = object : com.example.bubService.animation.AnimationEvent {
                override fun onAnimationUpdatePosition(positionX: Float, positionY: Float) {
                    layoutParams?.x = positionX.toInt()
                    layoutParams?.y = positionY.toInt()
                    update()
                }

                override fun onAnimationEnd() {
                    springAnimation = null
                    animationComplete()
                }
            }
        )
    }

    /// updateUiPosition is a function that is used to update the position of the bubble
    fun updateUiPosition(
        positionX: Float,
        positionY: Float,
        callBack: ((Int, Int) -> Unit)? = null
    ) {
        val mIconDeltaX = positionX - rawPointOnDown.x
        val mIconDeltaY = positionY - rawPointOnDown.y


        newPoint.x = prevPoint.x + mIconDeltaX.toInt()
        newPoint.y = prevPoint.y + mIconDeltaY.toInt()

        /// Checking bubble in the screen
        val limitTop = 0
        val limitBottom = sez.safeHeight - (root?.height ?: 0)

        val isUnderSoftNavBar = newPoint.x < limitTop
        val isAboveSoftNavBar = newPoint.y > limitBottom

        if (isUnderSoftNavBar) {
            newPoint.x = limitTop
        } else if (isAboveSoftNavBar) {
            newPoint.y = limitBottom
        }

        /// Update position of layoutParams
        layoutParams?.x = newPoint.x
        layoutParams?.y = newPoint.y

        update()

        /// Invoke the callback with the new position
        callBack?.invoke(newPoint.x, newPoint.y)
    }

    /// safeCancelAnimation is a function that is used to cancel the animation
    fun safeCancelAnimation() {
        springAnimation?.cancel()
        springAnimation = null
    }

    /// ===========================================================================

    /// ✨ Handle size of bubble
    fun resizeBubbleHeight() {}

    fun resizeBubbleWidth() {}

    fun resizeBubbleSize(percent: Float, blurBubble: Boolean = false) {
        resizeView(bubbleWidth * percent, bubbleHeight * percent)
        if (blurBubble) {
            blurView(percent)
        }
    }

    fun updateBubbleStatus(visible: Int) {
        root?.visibility = visible
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun customTouch() {
        fun handleMovement(event: MotionEvent) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    prevPoint.x = layoutParams?.x ?: 0
                    prevPoint.y = layoutParams?.y ?: 0

                    rawPointOnDown.x = event.rawX.toInt()
                    rawPointOnDown.y = event.rawY.toInt()

                    mListener?.onFingerDown(event.rawX, event.rawY)
                    listener?.onFingerDown(event.rawX, event.rawY)
                }

                MotionEvent.ACTION_UP -> {
                    mListener?.onFingerUp(event.rawX, event.rawY)
                    listener?.onFingerUp(event.rawX, event.rawY)
                }

                MotionEvent.ACTION_MOVE -> {
                    if (isDraggable) {
                        mListener?.onFingerMove(event.rawX, event.rawY)
                        listener?.onFingerMove(event.rawX, event.rawY)
                    }
                }
            }
        }

        /// IgnoreChildClickEvent is a function that is used to ignore the child click event
        fun ignoreChildClickEvent(event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_UP -> ignoreClick = false
                MotionEvent.ACTION_DOWN -> ignoreClick = false
                MotionEvent.ACTION_MOVE -> {
                    if (abs(event.rawX - rawPointOnDown.x) > 1f || abs(event.rawY - rawPointOnDown.y) > 1f) {
                        ignoreClick = true
                    }
                }
            }
            return ignoreClick
        }
        if (root != null) {
            root!!.visibility = View.INVISIBLE
            (root!! as BubbleLayout).apply {
                /// afterMeasured is a function that is used to update the gesture exclusion
                afterMeasured {
                    ///updates the gesture exclusion rect for the view
                    updateGestureExclusion()
                    bubbleWidth = width
                    bubbleHeight = height

                    root?.visibility = View.VISIBLE
                }
                if (forceDragging) {
                    doOnTouchEvent = ::handleMovement

                    ignoreChildEvent = ::ignoreChildClickEvent
                } else {
                    this.setOnTouchListener { _, motionEvent ->
                        handleMovement(motionEvent)
                        true
                    }
                }
            }

        }
    }
}