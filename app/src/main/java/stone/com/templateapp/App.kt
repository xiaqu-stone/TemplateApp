package stone.com.templateapp

import android.app.Activity
import android.app.Application
import android.os.Bundle


class App : Application(), Application.ActivityLifecycleCallbacks {

    private lateinit var curActivity: Activity


    override fun onCreate() {
        super.onCreate()
        app = this
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {
        //打开新的 使得在resume之前即可使用
        curActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
        //回退 栈中resume
        curActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    companion object {

        lateinit var app: App
//        private const val TAG = "App"
    }
}
