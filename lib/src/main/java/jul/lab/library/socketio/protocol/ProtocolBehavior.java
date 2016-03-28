package jul.lab.library.socketio.protocol;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by owner on 2015. 8. 12..
 */
public abstract class ProtocolBehavior {
    @SerializedName("UserNo")
    protected int _userNo;

    public ProtocolBehavior(int usrNo){
        _userNo = usrNo;
    }

    public abstract String getRequestName();
    public abstract String getResponseName();
    public String getRequestParam2String(){
        return new Gson().toJson(this).toString();
    }
}
