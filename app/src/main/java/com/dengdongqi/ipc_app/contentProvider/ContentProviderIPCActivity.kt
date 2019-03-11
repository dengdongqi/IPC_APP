package com.dengdongqi.ipc_app.contentProvider

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import com.dengdongqi.ipc_app.R
import android.widget.Toast
import android.database.ContentObserver
import android.net.Uri
import android.content.ContentValues
import android.support.v4.content.ContextCompat
import android.view.View
import com.githang.statusbar.StatusBarCompat
import kotlinx.android.synthetic.main.activity_content_provider_ipc.*


class ContentProviderIPCActivity : AppCompatActivity() {

    private val TAG = ContentProviderIPCActivity::class.java.simpleName
    private var mHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_content_provider_ipc)

        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.baseColor))

        mHandler = Handler()

        contentResolver.registerContentObserver(BookProvider.BOOK_CONTENT_URI, true, object : ContentObserver(mHandler) {
            override fun deliverSelfNotifications(): Boolean {
                return super.deliverSelfNotifications()
            }

            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
            }

            override fun onChange(selfChange: Boolean, uri: Uri) {
                Toast.makeText(this@ContentProviderIPCActivity, uri.toString(), Toast.LENGTH_SHORT).show()
                super.onChange(selfChange, uri)
            }
        })

        event()

    }

    private fun event() {
        btGet.setOnClickListener {
            query()
        }
    }

    fun insert() {
        val values = ContentValues()
        values.put("_id", 1123)
        values.put("name", "三国演义")
        contentResolver.insert(BookProvider.BOOK_CONTENT_URI, values)
    }

    fun delete() {
        contentResolver.delete(BookProvider.BOOK_CONTENT_URI, "_id = 4", null)
    }

    fun update() {
        val values = ContentValues()
        values.put("_id", 1123)
        values.put("name", "三国演义新版")
        contentResolver.update(BookProvider.BOOK_CONTENT_URI, values, "_id = 1123", null)
    }

    fun query() {
        val bookCursor = contentResolver.query(BookProvider.BOOK_CONTENT_URI, arrayOf("_id", "name"), null, null, null)
        val sb = StringBuilder()
        while (bookCursor!!.moveToNext()) {
            val book = Book(bookCursor.getInt(0), bookCursor.getString(1))
            sb.append(book.toString()).append("\n")
        }
        sb.append("--------------------------------").append("\n")
        bookCursor.close()

        val userCursor = contentResolver.query(BookProvider.USER_CONTENT_URI, arrayOf("_id", "name", "sex"), null, null, null)
        while (userCursor!!.moveToNext()) {
            sb.append(userCursor.getInt(0))
                    .append(userCursor.getString(1)).append(" ,")
                    .append(userCursor.getInt(2)).append(" ,")
                    .append("\n")
        }
        sb.append("--------------------------------")
        userCursor.close()
        tvContent!!.text = sb.toString()
    }

    data class Book(var id:Int, var name : String)
}
