package client;

import bankApp.*;
import com.zeroc.Ice.Current;
import server.PasswordService;


public class AccountI implements Account {
    private String firstName;
    private String lastName;
    private String pesel;
    private long monthlyIncome;
    protected AccountType accountType;
    private String password;
    protected int cash = 0;

    public AccountI(AccountForm accountForm, String plainPassword)
    {
        this.firstName = accountForm.firstName;
        this.lastName =  accountForm.lastName;
        this.pesel =  accountForm.pesel;
        this.monthlyIncome =  accountForm.monthlyIncome;
        this.accountType = AccountType.STANDARD;
        this.password = PasswordService.getInstance().md5(plainPassword);

    }

    public String getPesel(Current current){
        return this.pesel;
    }

    @Override
    public boolean checkPassword(String password, Current current) {
        return this.checkPassword(password);
    }

    private boolean checkPassword(String password) {
        return this.password.equals(PasswordService.getInstance().md5(password));
    }

    @Override
    public String info(Current current) throws NotAuthorizedException {
        String password = current.ctx.get("password");
        if (this.checkPassword(password)) {
            return this.toString();
        } else {
            throw new NotAuthorizedException();
        }
    }

    public String toString() {
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
}
