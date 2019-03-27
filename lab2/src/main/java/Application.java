import java.util.Scanner;

public class Application {
    private JGroupManager manager = new JGroupManager();
    private DistributedMap distributedMap = new DistributedMap();

    public static void main(String[] args) throws Exception {
        Application app = new Application();
        app.runConsole();
    }

    private Application() {
        try {
            manager.init(distributedMap);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void runConsole() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("WRITE COMMAND");

            String line = scanner.nextLine();
            String[] words = line.split(" ");
            String command = words[0];


            if(command.equals("PUT") && words.length >= 3) {
                String key = words[1];
                Integer value = Integer.parseInt(words[2]);

                distributedMap.put(key, value);

                Operation operation = Operation.createOperationPut(key, value);
                manager.sendOperation(operation);

                System.out.println("PUT: KEY:" + key + " VALUE: " + value);
            } else if(command.equals("GET") && words.length >= 2) {
                String key = words[1];

                System.out.println("GET: " + distributedMap.get(key));
            } else if(command.equals("CONTAIN") && words.length >= 2) {
                String key = words[1];

                System.out.println("CONTAIN: " + (distributedMap.containsKey(key) ? "TRUE" : "FALSE"));
            } else if(command.equals("REMOVE")) {
                String key = words[1];

                Operation operation = Operation.createOperationRemove(key);
                manager.sendOperation(operation);

                System.out.println("REMOVE: KEY:" + distributedMap.remove(key));
            } else if(command.equals("CLOSE")) {
                manager.close();
                System.exit(0);

                System.out.println("CLOSING");
            } else {
                System.out.println("COMMAND NOT FOUND");
            }

        }

    }


}
