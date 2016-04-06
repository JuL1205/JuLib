package jul.lib.test.crawling;

import java.util.ArrayList;

/**
 * Created by owner on 2016. 4. 6..
 */
public class ParsedPageList extends ArrayList<String> {
    public boolean wasParsed(String page){
        synchronized (this){
            return contains(page);
        }
    }


    public void atomicAdd(String page){
        synchronized (this){
            add(page);
        }
    }
}
