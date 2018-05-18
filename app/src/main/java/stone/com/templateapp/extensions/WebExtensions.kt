package stone.com.templateapp.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import stone.com.templateapp.R
import stone.com.templateapp.module.web.MyDownListener
import stone.com.templateapp.util.Logs

/**
 * 关于WebView及其附属类的一些函数扩展
 */

/**
 * 使用此项需要注意的是，WebView依赖的上下文需要是Activity Context，而非Application Context
 */
internal val WebView.activity: Activity
    get() = ctx as Activity


/**
 * 根据项目需要，配置WebView
 */
@SuppressLint("SetJavaScriptEnabled")
internal fun WebView.customSetting() {
    settings.javaScriptEnabled = true
    settings.defaultTextEncodingName = "UTF-8"
    settings.domStorageEnabled = true
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
    }
    Logs.e("WebActivity", "initWebView: " + settings.userAgentString)
    //设置下载监听
    setDownloadListener(MyDownListener(activity))
}

/**
 * loadURL之前，针对URL做特定过滤处理
 */
internal fun WebView.loadUrlWithCheck(url: String?) {
    if (url.isNullOrEmpty()) return
    else loadUrl(url)
}

/**
 * WebViewClient.shouldOverrideUrlLoading自定义逻辑处理
 * @return true: 使用自定义逻辑处理了当前的URL，阻拦WebView默认逻辑的执行；false：可自定义逻辑，但是不阻拦WebView的默认逻辑执行
 */
internal fun WebView.overrideUrlLoading(url: String?): Boolean {
    if (url == null) return true
    if (url.startsWith("tel:")) {//支持网页拨号
        if (activity.isValid()) {
//            todo
//            DialogMD(activity, "是否拨打电话：\n$url")
//                    .setDefaultBtnNegative()
//                    .setPositiveListener { _, _ ->
//                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
//                        activity.startActivity(intent)
//                    }.show()
            return true
        }
    } else if (url.startsWith("weixin://") || url.startsWith("intent://")) {
        try {
            val resultUrl: String = if (url.startsWith("intent://") && url.contains("com.tencent.mm")) {
                url.replace("intent://", "weixin://")
            } else {
                url
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(resultUrl))
            if (activity.isValid()) {
                activity.startActivity(intent)
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    return false
}

/**
 * 将WebActivity设置标题的方法，扩展到WebView中，避免ChromeClient的receiveTitle回调中直接引用WebActivity的方法
 */
internal fun WebView.setTitle(title: String?) {
    if (title.isNullOrEmpty() || title!!.contains("http") || title.contains("daiqianb")) {
        activity.setTitle(R.string.app_name)
    } else if (title == "找不到网页" || title == "网页无法打开") {
        activity.title = "网络连接失败"
    } else {
        activity.title = title
    }
    //设置title
    Logs.i("WebActivity", "setTitle() called with: title = [$title]")
    Logs.i("WebActivity", "setTitle: webView.getOriginalUrl() = " + this?.originalUrl)
}

/**
 * 销毁WebView
 */
internal fun WebView?.selfDestroy() {
    if (this == null) return
    try {
        settings.builtInZoomControls = true
        visibility = View.GONE
    } catch (ignored: Throwable) {

    }

    try {
        stopLoading()
    } catch (ignored: Throwable) {

    }

    try {
        removeAllViews()
    } catch (ignored: Throwable) {

    }

    try {
        webChromeClient = null
    } catch (ignored: Throwable) {

    }

    try {
        webViewClient = null
    } catch (ignored: Throwable) {

    }

    try {
        destroy()
    } catch (ignored: Throwable) {

    }

    try {
        if (null != parent && parent is ViewGroup) {
            (parent as ViewGroup).removeView(this)
        }
    } catch (ignored: Throwable) {

    }
}