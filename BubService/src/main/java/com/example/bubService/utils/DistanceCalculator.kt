package com.example.bubService.utils

import kotlin.math.sqrt
import kotlin.math.pow

internal object DistanceCalculator {
    /**
     * * Calculates the distance between two points in a 2D space.
     */
    fun distance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        return sqrt((x2 - x1).pow(2) + (y2 - y1).pow(2))
    }

    /**
        * Calculates the distance to close a bubble based on the screen width and the distance to close.
        * @param halfScreenWidth The half width of the screen.
        * @param bubbleDistance The distance of the bubble from the center of the screen.
        * @param distanceToClose The distance to close the bubble.
     */
    fun newDistanceCloseX(
        halfScreenWidth: Double,
        bubbleDistance: Double,
        distanceToClose: Double
    ): Double {
        return (bubbleDistance * distanceToClose) / halfScreenWidth
    }

    fun newDistanceCloseY(
        halfScreenHeight: Double,
        bubbleDistance: Double,
        distanceToClose: Double
    ): Double {
        return (bubbleDistance * distanceToClose) / halfScreenHeight
    }
}