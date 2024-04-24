package fr.sio.chat.app.services.interfaces;

import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.FriendException;
import fr.sio.chat.app.models.Friend;
import fr.sio.chat.app.models.User;
import java.util.List;

public interface IFriendService extends IService {
    int insert(User userFriend) throws FriendException, DataAccessException;
    void delete(Friend friend) throws DataAccessException;
    void update(Friend friend) throws DataAccessException;
    List<Friend> getFriendRequestsByUser(User user);
    List<Friend> getFriendsByUser(User user);
    boolean isFriend(User user, User friend);
    boolean hasAlreadyRequestedFriend(User user, User friend);
}
