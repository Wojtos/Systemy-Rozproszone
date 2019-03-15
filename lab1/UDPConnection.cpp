#include "UDPConnection.h"

const char* loggerIP = "237.0.0.0";
const int loggerPort = 10000;

char* id;
char* myIP;
int myPort;
char* successorIP;
int successorPort;
int protocolId;
int hasToken;



int mySocket;
int neigbourSocket;
int loggerSocket;


pthread_t listener;
pthread_t sender;
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

queue<Token> waitingTokens;


int initMySocket() {
    if ((mySocket = socket(AF_INET, SOCK_DGRAM, protocolId)) < 0) {
        printf("%s\n", "Error during initializing my socket");
        exit(EXIT_FAILURE);
    }

    struct sockaddr_in socketAddress;

    socketAddress.sin_family = AF_INET;
    socketAddress.sin_addr.s_addr = inet_addr(myIP);
    socketAddress.sin_port = htons(myPort);

    if(bind(mySocket, (const struct sockaddr* ) &socketAddress, sizeof(socketAddress)) < 0) {
        printf("%s\n", "Error during binding my socket");
        exit(EXIT_FAILURE);
    }
}

int initNeighbourSocket() {
    if ((neigbourSocket = socket(AF_INET, SOCK_DGRAM, protocolId)) < 0) {
        printf("%s\n", "Error during initializing neighbour socket");
        exit(EXIT_FAILURE);
    }
}

int initLoggerSocket() {
    if ((loggerSocket = socket(AF_INET, SOCK_DGRAM, protocolId)) < 0) {
        printf("%s\n", "Error during initializing logger socket");
        exit(EXIT_FAILURE);
    }
}

int init() {
    initMySocket();
    initNeighbourSocket();
    initLoggerSocket();
}

int sendToken(Token token) {
    struct sockaddr_in socketAddress;

    socketAddress.sin_family = AF_INET;
    socketAddress.sin_addr.s_addr = inet_addr(successorIP);
    socketAddress.sin_port = htons(successorPort);

    if(sendto(neigbourSocket, (const void*) &token, sizeof(token), 0, (const struct sockaddr* ) &socketAddress, sizeof(socketAddress)) < 0) {
        printf("%s\n", "Error during sending of token");
        exit(EXIT_FAILURE);
    }
}

Token createBaseToken() {
    Token token;

    strcpy(token.sourceIP, myIP);
    token.sourcePort = myPort;
    token.ttl = MAX_TTL;

    return token;
}

Token createConnectRequestToken() {
    Token token = createBaseToken();

    token.tokenType = CONNECT_REQUEST;
    strcpy(token.destinationIP, successorIP);
    token.destinationPort = successorPort;
    strcpy(token.message, "NONE");

    return token;
}

Token createFreeToken() {
    Token token = createBaseToken();

    token.tokenType = FREE;
    strcpy(token.destinationIP, successorIP);
    token.destinationPort = successorPort;
    strcpy(token.message, "NONE");

    return token;
}

Token createMessageToken(char destinationIP[100], int destinationPort, char message[MAX_LENGTH]) {
    Token token = createBaseToken();

    token.tokenType = MESSAGE;
    strcpy(token.destinationIP, destinationIP);
    token.destinationPort = destinationPort;
    strcpy(token.message, message);

    return token;
}

void printToken(Token token) {
    printf("Type: %d\n", token.tokenType);
    printf("Source IP: %s\n", token.sourceIP);
    printf("Source Port: %d\n", token.sourcePort);
    printf("Destination IP: %s\n", token.destinationIP);
    printf("Destination Port: %d\n", token.destinationPort);
    printf("Message: %s\n", token.message);
    printf("TTL: %d\n", token.ttl);
    printf("\n");
}

void sendInfoToLogger(Token token) {
    char message[MAX_LENGTH] = {'\0'};
    sprintf(
            message,
            "%s:%d %s",
            myIP,
            myPort,
            id
        );


    struct sockaddr_in socketAddress;

    socketAddress.sin_family = AF_INET;
    socketAddress.sin_addr.s_addr = inet_addr(loggerIP);
    socketAddress.sin_port = htons(loggerPort);


    if(sendto(loggerSocket, (const void*) message, strlen(message), 0, (const struct sockaddr* ) &socketAddress, sizeof(socketAddress)) < 0) {
        printf("%s\n", "Error during sending of message to logger");
        exit(EXIT_FAILURE);
    }


}

