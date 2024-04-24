package fr.sio.chat.app.websocket.requests;

import fr.sio.chat.app.events.dispatchers.MessageEditedDispatcher;
import fr.sio.chat.app.events.models.MessageEditedEvent;
import fr.sio.chat.app.models.Message;

import java.util.List;

public class RequestMessageEdit extends Request {
    private final Message message;

    public RequestMessageEdit(Message message, List<Integer> receiversIds) {
        this.type = ERequestType.MESSAGE_EDIT;
        this.message = message;
        this.receiversId = receiversIds;
    }

    @Override
    public void dispatch() {
        MessageEditedDispatcher.getInstance().dispatch(new MessageEditedEvent(this));
    }

    public Message getMessage() {
        return message;
    }
}
