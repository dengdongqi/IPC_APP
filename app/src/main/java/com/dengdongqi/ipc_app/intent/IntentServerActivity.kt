package com.dengdongqi.ipc_app.intent

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import com.githang.statusbar.StatusBarCompat
import com.dengdongqi.ipc_app.R
import kotlinx.android.synthetic.main.activity_intent_server.*
import kotlinx.android.synthetic.main.title_bar.*

class IntentServerActivity : AppCompatActivity() {
    private lateinit var bundle: Bundle
    //静态变量
    companion object {
        var Message = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intent_server)
        inits()
        event()
    }

    private fun inits() {
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.baseColor))
        tvTitle.text = "Intent IPC"
        tvMsg1.text = intent.getStringExtra("Message")
        tvMsg2.text = Message
        Log.e("IPC","intent Msg = ${tvMsg1.text} , Message = $Message")
    }

    private fun event() {
        ivBack.setOnClickListener {
            finish()
        }
    }

}
