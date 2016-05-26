package jul.lib.test.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
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
import jul.lib.test.fragment.PagerAdapter;
import jul.lib.test.presenter.MaterialTransitionPresenter;

/**
 * Created by owner on 2016. 5. 4..
 */
public class MaterialTransitionActivity extends AppCompatActivity{
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    public static void invoke(Context context){
        Intent i = new Intent(context, MaterialTransitionActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_material_transition);

        initViews();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbarLayout.setExpandedTitleColor(0xffa3a3a3);
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(0xffffffff);
    }

    private void initViews(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(pagerAdapter);

        mTabLayout.setTabsFromPagerAdapter(pagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
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
}
