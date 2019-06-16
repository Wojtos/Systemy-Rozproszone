package zooKeeper;


import java.util.Arrays;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.*;
import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.Watcher.Event.EventType;

public class DataMonitor implements Watcher, StatCallback, ChildrenCallback {

    ZooKeeper zk;

    String znode;

    Watcher chainedWatcher;

    boolean dead;

    DataMonitorListener listener;

    public DataMonitor(ZooKeeper zk, String znode, Watcher chainedWatcher,
                       DataMonitorListener listener) {
        this.zk = zk;
        this.znode = znode;
        this.chainedWatcher = chainedWatcher;
        this.listener = listener;
        // Get things started by checking if the node exists. We are going
        // to be completely event driven
        zk.exists(znode, true, this, null);
        zk.getChildren(znode, true, this, null);

    }


    @Override
    public void process(WatchedEvent event) {
        System.out.println(event.getType() + " " + event.getPath());
        String path = event.getPath();
        if (event.getType() == Event.EventType.None) {
            switch (event.getState()) {
                case SyncConnected:
                    break;
                case Expired:
                    dead = true;
                    listener.closing(KeeperException.Code.SessionExpired);
                    break;
            }
        } else {
            if (path != null && path.equals(znode)) {
                zk.exists(znode, true, this, null);
            }
        }

        if(event.getType() == EventType.NodeChildrenChanged) {

            ChildrenManager childrenManager = new ChildrenManager(zk);
            childrenManager.lsRecursive(event.getPath());
            System.out.println(childrenManager.countRecursive(event.getPath()));

        }

        if (chainedWatcher != null) {
            chainedWatcher.process(event);
        }
    }


    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        boolean exists;
        switch (rc) {
            case Code.Ok:
                exists = true;
                break;
            case Code.NoNode:
                exists = false;
                break;
            case Code.SessionExpired:
            case Code.NoAuth:
                dead = true;
                listener.closing(rc);
                return;
            default:
                zk.exists(znode, true, this, null);
                return;
        }

        listener.nodeStatus(exists);


    }

    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children) {
        switch (rc) {
            case Code.SessionExpired:
            case Code.NoAuth:
                dead = true;
                listener.closing(rc);
                return;
            default:
                zk.getChildren(znode, true, this, null);
        }
    }
}