package com.stone.templateapp.http

import android.os.Build
import com.stone.okhttp3.logging.HttpLoggingInterceptor
import com.stone.templateapp.App
import com.stone.templateapp.BuildConfig
import com.stone.templateapp.http.download.DownloadProgressInterceptor
import com.stone.templateapp.http.download.DownloadProgressListener
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

object ApiHelper {
    private const val HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024
    private const val CONTENT_TYPE = "application/json"
    private const val USER_AGENT = "android"
    const val TIME_OUT_NORMAL = 30 * 1000
    const val TIME_OUT_START_PAGE = 5 * 1000

    private val REWRITE_CACHE_CONTROL_INTERCEPTOR = Interceptor { chain ->
        val originalResponse = chain.proceed(chain.request())
        originalResponse.newBuilder()
                .header("Cache-Control", String.format(Locale.CHINA, "max-age=%d, only-if-cached, max-stale=%d", 1200, 0))
                .build()
    }


    const val TAG = "ApiHelper"

    /**
     * APP接口请求的Client
     */
    fun createOkHttpClient(timeout: Long): OkHttpClient {
        val sslSocketFactory = HttpsUtils.getSslSocketFactory(null, null, null)
        val okHttpBuilder = OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslSocketFactory.sSLSocketFactory, sslSocketFactory.trustManager)
                .retryOnConnectionFailure(false)
//        okHttpBuilder.addInterceptor { chain ->
//
//            val original = chain.request()
////            val request = original.newBuilder()
////                    .header("Content-Type", CONTENT_TYPE)
////                    .header("User-Agent", USER_AGENT)
////                    .header("X-UDID", App.app.uuid)
////                    .header("X-ACCESS-TOKEN", token)
////                    .header("X-CHANNEL", App.app.appID)
////                    .header("X-APP-ID", App.app.appID)
////                    .header("X-USER-PHONE-BRAND", Build.MODEL)
////                    .addHeader("Cache-Control", String.format(Locale.CHINA, "max-age=%d, no-cache, max-stale=%d", 10, 0)) // 全局10秒缓存
////                    .build()
//            val response = chain.proceed(request)
//            if (response.code() == HttpURLConnection.HTTP_OK) {
//                val cookieValue = response.header("Set-Cookie")
//            }
//            response
//        }

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        okHttpBuilder.addInterceptor(logging)

//        okHttpBuilder.addNetworkInterceptor(StethoInterceptor())
        val baseDir = App.getApp().cacheDir
        if (baseDir != null) {
            val cacheDir = File(baseDir, "HttpResponseCache")
            okHttpBuilder.cache(Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE.toLong()))
        }
        return okHttpBuilder.build()
    }

    /**
     * 下载Client
     *
     * @param listener 进度回调
     */
    fun createDownloadOkHttpClient(listener: DownloadProgressListener?): OkHttpClient {
        val okHttpBuilder = createDefaultBuilder()
                .addInterceptor(DownloadProgressInterceptor(listener))
                .retryOnConnectionFailure(true)
        okHttpBuilder.addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                    .addHeader("Cache-Control", String.format(Locale.CHINA, "max-age=%d, no-cache, max-stale=%d", 0, 0))//不做缓存
                    .build()
            chain.proceed(request)
        }
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS) //此处不可使用Body，否则会使得下载文件被读进内存并使进度拦截器失效
            okHttpBuilder.addInterceptor(logging)
//            okHttpBuilder.addNetworkInterceptor(StethoInterceptor())
        }
        return okHttpBuilder.build()
    }

    fun createUploadClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        val builder = createDefaultBuilder()
                .retryOnConnectionFailure(true)
                .addInterceptor(logging)
//                .addNetworkInterceptor(StethoInterceptor())

        builder.addInterceptor { chain ->
            //            var token = SPUtil.instance.getToken()
//            if (token == null) {
//                token = ""
//            }
            val original = chain.request()
            val request = original.newBuilder()
//                    .header("User-Agent", USER_AGENT)
//                    .header("X-UDID", App.app.uuid)
//                    .header("X-ACCESS-TOKEN", token)
//                    .header("X-APP-ID", App.app.appID)
                    .header("X-USER-PHONE-BRAND", Build.MODEL)
                    .build()
            chain.proceed(request)
        }
        return builder.build()
    }


    private fun createDefaultBuilder(): OkHttpClient.Builder {
        val sslSocketFactory = HttpsUtils.getSslSocketFactory(null, null, null)
        return OkHttpClient.Builder()
                .connectTimeout(TIME_OUT_NORMAL.toLong(), TimeUnit.MILLISECONDS)
                .readTimeout(TIME_OUT_NORMAL.toLong(), TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslSocketFactory.sSLSocketFactory, sslSocketFactory.trustManager)
    }


}
