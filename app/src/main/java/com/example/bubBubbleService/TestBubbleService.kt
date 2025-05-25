package com.example.bubBubbleService

import android.graphics.Point
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.bubBubbleService.compose.TestBubbleCompose
import com.example.bubService.event.BubbleListener
import com.example.bubService.notification.NotificationHelper
import com.example.bubService.service.BaseBubbleService
import com.example.bubService.service.BuBubbleBuilder
import com.example.bubService.utils.BubbleEdgeSide

class TestBubbleService : BaseBubbleService() {

    private lateinit var bubbleNotificationBuilder: NotificationCompat.Builder

    private val notificationHelper: NotificationHelper
        get() = NotificationHelper(this, NOTIFICATION_CHANNEL_ID, CHANNEL_NAME)

    override fun onCreate() {
        super.onCreate()
        startNotificationForeground()
        showBubble()
    }

    override fun configBubble(): BuBubbleBuilder {

        val closeBubbleView = ImageView(this).apply {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close_bubble)).apply {
                layoutParams = ViewGroup.LayoutParams(60, 60)
            }
        }

        return BuBubbleBuilder(this)
            .bubbleViewCompose {
                TestBubbleCompose()
            }
            .closeView(closeBubbleView)
            .bubbleStartPoint(Point(0, 400))
            .bubbleForceDragging(true)
            .bubbleDraggable(true)
            .bubbleAnimateToEdgeEnabled(true)
            .bubbleDistanceToClose(200)
            .bubbleListener(
                object : BubbleListener {
                    override fun onFingerDown(x: Float, y: Float) {
                        TODO("Not yet implemented")
                    }

                    override fun onFingerMove(x: Float, y: Float) {
                        TODO("Not yet implemented")
                    }

                    override fun onFingerUp(x: Float, y: Float) {
                        TODO("Not yet implemented")
                    }
                }
            )
    }

    override fun startNotificationForeground() {
        bubbleNotificationBuilder = notificationHelper.initNotificationBuilder(
            smallIcon = R.drawable.ic_flutter,
            contentTitle = "Test Bubble Service",
            contentText = "This is a test bubble service notification",
        )
        notificationHelper.createNotificationChannel()
        startForeground(BUBBLE_NOTIFICATION_ID, bubbleNotificationBuilder.build())
    }

    override fun clearCachedData() {
        TODO("Not yet implemented")
    }

    override fun changeBubbleEdgeSideListener(edgeSide: BubbleEdgeSide) {
        TODO("Not yet implemented")
    }

    override fun onCheckBubbleTouchLeavesListener(x: Float, y: Float) {
        TODO("Not yet implemented")
    }

    override fun onCloseBubbleListener() {
        TODO("Not yet implemented")
    }

    override fun refreshBubbleIconStateListener(isClearCachedData: Boolean) {
        TODO("Not yet implemented")
    }

}

