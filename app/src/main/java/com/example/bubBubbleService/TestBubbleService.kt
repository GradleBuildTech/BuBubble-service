package com.example.bubBubbleService

import android.graphics.Point
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
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
                layoutParams = ViewGroup.LayoutParams(80, 80)
            }
        }

        val bubbleView = ImageView(this).apply {
            setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_flutter)).apply {
                layoutParams = ViewGroup.LayoutParams(80, 80)
            }
        }

        return BuBubbleBuilder(this)
            .bubbleView(bubbleView)
            .closeView(closeBubbleView)
            .bubbleStartPoint(Point(0, 400))
            .bubbleForceDragging(true)
            .bubbleAnimateToEdgeEnabled(true)
            .bubbleDistanceToClose(200)
            .bubbleCloseBottomDist(100)
            .bubbleAnimatedClose(true)
            .bubbleListener(
                object : BubbleListener {
                    override fun onFingerDown(x: Float, y: Float) { 
                    }

                    override fun onFingerMove(x: Float, y: Float) {
                    }

                    override fun onFingerUp(x: Float, y: Float) {
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
    }

    override fun changeBubbleEdgeSideListener(edgeSide: BubbleEdgeSide) {
    }

    override fun onCheckBubbleTouchLeavesListener(x: Float, y: Float) {
    }

    override fun onCloseBubbleListener() {
    }

    override fun refreshBubbleIconStateListener(isClearCachedData: Boolean) {
    }

}

