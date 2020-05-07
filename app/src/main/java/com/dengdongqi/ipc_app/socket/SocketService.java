package com.dengdongqi.ipc_app.socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.Utils;

/**
 * <pre>
 * Created by DengDongQi on 2020/5/5
 *
 * </pre>
 */
public class SocketService extends Service implements SocketPresenter.SocketClientMsgListener {
    private final static String TAG = SocketService.class.getSimpleName()+" :";

    //通过binder实现调用者client与Service之间的通信
    public class MyBinder extends Binder {
        public SocketService getService(){
            return SocketService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {

        initSocket();

        return new MyBinder();
    }

    private void initSocket() {
        //启动socket服务端
        NetworkUtils.getIPAddressAsync(true,new Utils.Consumer<String>(){
            @Override
            public void accept(String s) {
                if(!TextUtils.isEmpty(s)){ //获取到Ip
                    LogUtils.d(TAG+"ip ="+s);
                    if(SocketPresenter.getInstance().isServiceStart()) {
                        SocketPresenter.getInstance().startService(s, 8887);
                        //监听客户端消息
                        SocketPresenter.getInstance().setListener(SocketService.this);
                    }
                }
            }
        });

    }

    @Override
    public void OnClientStrMsg(String msg) {
        LogUtils.d(TAG+"接收到客户端信息:"+msg);
        SocketPresenter.getInstance().sendMsgToClient("BACK:"+msg);
    }

    @Override
    public void OnClientByteMsg(byte[] msg) {
//        LogUtils.d(TAG+"接收到客户端信息:"+ RjUtil.bytesToHexStrings(msg));
    }

    @Override
    public boolean onUnbind(Intent intent) {
        SocketPresenter.getInstance().closeService();
        return super.onUnbind(intent);
    }
}
