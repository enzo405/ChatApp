package fr.sio.chat.app.services;

import fr.sio.chat.app.dao.interfaces.IMessageDao;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.Message;
import fr.sio.chat.app.services.interfaces.IMessageService;

import java.sql.Timestamp;
import java.util.List;

public class MessageService implements IMessageService {
    private final IMessageDao messageDao;

    public MessageService(IMessageDao messageDao) {
        this.messageDao = messageDao;
    }

    /**
     * Insère un nouveau message
     * @param message
     * @return L'id du message inséré
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public Integer insert(Message message) throws DataAccessException {
        message.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        return messageDao.insert(message);
    }

    /**
     * Insère un nouveau message avec un message répondu
     * @param message
     * @return L'id du message inséré
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public Integer insertWithAnsweredMessage(Message message) throws DataAccessException {
        message.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        return messageDao.insertWithAnsweredMessage(message);
    }

    /**
     * Met à jour un message
     * @param message
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void update(Message message) throws DataAccessException {
        message.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        messageDao.update(message);
    }

    /**
     * Supprime un message
     * @param message
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void delete(Message message) throws DataAccessException {
        messageDao.delete(message);
    }

    /**
     * Récupère les messages grâce à une Discussion
     * @param discussion
     * @return Une liste de Message
     */
    @Override
    public List<Message> getMessagesByDiscussion(Discussion discussion) throws DataAccessException {
        return messageDao.getMessagesByDiscussion(discussion);
    }

    /**
     * Récupère les messages épinglés grâce à une Discussion
     * @param discussion
     * @return Une liste de Message
     */
    @Override
    public List<Message> getPinnedMessagesByDiscussion(Discussion discussion) throws DataAccessException {
        return messageDao.getPinnedMessagesByDiscussion(discussion);
    }

    /**
     * Récupère les messages grâce à un id
     * @param discussion
     * @return Une Message
     */
    @Override
    public Message getMessageById(int idMessage, Discussion discussion) {
        try {
            return messageDao.getMessageById(idMessage, discussion);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    /**
     * Récupère les messages grâce à un mot clé
     * @param discussion
     * @return Une liste de Message
     */
    @Override
    public List<Message> findMessagesByDiscussion(String content, Discussion discussion) throws DataAccessException {
        return messageDao.findMessagesByDiscussion(content, discussion);
    }
}
