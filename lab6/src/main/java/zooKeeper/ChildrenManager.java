package zooKeeper;

import org.apache.zookeeper.ZooKeeper;

public class ChildrenManager {
    private ZooKeeper zk;

    public ChildrenManager(ZooKeeper zk) {
        this.zk = zk;
    }

    public void lsRecursive(String path) {
        System.out.println(path);
        lsRecursive(path, 1);
    }

    private void lsRecursive(String path, int level) {
        try {
            zk.getChildren(path, true).forEach((file) -> {
                for (int i = 0; i < level; i++) {
                    System.out.print("  ");
                }
                String newPath = path + "/" + file;
                System.out.println(newPath);
                lsRecursive(newPath, level + 1);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int countRecursive(String path) {
        int count = 0;
        try {
            count = zk.getChildren(path, true).stream().map((file) -> {

                String newPath = path + "/" + file;
                return countRecursive(newPath);
            }).reduce(Integer::sum).orElse(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count + 1;
    }
}
