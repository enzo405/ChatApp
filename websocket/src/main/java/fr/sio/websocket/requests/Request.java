package fr.sio.websocket.requests;

import fr.sio.websocket.RequestCodec;
import fr.sio.websocket.SessionManager;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Request {
    private static final Logger logger = LoggerFactory.getLogger(Request.class);
    protected ERequestType type;
    protected List<Integer> receiversId = new ArrayList<>();

    public abstract void handle(Session session);

    protected void broadcastMessage(Request request) {
        if (receiversId.isEmpty()) {
            logger.warn("La liste des receivers est vide");
            return;
        }
        for (Integer memberId : receiversId) {
            Session session = SessionManager.getSessionByUserId(memberId);
            if (session != null && session.isOpen()) {
                try {
                    String message = RequestCodec.encode(request);
                    session.getBasicRemote().sendText(message);
                    logger.info("Envoi à l'utilisateur " + memberId + " (session " + session.getId() + ") => " + message);
                } catch (IOException e) {
                    logger.error("Échec de l'envoie de la requête: " + e.getMessage(), e);
                }
            }
        }
    }
}
