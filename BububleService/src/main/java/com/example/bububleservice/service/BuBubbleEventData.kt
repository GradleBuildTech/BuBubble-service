package com.example.bububleservice.service

///BuBubbleEventData is used to store the data of the bubble
data class BuBubbleEventData(
    val isBubbleActivated: Boolean = false,
    var isBubbleShow: Boolean = false,
    var isBubbleVisible: Boolean = false,

    var isShowingFlowKeyboardBubble: Boolean = false,
    var isDisableShowBubble: Boolean = false,
)