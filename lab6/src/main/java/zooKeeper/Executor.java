package zooKeeper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class Executor
        implements Watcher, Runnable, DataMonitorListener
{
    DataMonitor dm;

    ZooKeeper zk;


    String exec[];

    Process child;

    public Executor(String hostPort, String znode,
                    String exec[]) throws KeeperException, IOException {
        this.exec = exec;
        zk = new ZooKeeper(hostPort, 3000, this);
        dm = new DataMonitor(zk, znode, null, this);
    }


    public static void main(String[] args) {
        if (args.length < 3) {
            System.err
                    .println("USAGE: Executor hostPort znode filename program [args ...]");
            System.exit(2);
        }
        String hostPort = args[0];
        String znode = args[1];
        String exec[] = new String[args.length - 2];
        System.arraycopy(args, 2, exec, 0, exec.length);
        try {
            new Executor(hostPort, znode, exec).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void process(WatchedEvent event) {
        dm.process(event);
    }

    public void run() {
        try {
            synchronized (this) {
                while (!dm.dead) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
        }
    }

    public void closing(int rc) {
        synchronized (this) {
            notifyAll();
        }
    }


    public void nodeStatus(boolean exists) {
        if (child != null && !exists) {
            System.out.println("Stopping child");
            child.destroy();
            try {
                child.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (child == null && exists) {

            try {
                System.out.println("Starting child");
                child = Runtime.getRuntime().exec(exec);
                new StreamWriter(child.getInputStream(), System.out);
                new StreamWriter(child.getErrorStream(), System.err);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class StreamWriter extends Thread {
        private OutputStream os;
        private InputStream is;

        StreamWriter(InputStream is, OutputStream os) {
            this.is = is;
            this.os = os;
            start();
        }

        @Override
        public void run() {
            byte[] b = new byte[80];
            int rc;
            try {
                while ((rc = is.read(b)) > 0) {
                    os.write(b, 0, rc);
                }
            } catch (IOException ignored) {
            }

        }
    }
}