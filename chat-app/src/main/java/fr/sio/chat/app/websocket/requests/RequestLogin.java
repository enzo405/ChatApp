package fr.sio.chat.app.websocket.requests;


import fr.sio.chat.app.models.User;

public class RequestLogin extends Request {
    private final User requester;

    public RequestLogin(User user) {
        this.type = ERequestType.LOGIN;
        this.requester = user;
    }

    @Override
    public void dispatch() {
        // TODO servira pour le status
    }

    public User getRequester() {
        return requester;
    }
}
