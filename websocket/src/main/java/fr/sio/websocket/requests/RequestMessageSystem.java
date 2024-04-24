package fr.sio.websocket.requests;

import fr.sio.websocket.models.Discussion;
import jakarta.websocket.Session;

public class RequestMessageSystem extends Request {
    private Discussion discussion;
    private String content;

    @Override
    public void handle(Session session) {
        broadcastMessage(this);
    }
}
