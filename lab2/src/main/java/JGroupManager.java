import org.jgroups.*;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.*;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.util.Util;
import java.net.InetAddress;

public class JGroupManager {
    private final String channelName = "Woooooo";

    private JChannel channel;
    private SimpleStringMap map;

    public void init(DistributedMap map) throws Exception {
        System.setProperty("java.net.preferIPv4Stack","true");

        this.map = map;
        channel = new JChannel(false);

        ProtocolStack stack = new ProtocolStack();
        channel.setProtocolStack(stack);
        stack.addProtocol(new UDP().setValue("mcast_group_addr", InetAddress.getByName("230.100.200.167")))
                .addProtocol(new PING())
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK2())
                .addProtocol(new UNICAST3())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new FRAG2())
                .addProtocol(new STATE());

        stack.init();

        MapReciverAdapter reciverAdapter = new MapReciverAdapter(channel, map);

        channel.setReceiver(reciverAdapter);
        channel.connect(channelName);
        channel.setDiscardOwnMessages(true);
        channel.getState(null, 0);
    }

    public void close() {
        channel.close();
    }

    public void sendOperation(Operation operation) {
        try {
            channel.send(
                    new Message(
                            null,
                            null,
                            operation
                    )
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
