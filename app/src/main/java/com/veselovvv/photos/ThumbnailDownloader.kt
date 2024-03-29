package com.veselovvv.photos

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.concurrent.ConcurrentHashMap

class ThumbnailDownloader<in T>(
    private val responseHandler: Handler,
    private val onThumbnailDownloaded: (T, Bitmap) -> Unit
) : HandlerThread(TAG), RequestHandler<T> {
    private var hasQuit = false
    private val requestMap = ConcurrentHashMap<T, String>()
    private lateinit var requestHandler: Handler

    @Suppress("UNCHECKED_CAST") // сообщает Lint о приведении к типу T без проверки
    @SuppressLint("HandlerLeak") // убирает предупреждение HandlerLeak
    override fun onLooperPrepared() {
        requestHandler = object : Handler() {
            override fun handleMessage(message: Message) {
                if (message.what == MESSAGE_DOWNLOAD) {
                    val target = message.obj as T
                    handleRequest(target)
                }
            }
        }
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    override fun handleRequest(target: T) {
        val url = requestMap[target] ?: return
        val bitmap = FlickrFetchr().fetchImage(url) ?: return

        // Запись Runnable в очередь основного потока:
        responseHandler.post(Runnable {
            if (requestMap[target] != url || hasQuit) return@Runnable
            requestMap.remove(target)
            onThumbnailDownloaded(target, bitmap)
        })
    }

    // Наблюдатель за жизненным циклом фрагмента:
    fun fragmentLifecycleObserver() = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun setup() {
            start() // запуск потока
            looper
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun tearDown() = quit() // остановка потока
    }

    // Наблюдатель жизненного цикла представления:
    fun viewLifecycleObserver() = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun clearQueue() {
            requestHandler.removeMessages(MESSAGE_DOWNLOAD)
            requestMap.clear()
        }
    }

    fun queueThumbnail(target: T, url: String) {
        requestMap[target] = url
        // Постановка нового сообщения в очередь сообщений фонового потока:
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()
    }

    companion object {
        private const val TAG = "ThumbnailDownloader"
        private const val MESSAGE_DOWNLOAD = 0
    }
}
