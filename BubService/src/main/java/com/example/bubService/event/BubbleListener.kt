package com.example.bubService.event

interface BubbleListener {
    fun onFingerDown(x: Float, y: Float) {}

    fun onFingerUp(x: Float, y: Float) {}

    fun onFingerMove(x: Float, y: Float) {}
}