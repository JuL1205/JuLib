package jul.lib.test.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import jul.lib.test.R;

/**
 * Created by owner on 2016. 5. 2..
 */
public class CollapsingToolbarActivity extends AppCompatActivity {

    public static void invoke(Context context){
        Intent i = new Intent(context, CollapsingToolbarActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_collapsing_toolbar);


    }
}
