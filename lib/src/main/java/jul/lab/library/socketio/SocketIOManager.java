package jul.lab.library.socketio;

import android.os.Handler;
import android.os.Looper;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jul.lab.library.log.Log;
import jul.lab.library.socketio.protocol.ProtocolBehavior;
import jul.lab.library.socketio.response.OnResponseListener;
import jul.lab.library.socketio.response.ResponseModel;

/**
 * Created by owner on 2016. 1. 19..
 */
public class SocketIOManager {

    private List<String> mEventList = new ArrayList<>();

    private Socket mSocket;

    private static SocketIOManager sInstance = null;

    private Map<String, OnNotiEventListener> mNotiEventListeners = new HashMap<>();

    private List<Integer> mInvalidAuthCodes;

    private ProtocolBehavior mStartProtocol;

    public interface OnNotiEventListener{
        void onSuccess(ResponseModel responseModel);
    }

    public static void clearForTest(){
        sInstance = null;
    }

    public static void initialize(SocketIOConfig config){
        if(sInstance == null){
            sInstance = new SocketIOManager(config.getIp(), config.getPort(), config.getInvalidAuthCodeList(), config.getStartProtocol());
        }
    }

    public static SocketIOManager getInstance(){
        if(sInstance == null){
            throw new RuntimeException("Singleton instance is null. Call initialize() before getInstance().");
        }
        return sInstance;
    }

    private SocketIOManager(String ip, int port, List<Integer> invalidAuthCodes, ProtocolBehavior startProtocol){
        initSocket(ip, port);
        mInvalidAuthCodes = invalidAuthCodes;
        mStartProtocol = startProtocol;
    }

    private void initSocket(String ip, int port){
        try {
            IO.Options opts = new IO.Options();
            opts.transports = new String[]{"websocket"};
            opts.timeout = -1;
            mSocket = IO.socket(ip+":"+port, opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void connect(){
        if(mSocket != null){
            mSocket.connect();
        }

    }

    public boolean isConnected(){
        return mSocket != null && mSocket.connected();
    }

    public void disconnect() {
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.close();
        }
    }

    private void registerListener(String event, Emitter.Listener listener){
        if(event != null && listener != null && event.length() > 0 && !mEventList.contains(event)){
            mEventList.add(event);
            mSocket.on(event, listener);
        }

    }

    public void setNotiEventListener(String event, OnNotiEventListener listener){
        mNotiEventListeners.put(event, listener);
    }

    public void removeNotiEventListener(String event){
        Log.w("remove noti event["+event+"]");
        mNotiEventListeners.remove(event);
    }

    public void removeAllNotiEventListener(){
        Log.w("remove all noti event");
        mNotiEventListeners.clear();
    }


    /**
     * 앱의 라이플 사이클 내에서 글로벌로 등록되어있어야 할 event가 필요할 경우.
     * @param event
     */
    public void registerNotiEvent(final int myUserNo, final String event, final OnResponseListener listener){
        if(!mEventList.contains(event)){
            mEventList.add(event);

            mSocket.on(event, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
//                    final OnResponseListener listener = mNotiEventListeners.get(event);
                    if (listener != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                ResponseModel resModel = listener.onResponse(event, args[0].toString());

                                if (resModel != null && (resModel._userNo == -1 || resModel._userNo == myUserNo)) {   //내 패킷일 경우
                                    OnNotiEventListener dispatcher = mNotiEventListeners.get(event);
                                    if (dispatcher != null) {
                                        dispatcher.onSuccess(resModel);
                                    } else {
                                        Log.w("Event[" + event + "] dispatcher listener is null.");
                                    }
                                } else { //그러지 않을 경우
                                    if (resModel != null) {
                                        Log.w("This packet is not mine. myUserNo[" + myUserNo + "]/packetUserNo[" + resModel._userNo + "]");
                                    }
                                }
                            }
                        });

                    }
                }
            });
        }
    }

    public void unregisterAllListener(){
        if(mSocket != null){
            for(String event : mEventList){
                mSocket.off(event);
            }
            mEventList.clear();
        }
    }

    /**
     * 1회성 req-res 통신을 위한 함수. res listener는 알아서 해제된다.
     * @param protocol
     * @param resDispatcher
     */
    public void send(final int myUserNo, final ProtocolBehavior protocol, final OnResponseListener resDispatcher){

        final Emitter.Listener listener = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(resDispatcher != null){
                            Log.v("["+protocol.getResponseName()+"] origin data : " + args[0]);
                            ResponseModel res = resDispatcher.onResponse(protocol.getResponseName(), args[0].toString());
                            if(mInvalidAuthCodes.contains(res._resCode)){    //어떤 요청이었든 이 res code가 내려오면 start protocol부터 다시 날려줘야 한다.
                                doSend(mStartProtocol, null);
                            }
                        }
                    }
                });
                mEventList.remove(protocol.getResponseName());
                mSocket.off(protocol.getResponseName(), this);
            }
        };

        if(!isConnected()){   //연결이 되어있지 않다면 자동 연결 및 MobStart 요청 후 send
            getInstance().registerListener(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mEventList.remove(Socket.EVENT_CONNECT);
                    mSocket.off(Socket.EVENT_CONNECT, this);

                    if(mStartProtocol.getRequestName().equals(protocol.getRequestName())){    //요청하려는 패킷이 어차피 start protocol일 경우
                        doSend(protocol, listener);
                    } else{
                        doSend(mStartProtocol, new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                Log.v("Emitter.listener origin data : "+args[0]);
                                doSend(protocol, listener);
                            }
                        });
                    }

                    registerListener(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.e("!!!!!!!!!!!! socket disconnect !!!!!!!!!!!!!!");
                            unregisterAllListener();
                        }
                    });

                    registerListener(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.e("!!!!!!!!!!!! socket timeout !!!!!!!!!!!!!!");
                            unregisterAllListener();
                        }
                    });

                    registerListener(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.e("!!!!!!!!!!!! socket connect error !!!!!!!!!!!!!!");
                            unregisterAllListener();
                        }
                    });

                }
            });
            connect();
        } else{     //연결이 되어있다면 바로 send
            doSend(protocol, listener);
        }
    }


    private void doSend(ProtocolBehavior protocol, Emitter.Listener listener){
        registerListener(protocol.getResponseName(), listener);
        String reqName = protocol.getRequestName();
        String reqParam = protocol.getRequestParam2String();
        Log.i("\n============Req============\n"+reqName + reqParam+"\n===========================");
        mSocket.emit(reqName, reqParam);
    }
}
