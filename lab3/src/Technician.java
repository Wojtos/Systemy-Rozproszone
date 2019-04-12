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
    static String EXCHANGE_NAME = "topic";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.basicQos(1);

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String firstInjuryName = br.readLine();
        String firstInjury = channel.queueDeclare(firstInjuryName, false, false, false, null).getQueue();
        channel.queueBind(firstInjury, EXCHANGE_NAME, "technican." + firstInjuryName);

        String secondInjuryName = br.readLine();
        String secondInjury = channel.queueDeclare(secondInjuryName, false, false, false, null).getQueue();
        channel.queueBind(secondInjury, EXCHANGE_NAME, "technican." + secondInjuryName);

        System.out.println("Done!");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Message message = Message.fromBytes(body);
                System.out.println(message.toString());

                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }

                message.examine();
                channel.basicPublish(EXCHANGE_NAME, "doctor." + message.getDoctorName(), null, message.getBytes());
                channel.basicPublish(EXCHANGE_NAME, "admin.*", null, message.getBytes());
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume(firstInjury, false, consumer);
        channel.basicConsume(secondInjury, false, consumer);


        String queueNameAdmin = channel.queueDeclare().getQueue();
        channel.queueBind(queueNameAdmin, EXCHANGE_NAME, "info.*");

        Consumer adminConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("Admin info: " + new String(body));
            }
        };
        channel.basicConsume(queueNameAdmin, true, adminConsumer);
    }
}
