package jul.lab.library.socketio.response;

import com.google.gson.Gson;

import jul.lab.library.log.Log;

/**
 * Created by owner on 2015. 8. 12..
 */
public abstract class OnResponseListener<T extends ResponseModel> {
    private Class<T> mType;

    public OnResponseListener(Class<T> type){
        mType = type;
    }
    public ResponseModel onResponse(String event, String resJson) {
        try{
            T model = new Gson().fromJson(resJson, mType);
            Log.d("OnResponseListener result = " + model.toString());
            onResponse(event, model);

            return model;
        }catch(Exception e){
            Log.w("OnResponseListener ERROR ON \n" + event + " / origin json = " + resJson);
            e.printStackTrace();
        }

        return null;
    }

    protected abstract void onResponse(String event, T res);
}
