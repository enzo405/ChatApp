package fr.sio.chat.app.websocket.requests;

import fr.sio.chat.app.events.dispatchers.MessageReceivedDispatcher;
import fr.sio.chat.app.events.models.MessageReceivedEvent;
import fr.sio.chat.app.models.Message;
import java.util.List;

public class RequestMessageNew extends Request {
    private final Message message;

    public RequestMessageNew(Message message, List<Integer> receiversIds) {
        this.type = ERequestType.MESSAGE_NEW;
        this.message = message;
        this.receiversId = receiversIds;
    }

    @Override
    public void dispatch() {
        MessageReceivedDispatcher.getInstance().dispatch(new MessageReceivedEvent(this));
    }

    public Message getMessage() {
        return message;
    }
}
