package jul.lib.test.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

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

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

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

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbarLayout.setExpandedTitleColor(0xffff0000);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(0xffffffff);

        mPresenter.initSampleImageList();

    }

    private void initViews(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new MaterialTransitionAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateImageList(List<Integer> imageResIds) {
        mAdapter.setImageResIds(imageResIds);
    }
}
