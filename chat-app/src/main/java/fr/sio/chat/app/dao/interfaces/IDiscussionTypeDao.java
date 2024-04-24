package fr.sio.chat.app.dao.interfaces;

import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.DiscussionType;

import java.util.List;

public interface IDiscussionTypeDao extends IDao<DiscussionType> {
    DiscussionType getDiscussionTypeById(int discussionTypeId) throws DataAccessException;
    DiscussionType getDiscussionTypeByName(String name) throws DataAccessException;
    List<DiscussionType> getAllDiscussionType() throws DataAccessException;
}
