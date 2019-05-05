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

  struct CreditForm{
      long amount;
      long months;
      Currency creditCurrency;
  }

  struct CreditAcceptance {
      long amountOrginal;
      long amountPLN;
      Currency orginalCurrency;
  }

  exception AccountExistsException {}
  exception NotAuthorizedException {}
  exception OperationNotAllowedException {}

  interface Account
  {
    bool checkPassword(string password);
    string info() throws NotAuthorizedException;
  };

  interface PremiumAccount extends Account {
      void addCreditCash(long amount);
  }

  interface Bank
  {
    string createAccount(AccountForm accountForm) throws AccountExistsException;
    CreditAcceptance applyForCredit(PremiumAccount* account, CreditForm creditForm) throws NotAuthorizedException;
    bool isCurrencyServiced(Currency currency);
  };
};

#endif