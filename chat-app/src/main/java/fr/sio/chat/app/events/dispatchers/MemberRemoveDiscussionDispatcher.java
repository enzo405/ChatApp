package fr.sio.chat.app.events.dispatchers;

import fr.sio.chat.app.events.interfaces.IEvent;
import fr.sio.chat.app.events.interfaces.IEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MemberRemoveDiscussionDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(MemberRemoveDiscussionDispatcher.class);
    private static MemberRemoveDiscussionDispatcher instance = null;
    private final List<IEventListener> listeners = new ArrayList<>();

    public static MemberRemoveDiscussionDispatcher getInstance() {
        if (instance == null) {
            instance = new MemberRemoveDiscussionDispatcher();
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
