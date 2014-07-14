package jul.lab.library.concurrent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by JuL on 2014-07-12.
 */
public abstract class AsyncJob<T> implements Comparable<AsyncJob>{

    public static final int PRIORITY_HIGH				= 5;
    public static final int PRIORITY_NORMAL			= 0;
    public static final int PRIORITY_IDLE				= -5;

    private static final AtomicLong mIdGenerator = new AtomicLong(Long.MIN_VALUE);
    private final long mId;

    final int mPriority;

    public AsyncJob() {
        this(PRIORITY_NORMAL);
    }

    public AsyncJob(int priority) {
        this.mPriority = priority;
        mId = mIdGenerator.getAndIncrement();
    }

    @Override
    public int compareTo(AsyncJob another) {
        if(this.equals(another)){
            return 0;
        } else{
            if (getPriority() == another.getPriority()) {
                return mId < another.mId ? -1 : 1;
            } else {
                return getPriority() > another.getPriority() ? -1 : 1;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || ((Object)this).getClass() != o.getClass()) return false;

        AsyncJob asyncJob = (AsyncJob) o;

        if (mPriority != asyncJob.mPriority) return false;
        if (mId != asyncJob.mId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (mId ^ (mId >>> 32));
        result = 31 * result + mPriority;
        return result;
    }

    public int getPriority() {
        return mPriority;
    }

    public long getId() {
        return mId;
    }

    public void offer() {
        AsyncJobExecutor.offer(this);
    }

    protected abstract T run();

    protected abstract void doneOnMainThread(T result);

    protected void offerFail(){
        ;
    }

}
