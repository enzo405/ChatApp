package fr.sio.chat.app.dao.interfaces;

import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.Message;

import java.util.List;

public interface IMessageDao extends IDao<Message>{
    Integer insertWithAnsweredMessage(Message message) throws DataAccessException;
    void delete(Message message) throws DataAccessException;
    void update(Message message) throws DataAccessException;
    List<Message> getMessagesByDiscussion(Discussion discussion) throws DataAccessException;
    List<Message> getPinnedMessagesByDiscussion(Discussion discussion) throws DataAccessException;
    Message getMessageById(int idMessage, Discussion discussion) throws DataAccessException;
    List<Message> findMessagesByDiscussion(String content, Discussion discussion) throws DataAccessException;
}
