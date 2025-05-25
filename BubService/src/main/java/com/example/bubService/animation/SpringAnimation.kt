package com.example.bubService.animation

import androidx.dynamicanimation.animation.FloatValueHolder
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

/// SpringAnimationHelper class
/// This class is used to handle the spring animation

object SpringAnimationHelper {

    /// Note values
    /// stiffness is the stiffness of the spring
    /// * SpringForce.STIFFNESS_LOW
    /// * SpringForce.STIFFNESS_MEDIUM
    /// * SpringForce.STIFFNESS_HIGH
    /// * SpringForce.STIFFNESS_VERY_HIGH

    /// damping is the damping of the spring
    /// * SpringForce.DAMPING_RATIO_NO_BOUNCY
    /// * SpringForce.DAMPING_RATIO_LOW_BOUNCY
    /// * SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY
    /// * SpringForce.DAMPING_RATIO_HIGH_BOUNCY
    /// ========================================================================

    /// startSpring is a function that is used to start the spring animation
    /// It takes in the startPosition, finalPosition, event, stiffness and damping
    /// Example of usage:
    /// ```kotlin
    /** springAnimation = SpringAnimationHelper.startSpring(
        startValue =  startX.toFloat(),
        finalPosition = endX.toFloat(),
        event = object : SpringAnimationHelper.AnimationEvent{
            override fun onUpdate(float: Float) {
                try {
                    layoutParams.x = float.toInt()
                    update()
                }catch (exception: Exception){
                    exception.printStackTrace()
                }
            }
            override fun onEnd() {
                springAnimation = null
                snapComplete(if(isOnTheLeftSide) BubbleEdgeSide.LEFT else BubbleEdgeSide.RIGHT)
            }
        }
    ) **/
    fun startSpring(
        ///It can be the positionX of the bubble or the positionY of the bubble
        startPosition: Float,
        finalPosition: Float,

        event: com.example.bubService.animation.AnimationEvent,

        stiffness: Float = SpringForce.STIFFNESS_LOW,
        damping: Float = SpringForce.DAMPING_RATIO_NO_BOUNCY
    ): SpringAnimation {
        ///Initialize the spring animation
        ///FloatValueHolder is a class that extends FloatPropertyCompat
        val springAnimation = SpringAnimation(FloatValueHolder())


        ///Set the spring of the spring animation

        springAnimation.spring = SpringForce().apply {
            springAnimation.setStartValue(startPosition)
            setFinalPosition(finalPosition)
            this.stiffness = stiffness
            this.dampingRatio = damping
        }

        springAnimation.addUpdateListener() { _, value, _ ->
            ///Call update function of the event
            /// In this case value is the positionX of bubble
            event.onAnimationUpdate(value)
        }

        springAnimation.addEndListener() { _, _, _, _ ->
            event.onAnimationEnd()
        }

        return springAnimation
    }

    /// animateToPosition is a function that is used to animate the bubble to a specific position
    /// Example of usage:
    /// ```kotlin
    /** springAnimation = SpringAnimationHelper.animateToPosition(
        startPositionX = startX.toFloat(),
        startPositionY = startY.toFloat(),
        finalPositionX = endX.toFloat(),
        finalPositionY = endY.toFloat(),
        stiffness = SpringForce.STIFFNESS_MEDIUM,
        dampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY,
        event = object : SpringAnimationHelper.AnimationEvent{
            override fun onAnimationUpdatePosition(x: Float, y: Float) {
                try {
                    layoutParams.x = x.toInt()
                    layoutParams.y = y.toInt()
                    update()
                }catch (exception: Exception){
                    exception.printStackTrace()
                }
            }
            override fun onAnimationEnd() {
                springAnimation = null
                snapComplete(if(isOnTheLeftSide) BubbleEdgeSide.LEFT else BubbleEdgeSide.RIGHT)
            }
        }
    ) **/
    fun animateToPosition(
        startPositionX: Float,
        startPositionY: Float,
        finalPositionX: Float,
        finalPositionY: Float,
        stiffness: Float = SpringForce.STIFFNESS_MEDIUM,
        dampingRatio: Float = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY,
        event: com.example.bubService.animation.AnimationEvent,
    ) : SpringAnimation {
        val distanceX = finalPositionX - startPositionX
        val distanceY = finalPositionY - startPositionY

        val springAnimation = SpringAnimation(FloatValueHolder())

        springAnimation.spring = SpringForce().apply {
            springAnimation.setStartValue(startPositionX)
            finalPosition = finalPositionX
            this.stiffness = stiffness
            this.dampingRatio = dampingRatio
        }

        springAnimation.addUpdateListener { _, value, _ ->
            event.onAnimationUpdatePosition(
                positionX = value,
                positionY = startPositionY + ((value - startPositionX) * distanceY) / distanceX
            )
        }

        springAnimation.addEndListener { _, _, _, _ ->
            event.onAnimationEnd()
        }

        event.onAnimationStart()
        springAnimation.start()
        return springAnimation
    }


}