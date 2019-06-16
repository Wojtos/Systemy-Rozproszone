package agents.workers;

import akka.actor.AbstractActor;

import requests.PriceRequest;
import responses.PriceResponse;

import java.io.*;
import java.util.Arrays;

public class SearchWorker extends AbstractActor {
    private File db;

    public SearchWorker(String dbPath) {
        this.db = new File(dbPath);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PriceRequest.class, o -> {
                    String line = searchBook(o.getBook().getName());
                    if (line.equals(""))  {
                        sender().tell(new PriceResponse.NoPriceResponse(o.getTransactionId()), getSender());
                    } else {
                        sender().tell(new PriceResponse(line, o.getTransactionId()), getSender());
                    }
                })

                .matchAny(o -> System.out.println("Unknown message in " + this.getClass() + " from " + o.getClass()))
                .build();
    }

    private String searchBook(String name) throws IOException {
        if (!db.exists()) throw new IOException();

        BufferedReader reader = new BufferedReader(new FileReader(db));
        String line = reader.readLine();
        while (line != null) {
            String foundTitle = String.join(" ", Arrays.asList(line.split(" ")).subList(0, line.split(" ").length - 1));
            if (foundTitle.trim().equals(name)) {
                reader.close();
                return line;
            }
            line = reader.readLine();
        }
        reader.close();
        return "";
    }
}