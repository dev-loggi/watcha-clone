package com.example.csr83.watchaproject.presentation

import android.view.ViewGroup
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.view.ViewTreeObserver
import android.support.annotation.IdRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window

@SuppressWarnings("Registered")
open class BaseActivity : AppCompatActivity() {
    private val keyboardLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val heightDiff = rootLayout!!.rootView.height - rootLayout!!.height
        val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top

        val broadcastManager = LocalBroadcastManager.getInstance(this@BaseActivity)

        if (heightDiff <= contentViewTop) {
            onHideKeyboard()

            val intent = Intent("KeyboardWillHide")
            broadcastManager.sendBroadcast(intent)
        } else {
            val keyboardHeight = heightDiff - contentViewTop
            onShowKeyboard(keyboardHeight)

            val intent = Intent("KeyboardWillShow")
            intent.putExtra("KeyboardHeight", keyboardHeight)
            broadcastManager.sendBroadcast(intent)
        }
    }

    private var keyboardListenersAttached = false
    private var rootLayout: ViewGroup? = null

    protected fun onShowKeyboard(keyboardHeight: Int) {}
    protected fun onHideKeyboard() {}

    protected fun attachKeyboardListeners(@IdRes idRootLayout: Int) {
        if (keyboardListenersAttached) {
            return
        }

        rootLayout = findViewById(idRootLayout)
        if (rootLayout is ViewGroup) {
            rootLayout!!.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)

            keyboardListenersAttached = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (keyboardListenersAttached) {
            rootLayout!!.viewTreeObserver.removeGlobalOnLayoutListener(keyboardLayoutListener)
            keyboardListenersAttached = false
        }
    }
}