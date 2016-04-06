package jul.lib.test.crawling;

import java.util.ArrayList;

/**
 * Created by owner on 2016. 4. 5..
 */
public class ImagePathList extends ArrayList<String> {

    /**
     * 중복을 제거한 add
     * @param obj
     */
    public synchronized void atomicAdd(String obj){
        if(!contains(obj)){
            add(obj);
        }
    }

}
