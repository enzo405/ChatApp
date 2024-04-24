package fr.sio.chat.app.websocket.requests;


public class RequestLogout extends Request {
    public RequestLogout() {
        this.type = ERequestType.LOGOUT;
    }

    @Override
    public void dispatch() {
        // TODO servira pour le status
    }
}
