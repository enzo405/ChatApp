package fr.sio.chat.app.websocket.requests;

import fr.sio.chat.app.events.dispatchers.MemberRemoveDiscussionDispatcher;
import fr.sio.chat.app.events.models.MemberRemoveDiscussionEvent;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.User;

import java.util.List;

public class RequestMemberRemoveDiscussion extends Request {
    private final Discussion discussion;
    private final User user;

    public RequestMemberRemoveDiscussion(User user, Discussion discussion, List<Integer> receiversIds) {
        this.type = ERequestType.MEMBER_REMOVE_DISCUSSION;
        this.user = user;
        this.discussion = discussion;
        this.receiversId = receiversIds;
    }

    @Override
    public void dispatch() {
        MemberRemoveDiscussionDispatcher.getInstance().dispatch(new MemberRemoveDiscussionEvent(this));
    }

    public Discussion getDiscussion() {
        return discussion;
    }

    public User getUser() {
        return user;
    }
}
