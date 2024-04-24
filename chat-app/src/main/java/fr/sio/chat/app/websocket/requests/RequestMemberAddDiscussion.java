package fr.sio.chat.app.websocket.requests;

import fr.sio.chat.app.events.dispatchers.MemberAddDiscussionDispatcher;
import fr.sio.chat.app.events.models.MemberAddDiscussionEvent;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.User;

import java.util.List;

public class RequestMemberAddDiscussion extends Request {
    private final Discussion discussion;
    private final User user;

    public RequestMemberAddDiscussion(User user, Discussion discussion, List<Integer> receiversIds) {
        this.type = ERequestType.MEMBER_ADD_DISCUSSION;
        this.user = user;
        this.discussion = discussion;
        this.receiversId = receiversIds;
    }

    @Override
    public void dispatch() {
        MemberAddDiscussionDispatcher.getInstance().dispatch(new MemberAddDiscussionEvent(this));
    }

    public Discussion getDiscussion() {
        return discussion;
    }

    public User getUser() {
        return user;
    }
}
