package client;

import bankApp.AccountForm;
import bankApp.AccountType;
import bankApp.PremiumAccount;
import com.zeroc.Ice.Current;

public class PremiumAccountI extends AccountI implements PremiumAccount {
    public PremiumAccountI(AccountForm accountForm, String plainPassword) {
        super(accountForm, plainPassword);
        this.accountType = AccountType.PREMIUM;
    }

    @Override
    public void addCreditCash(long amount, Current current) {
        this.cash += amount;
    }
}
