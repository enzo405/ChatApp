package fr.sio.chat.app.dao.interfaces;

import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.User;
import java.util.List;

public interface IDiscussionDao extends IDao<Discussion> {
    void delete(Discussion discussion) throws DataAccessException;
    void update(Discussion discussion) throws DataAccessException;
    Discussion getDiscussionById(int id) throws DataAccessException;
    List<Discussion> getDiscussionsByUser(User user) throws DataAccessException;
    List<Discussion> getDiscussionsByUser(User user, boolean isGroup) throws DataAccessException;
    List<Discussion> getDiscussionsFriendByUsers(User user1, User user2) throws DataAccessException;
}