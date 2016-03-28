package jul.lab.library.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import jul.lab.library.log.Log;

/**
 * Created by owner on 2016. 3. 28..
 */
public abstract class BTStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Log.e("Bluetooth off");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.e("Turning Bluetooth off...");
                    break;
                case BluetoothAdapter.STATE_ON:
                    Log.e("Bluetooth on");
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.e("Turning Bluetooth on...");
                    break;
            }

            onStateChanged(state);
        }
    }


    protected abstract void onStateChanged(int state);

}

