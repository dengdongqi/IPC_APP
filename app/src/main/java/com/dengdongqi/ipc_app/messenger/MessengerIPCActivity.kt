package com.dengdongqi.ipc_app.messenger

import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.blankj.utilcode.util.ToastUtils
import com.dengdongqi.ipc_app.R
import com.dengdongqi.ipc_app.messenger.MessengerIPCActivity.Companion.MSG_FROM_CLIENT
import kotlinx.android.synthetic.main.activity_message_ipc.*
import kotlinx.android.synthetic.main.title_bar.*

class MessengerIPCActivity : AppCompatActivity() {
    var isok = false

    companion object {
        //静态常量
        val TAG = MessengerIPCActivity::class.java.simpleName
        val MSG_FROM_CLIENT = 100
    }

    // 客户端handle
    private var mhandle = ClientHandler()
    // 客户端送信者
    private var mMessenger = Messenger(mhandle)

    private class ClientHandler : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            if (msg?.what == MessengerService.MSG_FROM_SERVICE) {
                ToastUtils.showShort( "服务端返回的数据：${msg.data.getString("ServiceMsg")}")
            }
        }
    }

    // 服务端送信者
    private var mServiceMessenger: Messenger? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_ipc)
        inits()
        event()
    }

    private fun inits() {
        tvTitle.text = "Messenger IPC"

        ToastUtils.showShort("Log 查看数据传递")
    }

    private fun event() {
        ivBack.setOnClickListener {
            finish()
        }

        btBind.setOnClickListener {
            bindService()
        }

        btSendMsg.setOnClickListener {
            if (mServiceMessenger == null) {
                Toast.makeText(this, "请先绑定服务", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val msg = etMSg.text.toString()
            if (msg.isEmpty()) {
                Toast.makeText(this, "请输入要发送的信息", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            sendMsg(msg)
        }
    }

    /**
     * 发送消息到服务端
     * @param msg
     * */
    fun sendMsg(msg: String) {
        val message = Message()
        message.what = MSG_FROM_CLIENT
        val bundle = Bundle()
        bundle.putString("clientMsg", msg)
        message.data = bundle
        message.replyTo = mMessenger
        mServiceMessenger!!.send(message)
    }

    /**
     * 绑定服务
     * */
    fun bindService() {
        val intent = Intent(this, MessengerService::class.java)
        bindService(intent, mServiceConnected, Service.BIND_AUTO_CREATE)
    }

    val mServiceConnected = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            ToastUtils.showShort( "服务绑定成功")
            isok = true;
            //获取服务端信息发送者
            mServiceMessenger = Messenger(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            ToastUtils.showShort( "服务解绑成功")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(isok) {
            unbindService(mServiceConnected)
        }
        mhandle.removeCallbacksAndMessages(null)
    }
}
