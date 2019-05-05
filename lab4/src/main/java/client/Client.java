package client;

import com.zeroc.Ice.*;

import bankApp.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Exception;
import java.util.HashMap;
import java.util.Map;

public class Client {
    private BankPrx bankPrx;
    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static final String PROXY_FORMAT =  "%s/%s:tcp -h localhost -p 10000";
    private Map<String, String> currentAuthorization;
    private AccountPrx currentAccountPrx;
    private String bankName;

    public static void main(String[] args) {
        new Client().init(args);
    }

    public void init(String[] args) {
        String configFile = args[0];
        try(Communicator communicator = Util.initialize(args, configFile)) {
            this.connectToBank(communicator);
            this.commandLine(communicator);



        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private BankPrx connectToBank(Communicator communicator) {
        while (true){
            System.out.println("Write name of bank, which you want to connect with.");
            try {
                this.bankName = br.readLine();
                String proxy = String.format(
                        PROXY_FORMAT,
                        "bank",
                        this.bankName
                );
                ObjectPrx base = communicator.stringToProxy(proxy);
                this.bankPrx = BankPrx.checkedCast(base);
                if (this.bankPrx == null) throw new Error("Invalid proxy");
                return this.bankPrx;
            } catch (ConnectFailedException e) {
                System.out.println("Couldn't connect");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (ObjectNotExistException e) {
                System.out.println("Bank having that name doesn't exsist");
            }
        }
    }

    private void commandLine(Communicator communicator) {
        String line;
        while (true) {
            try {
                System.out.println("Write command");
                line = br.readLine();
                if (line.equals("CREATE")) {
                    System.out.println("First name");
                    String firstName = br.readLine();

                    System.out.println("Last name");
                    String lastName = br.readLine();

                    System.out.println("PESEL");
                    String pesel = br.readLine();

                    System.out.println("Monthly income");
                    int monthlyIncome = Integer.parseInt(br.readLine());

                    AccountForm accountForm =
                            new AccountForm(
                                    firstName,
                                    lastName,
                                    pesel,
                                    monthlyIncome
                            );
                    String key = bankPrx.createAccount(accountForm);
                    System.out.println("Created account, your password: " + key);
                } else if (line.equals("LOG IN") && this.currentAccountPrx == null) {
                    System.out.println("PESEL");
                    String pesel = br.readLine();

                    System.out.println("Password");
                    String password = br.readLine();

                    Map<String, String> authorization = new HashMap<>();
                    authorization.put("PESEL", pesel);
                    authorization.put("password", password);

                    String proxy = String.format(
                            PROXY_FORMAT,
                            "account" + this.bankName,
                            pesel
                    );
                    ObjectPrx base = communicator.stringToProxy(proxy);
                    AccountPrx accountPrx = AccountPrx.checkedCast(base);

                    if (accountPrx.checkPassword(password)) {
                        this.currentAccountPrx = AccountPrx.checkedCast(base);
                        this.currentAuthorization = authorization;
                        System.out.println("Logged in");
                    } else {
                        System.out.println("Wrong password!");
                    }


                } else if (line.equals("LOG OUT") && this.currentAccountPrx != null) {
                    this.currentAuthorization = null;
                    this.currentAccountPrx = null;
                    System.out.println("Logged out");

                } else if (line.equals("INFO") && this.currentAccountPrx != null) {
                    System.out.println(this.currentAccountPrx.info(this.currentAuthorization));
                } else if (line.equals("CREDIT APPLICATION")) {
                    System.out.println("Currency");
                    String currencyShortcut = br.readLine();
                    Currency currency = Currency.valueOf(currencyShortcut);
                    if (!bankPrx.isCurrencyServiced(currency)) {
                        System.out.println("Currency is not serviced");
                        continue;
                    }

                    System.out.println("Amount");
                    int amount = Integer.parseInt(br.readLine());

                    System.out.println("Months");
                    int months = Integer.parseInt(br.readLine());

                    CreditForm creditForm = new CreditForm(amount, months, currency);

                    PremiumAccountPrx premiumAccountPrx = PremiumAccountPrx.checkedCast(this.currentAccountPrx);
                    CreditAcceptance creditAcceptance = bankPrx.applyForCredit(premiumAccountPrx, creditForm, this.currentAuthorization);

                    if (creditAcceptance != null){
                        System.out.println("Got credit!");
                        System.out.println("PLN amount to pay: " + creditAcceptance.amountPLN);
                        System.out.println(creditAcceptance.orginalCurrency + " amount to pay: " + creditAcceptance.amountOrginal);
                    } else {
                        System.out.println("There is some problem with credit!");
                    }
                } else {
                    System.out.println("Wrong command!");
                }


            } catch (IOException e) {
                System.out.println(e.getMessage());
            } catch (AccountExistsException e) {
                System.out.println("User identifed by the same PESEL exists");
            } catch (NumberFormatException e) {
                System.out.println("Couldn't parse string to int");
            } catch (NotAuthorizedException e) {
                System.out.println("You are not authorized!");
            } /*catch (BadLoginOrPasswordException e) {
                System.out.println("Bad PESEL or password");
            }*/ catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Not allowed!");
            }
        }
    }
}