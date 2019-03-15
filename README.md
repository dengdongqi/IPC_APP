# IPC_APP
```
class MainActivity : AppCompatActivity() {

    companion object {
        val TAG = "IPC"
    }

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
        initpermission()
    }

    private fun initpermission() {

        if (!PermissionUtils.isGranted(PermissionConstants.STORAGE)){
            PermissionUtils.permission(PermissionConstants.STORAGE)
                    .callback(object :PermissionUtils.FullCallback{
                        override fun onGranted(permissionsGranted: MutableList<String>?) {

                        }

                        override fun onDenied(permissionsDeniedForever: MutableList<String>?, permissionsDenied: MutableList<String>?) {
                            initpermission()
                        }

                    })
                    .request()
        }
    }

    private fun event() {
        bt1.setOnClickListener {
            //intent IPC
            /*Activity，Service，Receiver 都支持在 Intent 中传递 数据 以及 Bundle 数据，
             而 Intent 和 Bundle 都 实现了 Parcelable 接口，可以在不同的进程间进行传输。*/
            val intent = Intent(this, IntentServerActivity::class.java)
            intent.putExtra("Message","消息1")
            IntentServerActivity.Message = "消息2"
            startActivity(intent)
        }

        bt2.setOnClickListener {
            //Messenger IPC
            /*Messenger 是一种轻量级的 IPC 方案，它的底层实现是 AIDL ，可以在不同进程中传递 Message
            对象，它一次只处理一个请求，在服务端不需要考虑线程同步的问题，服务端不存在并发执行的情形。*/
            startActivity(Intent(this,MessengerIPCActivity::class.java))
        }

        bt3.setOnClickListener {
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

        bt4.setOnClickListener {
            //ContentProvider IPC
            /* ContentProvider的底层是采用 Android中的 Binder机制 */
            startActivity(Intent(this,ContentProviderIPCActivity::class.java))
        }

        bt5.setOnClickListener {
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
    }
}
```
