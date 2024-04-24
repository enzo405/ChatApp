package fr.sio.chat.app.websocket.requests;

import fr.sio.chat.app.events.dispatchers.MessageDeletedDispatcher;
import fr.sio.chat.app.events.models.MessageDeletedEvent;
import fr.sio.chat.app.models.Message;

import java.util.List;


public class RequestMessageDelete extends Request {
    private final Message message;

    public RequestMessageDelete(Message message, List<Integer> receiversIds) {
        this.type = ERequestType.MESSAGE_DELETE;
        this.message = message;
        this.receiversId = receiversIds;
    }

    @Override
    public void dispatch() {
        MessageDeletedDispatcher.getInstance().dispatch(new MessageDeletedEvent(this));
    }

    public Message getMessage() {
        return message;
    }
}