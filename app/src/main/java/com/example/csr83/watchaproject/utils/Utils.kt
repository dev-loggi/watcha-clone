package com.example.csr83.watchaproject.utils

import android.content.Context

class Utils {

    companion object {
        fun getStatusBarHeight(context: Context?): Int {
            if (context != null) {
                val statusBarHeightResourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
                if (statusBarHeightResourceId > 0) {
                    return context.resources.getDimensionPixelSize(statusBarHeightResourceId)
                }
            }
            return 0
        }
    }
}