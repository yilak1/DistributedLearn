package com.order.listener;

import com.order.utils.LoadBalance;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class InitListener implements ServletContextListener {

    private  static final String BASE_SERVICES = "/services";
    private static final String  SERVICE_NAME="/products";

    private ZooKeeper zooKeeper;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            zooKeeper = new ZooKeeper("192.168.3.21:2181,192.168.3.22:2181,192.168.3.23:2181",
                    5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged &&
                            watchedEvent.getPath().equals(BASE_SERVICES+SERVICE_NAME)) {
                        updateServiceList();
                    }
                }
            });
            //最开始的update，节点没发生变化的时候。
            updateServiceList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateServiceList(){
        try {
            List<String> children = zooKeeper.getChildren(BASE_SERVICES + SERVICE_NAME, true);
            List<String> newServerList = new ArrayList<String>();
            for (String subNode : children) {
                byte[] data = zooKeeper.getData(BASE_SERVICES  + SERVICE_NAME + "/" + subNode, false, null);
                String host = new String(data, "utf-8");
                System.out.println("host:"+host);
                newServerList.add(host);
            }
            LoadBalance.SERVICE_LIST = newServerList;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
