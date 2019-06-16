package zooKeeper;

public interface DataMonitorListener {
    /**
     * The existence status of the node has changed.
     */
    void nodeStatus(boolean exists);

    /**
     * The ZooKeeper session is no longer valid.
     *
     * @param rc
     *                the ZooKeeper reason code
     */
    void closing(int rc);
}