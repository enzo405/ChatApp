package fr.sio.chat.app.dao.interfaces;

import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Friend;
import fr.sio.chat.app.models.User;

import java.util.List;

public interface IFriendDao extends IDao<Friend> {
    void delete(Friend friend) throws DataAccessException;
    void update(Friend friend) throws DataAccessException;
    List<Friend> getFriendsByUser(User user) throws DataAccessException;
    List<Friend> getFriendRequestsByUser(User user) throws DataAccessException;
    boolean hasFriendRelation(User user, User friend, boolean estAccepte) throws DataAccessException;
}
