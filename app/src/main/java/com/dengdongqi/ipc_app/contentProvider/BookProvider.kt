package com.dengdongqi.ipc_app.contentProvider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.SyncStateContract.Helpers.update
import android.database.sqlite.SQLiteDatabase
import android.content.UriMatcher
import android.util.Log


class BookProvider : ContentProvider() {

    private var mContext: Context? = null
    private var mDB: SQLiteDatabase? = null

    override fun onCreate(): Boolean {
        mContext = context
        initProviderData()

        return true
    }

    private fun initProviderData() {
        //不建议在 UI 线程中执行耗时操作
        mDB = DBOpenHelper(mContext).writableDatabase
        mDB!!.execSQL("delete from " + DBOpenHelper.BOOK_TABLE_NAME)
        mDB!!.execSQL("delete from " + DBOpenHelper.USER_TABLE_NAME)
        mDB!!.execSQL("insert into book values(3,'Android');")
        mDB!!.execSQL("insert into book values(4,'iOS');")
        mDB!!.execSQL("insert into book values(5,'Html5');")
        mDB!!.execSQL("insert into user values(1,'haohao',1);")
        mDB!!.execSQL("insert into user values(2,'nannan',0);")
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        Log.d(TAG, "query, current thread" + Thread.currentThread())
        val table = getTableName(uri) ?: throw IllegalArgumentException("Unsupported URI$uri")

        return mDB!!.query(table, projection, selection, selectionArgs, null, null, sortOrder, null)
    }

    override fun getType(uri: Uri): String? {
        Log.d(TAG, "getType")
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d(TAG, "insert")
        val table = getTableName(uri) ?: throw IllegalArgumentException("Unsupported URI$uri")
        mDB!!.insert(table, null, values)
        // 通知外界 ContentProvider 中的数据发生变化
        mContext!!.getContentResolver().notifyChange(uri, null)
        return uri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        Log.d(TAG, "delete")
        val table = getTableName(uri) ?: throw IllegalArgumentException("Unsupported URI$uri")
        val count = mDB!!.delete(table, selection, selectionArgs)
        if (count > 0) {
            mContext!!.getContentResolver().notifyChange(uri, null)
        }

        return count
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        Log.d(TAG, "update")
        val table = getTableName(uri) ?: throw IllegalArgumentException("Unsupported URI$uri")
        val row = mDB!!.update(table, values, selection, selectionArgs)
        if (row > 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }
        return row
    }

    private fun getTableName(uri: Uri): String? {
        var tableName: String? = null
        when (sUriMatcher.match(uri)) {
            BOOK_URI_CODE -> tableName = DBOpenHelper.BOOK_TABLE_NAME
            USER_URI_CODE -> tableName = DBOpenHelper.USER_TABLE_NAME
            else -> {
            }
        }

        return tableName

    }

    companion object {
        private val TAG = "BookProvider"
        val AUTHORITY = "com.dengdongqi.proivider"

        val BOOK_CONTENT_URI = Uri.parse("content://$AUTHORITY/book")
        val USER_CONTENT_URI = Uri.parse("content://$AUTHORITY/user")

        val BOOK_URI_CODE = 0
        val USER_URI_CODE = 1
        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            sUriMatcher.addURI(AUTHORITY, "book", BOOK_URI_CODE)
            sUriMatcher.addURI(AUTHORITY, "user", USER_URI_CODE)
        }
    }
}
