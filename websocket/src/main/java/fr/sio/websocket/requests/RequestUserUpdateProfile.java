package fr.sio.websocket.requests;

import fr.sio.websocket.models.User;
import jakarta.websocket.Session;

public class RequestUserUpdateProfile extends Request {
    private User requester;

    @Override
    public void handle(Session session) {
        broadcastMessage(this);
    }
}
