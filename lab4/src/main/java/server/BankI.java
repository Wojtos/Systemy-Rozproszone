package server;

import bankApp.*;
import client.AccountI;
import client.PremiumAccountI;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.Identity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BankI implements Bank {
    private Map<String, Account> accountsByPesel = new HashMap<>();
    private Set<Currency> servicedCurrencies;
    private String name;
    private ExchangeOffice exchangeOffice = new ExchangeOfficeGrpc("localhost", 50051);
    //private ExchangeOffice exchangeOffice = new MockExchangeOffice();
    private static final double MARKUP = 0.1;

    public BankI(String name, Set<Currency> servicedCurrencies) {
        this.name = name;
        this.servicedCurrencies = servicedCurrencies;
    }

    @Override
    public String createAccount(AccountForm accountForm, Current current) throws AccountExistsException {
        String pesel = accountForm.pesel;

        if (accountsByPesel.containsKey(pesel)){
            System.out.println("Didn't create account - account having the same PESEL exists");
            throw new AccountExistsException();
        }

        String plainPassword = PasswordService.getInstance().generatePassword();

        Account newAccount;
        if (accountForm.monthlyIncome > 2000) {
            newAccount = new PremiumAccountI(accountForm, plainPassword);
        } else {
            newAccount = new AccountI(accountForm, plainPassword);
        }


        Identity identity = new Identity(pesel, "account" + this.name);
        current.adapter.add(newAccount, identity);

        accountsByPesel.put(pesel, newAccount);

        System.out.println("Created account - PESEL: " + pesel);
        return plainPassword;
    }

    @Override
    public CreditAcceptance applyForCredit(PremiumAccountPrx premiumAccountPrx, CreditForm creditForm, Current current) throws NotAuthorizedException {
        String pesel = current.ctx.get("PESEL");
        String password = current.ctx.get("password");

        if (!premiumAccountPrx.checkPassword(password)) {
            throw new NotAuthorizedException();
        }

        if (!this.existsAccountByPesel(pesel)) {
            throw new NotAuthorizedException();
        }

        if (!this.isCurrencyServiced(creditForm.creditCurrency, current)) {
            return null;
        }

        double ratioToPLN = exchangeOffice.getRatioToPLN(creditForm.creditCurrency);

        long dispensedAmountPLN = Math.round(creditForm.amount * ratioToPLN);
        premiumAccountPrx.addCreditCash(dispensedAmountPLN);

        double wholeCreditMarkup = creditForm.months * MARKUP;

        CreditAcceptance creditAcceptance = new CreditAcceptance();
        creditAcceptance.amountOrginal = Math.round(creditForm.amount * (1 + wholeCreditMarkup));
        creditAcceptance.amountPLN = Math.round(creditAcceptance.amountOrginal * ratioToPLN);
        creditAcceptance.orginalCurrency = creditForm.creditCurrency;

        System.out.println("Accepted credit application, cashed " + dispensedAmountPLN + " PLN!");
        return creditAcceptance;
    }

    @Override
    public boolean isCurrencyServiced(Currency currency, Current current) {
        return this.servicedCurrencies.contains(currency);
    }

    private boolean existsAccountByPesel(String pesel) {
        return this.accountsByPesel.containsKey(pesel);
    }

    private Identity createAccountIdentity(String pesel) {
        return new Identity(pesel, "account" + this.name);
    }
}
