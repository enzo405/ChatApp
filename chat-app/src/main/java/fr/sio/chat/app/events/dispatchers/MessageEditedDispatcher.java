package fr.sio.chat.app.events.dispatchers;

import fr.sio.chat.app.events.interfaces.IEvent;
import fr.sio.chat.app.events.interfaces.IEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MessageEditedDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(MessageEditedDispatcher.class);
    private static MessageEditedDispatcher instance = null;
    private final List<IEventListener> listeners = new ArrayList<>();

    public static MessageEditedDispatcher getInstance() {
        if (instance == null) {
            instance = new MessageEditedDispatcher();
        }
        return instance;
    }

    public void addEventListener(IEventListener eventListener) {
        listeners.add(eventListener);
    }

    public void removeEventListener(IEventListener eventListener) {
        listeners.remove(eventListener);
    }

    public void dispatch(IEvent event) {
        for (IEventListener eventListener : listeners) {
            eventListener.handle(event);
        }
    }
}
