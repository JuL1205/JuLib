package jul.lib.test.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import jul.lib.test.R;
import jul.lib.test.crawling.ImagePathList;
import jul.lib.test.crawling.ParsedPageList;
import jul.lib.test.crawling.parser.HTMLImageParser;

/**
 * Created by owner on 2016. 4. 5..
 */
public class ImageDownloadActivity extends Activity {


    public static void invoke(Context context){
        Intent i = new Intent(context, ImageDownloadActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_download);


        new HTMLImageParser("/default.aspx", new ImagePathList(), new ParsedPageList()).execute();
    }
}
