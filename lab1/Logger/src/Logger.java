import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class Logger {
    public static void main(String args[])
    {
        System.out.println("JAVA LOGGER");
        MulticastSocket socket = null;
        PrintWriter writer = null;
        String multicastIP = "237.0.0.0";
        int portNumber = 10000;
        String fileName = "logs-" + System.currentTimeMillis() + ".txt";

        try{
            writer = new PrintWriter(fileName, "UTF-8");

            socket = new MulticastSocket(portNumber);
            socket.joinGroup(InetAddress.getByName(multicastIP));
            byte[] receiveBuffer = new byte[1024];

            while(true) {
                Arrays.fill(receiveBuffer, (byte)0);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                String msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(msg);
                writer.print("[LOG: " + System.currentTimeMillis() +"]  " + msg);
                writer.println();
                writer.flush();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if (socket != null) {
                socket.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }
}
