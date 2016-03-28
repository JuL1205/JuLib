package jul.lab.library.concurrent;

/**
 * Created by JuL on 2014-07-12.<br><br>
 *
 * {@link AsyncJob} 에 chain되는 작업을 정의할 때 사용한다. <br>
 * main job 과 chain job은 모두 같은 thread에서 수행될 것이다. <br>
 *
 *
 * <p>Generally used like this:
 * <pre>
 * AsyncJob job = new AsyncJob<Boolean>() {
 * protected Boolean run() throws InterruptedException {
 *      //run job
 *      return true;
 *      }
 *
 * protected void doneOnMainThread(Object finalResult) {
 *      //chain job까지 모두 완료 되었을때 호출된다.
 *
 *      ClassB b = (ClassB) finalResult;
 * }};
 *
 * job.addChain(new JobChain<Integer>() {
 * protected Integer runChain(Object preResult) throws InterruptedException {
 *      Boolean b = (Boolean) preResult; //true
 *      //run chaining job
 *      return 100;
 *      }
 *   }
 * ).addChain(new JobChain<ClassA>() {
 * protected ClassA runChain(Object preResult) throws InterruptedException {
 *      Integer i = (Integer) preResult; //100
 *      //run chaining job
 *
 *      ClassA res = new ClassA();
 *      return res;
 *      }
 *   }
 * ).addChain(new JobChain<ClassB>() {
 * protected ClassB runChain(Object preResult) throws InterruptedException {
 *      ClassA a = (ClassA) preResult;
 *      //run chaining job
 *      ClassB res = new ClassB();
 *      return res;
 *      }
 *   }
 * );
 *
 *
 * job.execute();
 * </pre>
 *
 */
public abstract class JobChain<Result> {

    /**
     * 실제 작업이 돌아가는 부분.
     * @param preResult 이전 작업에서 return된 value.
     * @return 다음 작업으로 전달할 value.
     * @throws InterruptedException
     */
    protected abstract Result runChain(Object preResult) throws InterruptedException;
}
