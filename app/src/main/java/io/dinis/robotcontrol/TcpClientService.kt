package io.dinis.robotcontrol

import android.app.Service
import android.content.Intent
import android.os.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.Socket


class TcpClientService : Service() {

    private lateinit var handlerThread: HandlerThread
    private lateinit var serviceHandler: ServiceHandler

    inner class ServiceHandler(looper: Looper?) : Handler(looper!!) {
        override fun handleMessage(message: Message) {
            if (message.what == -1){
                startSocket()
            }
            this@TcpClientService.sendSocketMessage(message.what)
        }
    }

    private var socket: Socket? = null
    var dataInputStream: DataInputStream? = null
    var dataOutputStream: DataOutputStream? = null
    private lateinit var messenger: Messenger

    override fun onCreate() {
        super.onCreate()
        handlerThread = HandlerThread("HandlerThread")
        handlerThread.start()
        serviceHandler = ServiceHandler(handlerThread.looper)
        messenger = Messenger(serviceHandler)
        messenger.send(Message.obtain(null, -1))
    }

    private fun startSocket(){
        val ip = InetAddress.getByName(IP)
        socket = Socket(ip, PORT)
        dataInputStream = DataInputStream(socket!!.getInputStream())
        dataOutputStream = DataOutputStream(socket!!.getOutputStream())
    }

    private fun sendSocketMessage(message: Int){
        val text = when(message){
            MSG_FORWARD->{
                "MSG_FORWARD"
            }
            MSG_BACK->{
                "MSG_BACK"
            }
            MSG_LEFT->{
                "MSG_LEFT"
            }
            MSG_RIGHT->{
                "MSG_RIGHT"
            }
            else -> {
                ""
            }
        }
        dataOutputStream?.write(text.toByteArray())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return messenger.binder
    }

    companion object {
        const val MSG_FORWARD = 0
        const val MSG_BACK = 1
        const val MSG_LEFT = 2
        const val MSG_RIGHT = 3

        private const val IP = "192.168.31.107"
        private const val PORT = 8080
    }
}