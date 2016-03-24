package jul.lib.test.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import jul.lab.library.concurrent.AsyncJob;
import jul.lab.library.log.Log;
import jul.lib.test.R;

/**
 * Created by owner on 2016. 3. 24..
 */
public class ConcurrentActivity extends Activity {

    private Button mBtnTest;

    private int TEST_THREAD_COUNT = 100;

    public static void invoke(Context context){
        Intent i = new Intent(context, ConcurrentActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_concurrent);

        initViews();


    }

    int mFinishCount = 0;
    long mStartTimeMs;
    private void initViews(){
        mBtnTest = (Button) findViewById(R.id.btn_test);

        mBtnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFinishCount = 0;
                mStartTimeMs = System.currentTimeMillis();
                for(int i = 0 ; i < TEST_THREAD_COUNT ; i++){
                    final AsyncJob job = new AsyncJob<Boolean>() {
                        @Override
                        protected Boolean run() throws InterruptedException {
                            Thread.sleep(10000);
                            return true;
                        }

                        @Override
                        protected void doneOnMainThread(Boolean result) {
                            mFinishCount++;
                            Log.i("doneOnMainThread : "+mFinishCount);
                            if(mFinishCount == TEST_THREAD_COUNT){
                                long duration = System.currentTimeMillis() - mStartTimeMs;
                                Log.i("total duration = "+duration);
                                Toast.makeText(ConcurrentActivity.this, "총 걸린 시간 : "+(duration/1000)+" sec.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    };
                    job.execute();
                }
            }
        });
    }
}
