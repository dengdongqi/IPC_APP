package com.dengdongqi.ipc_app.socket;

import com.blankj.utilcode.util.LogUtils;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import static com.dengdongqi.ipc_app.socket.WebSocketManager.conver;

/**
 * <pre>
 * Created by DengDongQi on 2020/5/4
 * 测试socket服务
 * </pre>
 */
public class SocketPresenter {

    private static final String TAG = SocketPresenter.class.getSimpleName();

    private static SocketPresenter instance = null;
    // socket服务
    private WebSocketServer socketService = null;
    // 客户端连接对象
    private WebSocket clientConn = null;
    //
    private SocketClientMsgListener listener;

    private boolean isInstantiation = false;

    private SocketPresenter(){
        if(isInstantiation){
            throw new RuntimeException("Prevents reflection from repeatedly instantiating singleton classes");
        }
        isInstantiation = true;
    }

    public static SocketPresenter getInstance(){
        if(instance==null){
            synchronized (SocketPresenter.class){
                if(instance == null){
                    instance = new SocketPresenter();
                }
            }
        }
        return instance;
    }

    public void setListener(SocketClientMsgListener listener) {
        this.listener = listener;
    }

    /**
     * 启动服务
     * @param address 服务器地址
     * @param port 端口
     * */
    public void startService(String address, int port){
        if(socketService == null){
            LogUtils.d(TAG+":"+"startService-"+address+":"+port);
            socketService = new WebSocketServer(new InetSocketAddress(address,port)) {
                @Override
                public void onOpen(WebSocket conn, ClientHandshake handshake) {
                    clientConn = conn;
                    //向当前连接的客户端发送
                    clientConn.send("客户端您好,欢迎您来到服务端!");
                    //此方法向所有连接的客户端发送一条消息
//                    broadcast( "new connection: " + handshake.getResourceDescriptor() );
                    LogUtils.d( TAG+":"+conn.getRemoteSocketAddress().getAddress()
                           + " 连接成功!" );
                }

                @Override
                public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                    LogUtils.d(TAG+":"+conn.getRemoteSocketAddress().getAddress()
                            .getHostAddress() +"\n" + " 断开连接!" );
                }

                @Override
                public void onMessage(WebSocket conn, String message) {
//                    broadcast(message);
                    handleReceivedMsg(message);

                    LogUtils.d( TAG+":onMessage-"+conn.getRemoteSocketAddress().getAddress()
                            .getHostAddress() +"\n" + ": " + message );
                }

                @Override
                public void onMessage(WebSocket conn, ByteBuffer message) {
                    super.onMessage(conn, message);
//                    broadcast(message);
                    byte[] bytes = conver(message);
                    handleReceivedMsg(bytes);
                }

                @Override
                public void onError(WebSocket conn, Exception ex) {
                    ex.printStackTrace();

                    LogUtils.d( TAG +"\n"
                            + "启动失败: 可能的错误原因(如无法分配请求的地址;端口绑定失败)" );
                }

                @Override
                public void onStart() {
                    LogUtils.d("Server started!");
                    if(socketService!=null){
                        LogUtils.d(TAG+":Server started:" + "\n"
                                + socketService.getPort() + "\n"
                                + socketService.getAddress() + "\n");
                    }
                    setConnectionLostTimeout(0);
                    setConnectionLostTimeout(100);
                }
            };
            //启动服务
            socketService.start();
        }else{
            LogUtils.d(TAG+":Server started-"+socketService.getAddress().getHostString());
        }
    }

    /**
     * socket服务端是否开启
     * */
    public boolean isServiceStart(){
        return socketService != null;
    }

    /**
     * 发送信息至客户端
     * clientConn.getRemoteSocketAddress().getAddress()区分客户端
     * */
    public void sendMsgToClient(String msg){
        if(clientConn !=null){
            clientConn.send(msg);
        }
    }

    /**
     * 发送信息至客户端
     * clientConn.getRemoteSocketAddress().getAddress()区分客户端
     * */
    public void sendMsgToClient(byte[] msg){
        if(clientConn !=null){
            clientConn.send(msg);
        }
    }

    /**
     * 处理客户端的数据
     * */
    public void handleReceivedMsg(Object message) {
        if(message instanceof String){
            if(listener!=null){
                listener.OnClientStrMsg(message.toString());
            }
        }else if(message instanceof byte[]){
            if(listener!=null){
                listener.OnClientByteMsg((byte[])message);
            }
        }
    }



    /**
     * 关闭服务
     * */
    public void closeService(){
        if(socketService!=null){
            try {
                LogUtils.d(TAG+":"+"closeService");
                socketService.stop();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
            socketService = null;
        }
    }

    /**
     * 接口回调出服务器发出的信息
     * */
    public interface SocketClientMsgListener{
        void OnClientStrMsg(String msg);
        void OnClientByteMsg(byte[] msg);
    }

}
