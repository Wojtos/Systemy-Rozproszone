package server;

import bankApp.*;
import client.AccountI;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BankI implements Bank {
    private Map<String, Account> accountsByPesel = new HashMap<>();
    private Set<Currency> servicedCurrencies;
    private String name;
    private MockExchangeOffice exchangeOffice = new MockExchangeOffice();
    private static final double MARKUP = 1.33;

    public BankI(String name, Set<Currency> servicedCurrencies) {
        this.name = name;
        this.servicedCurrencies = servicedCurrencies;
    }

    @Override
    public String createAccount(AccountForm accountForm, Current current) throws AccountExistsException {
        String pesel = accountForm.pesel;

        if (accountsByPesel.containsKey(pesel)){
            System.out.println("Didn't created account - account having the same PESEL exists");
            throw new AccountExistsException();
        }

        String plainPassword = PasswordService.getInstance().generatePassword();
        Account newAccount = new AccountI(accountForm, plainPassword);


        Identity identity = new Identity(pesel, "account" + this.name);
        current.adapter.add(newAccount, identity);

        accountsByPesel.put(pesel, newAccount);

        System.out.println("Created account - PESEL: " + pesel);
        return plainPassword;
    }

    @Override
    public CreditAcceptance applyForCredit(Currency currency, long amount, Current current) throws NotAuthorizedException {
        if (!this.isCurrencyServiced(currency, current)) {
            return null;
        }
        String pesel = current.ctx.get("PESEL");
        Account account = this.getAccountByPesel(pesel);

        double ratioToPLN = exchangeOffice.getRatioToPLN(currency);

        long dispensedAmountPLN = Math.round(amount * ratioToPLN);
        account.addCreditCash(dispensedAmountPLN, current);

        CreditAcceptance creditAcceptance = new CreditAcceptance();
        creditAcceptance.amountOrginal = Math.round(amount * MARKUP);
        creditAcceptance.amountPLN = Math.round(creditAcceptance.amountOrginal * ratioToPLN);
        creditAcceptance.orginalCurrency = currency;

        System.out.println("Accepted credit application, cashed " + dispensedAmountPLN + " PLN!");
        return creditAcceptance;
    }

    @Override
    public String info(Current current) throws NotAuthorizedException{
        if (this.logIn(current)) {
            String pesel = current.ctx.get("PESEL");
            Account account = this.getAccountByPesel(pesel);
            return account._toString(current);
        } else {
            throw new NotAuthorizedException();
        }
    }

    @Override
    public boolean logIn(Current current) {
        String pesel = current.ctx.get("PESEL");
        String password = current.ctx.get("password");
        if (accountsByPesel.containsKey(pesel)) {
            Account account = accountsByPesel.get(pesel);
            return account.checkPassword(password, current);
        } else {
            return false;
        }
    }

    @Override
    public boolean isCurrencyServiced(Currency currency, Current current) {
        return this.servicedCurrencies.contains(currency);
    }

    private Account getAccountByPesel(String pesel) {
        return this.accountsByPesel.get(pesel);
    }
}
