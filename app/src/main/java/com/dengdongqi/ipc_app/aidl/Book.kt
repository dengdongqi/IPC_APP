package com.dengdongqi.ipc_app.aidl

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Dengdongqi on 2019/3/11.
 * Copyright © 2018, 长沙豆子信息技术有限公司, All rights reserved.
 */
class Book(name: String?) : Parcelable{
    var name : String? = name

    fun readFromParcel(parcel: Parcel){
        name = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Book> {
        override fun createFromParcel(parcel: Parcel): Book {
            return Book(parcel.readString())
        }

        override fun newArray(size: Int): Array<Book?> {
            return arrayOfNulls(size)
        }
    }
}