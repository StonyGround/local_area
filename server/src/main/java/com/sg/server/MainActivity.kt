package com.sg.server

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.greenrobot.eventbus.EventBus
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    lateinit var log: TextView
    lateinit var input: EditText
    lateinit var btn: Button
    lateinit var stringBuilder: StringBuilder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        log = findViewById(R.id.log)
        input = findViewById(R.id.input)
        btn = findViewById(R.id.btn)

        stringBuilder = StringBuilder()
        log.text = "本机ip：" + getLocalIPAddress()

//        val nextInt = Random.nextInt(10000, 20000)

        SocketServer.ready()

        btn.setOnClickListener {
            EventBus.getDefault().post(Msg(input.text.toString()))
        }
    }

    // wifi下获取本地网络IP地址（局域网地址）
    public fun getLocalIPAddress(): String {
        val wifiManager =
            applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return (wifiInfo.ipAddress and 0xff).toString() + "." + (wifiInfo.ipAddress shr 8 and 0xff) + "." + (wifiInfo.ipAddress shr 16 and 0xff) + "." + (wifiInfo.ipAddress shr 24 and 0xff)
    }
}