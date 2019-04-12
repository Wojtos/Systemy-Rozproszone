
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

    public static void main(String[] args) throws Exception {
        System.out.println("Doctor");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(1);

        String EXCHANGE_NAME_ORD = "ordersE";
        channel.exchangeDeclare(EXCHANGE_NAME_ORD, BuiltinExchangeType.DIRECT);

        String EXCHANGE_NAME_RET = "returnsE";
        channel.exchangeDeclare(EXCHANGE_NAME_RET, BuiltinExchangeType.DIRECT);

        System.out.println("Name of doctor: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String myName = br.readLine();
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME_RET, myName);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Message message = Message.fromBytes(body);
                System.out.println("Received: " + message.toString());
            }
        };
        channel.basicConsume(queueName, true, consumer);

        while (true) {
            System.out.println("Name of patient: ");
            String nameOfPatient = br.readLine();

            System.out.println("Type of injury: ");
            Injury typeOfInjury = null;
            while(typeOfInjury == null) {
                try {
                    typeOfInjury = Injury.fromString(br.readLine());
                } catch (IllegalArgumentException e) {
                    System.out.println("We can't help you with that. Try: elbow, knee or hip");
                }
            }

            Message message = new Message(nameOfPatient, myName, typeOfInjury);

            channel.basicPublish(EXCHANGE_NAME_ORD, typeOfInjury.name().toLowerCase(), null, message.getBytes());
            System.out.println("Sent: " + message.toString());
        }
    }
}
