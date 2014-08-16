package jul.lab.library.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jul.lab.library.log.Log;

/**
 * Created by JuL on 2014-07-12.
 */
public class NetStateChangeReceiver extends BroadcastReceiver{
    private ScheduledThreadPoolExecutor mScheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> mScheduledFuture = null;

    private static long NETSTATE_DELAY_MILLIS;
    private static final long DEFAULT_DELAY = 6000;

    public NetStateChangeReceiver(){
        this(DEFAULT_DELAY);
    }

    public NetStateChangeReceiver(long delayMillis){
        NETSTATE_DELAY_MILLIS = delayMillis;
    }

    private class Worker implements Runnable {
        private Context mContext;

        public Worker(Context context){
            this.mContext = context;
        }


        @Override
        public void run() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String type = NetState.isAvailable(mContext);
                    Log.d("NetState Changed: Connected. type [", type, "]");
                    onConnected(mContext, type);
                    mScheduledFuture = null;
                }
            });
        }
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.v("onReceive() - start");

        if (intent.getAction() == null) {
            Log.v("onReceive() - end with no work.");
            return;
        }

        //일단 예약된 작업이 있으면 취소.
        if(mScheduledFuture != null){
            Log.w("Cancel pre-worker");
            mScheduledFuture.cancel(true);
            mScheduledFuture = null;
        }

        /*
         * action이 왔을 때, disconnect 상태 일 경우에는 바로 후처리 작업을 수행 해 줘야 한다.
         * connect상태에 대한 후처리는 불안정한 네트워크 환경일 때를 고려하여 일정 시간 후에 판단하도록 하자.
         */
        NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        NetworkInfo.State netState = networkInfo.getState();

        if(netState != NetworkInfo.State.CONNECTED){
            Log.d("NetState Changed: Disconneted");
            onDisconnected(context);
        }
        else{
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                Log.v("Scheduling worker");
                mScheduledFuture = mScheduledThreadPoolExecutor.schedule(new Worker(context), NETSTATE_DELAY_MILLIS, TimeUnit.MILLISECONDS);
            }
        }

        Log.v("onReceive() - end");
    }

    protected void onConnected(Context context, String type){};

    protected void onDisconnected(Context context){};
}
