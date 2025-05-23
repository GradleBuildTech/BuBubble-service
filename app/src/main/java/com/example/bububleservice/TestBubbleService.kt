package com.example.bububleservice

import android.graphics.Point
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.bububleservice.compose.TestBubbleCompose
import com.example.bububleservice.event.BubbleListener
import com.example.bububleservice.notification.NotificationHelper
import com.example.bububleservice.service.BaseBubbleService
import com.example.bububleservice.service.BuBubbleBuilder
import com.example.bububleservice.utils.BubbleEdgeSide

class TestBubbleService : BaseBubbleService() {


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
        NotificationHelper(this, "", "").createNotificationChannel()
//        startForeground(
//            1,
//            NotificationCompat.Builder
//        )
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