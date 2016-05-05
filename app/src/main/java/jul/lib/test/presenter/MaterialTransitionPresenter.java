package jul.lib.test.presenter;

import java.util.List;

import jul.lib.test.behavior.MaterialTransitionBehavior;
import jul.lib.test.model.SampleImageModel;

/**
 * Created by owner on 2016. 5. 5..
 */
public class MaterialTransitionPresenter implements MaterialTransitionBehavior.Presenter {

    private MaterialTransitionBehavior.View mView;
    private SampleImageModel mSampleImageModel;

    public MaterialTransitionPresenter(MaterialTransitionBehavior.View viewImpl){
        mView = viewImpl;
        mSampleImageModel = new SampleImageModel();
    }

    @Override
    public void initSampleImageList() {
        mView.updateImageList(mSampleImageModel.getImageResIds());
    }
}
