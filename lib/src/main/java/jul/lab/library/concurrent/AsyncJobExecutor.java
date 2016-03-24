package jul.lab.library.concurrent;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import jul.lab.library.log.Log;

/**
 * Created by JuL on 2014-07-12.
 */
public class AsyncJobExecutor {
    private static ExecutorService mThreadPoolExecutor = null;

    static {
        mThreadPoolExecutor = Executors.newCachedThreadPool();
    }

    private static Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    synchronized static void execute(final AsyncJob job) {
        if (job == null) {
            return;
        }

        mThreadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (!job.bIsCancel) {
                    try{
                        dispatchResult(job, job.run());
                    } catch (InterruptedException e){
                    }
                } else {
                    Log.w("This job was canceled.");
                }
            }
        });
    }

    private static void dispatchResult(final AsyncJob job, final Object result){
        mMainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                job.doneOnMainThread(result);
            }
        });
    }
}
