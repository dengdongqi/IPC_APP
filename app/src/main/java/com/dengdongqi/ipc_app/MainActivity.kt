package com.dengdongqi.ipc_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.githang.statusbar.StatusBarCompat
import com.dengdongqi.ipc_app.intent.IntentServerActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.title_bar.*

class MainActivity : AppCompatActivity() {

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
    }

    private fun event() {
        bt1.setOnClickListener {
            //intent IPC
            //Activity，Service，Receiver 都支持在 Intent 中传递 Bundle 数据，而 Bundle 实现了 Parcelable 接口，可以在不同的进程间进行传输。
            val intent = Intent(this, IntentServerActivity::class.java)
            intent.putExtra("data","跨进程通讯尝试1")
            IntentServerActivity.Name = "跨进程通讯尝试2"
            startActivity(intent)
        }

        bt2.setOnClickListener {
            //Message IPC
        }

        bt3.setOnClickListener {
            //FileShare IPC
        }

        bt4.setOnClickListener {
            //ContentProvider IPC
        }

        bt5.setOnClickListener {
            //AIDL IPC
        }
    }
}
