package jul.lib.test.model;

import java.util.ArrayList;
import java.util.List;

import jul.lib.test.R;

/**
 * Created by owner on 2016. 5. 4..
 */
public class SampleImageModel {


    public List<Integer> getImageResIds(){
        List<Integer> reVal = new ArrayList<>();
        reVal.add(R.drawable.sample1);
        reVal.add(R.drawable.sample2);
        reVal.add(R.drawable.sample3);
        reVal.add(R.drawable.sample4);
        reVal.add(R.drawable.sample5);
        reVal.add(R.drawable.sample6);
        reVal.add(R.drawable.sample7);
        reVal.add(R.drawable.sample8);
        reVal.add(R.drawable.sample9);
        reVal.add(R.drawable.sample10);

        return reVal;
    }
}
