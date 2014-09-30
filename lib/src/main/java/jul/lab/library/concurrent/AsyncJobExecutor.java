package jul.lab.library.concurrent;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import jul.lab.library.log.Log;

/**
 * Created by JuL on 2014-07-12.
 */
public class AsyncJobExecutor {
    private static final int THREAD_POOL_CORE_SIZE = 1;
    private static final int THREAD_POOL_MAX_SIZE = 1;
    private static final long THREAD_POOL_ALIVE_TIME = 1000;

    private static ThreadPoolExecutor mThreadPoolExecutor = null;

    static {
        mThreadPoolExecutor = new ThreadPoolExecutor(
                THREAD_POOL_CORE_SIZE, THREAD_POOL_MAX_SIZE,
                THREAD_POOL_ALIVE_TIME, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<Runnable>());
        mThreadPoolExecutor.prestartCoreThread();
    }

    private static Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    private static BlockingQueue<Runnable> getQueue(){
        return mThreadPoolExecutor.getQueue();
    }

    synchronized static void offer(final AsyncJob job) {
        if (job == null) {
            return;
        }

        boolean offerResult = getQueue().offer(new ComparableRunnable(job) {
            @Override
            public void run() {
                final Object result = job.run();
                dispatchResult(job, result);
            }
        });
        if(!offerResult){
            job.offerFail();
        }
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
