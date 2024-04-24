package fr.sio.chat.app.services.interfaces;

import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.DiscussionType;

import java.util.List;

public interface IDiscussionTypeService extends IService {
    DiscussionType getDiscussionTypeById(int discussionTypeId);
    DiscussionType getDefaultDiscussionType(); // Private
    List<DiscussionType> getAllDiscussionType() throws DataAccessException;
}
