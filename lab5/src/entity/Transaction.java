package entity;

import akka.actor.ActorRef;

public class Transaction {
    private int id;
    private ActorRef client;
    private int numberOfResponse;
    private boolean sent;

    public Transaction(int id, ActorRef client) {
        this.id = id;
        this.client = client;
        this.numberOfResponse = 0;
        this.sent = false;
    }

    public boolean addBadReponse() {
        addReponse();
        if (numberOfResponse == 2 && !sent) {
            sent = true;
            return true;
        }
        return false;
    }

    public boolean  addGoodReponse() {
        addReponse();
        if (!sent) {
            sent = true;
            return true;
        }
        return false;
    }

    private void addReponse() {
        this.numberOfResponse++;
    }

    public boolean isSent() {
        return sent;
    }

    public ActorRef getClient() {
        return client;
    }

}
