package fr.sio.chat.app.services.interfaces;

import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Session;
import fr.sio.chat.app.models.User;

public interface ISessionService extends IService {
    void createSession(User user, String token) throws DataAccessException ;
    Session getSessionByUserId(User user);
    Session getSessionByToken(String token);
    void expireSession(Session session) throws DataAccessException;
    boolean isSessionActive(Session session);
}
