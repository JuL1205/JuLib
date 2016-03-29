package jul.lib.test.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import jul.lib.test.R;

/**
 * Created by owner on 2016. 3. 29..
 */
public class CamcoderActivity extends Activity {
    public static void invoke(Context context){
        Intent i = new Intent(context, CamcoderActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camcoder);
    }
}
