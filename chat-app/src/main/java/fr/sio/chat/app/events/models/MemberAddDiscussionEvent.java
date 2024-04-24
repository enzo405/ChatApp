package fr.sio.chat.app.events.models;

import fr.sio.chat.app.events.interfaces.IEvent;
import fr.sio.chat.app.websocket.requests.RequestMemberAddDiscussion;

public class MemberAddDiscussionEvent implements IEvent {
    private final RequestMemberAddDiscussion request;

    public MemberAddDiscussionEvent(RequestMemberAddDiscussion request) {
        this.request = request;
    }

    public RequestMemberAddDiscussion getRequest() {
        return request;
    }
}
