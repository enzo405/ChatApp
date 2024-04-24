package fr.sio.chat.app.websocket;

import fr.sio.chat.app.models.User;
import fr.sio.chat.app.websocket.requests.Request;
import fr.sio.chat.app.websocket.requests.RequestLogin;
import fr.sio.chat.app.websocket.requests.RequestLogout;
import jakarta.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketClient extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketClient.class);
    private static WebSocketClient instance = null;
    private volatile boolean isWorking = true;
    private Session session;

    public static WebSocketClient getInstance() {
        if (instance == null) {
            instance = new WebSocketClient();
        }
        return instance;
    }

    public void login(User user) {
        // On envoie une requete de login jusqu'à ce que la session soit ouverte
        try {
            RequestLogin request = new RequestLogin(user);
            session.getBasicRemote().sendObject(RequestCodec.encode(request));
            logger.info("Requête de login envoyée au websocket");
        } catch (IOException | EncodeException | NullPointerException e) {
            logger.error("Echec d'envoie de la request: " + e.getMessage());
        }
    }

    public void logout() {
        try {
            RequestLogout request = new RequestLogout();
            session.getBasicRemote().sendObject(RequestCodec.encode(request));
            logger.info("Requête de logout envoyée au websocket");
        } catch (IOException | EncodeException e) {
            logger.error("Echec d'envoie de la request: " + e.getMessage());
        }
        isWorking = false;
    }

    public void sendRequest(Request request) {
        try {
            session.getBasicRemote().sendObject(RequestCodec.encode(request));
        } catch (IOException | EncodeException e) {
            logger.error("Echec d'envoie de la request: " + e.getMessage());
        }
    }

    public void run() {
        while(isWorking) {
            try {
                runClient();
            } catch (Exception ex) {
                logger.error(ex.getMessage(),ex);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setSession(Session session) {
        this.session = session;
    }

    private void runClient() throws URISyntaxException, DeploymentException, IOException {
        URI uri = new URI("ws://localhost:8080/websockets/chat");
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        webSocketContainer.connectToServer(ClientRequestEndpoint.class, uri);
        while (isWorking) {
            Thread.onSpinWait();
        }
    }
}
