package com.example.bububleservice.animation


/// AnimationEvent interface
/// This interface is used to handle the animation event
interface  AnimationEvent {
    /// onAnimationStart is a function that is called when the animation starts
    fun onAnimationStart() {}
    /// onAnimationEnd is a function that is called when the animation ends
    fun onAnimationEnd() {}
    /// onAnimationUpdate is a function that is called when the animation updates
    fun onAnimationUpdate(positionX: Float) {}
    /// onAnimationUpdatePosition is a function that is called when the animation updates the position
    fun onAnimationUpdatePosition(positionX: Float, positionY: Float) {}
}