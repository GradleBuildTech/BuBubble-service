package com.example.bububleservice.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.IBinder
import android.view.View
import com.example.bububleservice.event.BubbleListener
import com.example.bububleservice.utils.BubbleEdgeSide
import com.example.bububleservice.utils.ServiceInteraction
import com.example.bububleservice.utils.sez
import com.example.bububleservice.view.BubbleView
import com.example.bububleservice.view.CloseBubbleView
import com.example.bububleservice.view.ExpandBubbleView
import com.example.bububleservice.view.FlowKeyboardBubbleView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


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

    private lateinit var serviceScope: CoroutineScope

    private var bubbleState: BuBubbleEventData
        set(value) {
            bubbleStateFlow.value = value
        }
        get() = bubbleStateFlow.value

    ///✨ BuBubbleBuilder is an abstract class that is used to build a bubble
    /// Implement style config in this function
    abstract fun configBubble(): BuBubbleBuilder



    /// Abstract function support for handle bubble event
    abstract fun clearCachedData()
    abstract fun changeBubbleEdgeSideListener(edgeSide: BubbleEdgeSide)
    abstract fun onCheckBubbleTouchLeavesListener(x: Float, y: Float)
    abstract fun onCloseBubbleListener()


    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        serviceScope = CoroutineScope(Dispatchers.Main)
        super.onCreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        clearCachedData()
        if(newConfig.isScreenRound) {
            sez.refresh()
        }
        onCreateBubble(configBubble())
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

            bubble?.mListener = CustomBubbleListener(
                lBubble = bubble,
                lCloseBubble = closeBubble,
                context = this,
                isAnimatedToEdge = bubbleBuilder.isAnimateToEdgeEnabled,
                onCloseBubbleView = { onCloseBubbleListener() }
            )
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
        if(bubbleState.isShowingFlowKeyboardBubble) return
        closeBubble?.remove()
    }


    ///🎈 Expand bubble event
    fun showExpandBubble(isRemoveBubble: Boolean = false) {
        if(isRemoveBubble) {
            bubble?.remove()
            closeBubble?.remove()
        }
    }

    fun hideExpandBubble() {
        expandBubble?.updateVisibility(false)
    }

    ///🎈 Flow keyboard bubble event
    fun showFlowKeyboardBubble() {
        if ((flowKeyboardBubble?.isShown() == true) || bubbleState.isShowingFlowKeyboardBubble) return
        bubbleStateFlow.value = bubbleState.copy(isShowingFlowKeyboardBubble = true)
        expandBubble?.show()
        serviceScope.launch {
            delay(1000)
            bubbleStateFlow.value = bubbleState.copy(isShowingFlowKeyboardBubble = false)
        }
    }

    fun hideFlowKeyboardBubble() {
        expandBubble?.remove()
    }


    /**
     * ✨ CustomBubbleListener is a private inner class that implements the BubbleListener interface
     * @Param lBubble is the bubble view
     * @Param lCloseBubble is the close bubble view
     * @Param isAnimatedToEdge is a boolean value that indicates if the bubble is animated to the edge
     * @Param context is the context of the bubble
     */
    private inner class CustomBubbleListener(
        private val lBubble: BubbleView?,
        private val lCloseBubble: CloseBubbleView?,
        private val isAnimatedToEdge: Boolean = false,
        private val context: Context,

        //Function Param
        private val onCloseBubbleView: () -> Unit
    ) : BubbleListener {
        private var _onMove = false
        private var _fingerPositionX = 0f
        private var _fingerPositionY = 0f

        override fun onFingerDown(x: Float, y: Float) {
            _fingerPositionX = x
            _fingerPositionY = y
            lBubble?.safeCancelAnimation()

        }

        override fun onFingerMove(x: Float, y: Float) {
            if(lCloseBubble == null || lBubble == null) return
            val closeBubbleFlow = closeBubble?.tryAttractBubble(lBubble, x, y) ?: false
            if(_onMove.not()) {
                if(x != _fingerPositionX || y != _fingerPositionY) {
                    _onMove = true
                    lCloseBubble.show()
                }
            }
            if(closeBubbleFlow.not()) {
                lBubble.updateUiPosition(x , y)
            }
        }

        override fun onFingerUp(x: Float, y: Float) {
            if(lBubble == null || lCloseBubble == null) return
            _onMove = false
            lCloseBubble.remove()
            if(x == _fingerPositionX && y == _fingerPositionY) {
                return
            }
            if(lCloseBubble.isBubbleInCloseField(lBubble) && lCloseBubble.isFingerInCloseField(x, y)) {
                lBubble.remove()
                bubbleStateFlow.value = bubbleState.copy(isBubbleShow = false)

                this.onCloseBubbleView()
//                mBubble.setPosition(0, (sez.fullHeight / 2 - 40))
//                mBubble.updateByNewPosition()
//                bubbleStateFlow.value = bubbleState.copy( isBubbleShow = false )
//                bubbleStateFlow.value = bubbleState.copy(showingRecommendBubble = false)
//                bubbleStateFlow.value = bubbleState.copy(isDisableShowBubble = true)
//
//                hideBubble(isResetPosition = false)
//                changeScreenShotHandlerState(isHandled = false, isDisableUiData = true)
//                updateGalleryActionEnum(GalleryAction.NONE)
//                onCloseBubbleView()
//                refreshBubbleIconState(true)
//
//                context.showToastBubbleHidden()
            } else {
                if(isAnimatedToEdge) lBubble.snapToEdge {
                    changeBubbleEdgeSideListener(it)
                }
                onCheckBubbleTouchLeavesListener(x, y)
            }
        }
    }
}