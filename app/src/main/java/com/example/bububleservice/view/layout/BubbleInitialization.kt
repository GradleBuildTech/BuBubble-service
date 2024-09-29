package com.example.bububleservice.view.layout

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.example.bububleservice.lifrCycle.ComposeLifeCycleOwner

/// BubbleInitialization is a class that is used to initialize the bubble view.
/// It is used to set the context and the root view group.
/// It also contains a boolean value to determine if the bubble view should contain a Compose view.
/// This class is open so that it can be extended by other classes.

///Example:
///```kotlin
///```
open class BubbleInitialization(
    context: Context,
    root: ViewGroup? = null,
    private val containCompose: Boolean = false
) {
    private var _windowManager: WindowManager? = null
    private var _rootParams: WindowManager.LayoutParams? = null

    private val windowManager: WindowManager
        get() = _windowManager ?: throw IllegalStateException("WindowManager is not initialized")

    private var composeOwner: ComposeLifeCycleOwner? = null

    private var _root: ViewGroup? = null
    private var isComposeOwnerInitialized: Boolean = false
    val rootGroup get() = root

    var root
        get() = _root
        set(value) {
            _root = value
        }

    var layoutParams
        get() = _rootParams
        set(value) {
            _rootParams = value
        }

    init {
        _windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        _rootParams = WindowManager.LayoutParams()
        _root = root
        if(containCompose) {
            composeOwner = ComposeLifeCycleOwner()
            composeOwner?.attachToDecorView(_root)
        }
    }

    fun show(): Boolean {
        if (root?.windowToken != null) return false
        try {
            if (containCompose) {
                if (isComposeOwnerInitialized.not()) {
                    composeOwner?.onCreate()
                    isComposeOwnerInitialized = true
                }
                composeOwner?.onStart()
                composeOwner?.onResume()
            }
            _windowManager?.addView(root, _rootParams)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun remove(): Boolean{
        if(root?.windowToken == null) return false
        try {
            windowManager.removeView(root)
            if(containCompose) {
                composeOwner?.onPause()
                composeOwner?.onStop()
                composeOwner?.onDestroy()
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun update() {
        if(root?.windowToken == null) return
        try {
            windowManager.updateViewLayout(root, _rootParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resizeView(newWidth: Float, newHeight: Float) {
        if(root?.windowToken == null) return
        try {
            val childCount: Int = root?.childCount ?: 0
            for(i in 0 until childCount) {
                val v: View? = _root?.getChildAt(i)
                if(v != null) {
                    v.layoutParams?.width = newWidth.toInt()
                    v.layoutParams?.height = newHeight.toInt()
                }
            }
            windowManager.updateViewLayout(root, _rootParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun blurView(percent: Float) {
        _root?.alpha = percent
    }
}