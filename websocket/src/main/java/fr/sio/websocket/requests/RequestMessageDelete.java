package fr.sio.websocket.requests;

import fr.sio.websocket.models.Discussion;
import fr.sio.websocket.models.Message;
import jakarta.websocket.Session;

public class RequestMessageDelete extends Request {
    private Discussion discussion;
    private Message message;

    @Override
    public void handle(Session session) {
        broadcastMessage(this);
    }
}