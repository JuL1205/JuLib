package jul.lib.test;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import jul.lib.test.activity.CamcoderActivity;
import jul.lib.test.activity.CollapsingToolbarActivity;
import jul.lib.test.activity.ConcurrentActivity;
import jul.lib.test.activity.ImageCrawlingActivity;
import jul.lib.test.activity.MaterialTransitionActivity;
import jul.lib.test.activity.NetworkActivity;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private ListView mLvMenu;
    private Context mContext;

    private Toolbar mToolbar;

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

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
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
                View v = LayoutInflater.from(this).inflate(R.layout.layout_domain_dlg, null);
                final EditText etDomain = (EditText) v.findViewById(R.id.et_domain);
//                etDomain.setText("http://www.kakaocorp.com");
                final EditText etPage = (EditText) v.findViewById(R.id.et_page);
//                etPage.setText("");
                new AlertDialog.Builder(this).setView(v)
                        .setPositiveButton("GO!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ImageCrawlingActivity.invoke(MainActivity.this, etDomain.getText().toString(), etPage.getText().toString());
                            }
                        })
                        .show();

                break;
            case 4:
                MaterialTransitionActivity.invoke(this);
                break;
            default:
                break;
        }

    }
}
