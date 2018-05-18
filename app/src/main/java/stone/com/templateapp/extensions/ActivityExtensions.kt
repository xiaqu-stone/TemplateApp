package stone.com.templateapp.extensions

import android.app.Activity
import android.os.Build

/**
 * 关于Activity的扩展函数
 */

/**
 * 验证当前Activity是否有效可用
 * @return true: 可用
 */
internal fun Activity?.isValid(): Boolean {
    return if (this != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            !isDestroyed
        } else {
            !isFinishing
        }
    } else false
}

