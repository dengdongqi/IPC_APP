package com.dengdongqi.ipc_app

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.blankj.utilcode.constant.PermissionConstants
import com.blankj.utilcode.util.*
import com.dengdongqi.ipc_app.aidl.AIDLIPCActivity
import com.dengdongqi.ipc_app.contentProvider.ContentProviderIPCActivity
import com.dengdongqi.ipc_app.fileShare.FileIpcActivity
import com.githang.statusbar.StatusBarCompat
import com.dengdongqi.ipc_app.intent.BundleIpcActivity
import com.dengdongqi.ipc_app.messenger.MessengerIPCActivity
import com.dengdongqi.ipc_app.socket.TestSocketActivity
import kotlinx.android.synthetic.main.title_bar.*
import java.io.File

class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "IPC"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        inits()
    }

    private fun inits() {
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.baseColor))
        tvTitle.text = "主页"
        ivBack.visibility = View.INVISIBLE
        initpermission()
    }

    private fun initpermission() {
        if (!PermissionUtils.isGranted(PermissionConstants.STORAGE)){
            PermissionUtils.permission(PermissionConstants.STORAGE)
                    .callback(object :PermissionUtils.FullCallback{
                        override fun onGranted(permissionsGranted: MutableList<String>?) {}

                        override fun onDenied(permissionsDeniedForever: MutableList<String>?, permissionsDenied: MutableList<String>?) {
                            initpermission()
                        }
                    })
                    .request()
        }
    }

    public fun onclick(view:View){
        when(view.id){

            R.id.bt1->{
                //intent IPC
                /*Activity，Service，Receiver 都支持在 Intent 中传递 数据，可以在不同的进程间进行传输。*/

                val intent = Intent(this, BundleIpcActivity::class.java)
                intent.putExtra("Message","消息1")

                val bundle = Bundle()
                bundle.putString("bundle_Message", "消息2")
                intent.putExtras(bundle)

                BundleIpcActivity.MESSAGE = "消息3"

                startActivity(intent)
            }

            R.id.bt3->{
                //FileShare IPC
                /* Android 系统基于 Linux ，使得其并发读取文件没有限制地进行，甚至允许两个线程同时
                对一个文件进行读写操作，尽管这样可能会出问题。*/
                val fileDir = "IPC_APP/fileIpc/Log.txt"
                val filepath = "${SDCardUtils.getSDCardInfo()[0].path}/$fileDir"
                if (FileUtils.createOrExistsFile(filepath)){
                    val file = File(filepath)
                    if (file.exists()) {
                        if (FileIOUtils.readFile2String(file).isNullOrEmpty()){
                            FileIOUtils.writeFileFromString(file, "你好，我是app主进程")
                        }
                    }
                }
                startActivity(Intent(this,FileIpcActivity::class.java)
                    .putExtra("filePath",filepath)
                )
            }

            R.id.bt4->{
                //ContentProvider IPC
                /* ContentProvider的底层是采用 Android中的 Binder机制 */
                startActivity(Intent(this,ContentProviderIPCActivity::class.java))
            }

            R.id.bt2->{
                //Messenger IPC
                /*Messenger 是一种轻量级的 IPC 方案，它的底层实现是 AIDL ，可以在不同进程中传递 Message
                对象，它一次只处理一个请求，在服务端不需要考虑线程同步的问题，服务端不存在并发执行的情形。*/

                startActivity(Intent(this,MessengerIPCActivity::class.java))
            }

            R.id.bt5->{
                //AIDL IPC
                /*
                    1.创建 AIDL
                    创建要操作的实体类（Book.kt），实现 Parcelable 接口，以便序列化/反序列化;
                    AS下 File - new - AIDL 命名实体类相同名(Book.aidl) 删除默认接口 import 实体类
                    新建 BookController.aidl 文件夹，在其中创建接口 aidl 文件以及实体类的映射 aidl 文件
                    Make project -> 将自动生成  Binder 的 Java 文件（app/build/generated.....BookController.java）
                    2.服务端
                    创建 Service，在其中onBind() 中返回 Binder 对象实例(BookController.Stub())并实现接口定义的方法
                    3.客户端
                    实现 ServiceConnection 接口，在其中拿到 AIDL 类 (controller = BookController.Stub.asInterface(service))
                    bindService()
                    调用 AIDL 类中定义好的操作请求
                * */
                startActivity(Intent(this,AIDLIPCActivity::class.java))
            }
            R.id.bt6 ->{
                //Web Socket ICP
                // SocketPresenter -- webSocket服务端
                // WebSocketManager -- webSocket客户端
                startActivity(Intent(this, TestSocketActivity::class.java))
            }

        }
    }
}
