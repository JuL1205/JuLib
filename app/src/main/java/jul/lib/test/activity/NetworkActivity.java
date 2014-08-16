package jul.lib.test.activity;

import android.content.Context;
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
public class NetworkActivity extends ActionBarActivity{

    private Context mContext;
    private TextView mTvMsg;

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

        mContext = this;

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
