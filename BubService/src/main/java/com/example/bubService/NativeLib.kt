package com.example.bubService

class NativeLib {

    /**
     * A native method that is implemented by the 'bubService' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'bubService' library on application startup.
        init {
            System.loadLibrary("bubService")
        }
    }
}