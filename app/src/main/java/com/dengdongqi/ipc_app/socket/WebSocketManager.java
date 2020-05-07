package com.dengdongqi.ipc_app.socket;

import com.blankj.utilcode.util.LogUtils;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

/**
 * <pre>
 * Created by DengDongQi on 2020/5/4
 * java webSocket库实现 webSocket客户端
 * </pre>
 */
public class WebSocketManager {

    private static final String TAG = WebSocketManager.class.getSimpleName();

    private static WebSocketManager instance = null;
    // socket连接
    private WebSocketClient mWebSocketClient = null;
    // 服务消息接听
    private SocketServiceMsgListener listener;

    private boolean isInstantiation = false;

    private WebSocketManager(){
        if(isInstantiation){
            throw new RuntimeException("Prevents reflection from repeatedly instantiating singleton classes");
        }
        isInstantiation = true;
    }

    public static WebSocketManager getInstance(){
        if(instance==null){
            synchronized (WebSocketManager.class){
                if(instance == null){
                    instance = new WebSocketManager();
                }
            }
        }
        return instance;
    }

    public void setListener(SocketServiceMsgListener listener) {
        this.listener = listener;
    }

    /**
     * 创建webSocket连接
     * @param socketUri  格式:  new URI("ws://ip:port/")
     * */
    public void connectService(URI socketUri){
        if(mWebSocketClient == null) {
            mWebSocketClient = new WebSocketClient(socketUri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    LogUtils.i( TAG+": onOpen");
                }

                @Override
                public void onMessage(String s) {
                    LogUtils.i( TAG+":onMessage - " + s);
                    handleReceivedMsg(s);
                }

                @Override
                public void onMessage(ByteBuffer byteBuffer) {
                    super.onMessage(byteBuffer);
                    byte[] bytes = conver(byteBuffer);
                    handleReceivedMsg(bytes);
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    LogUtils.i( TAG+":onClose - "+s);
                    //mWebSocketClient = null;
                }

                @Override
                public void onError(Exception e) {
                    //e.printStackTrace();
                    LogUtils.i(TAG+":onError");
                    //mWebSocketClient = null;
                }
            };
            mWebSocketClient.connect();
        }else{
            //已连接的uri与方法参数是否一致
            if(socketUri.getHost().equals(mWebSocketClient.getURI().getHost())
                    && socketUri.getPort() == mWebSocketClient.getURI().getPort()){
                if(!mWebSocketClient.isOpen()) {
                    mWebSocketClient.reconnect();
                }
            }else{
                mWebSocketClient = null;
                connectService(socketUri);
            }
        }
    }

    /**
     * 发送信息至服务器
     * @param jsonMsg
     * */
    public void sendMessage(String jsonMsg){
        if(mWebSocketClient!=null){
            mWebSocketClient.send(jsonMsg);
        }
    }

    /**
     * 发送信息至服务器
     * @param byteMsg
     * */
    public void sendMessage(byte[] byteMsg){
        if(mWebSocketClient!=null){
            mWebSocketClient.send(byteMsg);
        }
    }

    /**
     * 处理服务器的数据
     * */
    public void handleReceivedMsg(Object message) {
        if(message instanceof String){
            if(listener!=null){
                listener.OnServiceStrMsg(message.toString());
            }
        }else if(message instanceof byte[]){
            if(listener!=null){
                listener.OnServiceByteMsg((byte[])message);
            }
        }
    }

    /**
     * 接口回调出服务器发出的信息
     * */
    public interface SocketServiceMsgListener{
        void OnServiceStrMsg(String msg);
        void OnServiceByteMsg(byte[] msg);
    }

    /**
     * byteBuffer 转 byte[]
     * @param byteBuffer
     * */
    public static byte[] conver(ByteBuffer byteBuffer){
        int len = byteBuffer.limit() - byteBuffer.position();
        byte[] bytes = new byte[len];

        if(byteBuffer.isReadOnly()){
            return null;
        }else {
            byteBuffer.get(bytes);
        }
        return bytes;
    }


}
