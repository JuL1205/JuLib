package jul.lib.test.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.concurrent.locks.ReentrantLock;

import jul.lab.library.crawling.ImageUrlList;
import jul.lab.library.crawling.ParsedPageList;
import jul.lab.library.crawling.parser.HTMLImageParser;
import jul.lab.library.log.Log;
import jul.lab.library.transition.DynamicChangeTransition;
import jul.lib.test.R;
import jul.lib.test.activity.ImageCrawlingActivity;
import jul.lib.test.adapter.ImageCrawlingAdapter;

public class GridFragment extends Fragment implements ImageCrawlingAdapter.OnThumbClickListener, ImageUrlList.OnDataChangeListener{

    private RecyclerView mRecyvlerView;
    private ImageCrawlingAdapter mImageCrawlingAdapter;
    private GridLayoutManager mGridLayoutManager;
    private ReentrantLock mReentrantLock;
    private ProgressBar mPbLoading;

    @Override
    public void onAdd(int addCount) {
        mImageCrawlingAdapter.notifyDataSetChanged();
        mPbLoading.setVisibility(View.GONE);

        ((ImageCrawlingActivity)getActivity()).setCount(mImageCrawlingAdapter.getItemCount());

//        int lastVisibleIndex = mGridLayoutManager.findLastVisibleItemPosition();
//        int totalCount = mImageCrawlingAdapter.getItemCount();
//        if(totalCount - 1 > lastVisibleIndex){
//            if(!mReentrantLock.isLocked()){
//                mReentrantLock.lock();
//            }
//        }
    }

    public GridFragment(String domain, String page, ImageUrlList list, ReentrantLock lock){
        Bundle b = new Bundle();
        b.putString("domain", domain);
        b.putString("page", page);
        b.putSerializable("list", list);
        b.putSerializable("lock", lock);
        setArguments(b);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grid, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPbLoading = (ProgressBar) view.findViewById(R.id.pb_loading);


        mRecyvlerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mGridLayoutManager = new GridLayoutManager(getContext(), 4);
        mRecyvlerView.setLayoutManager(mGridLayoutManager);
        mRecyvlerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyvlerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                int lastVisibleIndex = mGridLayoutManager.findLastVisibleItemPosition();
//                int totalCount = mImageCrawlingAdapter.getItemCount();
//                if (lastVisibleIndex == totalCount - 1) {
//                    synchronized (mReentrantLock) {
//                        if (mReentrantLock.isLocked()) {
//                            mReentrantLock.unlock();
//                        }
//                    }
//                }
//            }
//        });

        ImageUrlList urlList = (ImageUrlList) getArguments().getSerializable("list");
        urlList.setOnDataChangeListener(this);

        if(urlList.size() > 0){
            mPbLoading.setVisibility(View.GONE);
        }

        mImageCrawlingAdapter = new ImageCrawlingAdapter(getActivity().getWindowManager().getDefaultDisplay().getWidth(), urlList, this);
        mRecyvlerView.setAdapter(mImageCrawlingAdapter);
        mReentrantLock = (ReentrantLock) getArguments().getSerializable("lock");
        new HTMLImageParser(getActivity(), getArguments().getString("domain"), getArguments().getString("page"), urlList, new ParsedPageList(), mReentrantLock).execute();
    }

    @Override
    public void onClick(ImageCrawlingAdapter.ViewHolder holder, String url) {
        Log.i(url);
        ImageShowFragment showFragment = ImageShowFragment.newInstance(url);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            showFragment.setSharedElementEnterTransition(new DynamicChangeTransition());
            showFragment.setEnterTransition(new Fade());
            setExitTransition(new Fade());
            showFragment.setSharedElementReturnTransition(new DynamicChangeTransition());
        }

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(holder.mIvThumb, "kittenImage")
                .replace(R.id.container, showFragment)
                .addToBackStack(null)
                .commit();
    }
}
