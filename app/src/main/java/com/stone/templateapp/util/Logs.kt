package com.stone.templateapp.util

import android.text.TextUtils
import android.util.Log

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import com.stone.templateapp.BuildConfig

/**
 * Created by sqq on 17/6/1
 */
class Logs private constructor() {


    init {
        /* cannot be instantiated */
        throw UnsupportedOperationException("cannot be instantiated")
    }

    companion object {
        /**
         * 是否输出log
         */
        val isDebug = BuildConfig.DEBUG
        /**
         * json格式化显示的缩进字符数
         */
        private const val JSON_INDENT = 4
        /**
         * 应用名标签（用以本应用Log打印的默认TAG）
         */
        private const val TAG = "[rongyilai]"
        /**
         * 行分隔符
         */
        private val LINE_SEPARATOR = System.getProperty("line.separator")

        private const val VERBOSE = 1
        private const val DEBUG = 2
        private const val INFO = 3
        private const val WARN = 4
        private const val ERROR = 5
        private const val MAX_SINGLE_LOG_LENGTH = 3 * 1024//单条log系统默认最大输出限制，超出会自动截掉

        fun i(str: Any) {
            i("", str)
        }

        fun d(str: Any) {
            d("", str)
        }

        fun v(str: Any) {
            v("", str)
        }

        fun w(str: Any) {
            w("", str)
        }

        fun e(str: Any) {
            e("", str)
        }

        fun i(tag: String, str: Any) {
            var tag = tag
            if (isDebug) {
                val name = functionName
                tag = getTag(tag)
                if (name != null) {
                    log(INFO, tag, "$name - $str")
                } else {
                    log(INFO, tag, str.toString())
                }
            }

        }

        fun d(tag: String, str: Any) {
            var tag = tag
            if (isDebug) {
                val name = functionName
                tag = getTag(tag)
                if (name != null) {
                    log(DEBUG, tag, "$name - $str")
                } else {
                    log(DEBUG, tag, str.toString())
                }
            }
        }

        private fun getTag(tag: String): String {
            return tag + TAG
        }

        fun v(tag: String, str: Any) {
            var tag = tag
            if (isDebug) {
                val name = functionName
                tag = getTag(tag)
                if (name != null) {
                    log(VERBOSE, tag, "$name - $str")
                } else {
                    log(VERBOSE, tag, str.toString())
                }
            }
        }

        fun w(tag: String, str: Any) {
            var tag = tag
            if (isDebug) {
                val name = functionName
                tag = getTag(tag)
                if (name != null) {
                    log(WARN, tag, "$name - $str")
                } else {
                    log(WARN, tag, str.toString())
                }
            }
        }

        fun e(tag: String, str: Any) {
            var tag = tag
            if (isDebug) {
                val name = functionName
                tag = getTag(tag)
                if (name != null) {
                    log(ERROR, tag, "$name - $str")
                } else {
                    log(ERROR, tag, str.toString())
                }
            }
        }

        fun e(ex: Exception) {
            if (isDebug) {
                Log.e(TAG, "error", ex)
            }
        }

        fun e(log: String, tr: Throwable) {
            if (isDebug) {
                val line = functionName
                Log.e(TAG, "{Thread:" + Thread.currentThread().name + "}"
                        + "[" + line + ":] " + log + "\n", tr)
            }
        }

        fun json(json: Any) {
            json(TAG, json, null)
        }

        fun json(json: Any, url: String) {
            json(TAG, json, url)
        }

        private fun log(level: Int, tag: String, msg: Any?) {
            if (msg != null) {
                val length = msg.toString().length
                if (length > MAX_SINGLE_LOG_LENGTH) {
                    var start = 0
                    var end = MAX_SINGLE_LOG_LENGTH
                    val count = length / MAX_SINGLE_LOG_LENGTH
                    for (i in 0..count) {
                        if (length > end) {
                            showLog(level, tag, msg.toString().substring(start, end))
                            start = end
                            end += MAX_SINGLE_LOG_LENGTH
                        } else {
                            showLog(level, tag, msg.toString().substring(start, length))
                            break
                        }
                    }
                } else {
                    showLog(level, tag, msg)
                }
            }

        }

        private fun showLog(level: Int, tag: String, msg: Any) {
            when (level) {
                VERBOSE -> Log.v(tag, msg.toString())
                DEBUG -> Log.d(tag, msg.toString())
                INFO -> Log.i(tag, msg.toString())
                WARN -> Log.w(tag, msg.toString())
                ERROR -> Log.e(tag, msg.toString())
                else -> Log.d(tag, msg.toString())
            }
        }

        /**
         * 格式化输出json
         *
         * @param tag
         * @param json
         */
        fun json(tag: String, json: Any, url: String?) {
            if (isDebug) {
                var name = functionName
                if (name == null) name = ""
                Log.d(tag, "╔═══════════════════════════════════════════════════════════════════════════════════════")
                if (!TextUtils.isEmpty(url))
                    Log.w(tag, "║ onResponseSuccess URL：" + url!!)
                printJson(tag, name, json.toString())
                Log.d(tag, "╚═══════════════════════════════════════════════════════════════════════════════════════")
            }
        }

        /**
         * 格式化json
         */
        private fun printJson(tag: String, name: String, json: String) {
            var message: String
            message = try {
                when {
                    json.startsWith("{") -> {
                        val jsonObject = JSONObject(json)
                        jsonObject.toString(JSON_INDENT)
                    }
                    json.startsWith("[") -> {
                        val jsonArray = JSONArray(json)
                        jsonArray.toString(JSON_INDENT)
                    }
                    else -> json
                }
            } catch (e: JSONException) {
                json
            }

            message = name + LINE_SEPARATOR + message
            val lines = message.split(LINE_SEPARATOR.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (line in lines) {
                log(DEBUG, tag, "║ $line")
            }
        }


        private val functionName: String?
            get() = getFunctionName(false)

        /**
         * 获取当前线程、类、方法、行
         */
        private fun getFunctionName(isThreadBreak: Boolean): String? {
            val sts = Thread.currentThread().stackTrace ?: return null
            for (st in sts) {
                if (st.isNativeMethod) {
                    continue
                }
                if (st.className == Thread::class.java.name) {//java.lang.Thread
                    continue
                }
                if (st.className == Logs::class.java.name || st.className == "${Logs::class.java.name}\$Companion") {
                    continue
                }
                return if (isThreadBreak) {
                    ("[ " + Thread.currentThread().name + ": \n("
                            + st.fileName + ":" + st.lineNumber + ") "
                            + st.methodName + " ]")
                    // fileName=Logs.kt
                } else {
                    ("[ " + Thread.currentThread().name + ": ("
                            + st.fileName + ":" + st.lineNumber + ") "
                            + st.methodName + " ]")
                }
            }
            return null
        }
    }
}