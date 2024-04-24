package fr.sio.websocket.requests;

import fr.sio.websocket.SessionManager;
import fr.sio.websocket.models.User;
import jakarta.websocket.Session;

public class RequestLogin extends Request {
    private User requester;

    @Override
    public void handle(Session session) {
        SessionManager.addUser(session, requester.getId());
    }
}
