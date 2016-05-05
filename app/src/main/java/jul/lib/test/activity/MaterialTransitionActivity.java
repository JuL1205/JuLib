package jul.lib.test.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import jul.lib.test.R;
import jul.lib.test.adapter.MaterialTransitionAdapter;
import jul.lib.test.behavior.MaterialTransitionBehavior;
import jul.lib.test.presenter.MaterialTransitionPresenter;

/**
 * Created by owner on 2016. 5. 4..
 */
public class MaterialTransitionActivity extends AppCompatActivity implements MaterialTransitionBehavior.View{

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private MaterialTransitionAdapter mAdapter;

    private MaterialTransitionBehavior.Presenter mPresenter;

    public static void invoke(Context context){
        Intent i = new Intent(context, MaterialTransitionActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_material_transition);

        mPresenter = new MaterialTransitionPresenter(this);

        initViews();

        mPresenter.initSampleImageList();
    }

    private void initViews(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new MaterialTransitionAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void updateImageList(List<Integer> imageResIds) {
        mAdapter.setImageResIds(imageResIds);
    }
}
