package fr.sio.chat.app.events.models;

import fr.sio.chat.app.events.interfaces.IEvent;
import fr.sio.chat.app.websocket.requests.RequestMessageDelete;

public class MessageDeletedEvent implements IEvent {
    private final RequestMessageDelete request;

    public MessageDeletedEvent(RequestMessageDelete request) {
        this.request = request;
    }

    public RequestMessageDelete getRequest() {
        return request;
    }
}
