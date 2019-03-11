package com.dengdongqi.ipc_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import com.dengdongqi.ipc_app.fileShare.FileIpcActivity
import com.githang.statusbar.StatusBarCompat
import com.dengdongqi.ipc_app.intent.IntentServerActivity
import com.dengdongqi.ipc_app.messenger.MessengerIPCActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.title_bar.*
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "IPC"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inits()
        event()
    }

    private fun inits() {
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.baseColor))
        tvTitle.text = "主页"
        ivBack.visibility = View.INVISIBLE
        initpermission()
    }

    private fun initpermission() {

        if (!PermissionUtils.isGranted(PermissionConstants.STORAGE)){
            PermissionUtils.permission(PermissionConstants.STORAGE)
                    .callback(object :PermissionUtils.FullCallback{
                        override fun onGranted(permissionsGranted: MutableList<String>?) {

                        }

                        override fun onDenied(permissionsDeniedForever: MutableList<String>?, permissionsDenied: MutableList<String>?) {
                            initpermission()
                        }

                    })
                    .request()
        }
    }

    private fun event() {
        bt1.setOnClickListener {
            //intent IPC
            /*Activity，Service，Receiver 都支持在 Intent 中传递 数据 以及 Bundle 数据，
             而 Intent 和 Bundle 都 实现了 Parcelable 接口，可以在不同的进程间进行传输。*/
            val intent = Intent(this, IntentServerActivity::class.java)
            intent.putExtra("Message","消息1")
            IntentServerActivity.Message = "消息2"
            startActivity(intent)
        }

        bt2.setOnClickListener {
            //Messenger IPC
            /*Messenger 是一种轻量级的 IPC 方案，它的底层实现是 AIDL ，可以在不同进程中传递 Message
            对象，它一次只处理一个请求，在服务端不需要考虑线程同步的问题，服务端不存在并发执行的情形。*/
            startActivity(Intent(this,MessengerIPCActivity::class.java))
        }

        bt3.setOnClickListener {
            //FileShare IPC
            /* Android 系统基于 Linux ，使得其并发读取文件没有限制地进行，甚至允许两个线程同时
            对一个文件进行读写操作，尽管这样可能会出问题。*/
            val fileDir = "IPC_APP/fileIpc/Log.txt"
            val filepath = "${SDCardUtils.getSDCardInfo()[0].path}/$fileDir"
            if (FileUtils.createOrExistsFile(filepath)){
                val file = File(filepath)
                if (file.exists()) {
                    if (FileIOUtils.writeFileFromString(file, "你好，我是app主进程")){
                    }
                }
            }
            startActivity(Intent(this,FileIpcActivity::class.java)
                    .putExtra("filePath",filepath)
            )

        }

        bt4.setOnClickListener {
            //ContentProvider IPC
        }

        bt5.setOnClickListener {
            //AIDL IPC
        }
    }
}
