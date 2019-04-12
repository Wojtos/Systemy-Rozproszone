import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Admin {

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

        String adminName = br.readLine();
        String adminQueue = channel.queueDeclare(adminName, false, false, false, null).getQueue();
        channel.queueBind(adminQueue, EXCHANGE_NAME, "admin.*");

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
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume(adminQueue, false, consumer);
        while (true) {
            String message = br.readLine();
            System.out.println("Sending: " + message);

            channel.basicPublish(EXCHANGE_NAME, "info." + adminName, null, message.getBytes());
        }
    }
}
