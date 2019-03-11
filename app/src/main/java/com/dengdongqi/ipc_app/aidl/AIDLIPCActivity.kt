package com.dengdongqi.ipc_app.aidl

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.support.v4.content.ContextCompat
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.dengdongqi.ipc_app.BookController
import com.dengdongqi.ipc_app.R
import com.githang.statusbar.StatusBarCompat
import kotlinx.android.synthetic.main.activity_aidlipc.*
import kotlinx.android.synthetic.main.title_bar.*

class AIDLIPCActivity : AppCompatActivity() {

    companion object {
        val TAG = AIDLIPCActivity::class.java.simpleName
    }

    private lateinit var controller : BookController
    private lateinit var list : MutableList<Book>
    private var connected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aidlipc)
        inits()
        event()
    }

    private fun inits() {
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.baseColor))
        tvTitle.text = "AIDL IPC"
        bindAidlService()
    }

    private val connection =  object :ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {
            connected = false
            LogUtils.e("绑定服务失败")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            LogUtils.e("绑定服务成功")
            controller = BookController.Stub.asInterface(service)
            connected = true
        }

    }

    private fun bindAidlService() {
        var intent = Intent()
        intent.`package` = "com.dengdongqi.ipc_app"
        intent.action = "com.dengdongqi.ipc_app.aidl.service"
        bindService(intent,connection, Context.BIND_AUTO_CREATE)
    }

    private fun event() {

        ivBack.setOnClickListener {
            finish()
        }

        btAddBook.setOnClickListener {
            if (etBook.text.toString().isEmpty()){
                ToastUtils.showShort("请输入添加的书名")
                return@setOnClickListener
            }
            if (connected){
                val book = Book(etBook.text.toString())
                try {
                    controller.addBookInOut(book)
                    LogUtils.e("向服务器以InOut方式添加了一本新书.新书名：${book.name}")
                }catch (e: RemoteException){
                    e.printStackTrace()
                }
            }
        }

        btGetBookList.setOnClickListener {
            if (connected){
                try {
                    list = controller.bookList
                    LogUtils.e(list.toString())
                    tvContent.text = ""
                    if (!list.isNullOrEmpty()){
                        for (book in list){
                            if (book != null && !book.name.isNullOrEmpty()) {
                                tvContent.text = "${tvContent.text}\n${book.name}"
                            }
                        }
                    }
                }catch (e: RemoteException){
                    e.printStackTrace()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }
}
