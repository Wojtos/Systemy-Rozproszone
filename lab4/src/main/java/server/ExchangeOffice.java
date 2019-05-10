package server;

import bankApp.Currency;

interface ExchangeOffice {
    public double getRatioToPLN(Currency currency);
}
