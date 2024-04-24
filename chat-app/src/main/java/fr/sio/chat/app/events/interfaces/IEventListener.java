package fr.sio.chat.app.events.interfaces;

public interface IEventListener {
    void handle(IEvent event);
    void addListeners();
    void removeListeners();
}
