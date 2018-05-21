package com.stone.templateapp.module.web

import android.view.View
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
import com.stone.templateapp.extensions.setTitle
import com.stone.templateapp.util.Logs

/**
 * Created By: sqq
 * Created Time: 17/12/7 下午6:43.
 */

class MyChromeClient(
        private val mProgress: View? = null
) : WebChromeClient() {
    override fun onReceivedTitle(view: WebView?, title: String?) {
        super.onReceivedTitle(view, title)
        view?.setTitle(title)
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        Logs.d(TAG, newProgress)
//        if (view.visibility != View.VISIBLE && newProgress == 100) {
//            view.visibility = View.VISIBLE
//        }
        if (mProgress != null) {
            when (mProgress) {
                is ProgressBar -> {
                    mProgress.progress = newProgress
                }
                else -> {
//                    do nothing
                }
            }
        }
    }


    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        return super.onConsoleMessage(consoleMessage)

    }

    companion object {
        const val TAG = "MyChromeClient"
    }
}
