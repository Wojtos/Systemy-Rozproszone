package server;

import bankApp.Bank;
import bankApp.Currency;
import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.Identity;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class Server
{
    private ObjectAdapter adapter;
    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    public static void main(String[] args)
    {
        new Server().init(args);
    }

    public void init(String[] args)
    {
        String configFile = args[0];
        try(Communicator communicator = Util.initialize(args, configFile)) {
            adapter = communicator.createObjectAdapter("Adapter");

            this.parseBanks();

            adapter.activate();

            System.out.println("Entering event processing loop...");

            communicator.waitForShutdown();

        } catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void parseBanks() throws IOException {
        while (true){
            System.out.println("Write name of new bank. If you want to end, write END");
            String line = br.readLine();
            if (line.contains("END")) {
                return;
            }
            this.createBank(line);
        }
    }

    private void createBank(String bankName) throws IOException {
        Set<Currency> currencies = this.parseCurrencies();
        Bank bank = new BankI(bankName, currencies);
        adapter.add(bank, new Identity(bankName, "bank"));
    }

    private Set<Currency> parseCurrencies() throws IOException {
        Set<Currency> currencies = new HashSet<>();
        while (true){
            System.out.println("Add currency by their shortcut. If you want to end, write END");
            String currencyShortcut = br.readLine();
            if (currencyShortcut.contains("END")) {
                return currencies;
            }
            try {
                Currency currency = Currency.valueOf(currencyShortcut);
                currencies.add(currency);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

/*    private Currency tryToAddCurrency(String currencyShortcut) {
        try {
            Currency currency = Currency.valueOf(currencyShortcut);
        } catch (Exception e) {
            System.out.println(e.getClass().toString());
        }
    }*/

}