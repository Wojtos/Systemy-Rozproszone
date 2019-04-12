import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Technician {

    static Channel channel;
    static String EXCHANGE_NAME_ORD;
    static String EXCHANGE_NAME_RET;

    public static void main(String[] argv) throws Exception {

        System.out.println("Technician");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.basicQos(1);

        EXCHANGE_NAME_ORD = "ordersE";
        channel.exchangeDeclare(EXCHANGE_NAME_ORD, BuiltinExchangeType.DIRECT);

        EXCHANGE_NAME_RET = "returnsE";
        channel.exchangeDeclare(EXCHANGE_NAME_RET, BuiltinExchangeType.DIRECT);

        System.out.println("Types of injuries (knee, elbow, hip): ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String injury1 = channel.queueDeclare(br.readLine(), false, false, false, null).getQueue();
        channel.queueBind(injury1, EXCHANGE_NAME_ORD, injury1);

        String injury2 = channel.queueDeclare(br.readLine(), false, false, false, null).getQueue();
        channel.queueBind(injury2, EXCHANGE_NAME_ORD, injury2);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Message message = Message.fromBytes(body);
                System.out.println("Received: " + message.toString());

                try {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                message.examine();
                channel.basicPublish(EXCHANGE_NAME_RET, message.getDoctorName(), null, message.getBytes());
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume(injury1, false, consumer);
        channel.basicConsume(injury2, false, consumer);
    }
}
