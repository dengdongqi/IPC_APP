package com.dengdongqi.ipc_app.contentProvider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Created by Dengdongqi on 2019/3/11.
 * Copyright © 2018, 长沙豆子信息技术有限公司, All rights reserved.
 */
class DBOpenHelper(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private val DB_NAME = "book_provider.db"
        private val DB_VERSION = 1

        val BOOK_TABLE_NAME = "book"
        val USER_TABLE_NAME = "user"
    }

    constructor(context: Context?) : this(context,DB_NAME,null,DB_VERSION)

    private val CREATE_BOOK_TABLE = ("CREATE TABLE IF NOT EXISTS "
            + BOOK_TABLE_NAME + "(_id INTEGER PRIMARY KEY," + "name TEXT)")

    private val CREATE_USER_TABLE = ("CREATE TABLE IF NOT EXISTS "
            + USER_TABLE_NAME + "(_id INTEGER PRIMARY KEY," + "name TEXT,"
            + "sex INT)")



    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_BOOK_TABLE)
        db.execSQL(CREATE_USER_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

}