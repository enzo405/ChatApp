package fr.sio.chat.app.services.interfaces;

import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.NotAllowedException;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.UserDiscussion;

import java.util.List;

public interface IUserDiscussionService extends IService {
    void insert(UserDiscussion userDiscussion) throws DataAccessException;
    void insert(List<UserDiscussion> userDiscussions) throws DataAccessException;
    void delete(UserDiscussion userDiscussion) throws NotAllowedException, DataAccessException;
    List<UserDiscussion> getDiscussionMembers(Discussion discussion);
}
