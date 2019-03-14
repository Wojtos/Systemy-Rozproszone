#ifndef LAB1_UDPCONNECTION_H
#define LAB1_UDPCONNECTION_H
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <pthread.h>
#include <queue>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <thread>
#include <ctime>

#define MAX_LENGTH 1024
#define MAX_TTL 10

using namespace std;

enum TokenType { CONNECT_REQUEST, ACCEPT_REQUEST, FREE, MESSAGE };


typedef struct
{
    char sourceIP[MAX_LENGTH];
    int sourcePort;
    char destinationIP[MAX_LENGTH];
    int destinationPort;
    TokenType tokenType;
    char message[MAX_LENGTH];
    int ttl;
    time_t time;
} Token;

#endif //LAB1_UDPCONNECTION_H
