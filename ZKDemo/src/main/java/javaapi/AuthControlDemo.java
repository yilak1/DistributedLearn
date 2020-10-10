package javaapi;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AuthControlDemo implements Watcher {
    private static CountDownLatch countDownLatch=new CountDownLatch(1);
    private static CountDownLatch countDownLatch2=new CountDownLatch(1);
    public static void main(String[] args) throws Exception {
        ZooKeeper zookeeper = new ZooKeeper("127.0.0.1:2181", 5000, new AuthControlDemo());
        countDownLatch.await();
        ACL acl = new ACL(ZooDefs.Perms.ALL, new Id("digest", DigestAuthenticationProvider.generateDigest("root:root")));
        ACL acl2 = new ACL(ZooDefs.Perms.CREATE, new Id("ip", "127.0.0.1"));

        List<ACL> acls = new ArrayList<ACL>();
        acls.add(acl);
        acls.add(acl2);
        zookeeper.create("/auth1", "123".getBytes(), acls, CreateMode.PERSISTENT);
        zookeeper.addAuthInfo("digest", "root:root".getBytes());
        zookeeper.create("/auth1/auth1-1","123".getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL,CreateMode.EPHEMERAL);

        ZooKeeper zooKeeper1=new ZooKeeper("127.0.0.1:2181", 5000, new AuthControlDemo());
        countDownLatch.await();
        zooKeeper1.addAuthInfo("digest","root:root".getBytes());
        zooKeeper1.delete("/auth1/auth1-1",-1);

    }

    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                countDownLatch.countDown();
        }
    }
}
