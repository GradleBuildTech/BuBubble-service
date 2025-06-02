package com.example.bubService.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.example.bubService.utils.CLOSE_BOTTOM_DIST
import com.example.bubService.utils.DistanceCalculator
import com.example.bubService.utils.afterMeasured
import com.example.bubService.utils.applyCloseBubbleViewLayoutParams
import com.example.bubService.utils.getXYOnScreen
import com.example.bubService.utils.sez
import com.example.bubService.view.layout.BubbleInitialization

/**
 * CloseBubbleView.kt
 * This file is part of the BuBubbleService project.
 * It is used to create a close bubble view that can be used to close the bubble.
 * It extends the BubbleInitialization class and provides functionality to attract a bubble view towards it when the user touches the close field.
 * It calculates the distance from the bubble to the close field and updates the position of the bubble accordingly.
 * It also provides functionality to reset the position of the close bubble and update its UI position.
 * @param context The context of the application.
 * @param closeBottomDist The distance from the bottom of the screen to the close bubble.
 * @param distanceToClose The distance from the bubble to the close field.
* */
class CloseBubbleView(
    context: Context,
    private val closeBottomDist: Int = CLOSE_BOTTOM_DIST,
    private val distanceToClose: Int = 100
) : BubbleInitialization(
    context = context,
    root = LinearLayout(context)
) {
    private var bubbleWidth = 0
    private var bubbleHeight = 0

    ///✨ Position of the bubble
    private var positionX = 0
    private var positionY = 0

    ///✨ Center position of the bubble
    private var centerBubblePositionX = 0
    private var centerBubblePositionY = 0

    private val halfWidthScreen = sez.fullWidth / 2

    ///✨ The following variables are used to store the animation of the bubble
    private var isAnimatedBubble = false

    ///✨ The following variables are used to store the attraction of the bubble
    private var isAttracted = true

    init {
        layoutParams?.applyCloseBubbleViewLayoutParams()

        root?.visibility = View.INVISIBLE

        root?.afterMeasured {
            bubbleHeight = root?.height ?: 0
            bubbleWidth = root?.width ?: 0

            positionX = halfWidthScreen - (bubbleWidth / 2)
            positionY = sez.safeHeight - bubbleHeight - closeBottomDist

            centerBubblePositionX = halfWidthScreen
            centerBubblePositionY = positionY + (bubbleWidth / 2)

            layoutParams?.apply {
                this.x = positionX
                this.y = positionY
            }
            update()
            root?.visibility = View.VISIBLE
            isAttracted = true
        }
    }

    ///✨ The following function is used to calculate the distance from the bubble to the close field
    /// It takes in a bubbleView and returns a float
    private fun distanceFromBubbleToCloseField(bubbleView: BubbleView): Float {
        val bubbleWidth = bubbleView.root?.width ?: 0
        val bubbleHeight = bubbleView.root?.height ?: 0

        val (x, y) = bubbleView.root?.getXYOnScreen() ?: return 0f

        val centerPositionBubbleX = x + (bubbleWidth / 2)
        val centerPositionBubbleY = y + (bubbleHeight / 2)

        val distance = DistanceCalculator.distance(
            x1 = centerBubblePositionX.toDouble(),
            y1 = centerBubblePositionY.toDouble(),
            x2 = centerPositionBubbleX.toDouble(),
            y2 = centerPositionBubbleY.toDouble()
        )
        return (distanceToClose / distance).let {
            if (it > 1) return@let 0
            return@let 1 - it
        }.toFloat()
    }


    ///✨ The following function is used to calculate the distance from the position to the close field
    private fun distanceFromPositionToCloseField(x: Float, y: Float): Float {
        val distance = DistanceCalculator.distance(
            x1 = centerBubblePositionX.toDouble(),
            y1 = centerBubblePositionY.toDouble(),
            x2 = x.toDouble(),
            y2 = y.toDouble()
        )
        return (distanceToClose / distance).let {
            if (it > 1) return@let 0
            return@let 1 - it
        }.toFloat()
    }

    fun isFingerInCloseField(x: Float, y: Float): Boolean =
        distanceFromPositionToCloseField(
            x = (x - sez.safePaddingLeft),
            y = (y - sez.safePaddingTop)
        ) == 0.0f

    fun isBubbleInCloseField(bubbleView: BubbleView): Boolean =
        distanceFromBubbleToCloseField(bubbleView) == 0.0F

    /**
     * This function is used to check if the bubble is in the close field
     * It takes in a bubbleView and returns a boolean
     * @Param bubbleView is the bubble view
     * @Param fingerPositionX is the x position of the finger
     * @Param fingerPositionY is the y position of the finger
     * @return true if the bubble is in the close field
     */
    fun tryAttractBubble(
        bubbleView: BubbleView,
        fingerPositionX: Float,
        fingerPositionY: Float
    ): Boolean {
        if (isAttracted.not()) {
            return false
        }

        if (isFingerInCloseField(x = fingerPositionX, y = fingerPositionY)) {
            if (!isAnimatedBubble) {
                val bWidth = bubbleView.root?.width ?: 0
                val bHeight = bubbleView.root?.height ?: 0
                val xOffSet = (bubbleWidth - bWidth) / 2
                val yOffSet = (bubbleHeight - bHeight) / 2

                val newBubblePositionX = (positionX + xOffSet).toFloat()
                val newBubblePositionY = (positionY + yOffSet).toFloat()

                bubbleView.setPosition(newBubblePositionX.toInt(), newBubblePositionY.toInt())
                bubbleView.updateByNewPosition(inVisibleBefore = false)
                isAnimatedBubble = true
            }
            return true
        }
        isAnimatedBubble = false
        return false
    }

    fun resetCloseBubblePosition() {
        if (root?.windowToken == null) return

        positionX = halfWidthScreen - (bubbleWidth / 2)
        positionY = sez.safeHeight - bubbleHeight - 100

        centerBubblePositionX = halfWidthScreen
        centerBubblePositionY = positionY + (bubbleWidth / 2)

        layoutParams?.apply {
            this.x = positionX
            this.y = positionY
        }
        update()
    }

    fun updateUiPosition(positionX: Float? = null, positionY: Float? = null) {
        if (root?.windowToken == null) return

        if (positionX != null) {
            this.positionX = positionX.toInt()
        }
        if (positionY != null) {
            this.positionY = positionY.toInt()
        }

        layoutParams?.apply {
            if (positionX != null) this.x = positionX.toInt()
            this.y = positionY?.toInt() ?: centerBubblePositionY
        }
        update()
    }
}