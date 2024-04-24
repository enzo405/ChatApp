package fr.sio.websocket.requests;

import fr.sio.websocket.models.User;
import jakarta.websocket.Session;

public class RequestFriendRequest extends Request {
    private Integer idRelation;
    private User target;
    private User requester;
    private boolean isAccepted;

    @Override
    public void handle(Session session) {
        broadcastMessage(this);
    }
}
