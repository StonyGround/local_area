package com.sg.client

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import com.sg.client.ext.IP
import com.sg.client.ext.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity() {
    lateinit var log: TextView
    lateinit var stringBuilder: StringBuilder
    lateinit var input: EditText
    lateinit var btn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        EventBus.getDefault().register(this)

        log = findViewById(R.id.log)

        input = findViewById(R.id.input)
        btn = findViewById(R.id.btn)

        val runBlocking = runBlocking {
            dataStore.data.map { preferences ->
                // No type safety.
                preferences[IP]
            }.first()
        }
        input.text.append(runBlocking)


        stringBuilder = StringBuilder()
        sendLog(Msg("本机ip：" + getLocalIPAddress()))

        btn.setOnClickListener {
            SocketClient.connect(input.text.toString())

            CoroutineScope(Dispatchers.IO).launch {
                dataStore.edit { settings ->
                    settings[IP] = input.text.toString();
                }
            }
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun sendLog(event: Msg) {
        stringBuilder.append(event.string).append("\n")
        log.text = stringBuilder.toString()
    }

    fun getLocalIPAddress(): String {
        val wifiManager =
            applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return (wifiInfo.ipAddress and 0xff).toString() + "." + (wifiInfo.ipAddress shr 8 and 0xff) + "." + (wifiInfo.ipAddress shr 16 and 0xff) + "." + (wifiInfo.ipAddress shr 24 and 0xff)
    }
}