package fr.sio.websocket.requests;

import fr.sio.websocket.models.Discussion;
import fr.sio.websocket.models.User;
import jakarta.websocket.Session;

public class RequestMemberRemoveDiscussion extends Request {
    private Discussion discussion;
    private User user;

    @Override
    public void handle(Session session) {
        broadcastMessage(this);
    }
}
