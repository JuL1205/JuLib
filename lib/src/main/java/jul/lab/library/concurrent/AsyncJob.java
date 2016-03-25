package jul.lab.library.concurrent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by JuL on 2014-07-12.
 *
 * 비동기 작업 클래스. 실제 작업소는 {@link AsyncJobExecutor} 이며, UI callback까지 래핑한다.
 * UI callback 시 param type은 generic으로 정의한다.
 */
public abstract class AsyncJob<Result> {

    boolean bIsCancel;

    List<JobChain> mJobChainList = new ArrayList<>();

    public AsyncJob() {
    }

    public void execute() {
        AsyncJobExecutor.execute(this);
    }

    public void cancel(){
        bIsCancel = true;
    }

    public boolean isCancelled(){
        return bIsCancel;
    }

    public AsyncJob addChain(JobChain jobChain){
        mJobChainList.add(jobChain);

        return this;
    }

    public Iterator<JobChain> getChainList(){
        return mJobChainList.listIterator();
    }

    /**
     * 작업의 시작은 {@link #execute()} 를 호출해야 함에 주의.
     */
    protected abstract Result run() throws InterruptedException;

    protected abstract void doneOnMainThread(Result result);
}
