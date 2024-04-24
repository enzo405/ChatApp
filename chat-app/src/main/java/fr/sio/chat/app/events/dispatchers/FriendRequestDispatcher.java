package fr.sio.chat.app.events.dispatchers;

import fr.sio.chat.app.events.interfaces.IEvent;
import fr.sio.chat.app.events.interfaces.IEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FriendRequestDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(FriendRequestDispatcher.class);
    private static FriendRequestDispatcher instance = null;
    private final List<IEventListener> listeners = new ArrayList<>();

    public static FriendRequestDispatcher getInstance() {
        if (instance == null) {
            instance = new FriendRequestDispatcher();
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
