package com.product.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class ServiceRegister implements Watcher{

    private  static final String BASE_SERVICES = "/services";
    private static final String  SERVICE_NAME="/products";
    private static ZooKeeper zooKeeper;
    private static String address;
    private static int port;
    public static void register(String address1, int port1) throws IOException {
        address = address1;
        port = port1;

        zooKeeper = new ZooKeeper("192.168.3.21:2181,192.168.3.22:2181,192.168.3.23:2181",
                5000, new ServiceRegister());

    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            Stat stat = zooKeeper.exists(BASE_SERVICES, false);
            if (stat == null) {
                zooKeeper.create(BASE_SERVICES, "".getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
            Stat stat1 = zooKeeper.exists(BASE_SERVICES + SERVICE_NAME, false);
            if (stat == null) {
                zooKeeper.create(BASE_SERVICES + SERVICE_NAME, "".getBytes(),
                        ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            String server_path = address + ":" + port;
            zooKeeper.create(BASE_SERVICES + SERVICE_NAME + "/child",
                    server_path.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
