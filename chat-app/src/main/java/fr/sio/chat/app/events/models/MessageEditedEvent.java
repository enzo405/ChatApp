package fr.sio.chat.app.events.models;

import fr.sio.chat.app.events.interfaces.IEvent;
import fr.sio.chat.app.websocket.requests.RequestMessageEdit;

public class MessageEditedEvent implements IEvent {
    private final RequestMessageEdit request;

    public MessageEditedEvent(RequestMessageEdit request) {
        this.request = request;
    }

    public RequestMessageEdit getRequest() {
        return request;
    }
}
