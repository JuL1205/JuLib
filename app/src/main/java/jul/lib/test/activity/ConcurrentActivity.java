package jul.lib.test.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Messenger;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import jul.lab.library.concurrent.AsyncJob;
import jul.lab.library.concurrent.JobChain;
import jul.lab.library.log.Log;
import jul.lib.test.R;

/**
 * Created by owner on 2016. 3. 24..
 */
public class ConcurrentActivity extends Activity {

    private Button mBtnTest;
    private Button mBtnTest2;

    private int TEST_THREAD_COUNT = 10;

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

        mBtnTest2 = (Button) findViewById(R.id.btn_test2);

        mBtnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFinishCount = 0;
                mStartTimeMs = System.currentTimeMillis();
                for(int i = 0 ; i < TEST_THREAD_COUNT ; i++){
                    final AsyncJob job = new AsyncJob<Boolean>() {
                        @Override
                        protected Boolean run() throws InterruptedException {
                            mFinishCount++;
                            Log.i("run : " + mFinishCount);
                            Thread.sleep(1000);
                            return true;
                        }

                        @Override
                        protected void doneOnMainThread(Object result) {
//                            mFinishCount++;
//                            Log.i("doneOnMainThread : "+mFinishCount);
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


        mBtnTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncJob job = new AsyncJob<Boolean>() {
                    protected Boolean run() throws InterruptedException {
                        //run job
                        Log.i("run main job");
                        return true;
                    }

                    protected void doneOnMainThread(Object finalResult) {
                        //chain job까지 모두 완료 되었을때 호출된다.
                        ClassB b = (ClassB) finalResult;

                        Log.i("doneOnMainThread finalResult = "+b.classBString);
                    }
                };

                job.addChain(new JobChain<Integer>() {
                                 protected Integer runChain(Object preResult) throws InterruptedException {
                                     Boolean b = (Boolean) preResult; //true
                                     //run chaining job
                                     Log.i("run chain job1 preResult = "+b);
                                     return 100;
                                 }
                             }
                ).addChain(new JobChain<ClassA>() {
                               protected ClassA runChain(Object preResult) throws InterruptedException {
                                   Integer i = (Integer) preResult; //100
                                   //run chaining job
                                   Log.i("run chain job2 preResult = "+i);
                                   ClassA res = new ClassA();
                                   res.classAInt = -99;
                                   return res;
                               }
                           }
                ).addChain(new JobChain<ClassB>() {
                               protected ClassB runChain(Object preResult) throws InterruptedException {
                                   ClassA a = (ClassA) preResult;
                                   Log.i("run chain job3 preResult = "+a.classAInt);
                                   //run chaining job
                                   ClassB res = new ClassB();
                                   res.classBString = "final";
                                   return res;
                               }
                           }
                );


                job.execute();
            }
        });
    }
    
    
    public class ClassA{
        int classAInt;
    }
    
    public class ClassB{
        String classBString;
    }
}
