package jul.lib.test.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jul.lib.test.R;
import jul.lib.test.adapter.MaterialTransitionAdapter;
import jul.lib.test.behavior.MaterialTransitionBehavior;
import jul.lib.test.presenter.MaterialTransitionPresenter;

/**
 * Created by owner on 2016. 5. 18..
 */
public class TabFragment extends Fragment implements MaterialTransitionBehavior.View{
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private MaterialTransitionAdapter mAdapter;

    private MaterialTransitionBehavior.Presenter mPresenter;

    public static TabFragment newInstance() {
        TabFragment myFragment = new TabFragment();
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRecyclerView = new RecyclerView(getActivity());
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new MaterialTransitionAdapter();
        mRecyclerView.setAdapter(mAdapter);

        return mRecyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPresenter = new MaterialTransitionPresenter(this);

        mPresenter.initSampleImageList();
    }

    @Override
    public void updateImageList(List<Integer> imageResIds) {
        mAdapter.setImageResIds(imageResIds);
    }
}
