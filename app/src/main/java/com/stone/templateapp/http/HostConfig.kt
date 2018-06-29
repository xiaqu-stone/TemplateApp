package com.stone.templateapp.http

import android.text.TextUtils
import com.stone.templateapp.BuildConfig
import com.stone.templateapp.util.SPUtils

object HostConfig {

    lateinit var API_HOST: String
    lateinit var STATIC_HOST: String

    init {
        init()
    }

    fun init() {
        initApiHost()
        initWebHost()
    }

    fun initApiHost() {
        API_HOST = if (BuildConfig.DEBUG) {
            val configApiHost = SPUtils[SPUtils.API_HOST, ""] as String
            if (TextUtils.isEmpty(configApiHost)) {
                BuildConfig.API_HOST
            } else {
                configApiHost
            }
        } else {
            BuildConfig.API_HOST
        }
    }

    fun initWebHost() {
        STATIC_HOST = if (BuildConfig.DEBUG) {
            val configWebHost = SPUtils[SPUtils.H5_HOST, ""] as String
            if (TextUtils.isEmpty(configWebHost)) {
                BuildConfig.H5_HOST
            } else {
                configWebHost!!
            }
        } else {
            BuildConfig.H5_HOST
        }
    }
}
