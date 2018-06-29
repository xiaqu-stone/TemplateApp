package com.stone.templateapp.util

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
import com.stone.log.Logs
import com.stone.templateapp.R
import com.stone.templateapp.util.EDcryptUtils.toMD5
import org.jetbrains.anko.toast
import java.util.*

@Suppress("unused", "MemberVisibilityCanBePrivate")
object AppUtils {

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
     * 退出当前应用的进程
     */
    fun exitProcess() {
//        Process.killProcess(Process.myPid())
        //二选一即可
        System.exit(0)
    }

    private var exitTime: Long = 0L

    fun toastExitProcess(ctx: Context) {
        val curTime = System.currentTimeMillis()
        if (curTime - exitTime > 2000) {
            ctx.toast(R.string.app_exit_tips)
            exitTime = curTime
        } else {
            //todo MD Dialog展示debug处理
//            if (BuildConfig.DEBUG) {
//                DialogMD(activity, R.string.exit_or_change_host)
//                        .setCancelable(false)
//                        .setBtnPositive(R.string.btn_exit)
//                        .setBtnNegative(R.string.btn_change_host)
//                        .setBtnNeutral("取消")
//                        .setListener { dialog, which ->
//                            when (which) {
//                                -1 -> App.app.finishAll()
//                                -2 -> intent(TestApiActivity::class.java)
//                                -3 -> dialog.dismiss()
//                            }
//                        }.show()
//
//            } else {
            exitProcess()
//            }
        }
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
     * @param applicationId 要判断应用的 ApplicationId，build.gradle中定义的APPLICATION_ID
     * @return Boolean true：进程活着并且应用的任务栈不为空； false：进程死亡或者应用的任务栈为空
     */
    fun isAppAlive(ctx: Context, applicationId: String): Boolean {
        val activityManager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps = activityManager.runningAppProcesses ?: return false
        for (i in runningApps.indices) {
            Logs.d(TAG, "isAppAlive: i = $i , processName = ${runningApps[i].processName}")
            if (runningApps[i].processName == applicationId) { //此处processName进程名以applicationId命名，与包路径的包名无关
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val appTasks = activityManager.appTasks
                    for (task in appTasks) {
                        //直接获取的当前进程的活动栈，无需判断包名
                        if (task.taskInfo.numActivities > 0) {
                            Logs.i(TAG, "isAppAlive: the $applicationId is running, isAppAlive return true. the num of activities is ${task.taskInfo.numActivities}")
                            return true
                        }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    val runningTasks = activityManager.getRunningTasks(10)
                    for (taskInfo in runningTasks) {
                        //task.taskInfo.baseActivity.packageName 与 applicaitonId等同
                        //task.taskInfo.baseActivity.className 全限定了类名，前缀不一定是applicationId
                        //获取当前手机的所有活动栈，需要判断当前应用的包名
                        if (taskInfo.baseActivity.packageName == applicationId && taskInfo.numActivities > 0) {
                            Logs.i(TAG, "isAppAlive: the $applicationId is running, isAppAlive return true. the num of activities is ${taskInfo.numActivities}")
                            return true
                        }
                    }
                }

            }
        }
        Logs.i(TAG, "isAppAlive: the $applicationId is not running, isAppAlive return false.")
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

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission", "HardwareIds")
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


}
