package io.dinis.robotcontrol

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var service: Messenger? = null

    private var bound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            this@MainActivity.service = Messenger(service)
            bound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            this@MainActivity.service = null
            bound = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            sendMessage(TcpClientService.MSG_BACK)
        }
        findViewById<ImageButton>(R.id.btn_forward).setOnClickListener {
            sendMessage(TcpClientService.MSG_FORWARD)
        }
        findViewById<ImageButton>(R.id.btn_left).setOnClickListener {
            sendMessage(TcpClientService.MSG_LEFT)
        }
        findViewById<ImageButton>(R.id.btn_right).setOnClickListener {
            sendMessage(TcpClientService.MSG_RIGHT)
        }
    }

    private fun sendMessage(message: Int){
        if (!bound) {
            return
        }
        val msg: Message = Message.obtain(null,message, 0, 0)
        try {
            service?.send(msg)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, TcpClientService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (bound) {
            unbindService(connection)
            bound = false
        }
    }
}