package fr.sio.websocket.requests;

import fr.sio.websocket.SessionManager;
import jakarta.websocket.Session;

public class RequestLogout extends Request {
    @Override
    public void handle(Session session) {
        SessionManager.removeUser(session);
    }
}
