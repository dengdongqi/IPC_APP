package com.dengdongqi.ipc_app.messenger

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log

/**
 * 服务端创建一个 Service 来处理客户端请求，同时通过一个 Handler 对象来实例化一个 Messenger 对象，
 * 然后在 Service 的 onBind 中返回这个 Messenger 对象底层的 Binder 即可。
 * */
class MessengerService : Service() {

    companion object{
        val TAG = MessengerService::class.java.simpleName
        val MSG_FROM_SERVICE = 200
    }

    private class MessageHandler : Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when(msg!!.what){
                MessengerIPCActivity.MSG_FROM_CLIENT -> {

                    Log.e(TAG,"服务端收到客服端信息："+msg.data.getString("clientMsg"))

                    val client = msg.replyTo
                    val replyMsg = Message()
                    replyMsg.what = MSG_FROM_SERVICE
                    val bundle = Bundle()
                    bundle.putString("ServiceMsg","客服端你好！服务端已经收到你的信息了！")
                    replyMsg.data = bundle
                    client.send(replyMsg)
                }
            }
        }
    }

    private val messenger = Messenger(MessageHandler())


    override fun onBind(intent: Intent): IBinder {
        Log.e(TAG,"onBind")
        return messenger.binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.e(TAG,"onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.e(TAG,"onDestroy")
        super.onDestroy()
    }
}
