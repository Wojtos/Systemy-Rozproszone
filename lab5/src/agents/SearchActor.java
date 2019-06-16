package agents;

import agents.workers.SearchWorker;

import akka.actor.AbstractActor;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;

import entity.Book;

import entity.Transaction;
import entity.TransactionMap;
import requests.PriceRequest;
import requests.SearchRequest;
import responses.PriceResponse;
import responses.SearchResponse;
import scala.concurrent.duration.Duration;

import java.io.IOException;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;

public class SearchActor extends AbstractActor {
    private static final String dbPath0 = "src/resources/db/db0.txt";
    private static final String dbPath1 = "src/resources/db/db1.txt";

    private TransactionMap transactionMap = new TransactionMap();


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchRequest.class, req -> {
                    Book book = new Book(req.getBook().getName());
                    int transactionId = transactionMap.createTransaction(getSender());
                    PriceRequest priceRequest = new PriceRequest(book, transactionId);
                    for (int i = 0; i < 2; i++ ) {
                        context().child(String.format("searchWorker%d", i)).get().tell(priceRequest, getSelf());
                    }
                })
                .match(PriceResponse.NoPriceResponse.class, res -> {
                    Transaction transaction = transactionMap.getTransaction(res.getTransactionId());
                    if (transaction.addBadReponse()) {
                        transaction.getClient().tell(new SearchResponse.NoSearchResponse(res.getTransactionId()), getSelf());
                    }
                })
                .match(PriceResponse.class, res -> {
                    Transaction transaction = transactionMap.getTransaction(res.getTransactionId());
                    if (transaction.addGoodReponse()) {
                        transaction.getClient().tell(new SearchResponse(res.getMessage(), res.getTransactionId()), getSelf());
                    }
                })
                .matchAny(o -> System.out.println("Unknown message in " + this.getClass() + " caused by " + o.getClass()))
                .build();
    }

    @Override
    public void preStart() throws Exception {
        context().actorOf(Props.create(SearchWorker.class, dbPath0), "searchWorker0");
        context().actorOf(Props.create(SearchWorker.class, dbPath1), "searchWorker1");
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
