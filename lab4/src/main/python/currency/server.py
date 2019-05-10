import sys
sys.path.append('../currencyExchangeValue')
sys.path.append('../../proto')
import CurrencyExchangeValue_pb2
import CurrencyExchangeValue_pb2_grpc
import grpc

from concurrent import futures
import time
import random
import threading

_ONE_DAY_IN_SECONDS = 60 * 60 * 24

_AVARAGE_CURRENCIES_VALUES = {
    CurrencyExchangeValue_pb2.PLN: 1,
    CurrencyExchangeValue_pb2.EUR: 4.35,
    CurrencyExchangeValue_pb2.USD: 3.5
}

CURRENCIES_VALUES = {
    CurrencyExchangeValue_pb2.PLN: 1,
    CurrencyExchangeValue_pb2.EUR: 4.35,
    CurrencyExchangeValue_pb2.USD: 3.5
}

class CurrencyExchangeValue(CurrencyExchangeValue_pb2_grpc.CurrencyExchangeValueServicer):
  def Register(self, request, context):
    print("Registered new bank!")
    while True:
        for currency, value in CURRENCIES_VALUES.items():
            yield CurrencyExchangeValue_pb2.Response(currency=currency, value=value)
        time.sleep(2)

def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    currencyExchangeValue = CurrencyExchangeValue()
    CurrencyExchangeValue_pb2_grpc.add_CurrencyExchangeValueServicer_to_server(
        currencyExchangeValue, server)
    server.add_insecure_port('[::]:50051')
    server.start()
    print("Starting!")
    try:
        while True:
            time.sleep(_ONE_DAY_IN_SECONDS)
    except KeyboardInterrupt:
        server.stop(0)

def simulate_changes():
    while True:
        for currency, value in _AVARAGE_CURRENCIES_VALUES.items():
            if currency != CurrencyExchangeValue_pb2.PLN:
                average_value = _AVARAGE_CURRENCIES_VALUES[currency]
                CURRENCIES_VALUES[currency] = average_value + 0.1 * average_value * random.uniform(-1, 1)
                print ('Changed value of ' + str(currency) + ' to ' + str(CURRENCIES_VALUES[currency]))
        time.sleep(5)
threading.Thread(target = simulate_changes).start()
serve()
