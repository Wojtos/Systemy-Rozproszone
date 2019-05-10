package server;

import bankApp.Currency;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MockExchangeOffice implements ExchangeOffice {
    private Set<Currency> currencies;
    private Map<Currency, Double> currenciesValueToPLN;

    public MockExchangeOffice() {
        currencies = new HashSet<>();
        currencies.add(Currency.PLN);
        currencies.add(Currency.EUR);
        currencies.add(Currency.USD);

        currenciesValueToPLN = new HashMap<>();
        currenciesValueToPLN.put(Currency.PLN, 1.0);
        currenciesValueToPLN.put(Currency.EUR, 4.35);
        currenciesValueToPLN.put(Currency.USD, 2.5);
    }

    public double getRatioToPLN(Currency currency) {
        return currenciesValueToPLN.get(currency);
    }
}
