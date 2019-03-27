import org.jgroups.*;
import org.jgroups.util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class MapReciverAdapter extends ReceiverAdapter {
    private JChannel channel;
    private DistributedMap distributedMap;

    public MapReciverAdapter(JChannel channel, DistributedMap distributedMap) {
        this.channel = channel;
        this.distributedMap = distributedMap;
    }

    public void viewAccepted(View newView) {
        handleView(channel, newView);
    }

    private static void handleView(JChannel channel, View newView) {
        if(newView instanceof MergeView) {
            ViewHandler handler = new ViewHandler(channel, (MergeView)newView);
            handler.start();
        }
    }

    public void getState(OutputStream output) throws Exception {
        synchronized (distributedMap) {
            Util.objectToStream(distributedMap.getMap(), new DataOutputStream(output));
        }
    }

    public void setState(InputStream input) throws Exception {
        Map<String, Integer> revicedMap = (Map<String, Integer>)Util.objectFromStream(new DataInputStream(input));
        synchronized (distributedMap) {
            distributedMap.setMap(revicedMap);
        }
    }

    public void receive(Message msg) {
        Operation operation = (Operation)msg.getObject();
        if(operation.isPut()) {
            distributedMap.put(
                    operation.getKey(),
                    operation.getValue()
            );
        } else {
            if(operation.isRemove()) {
                distributedMap.remove(
                        operation.getKey()
                );
            }
        }
    }

    private static class ViewHandler extends Thread {
        JChannel channel;
        MergeView view;

        private ViewHandler(JChannel channel, MergeView view) {
            this.channel = channel;
            this.view = view;
        }

        public void run() {
            View firstView = view.getSubgroups().get(0);
            Address localAddress= channel.getAddress();
            if(firstView.getMembers().contains(localAddress)) {
                System.out.println("Not member of the new primary partition ("
                        + firstView + "), will do nothing");
            }
            else {
                System.out.println("Not member of the new primary partition ("
                        + firstView + "), will re-acquire the state");
                try {
                    channel.getState(null, 0);
                }
                catch(Exception ex) {
                }
            }
        }
    }

}
