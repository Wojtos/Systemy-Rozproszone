package client;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import entity.Book;

import requests.OrderRequest;
import requests.SearchRequest;
import responses.OrderResponse;
import responses.SearchResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class Client extends AbstractActor {
    private String server = "akka.tcp://server@127.0.0.1:10002/user/server";

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchRequest.class, req -> getContext().actorSelection(server).tell(req, getSelf()))
                .match(SearchResponse.class, resp -> System.out.println(resp.getMessage()))
                .match(OrderRequest.class, req -> getContext().actorSelection(server).tell(req, getSelf()))
                .match(OrderResponse.class, resp -> System.out.println(resp.getMessage()))
                .matchAny(o -> System.out.println("Do not understand"))
                .build();
    }

    public static void main(String[] args) throws Exception {
        File confFile = new File("conf/client.conf");
        Config config = ConfigFactory.parseFile(confFile);

        final ActorSystem system = ActorSystem.create("client", config);
        final ActorRef actor = system.actorOf(Props.create(Client.class), "client");

        System.out.println(actor.path());

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.startsWith("SEARCH") && line.split(" ").length > 1) {
                Book book = parseLine(line);
                actor.tell(new SearchRequest(book), null);
            } else if (line.startsWith("ORDER") && line.split(" ").length > 1) {
                Book book = parseLine(line);
                actor.tell(new OrderRequest(book), null);
            } else if (line.startsWith("QUIT")){
                break;
            } else {
                System.out.println("Do not understand");
            }
        }
        system.terminate();
    }

    private static Book parseLine(String line) {
        String name = line.replaceAll("^[a-zA-Z0-9]+\\s", "");
        return new Book(name);
    }
}