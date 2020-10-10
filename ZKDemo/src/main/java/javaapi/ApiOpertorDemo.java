package javaapi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ApiOpertorDemo implements Watcher{
    private final static String CONNECTSTRING = "127.0.0.1:2181";
    private static CountDownLatch countDownLatch = new CountDownLatch(1);
    private static Stat stat= new Stat();

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        //System.setProperties(new Properties("zookeeper.clientCnxnSocket", ""));
        ZooKeeper zooKeeper = new ZooKeeper(CONNECTSTRING, 5000, new ApiOpertorDemo());
        System.out.println(zooKeeper.getState());
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Zookeper session establish\n" + zooKeeper);
        //创建节点
//        String result = zooKeeper.create("/node2", "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
//        zooKeeper.getData("/node2", new ApiOpertorDemo(), stat);
//        System.out.println("创建成功" + result);
        //System.out.println("stat:" + stat);
        //修改数据
        //zooKeeper.setData("/node2", "deer2".getBytes(), -1);

        String path = "/node22";
        zooKeeper.create(path, "123".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        Stat stat = zooKeeper.exists(path + "/node1", true);
        if (stat == null) {
            zooKeeper.create(path + "/node1", "123".getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        //修改子路径
        zooKeeper.setData(path + "/node1", "deer".getBytes(), -1);
        //获取指定路径下的子节点
        List<String> children = zooKeeper.getChildren(path, true);
        System.out.println(children);

    }

    public void process(WatchedEvent watchedEvent) {
        //如果当前的连接状态是连接成功的，那么通过计数器去控制
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                countDownLatch.countDown();
                System.out.println(watchedEvent.getState()+"-->"+watchedEvent.getType());
            }else if (Event.EventType.NodeCreated == watchedEvent.getType()) {

            }else if (Event.EventType.NodeDataChanged == watchedEvent.getType()){

            }
        }
    }
}
