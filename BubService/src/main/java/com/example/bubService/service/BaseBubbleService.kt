package com.example.bubService.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.IBinder
import android.util.Log
import android.view.View
import com.example.bubService.event.BubbleListener
import com.example.bubService.utils.BubbleEdgeSide
import com.example.bubService.utils.DistanceCalculator
import com.example.bubService.utils.ServiceInteraction
import com.example.bubService.utils.canDrawOverlays
import com.example.bubService.utils.sez
import com.example.bubService.view.BubbleView
import com.example.bubService.view.CloseBubbleView
import com.example.bubService.view.ExpandBubbleView
import com.example.bubService.view.FlowKeyboardBubbleView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs


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

    /**
     * ✨ BuBubbleBuilder is an abstract class that is used to build a bubble
     * Implement style config in this function
     */
    abstract fun configBubble(): BuBubbleBuilder


    // Abstract function support for handle bubble event
    abstract fun startNotificationForeground()
    abstract fun clearCachedData()
    abstract fun changeBubbleEdgeSideListener(edgeSide: BubbleEdgeSide)
    abstract fun onCheckBubbleTouchLeavesListener(x: Float, y: Float)
    abstract fun onCloseBubbleListener()
    abstract fun refreshBubbleIconStateListener(isClearCachedData: Boolean)


    override fun onBind(p0: Intent?): IBinder? = null

    /**
     * 🎉 Bubble configuration
     */
    override fun onCreate() {
        super.onCreate()
        serviceScope = CoroutineScope(Dispatchers.Main)
        if (canDrawOverlays().not()) {
            throw IllegalStateException(
                "You must enable 'Draw over other apps' permission to use this service."
            )
        } else {
            sez.with(this.applicationContext)
            onCreateBubble(configBubble())
            this._serviceInteraction = object : ServiceInteraction {
                override fun requestStop() {
                    stopSelf()
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        clearCachedData()
        if (newConfig.isScreenRound) {
            sez.refresh()
        }
        onCreateBubble(configBubble())
    }

    override fun onDestroy() {
        hideBubble()
        serviceScope.cancel()
        super.onDestroy()
    }

    /**
     * ✨ onCreateBubble is a function that takes in a bubbleBuilder
     * It creates a bubble, closeBubble, expandBubble and flowKeyboardBubble
     * It adds the bubbleView to the bubble (close, expand, ....)
     */
    private fun onCreateBubble(bubbleBuilder: BuBubbleBuilder?) {
        if (bubbleBuilder != null) {
            if (bubbleBuilder.bubbleComposeView != null || bubbleBuilder.bubbleView != null) {
                assert(bubbleBuilder.bubbleComposeView != null || bubbleBuilder.bubbleView != null) {
                    "You must set bubbleView or bubbleComposeView"
                }
                _bubble = BubbleView(
                    context = this,
                    containCompose = bubbleBuilder.bubbleComposeView != null,
                    listener = bubbleBuilder.listener,
                    forceDragging = bubbleBuilder.forceDragging,
                    startPoint = bubbleBuilder.startPoint,
                )
                if (bubbleBuilder.bubbleComposeView != null) {
                    _bubble?.rootGroup?.addView(bubbleBuilder.bubbleComposeView)
                } else if (bubbleBuilder.bubbleView != null) {
                    _bubble?.rootGroup?.addView(bubbleBuilder.bubbleView)
                }
            }

            if (bubbleBuilder.closeComposeView != null || bubbleBuilder.closeView != null) {
                assert(bubbleBuilder.closeComposeView != null || bubbleBuilder.closeView != null) {
                    "You must set closeBubbleView or closeView"
                }
                _closeBubble = CloseBubbleView(
                    context = this,
                    closeBottomDist = bubbleBuilder.closeBottomDist,
                    distanceToClose = bubbleBuilder.distanceToClose
                )
                if (bubbleBuilder.closeComposeView != null) {
                    _closeBubble?.rootGroup?.addView(bubbleBuilder.closeComposeView)
                } else if (bubbleBuilder.closeView != null) {
                    _closeBubble?.rootGroup?.addView(bubbleBuilder.closeView)
                }
            }
            if (bubbleBuilder.expandBubbleView != null) {
                _expandBubble = ExpandBubbleView(
                    context = this,
                    containCompose = true,
                    dragToClose = bubbleBuilder.expandDragToClose
                )
                _expandBubble?.rootGroup?.addView(bubbleBuilder.expandBubbleView)
            }
            if (bubbleBuilder.flowKeyboardBubbleView != null) {
                _flowKeyboardBubble = FlowKeyboardBubbleView(context = this)
                _flowKeyboardBubble?.rootGroup?.addView(bubbleBuilder.flowKeyboardBubbleView)
            }

            _bubble?.mListener = CustomBubbleListener(
                context = this,
                lBubble = _bubble,
                lCloseBubble = _closeBubble,
                distanceToClose = bubbleBuilder.distanceToClose.toDouble(),
                halfScreen = (sez.fullWidth / 2).toDouble(),
                isAnimatedToEdge = bubbleBuilder.isAnimateToEdgeEnabled,
                isAnimatedClose = bubbleBuilder.animatedClose,
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

    // ✨ onClearAllBubbleData is a function that removes the bubble, closeBubble, expandBubble and flowKeyboardBubble
    private fun onClearAllBubbleData() {
        _bubble?.remove()
        _closeBubble?.remove()
        _expandBubble?.remove()
        _flowKeyboardBubble?.remove()
    }

    // ---------------------------------------------------------------------------------

    /**
     * 🎈 Bubble behavior
     * * This function is used to show the bubble view.
     */
    fun showBubble() {
        if (bubbleState.isBubbleServiceActivated.not() || bubbleState.isBubbleShow) return
        if (_expandBubble?.root?.isShown == true) return
        Log.d("BaseBubbleService", "showBubble: ${_bubble?.isShown()}")
        _bubble?.show()
        _bubble?.updateBubbleStatus(View.VISIBLE)
        _bubbleStateFlow.value = bubbleState.copy(isBubbleShow = true, isBubbleVisible = true)
    }

    private fun hideBubble() {
        if (bubbleState.isShowingFlowKeyboardBubble) return
        _bubble?.remove()
        _closeBubble?.remove()
    }

    // ---------------------------------------------------------------------------------


    /**
     * 🎈 Expand bubble behavior
     * * This function is used to show the expand bubble view.
     */
    fun showExpandBubble(isRemoveBubble: Boolean = false) {
        if (isRemoveBubble) {
            hideBubble()
        }
        _expandBubble?.onOpen()
    }

    fun hideExpandBubble(
        // This value is used to determine whether to show the bubble after closing the expand bubble
        showBubbleAfterClose: Boolean = true
    ) {
        _expandBubble?.onClose(
            closeComplete = {
                if(showBubbleAfterClose.not()) {
                    return@onClose
                }
                _bubble?.show()
                _bubble?.updateBubbleStatus(View.VISIBLE)
            }
        )
    }

    // ---------------------------------------------------------------------------------


    /**
     * 🎈 Flow keyboard bubble behavior
     * * This function is used to show the flow keyboard bubble view.
     */
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

    // ---------------------------------------------------------------------------------


    /**
     * ✨ CustomBubbleListener is a private inner class that implements the BubbleListener interface
     * @Param lBubble is the bubble view
     * @Param lCloseBubble is the close bubble view
     * @Param isAnimatedToEdge is a boolean value that indicates if the bubble is animated to the edge
     * @Param context is the context of the bubble
     * @Param halfScreen is the half screen width
     * @Param distanceToClose is the distance to close the bubble
     */
    private inner class CustomBubbleListener(
        private val context: Context,
        private val lBubble: BubbleView?,
        private val lCloseBubble: CloseBubbleView?,
        private val isAnimatedToEdge: Boolean = true,
        private val isAnimatedClose: Boolean = false,
        private val halfScreen: Double,
        private val distanceToClose: Double,
        private val onCloseBubbleView: () -> Unit,
    ) : BubbleListener {

        private var isMoving = false
        private var fingerStartX = 0f
        private var fingerStartY = 0f

        private fun updateCloseBubblePosition(x: Float, y: Float, isAttracted: Boolean) {
            if(isAnimatedClose.not()) return

            if (lBubble == null || lCloseBubble == null) return

            if(isAttracted) {
                lCloseBubble.updateUiPosition(positionX = x, positionY = y)
                return
            }

            val bubbleDistance = abs(x - halfScreen)
            val offsetX = DistanceCalculator.newDistanceCloseX(
                halfScreenWidth = halfScreen,
                bubbleDistance = bubbleDistance,
                distanceToClose = distanceToClose
            ).toFloat()



            val newX = if (x < halfScreen) {
                (halfScreen - offsetX).toFloat()
            } else {
                (halfScreen + offsetX).toFloat()
            }

            val newY = lCloseBubble.getAnimationPositionY(lBubble, y)

            lCloseBubble.updateUiPosition(positionX = newX, positionY = newY)

            Log.d(
                "BaseBubbleService",
                "updateCloseBubblePosition: $offsetX $x $bubbleDistance $halfScreen $distanceToClose"
            )
        }

        override fun onFingerDown(x: Float, y: Float) {
            fingerStartX = x
            fingerStartY = y
            lBubble?.safeCancelAnimation()
        }

        override fun onFingerMove(x: Float, y: Float) {
            if (lBubble == null || lCloseBubble == null) return

            val isAttracted = _closeBubble?.tryAttractBubble(lBubble, x, y) ?: false

            if (!isMoving && (x != fingerStartX || y != fingerStartY)) {
                isMoving = true
                lCloseBubble.show()
            }

            if (!isAttracted || isAnimatedClose) {
                lBubble.updateUiPosition(x, y) { iX, iY ->
                    updateCloseBubblePosition(iX.toFloat(), iY.toFloat(), isAttracted)
                }
            }
        }

        override fun onFingerUp(x: Float, y: Float) {
            if (lBubble == null || lCloseBubble == null) return

            isMoving = false
            lCloseBubble.resetCloseBubblePosition()
            lCloseBubble.remove()

            if (x == fingerStartX && y == fingerStartY) return

            val shouldClose = lCloseBubble.isBubbleInCloseField(lBubble) &&
                    lCloseBubble.isFingerInCloseField(x, y)

            if (shouldClose) {
                lBubble.setPosition(0, (sez.fullHeight / 2 - 40))
                lBubble.updateByNewPosition()
                onCloseBubbleView()
            } else {
                if (isAnimatedToEdge) {
                    lBubble.snapToEdge { edge ->
                        changeBubbleEdgeSideListener(edge)
                    }
                }
                onCheckBubbleTouchLeavesListener(x, y)
            }
        }
    }
}