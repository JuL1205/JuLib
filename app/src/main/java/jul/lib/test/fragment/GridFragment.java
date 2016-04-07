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

import jul.lab.library.crawling.ImageUrlList;
import jul.lab.library.crawling.ParsedPageList;
import jul.lab.library.crawling.parser.HTMLImageParser;
import jul.lab.library.log.Log;
import jul.lab.library.transition.DynamicChangeTransition;
import jul.lib.test.R;
import jul.lib.test.adapter.ImageCrawlingAdapter;

/**
 * Created by owner on 2016. 4. 7..
 */
public class GridFragment extends Fragment implements ImageCrawlingAdapter.OnThumbClickListener{

    public GridFragment(String domain, String page, ImageUrlList list){
        Bundle b = new Bundle();
        b.putString("domain", domain);
        b.putString("page", page);
        b.putSerializable("list", list);
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

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ImageUrlList urlList = (ImageUrlList) getArguments().getSerializable("list");

        recyclerView.setAdapter(new ImageCrawlingAdapter(getActivity().getWindowManager().getDefaultDisplay().getWidth(), urlList, this));
        new HTMLImageParser(getActivity(), getArguments().getString("domain"), getArguments().getString("page"), urlList, new ParsedPageList()).execute();
    }

    @Override
    public void onClick(ImageCrawlingAdapter.ViewHolder holder, String url) {
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
