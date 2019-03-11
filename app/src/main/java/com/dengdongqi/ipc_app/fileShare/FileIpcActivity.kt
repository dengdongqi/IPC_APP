package com.dengdongqi.ipc_app.fileShare

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.blankj.utilcode.util.FileIOUtils
import com.dengdongqi.ipc_app.R
import kotlinx.android.synthetic.main.activity_file_ipc.*
import kotlinx.android.synthetic.main.title_bar.*
import java.io.File

class FileIpcActivity : AppCompatActivity() {

    private lateinit var filePath : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_ipc)
        inits()
        event()
    }

    private fun inits() {
        tvTitle.text = "文件IPC"
        filePath = intent.getStringExtra("filePath")
    }

    private fun event() {
        ivBack.setOnClickListener {
            finish()
        }

        btGetFile.setOnClickListener {
            tvContent.text = getFileContent()
        }
    }

    fun getFileContent() : String {
        val file = File(filePath)
        var content = ""
        if(file.exists()){
            content = FileIOUtils.readFile2String(file)
        }
        return content
    }
}
