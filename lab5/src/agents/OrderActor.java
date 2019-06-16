package agents;

import agents.SearchActor;
import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import entity.Book;
import entity.Transaction;
import entity.TransactionMap;
import requests.OrderRequest;
import requests.SearchRequest;
import responses.OrderResponse;
import responses.SearchResponse;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;

public class OrderActor extends AbstractActor {
    private static final String ordersPath = "src/resources/orders.txt";

    private TransactionMap transactionMap = new TransactionMap();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(OrderRequest.class, req -> {
                    Book book = new Book(req.getBook().getName());
                    SearchRequest searchRequest = new SearchRequest(book);
                    transactionMap.createTransaction(getSender());
                    context().child("searchActor").get().tell(searchRequest, getSelf());

                })
                .match(SearchResponse.NoSearchResponse.class, res -> {
                    Transaction transaction = transactionMap.getTransaction(res.getTransactionId());
                    ActorRef client = transaction.getClient();
                    client.tell(new OrderResponse.NoOrderResponse(), client);
                })
                .match(SearchResponse.class, res -> {
                    appendToOrders(res.getMessage());
                    writeToClient(res);


                })
                .matchAny(o -> System.out.println("Unknown message in " + this.getClass() + " from " + o.getClass()))
                .build();
    }

    public void appendToOrders(String message) throws IOException {
        File ordersFile = new File(ordersPath);
        if (!ordersFile.exists()) ordersFile.createNewFile();
        FileWriter fileWriter = new FileWriter(ordersFile, true);
        fileWriter.append(String.format("%s\n", message));
        fileWriter.close();
    }

    public void writeToClient(SearchResponse response) {
        Transaction transaction = transactionMap.getTransaction(response.getTransactionId());
        ActorRef client = transaction.getClient();
        client.tell(new OrderResponse("You ordered " + response.getMessage()), client);
    }



    @Override
    public void preStart() throws Exception {
        context().actorOf(Props.create(SearchActor.class), "searchActor");
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(
                -1,
                Duration.Inf(),
                DeciderBuilder
                        .match(IOException.class, e -> resume())
                        .matchAny(e -> restart())
                        .build());
    }
}
