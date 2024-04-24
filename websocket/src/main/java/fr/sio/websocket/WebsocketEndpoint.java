package fr.sio.websocket;

import fr.sio.websocket.requests.*;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/chat")
public class WebsocketEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(WebsocketEndpoint.class);

    @OnOpen
    public void onOpen(Session session) {
        logger.info("Nouvelle session ouverte: " + session.getId());
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        logger.info("Session fermée: " + session.getId() + " Raison: " + closeReason.getReasonPhrase());
        SessionManager.removeUser(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        logger.error("Une erreur est survenue pour la session " + session.getId() + ": " + throwable.getMessage(), throwable);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("Message reçu de la session " + session.getId() + ": " + message);
        Request request = RequestCodec.decode(message);
        request.handle(session);
    }
}