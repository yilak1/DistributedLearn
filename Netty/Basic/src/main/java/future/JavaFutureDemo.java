package future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class JavaFutureDemo {
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
    public static void drinkTea(boolean waterOk, boolean cupOK){
        if (waterOk && cupOK) {
            System.out.println("泡茶");
        }else if (!waterOk) {
            System.out.println("烧水去");
        }else {
            System.out.println("刷杯子去");
        }

    }

    public static void main(String[] args) {
        Callable<Boolean> hJob = new HotWaterJob();
        Callable<Boolean> wJob = new WashJob();
        FutureTask<Boolean> hTask = new FutureTask<>(hJob);
        FutureTask<Boolean> wTask = new FutureTask<>(wJob);
        Thread hThread = new Thread(hTask,"hot thread");
        Thread wThread = new Thread(wTask,"wash thread");
        hThread.start();
        wThread.start();
        Thread.currentThread().setName("main thread");
        try {
            boolean waterOk = hTask.get();
            boolean cupOk = wTask.get();
            drinkTea(waterOk, cupOk);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
