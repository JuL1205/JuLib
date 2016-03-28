package jul.lab.library.concurrent;

import android.os.Handler;
import android.os.Looper;

import java.util.Iterator;
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
 * Created by JuL on 2014-07-12.<br><br>
 *
 * 작업이 수행될 thread를 관리하는 class.<br>
 * thread 관리정책은 {@link Executors#newCachedThreadPool()} 을 따른다.<br><br>
 *
 * 기본적으로 작업은 {@link AsyncJob}(main job)과 {@link JobChain}(chain job)으로 나눠지고,
 * 각각의 main job은 서로 다른 thread에서, main job과 chain job은 같은 thread에서 수행될 것이다.
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
                        dispatchResult(job, job.run(), job.getChainList());
                    } catch (InterruptedException e){
                    }
                } else {
                    Log.w("This job was canceled.");
                }
            }
        });
    }

    private static void dispatchResult(final AsyncJob job, final Object result, Iterator<JobChain> chainList){
        if(chainList.hasNext()){ //연결된 작업들이 존재한다.
            JobChain jobChain = chainList.next();
            if (!job.bIsCancel) {
                try {
                    dispatchResult(job, jobChain.runChain(result), chainList);
                } catch (InterruptedException e) {
                }
            } else{
                Log.w("This job was canceled[in chaining].");
            }
        } else{
            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    job.doneOnMainThread(result);
                }
            });
        }
    }
}
