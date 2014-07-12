package jul.lab.library.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by JuL on 2014-07-12.
 */
public class NetState {

    public static String isAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] infos = cm.getAllNetworkInfo();
        for (NetworkInfo info : infos) {
            if (info != null) {
                boolean isConnected = info.isConnectedOrConnecting();
                boolean isAvailable = info.isAvailable();

                if (isConnected && isAvailable) {
                    return info.getTypeName();
                }
            }
        }
        return null;
    }
}
