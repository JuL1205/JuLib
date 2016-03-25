package jul.lab.library.concurrent;

/**
 * Created by owner on 2016. 3. 25..
 */
public abstract class JobChain<Result> {
    protected abstract Result runChain(Object preResult) throws InterruptedException;
}
