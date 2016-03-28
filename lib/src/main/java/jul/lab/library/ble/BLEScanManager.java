package jul.lab.library.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

/**
 * Created by owner on 2016. 3. 28..
 */
public class BLEScanManager {

    public interface OnScanStateListener{
        void onScan();
        void onFindBeacon(String deviceName, String uuid, int major, int minor, double distance);
    }
    private static BLEScanManager sInstance = new BLEScanManager();

    private OnScanStateListener mOnScanStateListener;

    private char[] mHexArray = "0123456789ABCDEF".toCharArray();

    private static final int WHAT_PAUSE = 1;
    private static final int WHAT_RESUME = 2;

    private static final long SCAN_TIMEOUT = 60 * 1000; //BLE scan timeout 시간
    private static final long PAUSE_TIMEOUT = 30 * 1000;    //BLE scan pause timeout 시간

    private Handler mScanPeriodHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if(msg.what == WHAT_PAUSE){
                BLEScanManager.getInstance().stop();
                sendMessageDelayed(Message.obtain(this, WHAT_RESUME), PAUSE_TIMEOUT);
            } else if(msg.what == WHAT_RESUME){
                BLEScanManager.getInstance().scan();
            }
        }
    };

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;

    private BLEScanManager(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            try{
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

                        int startByte = 2;
                        boolean patternFound = false;
                        while (startByte <= 5) {
                            if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                                patternFound = true;
                                break;
                            }
                            startByte++;
                        }

                        if (patternFound) {
                            //Convert to hex String
                            byte[] uuidBytes = new byte[16];
                            System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
                            String hexString = bytesToHex(uuidBytes);

                            //Here is your UUID
                            String uuid = hexString.substring(0, 8) + "-" +
                                    hexString.substring(8, 12) + "-" +
                                    hexString.substring(12, 16) + "-" +
                                    hexString.substring(16, 20) + "-" +
                                    hexString.substring(20, 32);

                            //Here is your Major value
                            int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);

                            //Here is your Minor value
                            int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);

                            int txPw = scanRecord[startByte + 24];

                            final double accuracy = calculateAccuracy(txPw, rssi);

                            if(mOnScanStateListener != null){
                                mOnScanStateListener.onFindBeacon(device.getName(), uuid, major, minor, accuracy);
                            }
                        }
                    }
                };
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public static BLEScanManager getInstance(){
        if(sInstance == null){
            sInstance = new BLEScanManager();
        }
        return sInstance;
    }


    public static boolean isSupportBLE(Context context){
        return getInstance().mBluetoothAdapter != null && getInstance().mLeScanCallback != null && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    public boolean isBluetoothOn() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = mHexArray[v >>> 4];
            hexChars[j * 2 + 1] = mHexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    protected double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }

        double ratio = rssi*1.0/txPower;
        if (ratio < 1.0) {
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }

    public void scan(){
        if(isBluetoothOn()/* && mBluetoothAdapter.startLeScan(mLeScanCallback)*/){
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            mScanPeriodHandler.removeMessages(WHAT_RESUME);
            mScanPeriodHandler.removeMessages(WHAT_PAUSE);

            mScanPeriodHandler.sendMessageDelayed(Message.obtain(mScanPeriodHandler, WHAT_PAUSE), SCAN_TIMEOUT);
            if(mOnScanStateListener != null){
                mOnScanStateListener.onScan();
            }
        }
    }

    public void stop(){
        if(mBluetoothAdapter != null){
            mScanPeriodHandler.removeMessages(WHAT_RESUME);
            mScanPeriodHandler.removeMessages(WHAT_PAUSE);
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    public void enable(){
        if(mBluetoothAdapter != null){
            mBluetoothAdapter.enable();
        }
    }

    public void disable(){
        if(mBluetoothAdapter != null){
            mBluetoothAdapter.disable();
        }
    }

    /**
     * 반드시 scan을 호출하기 전에 호출되어야 한다.
     * @param listener
     */
    public void setOnScanStateListener(OnScanStateListener listener){
        mOnScanStateListener = listener;
    }
}
