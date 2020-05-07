package com.dengdongqi.ipc_app.aidl

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.dengdongqi.ipc_app.BookController

class AidlService : Service() {

    companion object {
        val TAG = AidlService::class.java.simpleName
    }

    private lateinit var bookLists : MutableList<Book>

    override fun onCreate() {
        super.onCreate()
        bookLists = mutableListOf()
        initbook()
    }

    private fun initbook() {
        bookLists.add(Book("三国演义"))
        bookLists.add(Book("水浒传"))
        bookLists.add(Book("红楼梦"))
        bookLists.add(Book("西游记"))
        bookLists.add(Book("帝霸"))
        bookLists.add(Book("三体"))
        bookLists.add(Book("斗破苍穹"))
    }

    val stub = object : BookController.Stub(){
        override fun getBookList(): MutableList<Book> {
            return bookLists
        }

        override fun addBookInOut(book: Book?) {
            if (book != null) {
                bookLists.add(book)
                ToastUtils.showShort("AidlService 收到添加${book.name}")
            } else {
                ToastUtils.showShort("接收到了一个空对象 InOut")
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return stub
    }
}
