package jul.lab.library.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import jul.lab.library.log.Log;

/**
 * Created by JuL on 2014-07-12.
 */
public abstract class NetStateChangeReceiver extends BroadcastReceiver{
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<Void> future = null;

    public static final long NETSTATE_DELAY_MILLIS		= 6000;

    private class Worker implements Callable<Void> {
        private Context context;
        private Intent intent;

        public Worker(Context context, Intent intent){
            this.context = context;
            this.intent = intent;
        }

        @Override
        public Void call() throws Exception {
            String type = NetState.isAvailable(context);
            Log.d("NetState Changed: Connected. type = ", type);
            onConnected(context, type);
            return null;
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
        if(future != null){
            future.cancel(true);
            future = null;
        }

		/*
		 * action이 왔을 때, disconnect 상태 일 경우에는 바로 session 정리 작업을 수행 해 줘야 한다.
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
                future = executor.schedule(new Worker(context, intent), NETSTATE_DELAY_MILLIS, TimeUnit.MILLISECONDS);
            }
        }

        Log.v("onReceive() - end");
    }

    protected abstract void onConnected(Context context, String type);

    protected abstract void onDisconnected(Context context);
}
