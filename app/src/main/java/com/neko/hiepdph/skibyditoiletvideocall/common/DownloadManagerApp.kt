package com.neko.hiepdph.skibyditoiletvideocall.common

import android.content.Context
import android.util.Log
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class DownloadManagerApp(val context: Context) {
    private var config: PRDownloaderConfig? = null
    private var currentIdDownload = -1
    private var scope: CoroutineScope? = null
    private var job: Job? = null

    companion object {
        lateinit var INSTANCE: DownloadManagerApp

        @JvmStatic
        fun getInstance(context: Context): DownloadManagerApp {
            if (!Companion::INSTANCE.isInitialized) {
                INSTANCE = DownloadManagerApp(context)
            }
            return INSTANCE
        }

    }

    init {
        initDownloadManager()
    }

    private fun initDownloadManager() {
        config = PRDownloaderConfig.newBuilder().setConnectTimeout(10000).setReadTimeout(10000)
            .setDatabaseEnabled(true).build()
        PRDownloader.initialize(context, config)
        scope = CoroutineScope(Dispatchers.IO)
    }

    fun makeRequestDownload(
        url: String,
        filePath: String,
        fileName: String,
        onDownloadCompleted: () -> Unit,
        onDownloadFailed: () -> Unit
    ) {
        job = scope?.launch {
            currentIdDownload = PRDownloader.download(url, filePath, fileName).build()
                .setOnStartOrResumeListener { }.setOnPauseListener { }.setOnCancelListener { }
                .setOnProgressListener { }.start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        onDownloadCompleted.invoke()
                        Log.d("TAG", "onDownloadComplete: ")
                    }

                    override fun onError(error: com.downloader.Error?) {
                        Log.d("TAG", "Error: ")
                        onDownloadFailed?.invoke()
                    }

                })
        }
    }

    fun stopDownload() {
        if (currentIdDownload != -1) {
            PRDownloader.cancel(currentIdDownload)
            PRDownloader.cancelAll()
        }
        try {
            job?.cancel()
            scope?.cancel()
        } catch (e: Exception) {
        }

    }
}