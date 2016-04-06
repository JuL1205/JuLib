package jul.lib.test.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import jul.lab.library.crawling.ImageUrlList;
import jul.lab.library.crawling.ParsedPageList;
import jul.lab.library.crawling.parser.HTMLImageParser;
import jul.lib.test.R;
import jul.lib.test.adapter.ImageCrawlingAdapter;

/**
 * Created by owner on 2016. 4. 5..
 */
public class ImageCrawlingActivity extends Activity {
    private RecyclerView mRecyclerView;
    private ImageCrawlingAdapter mAdapter;

    private static final String EXTRA_DOMAIN = "extra_domain";
    private static final String EXTRA_PAGE = "extra_page";

    public static void invoke(Context context, String domain, String page){
        Intent i = new Intent(context, ImageCrawlingActivity.class);
        i.putExtra(EXTRA_DOMAIN, domain);
        i.putExtra(EXTRA_PAGE, page);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crawling);

        initViews();

        ImageUrlList urlList = new ImageUrlList();
        new HTMLImageParser(this, getIntent().getStringExtra(EXTRA_DOMAIN), getIntent().getStringExtra(EXTRA_PAGE), urlList, new ParsedPageList()).execute();

        mAdapter = new ImageCrawlingAdapter(getWindowManager().getDefaultDisplay().getWidth(), urlList);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initViews(){
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.setHasFixedSize(true);


    }
}
