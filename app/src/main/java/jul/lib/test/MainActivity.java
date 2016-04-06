package jul.lib.test;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import jul.lib.test.activity.CamcoderActivity;
import jul.lib.test.activity.ConcurrentActivity;
import jul.lib.test.activity.ImageCrawlingActivity;
import jul.lib.test.activity.NetworkActivity;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{

    private ListView mLvMenu;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        initViews();
    }

    private void initViews(){
        mLvMenu = (ListView) findViewById(R.id.lv_menu);

        mLvMenu.setAdapter(new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.main_menu)));
        mLvMenu.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                NetworkActivity.invoke(this);
                break;
            case 1:
                ConcurrentActivity.invoke(this);
                break;
            case 2:
                CamcoderActivity.invoke(this);
                break;
            case 3:
                ImageCrawlingActivity.invoke(this);
                break;
            default:
                break;
        }

    }
}
