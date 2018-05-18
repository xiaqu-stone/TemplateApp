@file:Suppress("unused")

package stone.com.templateapp.util

import android.content.Context
import android.content.SharedPreferences
import stone.com.templateapp.App
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * SP工具类
 */
object SPUtils {

    val FILE_NAME = AppUtil.toMD5("loan_share_data")

    val TOKEN = AppUtil.toMD5("TOKEN")
    val UUID = AppUtil.toMD5("UUID")
    val IMEI = AppUtil.toMD5("IMEI")

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */
    fun put(context: Context, key: String, value: Any) {

        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)
        val editor = sp.edit()

        when (value) {
            is String -> editor.putString(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key, value)
            is Long -> editor.putLong(key, value)
            else -> editor.putString(key, value.toString())
        }

        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    operator fun get(context: Context, key: String, defaultValue: Any): Any {
        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)

        return when (defaultValue) {
            is String -> sp.getString(key, defaultValue)
            is Int -> sp.getInt(key, defaultValue)
            is Boolean -> sp.getBoolean(key, defaultValue)
            is Float -> sp.getFloat(key, defaultValue)
            is Long -> sp.getLong(key, defaultValue)
            else -> ""
        }
    }

    fun put(key: String, value: Any) {
        put(App.app, key, value)
    }

    operator fun get(key: String, defaultValue: Any): Any {
        return get(App.app, key, defaultValue)
    }

    fun remove(key: String) {
        remove(App.app, key)
    }

    /**
     * 移除某个key值已经对应的值
     */
    fun remove(context: Context, key: String) {
        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.remove(key)
        SharedPreferencesCompat.apply(editor)
    }


    /**
     * 清除所有数据
     */
    fun clear(context: Context) {
        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()
        SharedPreferencesCompat.apply(editor)
    }

    /**
     * 查询某个key是否已经存在
     */
    fun contains(context: Context, key: String): Boolean {
        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)
        return sp.contains(key)
    }

    /**
     * 返回所有的键值对
     */
    fun getAll(context: Context): Map<String, *> {
        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)
        return sp.all
    }

    /**
     * 清除用户数据
     */
    fun clearUserData(context: Context) {
//        App.app.setToken("")
//        val sp = context.getSharedPreferences(FILE_NAME,
//                Context.MODE_PRIVATE)
//        val editor = sp.edit()
//        editor.remove(SPUtils.TOKEN)
        //        editor.remove(SPUtils.USER_UID);
        //        editor.remove(SPUtils.USER_HEAD_URL);
        //        editor.remove(SPUtils.LOCK_KEY);
        //        editor.remove(SPUtils.MONEY_SHOW);
//        SharedPreferencesCompat.apply(editor)
//        WebUtils.removeCookie(context)
    }


    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private object SharedPreferencesCompat {
        private val sApplyMethod = findApplyMethod()

        /**
         * 反射查找apply的方法
         */
        private fun findApplyMethod(): Method? {
            try {
                val clz = SharedPreferences.Editor::class.java
                return clz.getMethod("apply")
            } catch (e: NoSuchMethodException) {
            }

            return null
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         */
        internal fun apply(editor: SharedPreferences.Editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor)
                    return
                }
            } catch (e: IllegalArgumentException) {
            } catch (e: IllegalAccessException) {
            } catch (e: InvocationTargetException) {
            }

            editor.commit()
        }
    }
}
