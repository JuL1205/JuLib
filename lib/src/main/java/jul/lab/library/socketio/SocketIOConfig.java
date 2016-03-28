package jul.lab.library.socketio;


import java.util.ArrayList;
import java.util.List;

import jul.lab.library.socketio.protocol.ProtocolBehavior;

/**
 * Created by owner on 2016. 1. 19..
 */
public class SocketIOConfig {

    private String mIp;
    private int mPort;
    private String mLogFileDir;
    private String mLogFileName;
    private boolean bLogFileModeEnable = false;
    private List<Integer> mInvalidAuthCode = new ArrayList<>();
    private ProtocolBehavior mStartProtocol;

    public static class Builder{
        private String mIp;
        private int mPort;
        private String mLogFileDir;
        private String mLogFileName;
        private boolean bLogFileModeEnable = false;
        private Integer[] mInvalidAuthCode;
        private ProtocolBehavior mStartProtocol;

        public Builder setIp(String ip, int port){
            mIp = ip;
            mPort = port;

            return this;
        }

        public Builder setInvalidAuthResCode(Integer... codes){
            mInvalidAuthCode = codes;

            return this;
        }

        public Builder setLogFilePath(String dirPath, String fileName){
            mLogFileDir = dirPath;
            mLogFileName = fileName;

            return this;
        }

        public Builder setLogFileMode(boolean enable){
            bLogFileModeEnable = enable;

            return this;
        }

        public Builder setStartProtocol(ProtocolBehavior startProtocol){
            mStartProtocol = startProtocol;

            return this;
        }

        public SocketIOConfig build(){
            return new SocketIOConfig(this);
        }
    }

    private SocketIOConfig(Builder builder){
        mIp = builder.mIp;
        mPort = builder.mPort;
        mLogFileDir = builder.mLogFileDir;
        mLogFileName = builder.mLogFileName;
        bLogFileModeEnable = builder.bLogFileModeEnable;
        mStartProtocol = builder.mStartProtocol;
        if(builder.mInvalidAuthCode != null){
            for(Integer i : builder.mInvalidAuthCode){
                mInvalidAuthCode.add(i);
            }
        }
    }
    
    public List<Integer> getInvalidAuthCodeList(){
        return mInvalidAuthCode;
    }

    public String getIp(){
        return mIp;
    }

    public int getPort(){
        return mPort;
    }

    public String getLogFileDir(){
        return mLogFileDir;
    }

    public String getLogFileName(){
        return mLogFileName;
    }

    public ProtocolBehavior getStartProtocol(){
        return mStartProtocol;
    }

    public boolean isLogFileMode(){
        return bLogFileModeEnable;
    }
}
