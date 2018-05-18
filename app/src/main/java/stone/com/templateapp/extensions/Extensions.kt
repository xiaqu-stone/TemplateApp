package stone.com.templateapp.extensions

import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import stone.com.templateapp.App

/**
 * Created By: sqq
 * Created Time: 18/5/18 下午6:52.
 */
fun Any.toast(msg: CharSequence) {
    App.getApp().toast(msg)
}

fun Any.toast(msg: Int) {
    App.getApp().toast(msg)
}

fun Any.longToast(msg: CharSequence) {
    App.getApp().longToast(msg)
}

fun Any.longToast(msg: Int) {
    App.getApp().longToast(msg)
}