package jul.lab.library.crawling;

import java.util.ArrayList;

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
