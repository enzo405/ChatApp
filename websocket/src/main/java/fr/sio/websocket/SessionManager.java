package fr.sio.websocket;

import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private static final Map<Session, Integer> sessionUserMap = new HashMap<>();

    public static void addUser(Session session, Integer userId) {
        if (!sessionUserMap.containsKey(session)) {
            sessionUserMap.put(session, userId);
            logger.info("Session n°" + session.getId() + " (utilisateur n°" + userId + ") ajouté aux sessions ouverte\nNbr de session ouverte: " + sessionUserMap.size());
        } else {
            logger.info("La session " + session.getId() + " est déjà ouverte");
        }
    }

    public static void removeUser(Session session) {
        if (session != null && sessionUserMap.containsKey(session)) {
            Integer userId = sessionUserMap.get(session);
            sessionUserMap.remove(session);
            logger.info("Session n°" + session.getId() + " (utilisateur n°" + userId + ") supprimé des sessions ouverte\nNbr de session ouverte: " + sessionUserMap.size());
        }
    }

    public static Session getSessionByUserId(Integer userId) {
        for (Map.Entry<Session, Integer> entry : sessionUserMap.entrySet()) {
            if (entry.getValue() == userId) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static Map<Session, Integer> getSessionUserMap() {
        return sessionUserMap;
    }
}