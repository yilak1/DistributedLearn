package future;


import com.google.common.util.concurrent.*;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuavaFutureDemo {
    public static final int SLEEP_GAP = 500;

    public static String getCurThreadName(){
        return Thread.currentThread().getName();
    }

    static class HotWaterJob implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            try{
                System.out.println("洗好水壶");
                System.out.println("灌水");
                System.out.println("烧水");
                Thread.sleep(SLEEP_GAP);
                System.out.println("水开了");

            }catch (InterruptedException e) {
                System.out.println("异常中断");
                return false;
            }
            System.out.println("运行结束");
            return true;
        }
    }

    static class WashJob implements Callable<Boolean> {

        @Override
        public Boolean call() throws Exception {
            try{
                System.out.println("洗茶壶");
                System.out.println("茶杯");
                System.out.println("拿茶叶");
                Thread.sleep(SLEEP_GAP);
                System.out.println("洗完了");

            }catch (InterruptedException e) {
                System.out.println("异常中断");
                return false;
            }
            System.out.println("运行结束");
            return true;
        }
    }

    static class MainJob implements Runnable{
        boolean warterOk = false;
        boolean cupOk = false;
        int gap = SLEEP_GAP / 10;

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(gap);
                    System.out.println("读书中。。。。。");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (warterOk && cupOk) {
                    drinkTea(warterOk, cupOk);
                }
            }

        }
        public void drinkTea(Boolean wOk, Boolean cOK) {
            if (wOk && cOK) {
                System.out.println("泡茶喝，茶喝完");
                this.warterOk = false;
                this.gap = SLEEP_GAP * 100;
            } else if (!wOk) {
                System.out.println("烧水失败，没有茶喝了");
            } else if (!cOK) {
                System.out.println("杯子洗不了，没有茶喝了");
            }

        }
    }

    public static void main(String[] args) {
        final MainJob mainJob = new MainJob();
        Thread mainThread = new Thread(mainJob);
        mainThread.setName("main thread");
        mainThread.start();

        Callable<Boolean> hotJob = new HotWaterJob();
        Callable<Boolean> washJob = new WashJob();
        //java 线程池
        ExecutorService jPoll = Executors.newFixedThreadPool(10);
        //包装java线程池，构造guava 线程池
        ListeningExecutorService gPool = MoreExecutors.listeningDecorator(jPoll);
        //提交烧水的业务逻辑，取到异步任务
        ListenableFuture<Boolean> hotFuture = gPool.submit(hotJob);
        Futures.addCallback(hotFuture, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(@Nullable Boolean result) {
                if (result){
                    mainJob.warterOk = true;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("烧水失败");
            }
        }, jPoll);
        //提交清理茶壶逻辑
        ListenableFuture<Boolean> washFuture = gPool.submit(washJob);
        Futures.addCallback(washFuture, new FutureCallback<Boolean>(){

            @Override
            public void onSuccess(@Nullable Boolean result) {
                if (result) {
                    mainJob.cupOk = true;
                }
            }

            @Override
            public void onFailure(Throwable t) {
                System.out.println("杯子洗不了，没有茶喝了");
            }
        }, jPoll);


    }
}
