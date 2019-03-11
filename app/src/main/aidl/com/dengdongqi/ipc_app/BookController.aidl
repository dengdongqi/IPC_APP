// BookController.aidl
package com.dengdongqi.ipc_app;
import com.dengdongqi.ipc_app.aidl.Book;
// Declare any non-default types here with import statements

interface BookController {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    List<Book> getBookList();

    void addBookInOut(inout Book book);
}
