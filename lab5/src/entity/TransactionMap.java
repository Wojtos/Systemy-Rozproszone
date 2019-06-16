package entity;

import akka.actor.ActorRef;

import java.util.HashMap;
import java.util.Map;

public class TransactionMap {
    private int counterIds = 0;
    private Map<Integer, Transaction> transactions = new HashMap<>();

    public int createTransaction(ActorRef client) {
        int newId = counterIds;
        counterIds++;

        Transaction transaction = new Transaction(newId, client);
        transactions.put(newId, transaction);

        return newId;
    }

    public Transaction getTransaction(int id) {
        return transactions.get(id);
    }
}
