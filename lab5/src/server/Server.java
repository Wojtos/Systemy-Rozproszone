package server;

import agents.SearchActor;
import agents.OrderActor;
import akka.actor.*;
import akka.japi.pf.DeciderBuilder;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import requests.OrderRequest;
import requests.SearchRequest;
import scala.concurrent.duration.Duration;

import java.io.*;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;

public class Server extends AbstractActor {
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchRequest.class, req -> context().child("searchActor").get().tell(req, sender()))
                .match(OrderRequest.class, req -> context().child("orderActor").get().tell(req, sender()))
                .matchAny(o -> System.out.println("Unknown message in " + this.getClass() + " from " + o.getClass()))
                .build();
    }

    @Override
    public void preStart() throws Exception {
        context().actorOf(Props.create(SearchActor.class), "searchActor");
        context().actorOf(Props.create(OrderActor.class), "orderActor");
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

    public static void main(String[] args) throws Exception{
        File confFile = new File("conf/server.conf");
        Config config = ConfigFactory.parseFile(confFile);

        final ActorSystem system = ActorSystem.create("server", config);
        final ActorRef actor = system.actorOf(Props.create(Server.class), "server");

        System.out.println(actor.path());

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String line = br.readLine();
            if (line.startsWith("QUIT")) {
                break;
            }
            actor.tell(line, null);
        }
        system.terminate();
    }
}