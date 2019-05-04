#ifndef CALC_ICE
#define CALC_ICE

module bankApp
{
  enum Currency { PLN, USD, EUR };
  enum AccountType {STANDARD, PREMIUM};

  struct AccountForm {
    string firstName;
    string lastName;
    string pesel;
    long monthlyIncome;
  }

  struct CreditAcceptance {
      long amountOrginal;
      long amountPLN;
      Currency orginalCurrency;
    }

  exception AccountExistsException {}
  exception NotAuthorizedException {}

  interface Account
  {
    string getPesel();
    string toString();
    bool checkPassword(string password);
    void addCreditCash(long amount);
  };

  interface Bank
  {
    string createAccount(AccountForm accountForm) throws AccountExistsException;
    CreditAcceptance applyForCredit(Currency currency, long amount) throws NotAuthorizedException;
    string info() throws NotAuthorizedException;
    bool logIn();
    bool isCurrencyServiced(Currency currency);
  };
};

#endif