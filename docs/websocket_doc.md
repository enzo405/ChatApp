- [Documentation Websocket](#documentation-websocket)
  - [Serveur](#serveur)
      - [Classes Principales](#classes-principales)
  - [Client](#client)
      - [Classes Principales](#classes-principales-1)


# Documentation Websocket

Dans ce projet, nous utilisons [Jakarta WebSocket v2.1](https://jakarta.ee/specifications/websocket/2.1/)

## Serveur

Voici la documentation jakarta serveur => [Jakarta WebSocket APIs common to both the client and server side.](https://jakarta.ee/specifications/websocket/2.1/apidocs/server/)

#### Classes Principales

1. **Main** :
   - Classe principale pour lancer le serveur WebSocket.
   - **Méthodes** :
     - `main(String[] args)`: Méthode principale qui démarre le serveur WebSocket sur le port spécifié (dans notre cas sur localhost port 8080)

2. **WebsocketEndpoint** :
   - Classe représentant l'endpoint du WebSocket.
   - **Annotations** :
     - `@ServerEndpoint(value = "/chat")`: Annotation déclarant que cette classe est un endpoint du serveur WebSocket accessible à l'URL "/chat".
   - **Méthodes** :
     - `onOpen(Session session, EndpointConfig endpointConfig)`: Méthode appelée lorsque la connexion WebSocket est ouverte.
     - `onError(Session session, Throwable throwable)`: Méthode appelée lorsqu'une erreur survient sur la session WebSocket.
     - `onMessage(Session session, String request)`: Méthode appelée lorsqu'un message est reçu sur la session WebSocket.

3. **WebsocketServer** :
   - Classe représentant le serveur WebSocket.
   - **Méthodes** :
     - `WebsocketServer(int port, String hostname)`: Constructeur pour initialiser le serveur WebSocket avec le port et le nom d'hôte spécifiés.
     - `run()`: Méthode pour démarrer le serveur WebSocket en tant que thread.
     - `getInstance()`: Méthode statique pour obtenir une instance unique du serveur WebSocket.
     - `runServer()`: Méthode privée pour démarrer effectivement le serveur WebSocket.
     - `stopServer()`: Méthode pour arrêter le serveur WebSocket.

---

## Client

Voici la documentation jakarta client => [Jakarta WebSocket APIs common to both the client and server side.](https://jakarta.ee/specifications/websocket/2.1/apidocs/client/jakarta/websocket/package-summary.html)

#### Classes Principales

1. **MyWebSocketClient** :
   - Classe représentant le client WebSocket.
   - **Annotations** :
     - `@ClientEndpoint`: Annotation déclarant que cette classe est un endpoint client WebSocket.
   - **Méthodes** :
     - `onOpen(Session session, EndpointConfig config)`: Méthode appelée lorsque la connexion WebSocket est ouverte.
     - `onClose(Session session, CloseReason closeReason)`: Méthode appelée lorsque la connexion WebSocket est fermée.

2. **App.java** :
   - Fichier principal de l'application JavaFX qui lance l'application.
   - **Méthodes** :
     - `startWebsocket()`: Méthode pour démarrer la connexion WebSocket.
     
