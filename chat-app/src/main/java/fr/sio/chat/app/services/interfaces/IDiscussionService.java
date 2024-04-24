package fr.sio.chat.app.services.interfaces;

import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.FriendException;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.User;

import java.util.List;

public interface IDiscussionService extends IService {
    int insert(Discussion discussion) throws DataAccessException;
    Discussion findOrCreateDiscussion(User user, User amis) throws FriendException, DataAccessException;
    void delete(Discussion discussion) throws DataAccessException;
    void update(Discussion discussion) throws DataAccessException;
    Discussion getDiscussionById(int id);
    List<Discussion> getDiscussionsByUser(User user);
    List<Discussion> getGroupDiscussionsByUser(User user);
    List<Discussion> getPrivateDiscussionsByUser(User user);
}
