package fr.sio.chat.app.events.models;

import fr.sio.chat.app.events.interfaces.IEvent;
import fr.sio.chat.app.websocket.requests.RequestFriendRequest;

public class FriendRequestEvent implements IEvent {
    private final RequestFriendRequest request;

    public FriendRequestEvent(RequestFriendRequest request) {
        this.request = request;
    }

    public RequestFriendRequest getRequest() {
        return request;
    }
}
