package fr.sio.chat.app.events.models;

import fr.sio.chat.app.events.interfaces.IEvent;
import fr.sio.chat.app.websocket.requests.RequestMessageNew;

public class MessageReceivedEvent implements IEvent {
    private final RequestMessageNew request;

    public MessageReceivedEvent(RequestMessageNew request) {
        this.request = request;
    }

    public RequestMessageNew getRequest() {
        return request;
    }
}
