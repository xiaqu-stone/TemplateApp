package stone.com.templateapp.extensions

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup

/**
 * Created By: sqq
 * Created Time: 18/1/4 下午8:21.
 */

//ViewExtensions
/**
 * 操作符扩展 使得ViewGroup获取子View时具有数组的操作性
 */
operator fun ViewGroup.get(position: Int): View = getChildAt(position)

/**
 * 扩展View中Context属性名为ctx，使得与Activity、Fragment中的名字保持一致
 */
val View.ctx: Context get() = context

/**
 * 扩展TextView的文案监听器，使得不需要每次都去实现三个方法
 */
interface BaseTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {}
}

