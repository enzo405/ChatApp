package fr.sio.chat.app.dao.interfaces;

import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Session;

public interface ISessionDao extends IDao<Session>{
    Session getSessionByToken(String token) throws DataAccessException;
    Session getSessionByUserId(int userId) throws DataAccessException;
    void expireSession(Session session) throws DataAccessException;
}
