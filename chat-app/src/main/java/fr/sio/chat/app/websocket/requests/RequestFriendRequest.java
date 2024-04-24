package fr.sio.chat.app.websocket.requests;

import fr.sio.chat.app.events.dispatchers.FriendRequestDispatcher;
import fr.sio.chat.app.events.models.FriendRequestEvent;
import fr.sio.chat.app.models.Friend;
import fr.sio.chat.app.models.User;

public class RequestFriendRequest extends Request {
    private final Integer idRelation;
    private final User target;
    private final User requester;
    private final boolean isAccepted;

    public RequestFriendRequest(Friend friend) {
        this.type = ERequestType.FRIEND_REQUEST;
        this.idRelation = friend.getIdRelation();
        this.target = friend.getCompte2();
        this.requester = friend.getCompte1();
        this.isAccepted = friend.getEstAccepte();

        this.receiversId.add(friend.getCompte2().getId()); // target
    }

    @Override
    public void dispatch() {
        FriendRequestDispatcher.getInstance().dispatch(new FriendRequestEvent(this));
    }

    public User getRequester() {
        return requester;
    }

    public Integer getIdRelation() {
        return idRelation;
    }

    public User getTarget() {
        return target;
    }
    public boolean getIsAccepted() { return isAccepted; }
}
