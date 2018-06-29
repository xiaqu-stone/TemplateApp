package com.stone.templateapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.stone.log.Logs
import com.stone.templateapp.http.download.DownloadApi
import com.stone.templateapp.http.download.DownloadProgressListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnHello.setOnClickListener {
            btnHello.isEnabled = false
            DownloadApi(
                    "http://ozawa-1251122539.file.myqcloud.com/bundlejs.zip",
                    "bundlejs.zip",
                    DownloadProgressListener { bytesRead, contentLength, done -> Logs.d(TAG, "bytesRead = $bytesRead, contentLength = $contentLength, done = $done") }
            ).startDownload()
        }
    }

    override fun onResume() {
        super.onResume()
//        AppUtil.isAppAlive(act, BuildConfig.APPLICATION_ID)
    }

    override fun onDestroy() {
        Logs.i("MainActivity1")
        super.onDestroy()

        Logs.i("MainActivity2")
    }

    companion object {
        const val TAG = "MainActivity"
    }

}
