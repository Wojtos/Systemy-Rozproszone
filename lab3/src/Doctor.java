
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

public class Doctor  {

    static String EXCHANGE_NAME = "topic";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(1);

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Doctor: ");
        String doctorName = br.readLine();

        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "doctor." + doctorName);

        Consumer technicanConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Message message = Message.fromBytes(body);
                System.out.println("Received: " + message.toString());
            }
        };
        channel.basicConsume(queueName, true, technicanConsumer);

        String queueNameAdmin = channel.queueDeclare().getQueue();
        channel.queueBind(queueNameAdmin, EXCHANGE_NAME, "info.*");

        Consumer adminConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("Admin info: " + new String(body));
            }
        };
        channel.basicConsume(queueNameAdmin, true, adminConsumer);

        System.out.println("Done!");

        while (true) {
            System.out.println("Patient: ");
            String nameOfPatient = br.readLine();

            System.out.println("Injury: ");
            Injury typeOfInjury = null;
            while(typeOfInjury == null) {
                try {
                    typeOfInjury = Injury.fromString(br.readLine());
                } catch (IllegalArgumentException e) {
                    System.out.println("Wrong command");
                }
            }


            Message message = new Message(nameOfPatient, doctorName, typeOfInjury);
            System.out.println("Sending: " + message.toString());

            channel.basicPublish(EXCHANGE_NAME, "technican." + typeOfInjury.name().toLowerCase(), null, message.getBytes());
            channel.basicPublish(EXCHANGE_NAME, "admin.*", null, message.getBytes());
        }
    }
}
