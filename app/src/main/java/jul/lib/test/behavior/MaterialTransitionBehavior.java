package jul.lib.test.behavior;

import java.util.List;

/**
 * Created by owner on 2016. 5. 5..
 */
public interface MaterialTransitionBehavior {

    interface View{
        void updateImageList(List<Integer> imageResIds);
    }

    interface Presenter{
        void initSampleImageList();
    }
}