int startConnection() {
    Token token = createConnectRequestToken();
    sendToken(token);
}

int handleConnectRequestAction(Token recivedToken) {
    recivedToken.tokenType = ACCEPT_REQUEST;

    pthread_mutex_lock(&mutex);
    waitingTokens.push(recivedToken);
    pthread_mutex_unlock(&mutex);

}


int handleFreeAction(Token recivedToken) {
    Token tokenToSend;

    pthread_mutex_lock(&mutex);
    if(waitingTokens.empty()) {
        tokenToSend = recivedToken;
    } else {
        tokenToSend = waitingTokens.front();
        waitingTokens.pop();
    }
    pthread_mutex_unlock(&mutex);

    sendToken(tokenToSend);
}

int handleAcceptRequestAction(Token recivedToken) {
    if (strcmp(recivedToken.destinationIP, successorIP) == 0 && recivedToken.destinationPort == successorPort) {


        strcpy(successorIP, recivedToken.sourceIP);
        successorPort = recivedToken.sourcePort;

        Token tokenToSend = createFreeToken();

        handleFreeAction(tokenToSend);
    } else {
        sendToken(recivedToken);
    }
}

int handleMessageAction(Token recivedToken) {
    if (strcmp(recivedToken.destinationIP, myIP) == 0 && recivedToken.destinationPort == myPort) {

        printf("Message to me: %s\n", recivedToken.message);
        printf("Source: %s:%d\n", recivedToken.sourceIP, recivedToken.sourcePort);

        Token tokenToSend = createFreeToken();

        handleFreeAction(tokenToSend);
    } else {
        sendToken(recivedToken);
    }
}

void* startListener(void* none) {
    while(true) {
        Token recivedToken;
        if (recv(mySocket, (void*) &recivedToken, sizeof(recivedToken), 0) < 0) {
            printf("%s\n", "Error during reciving token");
            exit(EXIT_FAILURE);
        }

        recivedToken.ttl--;

        if (recivedToken.ttl < 0) {
            recivedToken = createFreeToken();
        }

        if(recivedToken.tokenType != CONNECT_REQUEST) {
            sendInfoToLogger(recivedToken);
        }

        sleep(1);

        if(recivedToken.tokenType == CONNECT_REQUEST) {
            handleConnectRequestAction(recivedToken);
        } else if(recivedToken.tokenType == ACCEPT_REQUEST) {
            handleAcceptRequestAction(recivedToken);
        } else if(recivedToken.tokenType == FREE) {
            handleFreeAction(recivedToken);
        } else if(recivedToken.tokenType == MESSAGE) {
            handleMessageAction(recivedToken);
        }
    }
}

void* startSender(void* none) {
    while (true) {
        char destinationIP[100], message[MAX_LENGTH];
        int destinationPort;

        printf("%s\n", "Destination IP: ");
        scanf("%s", destinationIP);

        printf("%s\n", "Destination Port: ");
        scanf("%d", &destinationPort);

        printf("%s\n", "Message: ");
        scanf("%s", message);

        Token tokenToSend = createMessageToken(destinationIP, destinationPort, message);

        pthread_mutex_lock(&mutex);
        waitingTokens.push(tokenToSend);
        pthread_mutex_unlock(&mutex);

    }
}

int startUDP() {
    protocolId = 17;
    init();
    startConnection();

    if (hasToken) {
        Token freeToken = createFreeToken();
        handleFreeAction(freeToken);
    }

    if (pthread_create(&listener, NULL, startListener, NULL) != 0) {
        printf("%s\n", "Error: cant create new thread");
        exit(EXIT_FAILURE);
    }

    if (pthread_create(&sender, NULL, startSender, NULL) != 0) {
        printf("%s\n", "Error: cant create new thread");
        exit(EXIT_FAILURE);
    }

    pthread_join(listener, NULL);
}

int main(int argc, char* argv[])
{
    if(argc < 8) {
        printf("%s\n", "Error: too less parameters" );
        exit(EXIT_FAILURE);
    }

    id = argv[1];
    myIP = argv[2];
    myPort = atoi(argv[3]);
    successorIP = argv[4];
    successorPort = atoi(argv[5]);
    int isTCP = strcmp(argv[6], "tcp") == 0;
    hasToken = strcmp(argv[7], "yes") == 0;

    if (!isTCP) startUDP();

    return 0;
}