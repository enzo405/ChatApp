package fr.sio.chat.app.dao.interfaces;

import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.UserDiscussion;

import java.util.List;

public interface IUserDiscussionDao extends IDao<UserDiscussion> {
    void insert(List<UserDiscussion> userDiscussions) throws DataAccessException;
    void delete(UserDiscussion userDiscussion) throws DataAccessException;
    List<UserDiscussion> getByDiscussion(Discussion discussion) throws DataAccessException;
}
