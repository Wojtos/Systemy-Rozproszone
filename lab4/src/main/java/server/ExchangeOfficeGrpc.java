package server;
import bankApp.Currency;
import currencyExchangeValue.CurrencyExchangeValueGrpc;
import currencyExchangeValue.CurrencyExchangeValueOuterClass;
import currencyExchangeValue.CurrencyExchangeValueOuterClass.Response;
import currencyExchangeValue.CurrencyExchangeValueOuterClass.Application;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ExchangeOfficeGrpc implements ExchangeOffice {
    private final ManagedChannel channel;
    private final CurrencyExchangeValueGrpc.CurrencyExchangeValueStub asyncStub;
    private final CurrencyExchangeValueGrpc.CurrencyExchangeValueBlockingStub blockingStub;

    private Set<Currency> currencies;
    private Map<Currency, Double> currenciesValueToPLN = new HashMap<>();

    /** Construct client for accessing RouteGuide server at {@code host:port}. */
    public ExchangeOfficeGrpc(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    /** Construct client for accessing RouteGuide server using the existing channel. */
    public ExchangeOfficeGrpc(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = CurrencyExchangeValueGrpc.newBlockingStub(channel);
        asyncStub = CurrencyExchangeValueGrpc.newStub(channel);
        System.out.println("Creating office grpc!");
        this.run();
    }


    public void run() {
        final CountDownLatch finishLatch = new CountDownLatch(1);
        Application application = CurrencyExchangeValueOuterClass.Application.newBuilder()
                .setAddress("localhost")
                .setPort(50051)
                .addCurrency(CurrencyExchangeValueOuterClass.Currency.PLN)
                .build();

        final StreamObserver<Response> responseObserver = new StreamObserver<Response>() {
            @Override
            public void onNext(Response response) {
               /* System.out.println("Got response!");
                System.out.println(response.getCurrency());
                System.out.println(response.getValue());*/
                try {
                    currenciesValueToPLN.put(Currency.valueOf(response.getCurrencyValue()), response.getValue());
                } catch (Exception e) {
                    System.out.println(e.getClass().toString());
                }

            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t.getMessage());
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Completed!");
                finishLatch.countDown();
            }
        };

        try {
            asyncStub.register(application, responseObserver);
        } catch (Exception e) {
            System.out.println(e.getClass().toString());
        }

    }


    @Override
    public double getRatioToPLN(Currency currency) {
        return currenciesValueToPLN.get(currency);
    }
}
