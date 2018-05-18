/**
 *
 */
package stone.com.templateapp.util

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import android.text.TextUtils
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

object AppUtil {

    private const val TAG = "AppUtil"

    /**
     * 程序是否在前台运行
     */
    fun isAppOnForeground(context: Context): Boolean {
        val activityManager = context
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packageName = context.packageName

        val runningApps = activityManager
                .runningAppProcesses ?: return false

        for (app in runningApps) {
            if (app.processName == packageName && app.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true
            }
        }
        return false
    }

    /**
     * 获取当前进程名称
     *
     * @param context
     * @param pid
     * @return
     */
    fun getProcessName(context: Context, pid: Int): String? {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (app in runningApps) {
            if (app.pid == pid) {
                return app.processName
            }
        }
        return null
    }

    /**
     * 判断应用是否处于启动状态
     *
     * @param ctx         Content
     * @param packageName 要判断应用的 ApplicationId
     * @return Boolean true：进程活着并且应用的任务栈不为空； false：进程死亡或者应用的任务栈为空
     */
    fun isAppAlive(ctx: Context, packageName: String): Boolean {
        val activityManager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = activityManager.runningAppProcesses ?: return false
        for (i in runningApps.indices) {
            Logs.d(TAG, "isAppAlive: i = $i , processName = ${runningApps[i].processName}")
            if (runningApps[i].processName == packageName) {
                val runningTasks = activityManager.getRunningTasks(10)
                for (j in runningTasks.indices) {
                    val taskInfo = runningTasks[j]
//                    Logs.i(TAG, "isAppAlive: i = " + j + ", taskInfo.baseActivity = " + taskInfo.baseActivity.className + ", taskInfo.topActivity = " + taskInfo.topActivity.className)
//                    Logs.i(TAG, """isAppAlive: i = $j, taskInfo.numActivities = ${taskInfo.numActivities}, taskInfo.numRunning = ${taskInfo.numRunning}, taskInfo.description = ${taskInfo.description}, taskInfo.describeContents() = ${taskInfo.describeContents()}""")
                    //在此处判断全限定类名时，应以包名为基准（当applicationId与packageName不一致时）
                    if (taskInfo.baseActivity.className.startsWith(packageName) && taskInfo.numActivities > 0) {
                        Logs.i(TAG, "isAppAlive: the $packageName is running, isAppAlive return true. the num of activities is ${taskInfo.numActivities}")
                        return true
                    }
                }
            }
        }
        Logs.i(TAG, "isAppAlive: the $packageName is not running, isAppAlive return false.")
        return false
    }

    /**
     * @param name 渠道 key
     * @return 获取渠道信息
     */
    fun getMetaDataValue(name: String, ctx: Context): String {
        var value: String? = null
        val packageManager = ctx.packageManager
        val applicationInfo: ApplicationInfo?
        try {
            applicationInfo = packageManager.getApplicationInfo(ctx
                    .packageName, PackageManager.GET_META_DATA)
            if (applicationInfo?.metaData != null) {
                value = applicationInfo.metaData.getString(name)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException("Could not read the name in the manifest file.", e)
        }

        if (value == null) {
            throw RuntimeException("The name '" + name
                    + "' is not defined in the manifest file's meta data.")
        }
        return value
    }

    @SuppressLint("MissingPermission")
    fun getIMEI(ctx: Context): String {
        var imei = SPUtils[ctx, SPUtils.IMEI, ""].toString()
        if (TextUtils.isEmpty(imei)) {
            val manager = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            try {
                imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    manager.imei
                } else {
                    manager.deviceId
                }
                Logs.w(TAG, "getIMEI: the imei is : $imei")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (!TextUtils.isEmpty(imei)) {
                SPUtils.put(ctx, SPUtils.IMEI, imei)
            }
        }
        return imei
    }

    @SuppressLint("MissingPermission", "HardwareIds")
            /**
             * 获取设备的唯一码
             *
             * @return 获取设备的唯一码
             */
    fun getSystemUUID(ctx: Context, isOnlyAndroidId: Boolean): String {
        var uuid = SPUtils[ctx, SPUtils.UUID, ""].toString()
        if (TextUtils.isEmpty(uuid)) {
            var imei = ""
            var tmSerial = ""
            val androidId: String = "" + android.provider.Settings.Secure.getString(ctx.contentResolver, android.provider.Settings.Secure.ANDROID_ID)
            if (!isOnlyAndroidId) {
                // 获取 uuid
                val tm = (ctx as ContextWrapper).baseContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                imei = getIMEI(ctx)
                // SimSerialNumber
                try {
                    tmSerial = "" + tm.simSerialNumber
                    Logs.w(TAG, "getSystemUUID: tmSerial = $tmSerial")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            Logs.w(TAG, "getSystemUUID: androidId = $androidId")
            val deviceUuid = UUID(androidId.hashCode().toLong(), imei.hashCode().toLong() shl 32 or tmSerial.hashCode().toLong())

            uuid = toMD5(deviceUuid.toString())
            // Save data
            SPUtils.put(ctx, SPUtils.UUID, uuid)
            Logs.w(TAG, "getSystemUUID: uuid = $uuid")
        }
        return uuid
    }

    /**
     * @param string
     * @return MD5加密串
     */
    fun toMD5(string: String): String {
        val hash: ByteArray
        try {
            hash = MessageDigest.getInstance("MD5").digest(
                    string.toByteArray(charset("UTF-8")))
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Huh, MD5 should be supported?", e)
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException("Huh, UTF-8 should be supported?", e)
        }
        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            if ((b.toInt() and 0xFF) < 0x10)
                hex.append("0")
            hex.append(Integer.toHexString(b.toInt() and 0xFF))
        }
        return hex.toString()
    }
}
