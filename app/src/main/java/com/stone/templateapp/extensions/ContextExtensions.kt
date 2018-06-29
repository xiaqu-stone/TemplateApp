package com.stone.templateapp.extensions

import android.content.Context
import android.os.Build

/**
 * Created By: sqq
 * Created Time: 18/6/6 下午4:35.
 */


fun Context.getCompatColor(colorRes: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.getColor(colorRes)
    } else {
        this.resources.getColor(colorRes)
    }
}

fun Context.dp2px(dp: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (dp * scale + 0.5f * if (dp >= 0) 1 else -1).toInt()
}

fun Context.px2dp(px: Float): Int {
    val scale = this.resources.displayMetrics.density
    return (px / scale + 0.5f * if (px >= 0) 1 else -1).toInt()
}