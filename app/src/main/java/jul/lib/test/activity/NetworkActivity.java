package jul.lib.test.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;
import android.widget.Toast;

import jul.lab.library.log.Log;
import jul.lab.library.network.NetStateChangeReceiver;
import jul.lib.test.R;

/**
 * Created by JuL on 2014-07-14.
 */
public class NetworkActivity extends Activity{

    private TextView mTvMsg;

    public static void invoke(Context context){
        Intent i = new Intent(context, NetworkActivity.class);
        context.startActivity(i);
    }

    private NetStateChangeReceiver mNetStateChangeReceiver = new NetStateChangeReceiver() {
        @Override
        protected void onConnected(Context context, String type) {
            mTvMsg.setText("Connected by "+type);
        }

        @Override
        protected void onDisconnected(Context context) {
            mTvMsg.setText("Disconnected");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_network);

        initViews();

        registerReceiver(mNetStateChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        try{
            unregisterReceiver(mNetStateChangeReceiver);
        }catch (Exception e){
            Log.e(e);
        }

        super.onDestroy();
    }

    private void initViews() {
        mTvMsg = (TextView) findViewById(R.id.tv_msg);
    }
}
