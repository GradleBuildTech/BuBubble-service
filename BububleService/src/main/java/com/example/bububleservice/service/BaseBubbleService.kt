package com.example.bububleservice.service

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.IBinder
import android.view.View
import com.example.bububleservice.utils.ServiceInteraction
import com.example.bububleservice.view.BubbleView
import com.example.bububleservice.view.CloseBubbleView
import com.example.bububleservice.view.ExpandBubbleView
import com.example.bububleservice.view.FlowKeyboardBubbleView
import kotlinx.coroutines.flow.MutableStateFlow


abstract class BaseBubbleService : Service() {
    ///✨ serviceInteraction is a private variable of type ServiceInteraction
    /// It is used to interact with the service
    private var serviceInteraction: ServiceInteraction? = null

    ///✨ All Bubble type view
    private var bubble: BubbleView? = null

    private var closeBubble: CloseBubbleView? = null

    private var expandBubble: ExpandBubbleView? = null

    private var flowKeyboardBubble: FlowKeyboardBubbleView? = null

    ///✨ BuBubbleEventData is a data class that is used to store the bubble data
    private val bubbleStateFlow = MutableStateFlow(BuBubbleEventData())
    private var bubbleState: BuBubbleEventData
        set(value) {
            bubbleStateFlow.value = value
        }
        get() = bubbleStateFlow.value

    ///✨ BuBubbleBuilder is an abstract class that is used to build a bubble
    abstract fun configBubble(): BuBubbleBuilder
    abstract fun clearCachedData()


    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    ///✨ onCreateBubble is a function that takes in a bubbleBuilder
    /// It creates a bubble, closeBubble, expandBubble and flowKeyboardBubble
    /// It adds the bubbleView to the bubble (close, expand, ....)

    private fun onCreateBubble(bubbleBuilder: BuBubbleBuilder?) {
        if (bubbleBuilder != null) {
            if (bubbleBuilder.bubbleView != null) {
                bubble = BubbleView(
                    context = this,
                    containCompose = true,
                    listener = bubbleBuilder.listener,
                    forceDragging = bubbleBuilder.forceDragging
                )
                bubble!!.rootGroup?.addView(bubbleBuilder.bubbleView)
            }

            if (bubbleBuilder.closeBubbleView != null) {
                closeBubble = CloseBubbleView(
                    context = this,
                    distanceToClose = bubbleBuilder.distanceToClose
                )
                closeBubble!!.rootGroup?.addView(bubbleBuilder.closeBubbleView)
            }
            if (bubbleBuilder.expandBubbleView != null) {
                expandBubble = ExpandBubbleView(context = this)
                expandBubble!!.rootGroup?.addView(bubbleBuilder.expandBubbleView)
            }
            if (bubbleBuilder.flowKeyboardBubbleView != null) {
                flowKeyboardBubble = FlowKeyboardBubbleView(context = this)
                flowKeyboardBubble!!.rootGroup?.addView(bubbleBuilder.flowKeyboardBubbleView)
            }
        }
    }

    ///✨ onClearAllBubbleData is a function that removes the bubble, closeBubble, expandBubble and flowKeyboardBubble
    private fun onClearAllBubbleData() {
        bubble?.remove()
        closeBubble?.remove()
        expandBubble?.remove()
        flowKeyboardBubble?.remove()
    }

    ///🎈 Bubble event
    fun showBubble() {
        if (bubbleState.isBubbleActivated.not() || bubbleState.isBubbleShow.not()) return
        if (expandBubble?.root?.isShown == true) return
        bubble?.show()
        bubble?.updateBubbleStatus(View.VISIBLE)
        bubbleStateFlow.value = bubbleState.copy(isBubbleShow = true, isBubbleVisible = true)
    }

    fun hideBubble() {

    }

    ///🎈 Close bubble event
    fun showCloseBubble() {

    }

    fun hideCloseBubble() {

    }

    ///🎈 Expand bubble event
    fun showExpandBubble() {

    }

    fun hideExpandBubble() {

    }

    ///🎈 Flow keyboard bubble event
    fun showFlowKeyboardBubble() {

    }

    fun hideFlowKeyboardBubble() {

    }
}