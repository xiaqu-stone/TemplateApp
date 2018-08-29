package com.stone.templateapp.http.download

import android.content.Context
import android.os.Environment
import com.stone.log.Logs
import com.stone.templateapp.App
import com.stone.templateapp.http.ApiHelper
import com.stone.templateapp.util.CompressUtils
import com.stone.templateapp.util.FileUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.jetbrains.anko.toast
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File

/**
 * Created By: sqq
 * Created Time: 17/6/12 下午3:03.
 */

class DownloadApi {

    private var destination: File

    private var downUrl: String

    private var listener: DownloadProgressListener? = null

    fun startDownload() {
        createDownClient(downUrl, listener)
                .download(downUrl)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map { body: ResponseBody -> body.byteStream() }
                .observeOn(Schedulers.io())
                .doOnNext { inputStream ->
                    Logs.i(TAG, "accept() called with: inputStream = [$inputStream]")
                    FileUtils.saveSmallFile(inputStream, destination)
                    Logs.i(TAG, "accept() called with: inputStream = [$inputStream]")
                    CompressUtils.unzip(destination)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { App.getApp().toast("资源更新完毕") }
    }

    fun createDownClient(downUrl: String, listener: DownloadProgressListener?): DownloadService {
        val retrofit = Retrofit.Builder()
                .baseUrl(getHostName(downUrl))
                .client(ApiHelper.createDownloadOkHttpClient(listener))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        return retrofit.create(DownloadService::class.java)
    }

    /**
     * 根据URL获取域名
     */
    fun getHostName(urlString: String): String {
        var urlString = urlString
        var head = ""
        var index = urlString.indexOf("://")
        if (index != -1) {
            head = urlString.substring(0, index + 3)
            urlString = urlString.substring(index + 3)
        }
        index = urlString.indexOf("/")
        if (index != -1) {
            urlString = urlString.substring(0, index + 1)
        }
        return head + urlString
    }

    constructor(downUrl: String, destination: File, listener: DownloadProgressListener) {
        this.downUrl = downUrl
        this.destination = destination
        this.listener = listener
    }

    constructor(downUrl: String, destinationName: String, listener: DownloadProgressListener) {
        this.downUrl = downUrl
        this.destination = createDefaultDesFile(destinationName)
        this.listener = listener
    }

    /**
     * 根据FileName在默认的目录下保存下载文件
     *
     * @param fileName 下载文件保存name
     */
    private fun createDefaultDesFile(fileName: String): File {
        val destFile = getDefaultDownFile(App.getApp(), fileName)
        if (destFile.exists() && destFile.isFile) {
            destFile.delete()
        }
        return destFile
    }

    fun getDefaultDownFile(context: Context, fileName: String): File {
        //获取私有外部存储，可以避免关于的权限的适配问题
        var destFileDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (destFileDir == null) {//获取不到外部存储目录,使用内部存储
            destFileDir = context.filesDir
        }
        return File(destFileDir, fileName)
    }

    companion object {
        private const val TAG = "DownloadApi"
    }
}
