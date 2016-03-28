package jul.lab.library.socketio.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by owner on 2015. 8. 12..
 */
public class ResponseModel implements Serializable {
    @SerializedName("UserNo")
    public int _userNo = -1;

    @SerializedName("ResponseCode")
    public int _resCode;

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{" +
                "_userNo=" + _userNo +
                ", _resCode=" + _resCode + "}";
    }
}
