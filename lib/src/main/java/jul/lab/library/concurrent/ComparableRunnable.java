package jul.lab.library.concurrent;

/**
 * Created by jul1205 on 2014-09-30.
 */
abstract class ComparableRunnable implements Runnable, Comparable<ComparableRunnable>{
    AsyncJob mJob;

    public ComparableRunnable(AsyncJob job) {
        mJob = job;
    }

    @Override
    public int compareTo(ComparableRunnable another) {
        return mJob.compareTo(another.mJob);
    }
}
