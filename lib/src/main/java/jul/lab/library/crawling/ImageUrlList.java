package jul.lab.library.crawling;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;

import java.util.ArrayList;
import java.util.List;

import jul.lab.library.log.Log;

public class ImageUrlList extends ArrayList<String> {

    private OnDataChangeListener mOnDataChangeListener;

//    private Handler mMainLooper = new Handler(Looper.getMainLooper());

    public interface OnDataChangeListener{
        void onAdd(int addCount);
    }

    @MainThread
    public void atomicAdd(List<String> src){
        for(String url : src){
            if(!contains(url)){
                add(url);
                if(mOnDataChangeListener != null){
                    mOnDataChangeListener.onAdd(src.size());
                }
            }
        }

    }

    public void setOnDataChangeListener(OnDataChangeListener listener){
        mOnDataChangeListener = listener;
    }

}
