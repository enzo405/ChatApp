package fr.sio.chat.app.events.models;

import fr.sio.chat.app.events.interfaces.IEvent;
import fr.sio.chat.app.websocket.requests.RequestMemberRemoveDiscussion;

public class MemberRemoveDiscussionEvent implements IEvent {
    private final RequestMemberRemoveDiscussion request;

    public MemberRemoveDiscussionEvent(RequestMemberRemoveDiscussion request) {
        this.request = request;
    }

    public RequestMemberRemoveDiscussion getRequest() {
        return request;
    }
}
