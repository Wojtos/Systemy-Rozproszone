import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Message implements Serializable {

    private final String patientName;
    private final String doctorName;
    private final Injury type;
    private boolean isExamined = false;

    public Message(String patientName, String doctorName, Injury type){
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.type = type;
    }

    public void examine(){
        isExamined = true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(patientName);
        sb.append(", ");
        sb.append(type.name().toLowerCase());
        if(isExamined) sb.append(" DONE");
        return sb.toString();
    }

    public String getDoctorName() {
        return doctorName;
    }

    public byte[] getBytes() {
        byte[] bytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.flush();
            oos.reset();
            bytes = baos.toByteArray();
            oos.close();
            baos.close();
        } catch(IOException e){
            bytes = new byte[] {};
            e.printStackTrace();
        }
        return bytes;
    }

    public static Message fromBytes(byte[] body) {
        Message obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(body);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = (Message) ois.readObject();
            ois.close();
            bis.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

}
