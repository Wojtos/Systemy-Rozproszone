package client;

import bankApp.Account;
import bankApp.AccountForm;
import bankApp.AccountType;
import bankApp.CreditAcceptance;
import com.zeroc.Ice.Current;
import server.PasswordService;


public class AccountI implements Account {
    private String firstName;
    private String lastName;
    private String pesel;
    private long monthlyIncome;
    private AccountType accountType;
    private String password;
    private int cash = 0;

    public AccountI(AccountForm accountForm, String plainPassword)
    {
        this.firstName = accountForm.firstName;
        this.lastName =  accountForm.lastName;
        this.pesel =  accountForm.pesel;
        this.monthlyIncome =  accountForm.monthlyIncome;
        this.accountType = this.monthlyIncome > 2000 ? AccountType.PREMIUM : AccountType.STANDARD;
        this.password = PasswordService.getInstance().md5(plainPassword);

    }

    public String getPesel(Current current){
        return this.pesel;
    }

    @Override
    public String _toString(Current current) {
        StringBuilder sb = new StringBuilder();
        sb.append("First name: ");
        sb.append(this.firstName);
        sb.append("\n");

        sb.append("Last name: ");
        sb.append(this.lastName);
        sb.append("\n");

        sb.append("PESEL: ");
        sb.append(this.pesel);
        sb.append("\n");

        sb.append("Monthly income: ");
        sb.append(this.monthlyIncome);
        sb.append("\n");

        sb.append("Account type: ");
        sb.append(this.accountType.toString());
        sb.append("\n");

        sb.append("Cash: ");
        sb.append(this.cash);
        sb.append(" PLN\n");
        return sb.toString();
    }

    @Override
    public boolean checkPassword(String password, Current current) {
        return this.password.equals(PasswordService.getInstance().md5(password));
    }

    @Override
    public void addCreditCash(long amount, Current current) {
        this.cash += amount;
    }
}
