package com.dengdongqi.ipc_app.socket

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.blankj.utilcode.util.ConvertUtils
import com.dengdongqi.ipc_app.R
import kotlinx.android.synthetic.main.activity_test_socket.*
import java.net.URI


/**
 * <pre>
 * Created by DengDongQi on 2020/5/4
 *
</pre> *
 */
class TestSocketActivity : AppCompatActivity() {

    override fun onCreate(paramBundle: Bundle?) {
        super.onCreate(paramBundle)
        setContentView(R.layout.activity_test_socket)
        inits()
        event()
    }

    private fun inits() {

        /**
         * 服务器发出的消息接收回调
         * */
        WebSocketManager.getInstance().setListener(object : WebSocketManager.SocketServiceMsgListener {
            @SuppressLint("SetTextI18n")
            override fun OnServiceStrMsg(msg: String?) {
                runOnUiThread {
                    cRec.text = cRec.text.toString().trim() + "\n" + (msg ?: "")
                }
            }

            override fun OnServiceByteMsg(msg: ByteArray?) {
                runOnUiThread {
                    if(msg!=null) {
                        ivC.setImageBitmap(ConvertUtils.bytes2Bitmap(msg))
                    }
                }
            }
        })

        SocketPresenter.getInstance().setListener(object : SocketPresenter.SocketClientMsgListener{
            @SuppressLint("SetTextI18n")
            override fun OnClientStrMsg(msg: String?) {
                runOnUiThread {
                    sRec.text = sRec.text.toString().trim() + "\n" + (msg ?: "")
                }
            }

            override fun OnClientByteMsg(msg: ByteArray?) {
                runOnUiThread {
                    if(msg!=null) {
                        ivS.setImageBitmap(ConvertUtils.bytes2Bitmap(msg))
                    }
                }
            }
        })

    }

    private fun event() {
        startS.setOnClickListener {
            SocketPresenter.getInstance().startService("192.168.0.45",8887)
        }

        stopS.setOnClickListener {
            SocketPresenter.getInstance().closeService()
        }

        ljS.setOnClickListener {
            WebSocketManager.getInstance().connectService(URI("ws://192.168.0.45:8887/"))
        }

        cSend.setOnClickListener {
            var msg = etC.text.toString().trim()
            //发送String至服务器
            WebSocketManager.getInstance().sendMessage(msg)
            //发送byte[]
//            WebSocketManager.getInstance().sendMessage(
//                    ConvertUtils.bitmap2Bytes(
//                            ConvertUtils.drawable2Bitmap(
//                                    ContextCompat.getDrawable(this,R.mipmap.main_close)
//                            )
//                    )
//            )

        }

        sSend.setOnClickListener {
            val serMsg = etS.text.toString().trim()
            //发送String至客户端
            SocketPresenter.getInstance().sendMsgToClient(serMsg)
            //发送byte[]
//            TestSocketService.getInstance().sendMsgToClient(
//                    ConvertUtils.bitmap2Bytes(
//                            ConvertUtils.drawable2Bitmap(
//                                    ContextCompat.getDrawable(this,R.mipmap.main_close)
//                            )
//                    )
//            )
        }

    }
}