package fr.sio.chat.app.websocket.requests;

import fr.sio.chat.app.models.Discussion;

import java.util.List;

public class RequestMessageSystem extends Request {
    private final Discussion discussion;
    private final String content;

    public RequestMessageSystem(Discussion discussion, String content, List<Integer> receiversIds) {
        this.type = ERequestType.MESSAGE_SYSTEM;
        this.discussion = discussion;
        this.content = content;
        this.receiversId = receiversIds;
    }

    @Override
    public void dispatch() {
       //TODO: Servira pour les message système
    }

    public Discussion getDiscussion() {
        return discussion;
    }

    public String getContent() {
        return content;
    }
}
