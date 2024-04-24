package fr.sio.chat.app.websocket.requests;

import fr.sio.chat.app.models.User;

import java.util.List;

public class RequestUserUpdateProfile extends Request {
    private final User requester;

    public RequestUserUpdateProfile(User user, List<Integer> receiversIds) {
        this.type = ERequestType.USER_UPDATE_PROFILE;
        this.requester = user;
        this.receiversId = receiversIds;
    }

    @Override
    public void dispatch() {
       //TODO: A faire
    }

    public User getRequester() {
        return requester;
    }
}
