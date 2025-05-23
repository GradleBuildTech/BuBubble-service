package com.example.bububleservice.lifrCycle

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

///✨=====================================================================
///✨ ComposeLifeCycleOwner
/// Explanation:
/// This class is responsible for managing the lifecycle of the compose view.

internal class ComposeLifeCycleOwner: SavedStateRegistryOwner, ViewModelStoreOwner {
    /// _view is a private variable of type View. It is used to store the view.
    private var _view: View? = null
    /// ReComposer is a private variable of type ReComposer.
    // Recompose is a class that is responsible for managing the composition of the view.
    private var recompose: Recomposer? = null

    /// runRecomposeScope is a private variable of type CoroutineScope.
    // CoroutineScope is a class that is responsible for managing the coroutine.
    private var runRecomposeScope: CoroutineScope? = null

    /// coroutineContext is a private variable of type CoroutineContext.
    // CoroutineContext is a class that is responsible for managing the coroutine context.
    private var coroutineContext: CoroutineContext? = null


    init {
        /// coroutineContext is initialized with the current thread.
        coroutineContext = AndroidUiDispatcher.CurrentThread
    }

    fun onCreate() {
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        runRecomposeScope?.cancel()

        runRecomposeScope = CoroutineScope(coroutineContext!!)
        recompose = Recomposer(coroutineContext!!)
        _view?.compositionContext = recompose

        runRecomposeScope?.launch {
            recompose!!.runRecomposeAndApplyChanges()
        }


    }

    fun onStart() {
        try {
            /// The lifecycleRegistry is used to handle the lifecycle event ON_START.
            /// The ON_START event is triggered when the view is started.
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onResume() {
        /// The lifecycleRegistry is used to handle the lifecycle event ON_RESUME.
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    fun onPause() {
        /// The lifecycleRegistry is used to handle the lifecycle event ON_PAUSE.
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    }

    fun onStop() {
        /// The lifecycleRegistry is used to handle the lifecycle event ON_STOP.
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)

    }

    fun onDestroy() {
        /// The lifecycleRegistry is used to handle the lifecycle event ON_DESTROY.
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        /// The savedStateRegistryController is used to perform save operation.
        /// performSave is used to save the state of the view.
        savedStateRegistryController.performSave(Bundle())
    }

    /// onCreate is a function that is used to create the view.

    fun attachToDecorView(view: View?) {
        if (view == null) return

        _view = view

        /// setViewTreeLifecycleOwner is used to set the view as the lifecycle owner.
        view.setViewTreeLifecycleOwner(this)
        /// setViewTreeViewModelStoreOwner is used to set the view model store owner.
        view.setViewTreeViewModelStoreOwner(this)
        /// setViewTreeSavedStateRegistryOwner is used to set the saved state registry owner.
        view.setViewTreeSavedStateRegistryOwner(this)

    }

    ///LifeCycleRegistry is a class that is responsible for managing the lifecycle of the view.
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    /// return the lifecycle of the view.
    /// The lifecycle is used to manage the lifecycle of the view.
    override val lifecycle: Lifecycle get() = lifecycleRegistry

    ///SavedStateRegistryController is a class that is responsible for managing the saved state registry of the view.
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    /// return the saved state registry of the view.
    /// saveStateRegistry is used to manage the saved state registry of the view.
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry


    ///ViewModelStore is a class that is responsible for managing the view model store of the view.
    private val store = ViewModelStore()
    override val viewModelStore: ViewModelStore get() = store

}