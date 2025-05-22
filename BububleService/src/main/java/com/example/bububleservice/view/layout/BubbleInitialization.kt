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

///🙄 Example:
///```kotlin
/// Class BubbleView(
///     context: Context,
///     root: ViewGroup? = null,
///     containCompose: Boolean = false
/// ) : BubbleInitialization(
//          context = context,
//          root = BubbleLayout(context),
//          containCompose = containCompose
// ) {
///```
open class BubbleInitialization(
    context: Context,
    root: ViewGroup? = null,
    private val containCompose: Boolean = false
) {
    // WindowManager is a class that is responsible for managing the window.
    private var _windowManager: WindowManager? = null

    // WindowManager.LayoutParams is a class that is responsible for managing the layout parameters of the window.
    private var _rootParams: WindowManager.LayoutParams? = null

    // It is used to store the window manager.
    private val windowManager: WindowManager
        get() = _windowManager ?: throw IllegalStateException("WindowManager is not initialized")

    // ComposeLifeCycleOwner is a class that is responsible for managing the lifecycle of the compose view.
    private var composeOwner: ComposeLifeCycleOwner? = null

    /// _root is a private variable of type ViewGroup. It is used to store the root view group.
    private var _root: ViewGroup? = null
    /// isComposeOwnerInitialized is a private variable of type Boolean. It is used to determine if the compose owner is initialized.
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

    /// init is a block of code that is executed when the BubbleInitialization class is initialized.
    init {
        /// getWindowService is a method that is used to get the window service.
        _windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        _rootParams = WindowManager.LayoutParams()
        _root = root

        /// If the root view group is not null and the containCompose is true, then the composeOwner is initialized.
        if(containCompose) {
            composeOwner = ComposeLifeCycleOwner()
            composeOwner?.attachToDecorView(_root)
        }
    }

    /// show is a method that is used to show the bubble view.
    fun show(): Boolean {
        /// If the root view group is null or the window token is not null, then return false.
        if (root?.windowToken != null) return false
        try {
            ///✨ Handling if containCompose is true
            if (containCompose) {
                if (isComposeOwnerInitialized.not()) {
                    composeOwner?.onCreate()
                    isComposeOwnerInitialized = true
                }
                composeOwner?.onStart()
                composeOwner?.onResume()
            }
            /// Adding the root view group to the window manager.
            _windowManager?.addView(root, _rootParams)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /// remove is a method that is used to remove the bubble view.
    fun remove(): Boolean{
        /// If the root view group is null or the window token is null, then return false.
        if(root?.windowToken == null) return false
        try {
            /// Removing the root view group from the window manager.
            windowManager.removeView(root)
            ///✨ Handling if containCompose is true
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

    /// update is a method that is used to update the bubble view.
    fun update() {
        /// If the root view group is null or the window token is null, then return.
        if(root?.windowToken == null) return
        try {
            /// Updating the root view group.
            windowManager.updateViewLayout(root, _rootParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /// resizeView is a method that is used to resize the bubble view.
    fun resizeView(newWidth: Float, newHeight: Float) {
        /// If the root view group is null or the window token is null, then return.
        if(root?.windowToken == null) return
        try {
            /// Resizing the root view group.
            /// ChildCount is used to get the number of children in the root view group.
            val childCount: Int = root?.childCount ?: 0
            /// Looping through the children of the root view group.
            for(i in 0 until childCount) {
                val v: View? = _root?.getChildAt(i)
                if(v != null) {
                    v.layoutParams?.width = newWidth.toInt()
                    v.layoutParams?.height = newHeight.toInt()
                }
            }
            /// Updating the root view group.
            windowManager.updateViewLayout(root, _rootParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /// fun blurView is a method that is used to blur the bubble view.
    fun blurView(percent: Float) {
        _root?.alpha = percent
    }
}