package com.example.bubService.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.IBinder
import android.view.View
import com.example.bubService.event.BubbleListener
import com.example.bubService.utils.BubbleEdgeSide
import com.example.bubService.utils.ServiceInteraction
import com.example.bubService.utils.sez
import com.example.bubService.view.BubbleView
import com.example.bubService.view.CloseBubbleView
import com.example.bubService.view.ExpandBubbleView
import com.example.bubService.view.FlowKeyboardBubbleView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


abstract class BaseBubbleService : Service() {
    ///✨ serviceInteraction is a private variable of type ServiceInteraction
    /// It is used to interact with the service
    private var _serviceInteraction: ServiceInteraction? = null

    ///✨ All Bubble type view
    private var _bubble: BubbleView? = null

    private var _closeBubble: CloseBubbleView? = null

    private var _expandBubble: ExpandBubbleView? = null

    private var _flowKeyboardBubble: FlowKeyboardBubbleView? = null

    ///✨ BuBubbleEventData is a data class that is used to store the bubble data
    private val _bubbleStateFlow = MutableStateFlow(BuBubbleEventData())

    private lateinit var serviceScope: CoroutineScope

    private var bubbleState: BuBubbleEventData
        set(value) {
            _bubbleStateFlow.value = value
        }
        get() = _bubbleStateFlow.value

    ///✨ BuBubbleBuilder is an abstract class that is used to build a bubble
    /// Implement style config in this function
    abstract fun configBubble(): BuBubbleBuilder


    /// Abstract function support for handle bubble event
    abstract fun startNotificationForeground()
    abstract fun clearCachedData()
    abstract fun changeBubbleEdgeSideListener(edgeSide: BubbleEdgeSide)
    abstract fun onCheckBubbleTouchLeavesListener(x: Float, y: Float)
    abstract fun onCloseBubbleListener()
    abstract fun refreshBubbleIconStateListener(isClearCachedData: Boolean)


    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        serviceScope = CoroutineScope(Dispatchers.Main)
        super.onCreate()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        clearCachedData()
        if (newConfig.isScreenRound) {
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
                _bubble = BubbleView(
                    context = this,
                    containCompose = true,
                    listener = bubbleBuilder.listener,
                    forceDragging = bubbleBuilder.forceDragging
                )
                _bubble!!.rootGroup?.addView(bubbleBuilder.bubbleView)
            }

            if (bubbleBuilder.closeBubbleView != null || bubbleBuilder.closeView != null) {
                assert(bubbleBuilder.closeBubbleView != null && bubbleBuilder.closeView != null) {
                    "You must set closeBubbleView or closeView"
                }
                _closeBubble = CloseBubbleView(
                    context = this,
                    distanceToClose = bubbleBuilder.distanceToClose
                )
                if(bubbleBuilder.closeBubbleView != null) {
                    _closeBubble!!.rootGroup?.addView(bubbleBuilder.closeBubbleView)
                } else if(bubbleBuilder.closeView != null) {
                    _closeBubble!!.rootGroup?.addView(bubbleBuilder.closeView)
                }
            }
            if (bubbleBuilder.expandBubbleView != null) {
                _expandBubble = ExpandBubbleView(context = this)
                _expandBubble!!.rootGroup?.addView(bubbleBuilder.expandBubbleView)
            }
            if (bubbleBuilder.flowKeyboardBubbleView != null) {
                _flowKeyboardBubble = FlowKeyboardBubbleView(context = this)
                _flowKeyboardBubble!!.rootGroup?.addView(bubbleBuilder.flowKeyboardBubbleView)
            }

            _bubble?.mListener = CustomBubbleListener(
                lBubble = _bubble,
                lCloseBubble = _closeBubble,
                context = this,
                isAnimatedToEdge = bubbleBuilder.isAnimateToEdgeEnabled,
                onCloseBubbleView = {
                    _bubbleStateFlow.value = bubbleState.copy(
                        isBubbleShow = false,
                        isDisableShowBubble = true
                    )
                    hideBubble()
                    onCloseBubbleListener()
                    refreshBubbleIconStateListener(true)
                },
            )
        }
    }

    ///✨ onClearAllBubbleData is a function that removes the bubble, closeBubble, expandBubble and flowKeyboardBubble
    private fun onClearAllBubbleData() {
        _bubble?.remove()
        _closeBubble?.remove()
        _expandBubble?.remove()
        _flowKeyboardBubble?.remove()
    }

    ///🎈 Bubble event
    fun showBubble() {
        if (bubbleState.isBubbleServiceActivated.not() || bubbleState.isBubbleShow) return
        if (_expandBubble?.root?.isShown == true) return
        _bubble?.show()
        _closeBubble?.show()
        _bubble?.updateBubbleStatus(View.VISIBLE)
        _bubbleStateFlow.value = bubbleState.copy(isBubbleShow = true, isBubbleVisible = true)
    }

    private fun hideBubble() {
        if (bubbleState.isShowingFlowKeyboardBubble) return
        _closeBubble?.remove()
    }


    ///🎈 Expand bubble event
    fun showExpandBubble(isRemoveBubble: Boolean = false) {
        if (isRemoveBubble) {
            _bubble?.remove()
            _closeBubble?.remove()
        }
    }

    fun hideExpandBubble() {
        _expandBubble?.updateVisibility(false)
    }

    ///🎈 Flow keyboard bubble event
    fun showFlowKeyboardBubble() {
        if ((_flowKeyboardBubble?.isShown() == true) || bubbleState.isShowingFlowKeyboardBubble) return
        _bubbleStateFlow.value = bubbleState.copy(isShowingFlowKeyboardBubble = true)
        _expandBubble?.show()
        serviceScope.launch {
            delay(1000)
            _bubbleStateFlow.value = bubbleState.copy(isShowingFlowKeyboardBubble = false)
        }
    }

    fun hideFlowKeyboardBubble() {
        _expandBubble?.remove()
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
        private val onCloseBubbleView: () -> Unit,
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
            if (lCloseBubble == null || lBubble == null) return
            val closeBubbleFlow = _closeBubble?.tryAttractBubble(lBubble, x, y) ?: false
            if (_onMove.not()) {
                if (x != _fingerPositionX || y != _fingerPositionY) {
                    _onMove = true
                    lCloseBubble.show()
                }
            }
            if (closeBubbleFlow.not()) {
                lBubble.updateUiPosition(x, y)
            }
        }

        override fun onFingerUp(x: Float, y: Float) {
            if (lBubble == null || lCloseBubble == null) return
            _onMove = false
            lCloseBubble.remove()
            if (x == _fingerPositionX && y == _fingerPositionY) {
                return
            }
            if (lCloseBubble.isBubbleInCloseField(lBubble) && lCloseBubble.isFingerInCloseField(
                    x,
                    y
                )
            ) {
                lBubble.setPosition(0, (sez.fullHeight / 2 - 40))
                lBubble.updateByNewPosition()
                this.onCloseBubbleView()
            } else {
                if (isAnimatedToEdge) lBubble.snapToEdge {
                    changeBubbleEdgeSideListener(it)
                }
                onCheckBubbleTouchLeavesListener(x, y)
            }
        }
    }
}