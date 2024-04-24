package fr.sio.chat.app.dao;

import fr.sio.chat.app.dao.interfaces.IMessageDao;
import fr.sio.chat.app.dao.mapper.MessageMapper;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDao implements IMessageDao {

    private static final Logger logger = LoggerFactory.getLogger(MessageDao.class);
    private static final String tableName = "chatapp.message";
    private static final Connection conn = PostgreSQLManager.getInstance().getConnection();

    public MessageDao() { }

    public static void initializeTable() {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + tableName + """ 
                (
                    idMessage SERIAL PRIMARY KEY,
                    contenu TEXT,
                    misAJourLe BIGINT,
                    creeLe BIGINT,
                    estEpingle BOOLEAN,
                    repondA INT,
                    idDiscussion INT,
                    idCompte INT,
                    FOREIGN KEY(repondA) REFERENCES chatapp.message(idMessage),
                    FOREIGN KEY(idDiscussion) REFERENCES chatapp.discussion(idDiscussion),
                    FOREIGN KEY(idCompte) REFERENCES chatapp.comptes(idCompte)
                );
                
                SET search_path = chatapp;
                CREATE OR REPLACE FUNCTION delete_answer()
                RETURNS TRIGGER AS $BODY$
                BEGIN
                	UPDATE chatapp.message
                	SET repondA = null
                	WHERE repondA = OLD.idMessage;
                    RETURN OLD;
                END;
                $BODY$ LANGUAGE plpgsql;
                
                CREATE OR REPLACE TRIGGER delete_answer
                BEFORE DELETE ON chatapp.message
                FOR EACH ROW
                EXECUTE FUNCTION delete_answer();
                """;
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sqlCreate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Insère un Message
     * @param message
     * @return L'id de l'élément inséré
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public Integer insert(final Message message) throws DataAccessException {
        logger.info("insert: " + message.getContent());
        String sqlQuery = "INSERT INTO " + tableName +
                "(contenu,creeLe,estEpingle,idDiscussion,idCompte)" +
                "VALUES (?,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, message.getContent());
            preparedStatement.setLong(2, message.getCreatedDate().getTime());
            preparedStatement.setBoolean(3, message.getIsPinned());
            preparedStatement.setInt(4, message.getDiscussion().getId());
            preparedStatement.setInt(5, message.getCompte().getId());
            int rowsAffected = preparedStatement.executeUpdate();
            return DaoUtility.getInsertId(rowsAffected, preparedStatement);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Insère un Message avec un message répondu
     * @param message
     * @return L'id de l'élément inséré
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public Integer insertWithAnsweredMessage(final Message message) throws DataAccessException {
        logger.info("insertWithAnsweredMessage: " + message.getIdMessageAnswered() + " | " + message.getContent());
        String sqlQuery = "INSERT INTO " + tableName +
                "(contenu,creeLe,estEpingle,repondA,idDiscussion,idCompte)" +
                "VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, message.getContent());
            preparedStatement.setLong(2, message.getCreatedDate().getTime());
            preparedStatement.setBoolean(3, message.getIsPinned());
            preparedStatement.setInt(4, message.getIdMessageAnswered());
            preparedStatement.setInt(5, message.getDiscussion().getId());
            preparedStatement.setInt(6, message.getCompte().getId());
            int rowsAffected = preparedStatement.executeUpdate();
            return DaoUtility.getInsertId(rowsAffected, preparedStatement);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Supprime un message
     * @param message
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void delete(final Message message) throws DataAccessException {
        logger.info("delete: " + message.getId());
        String sqlQuery = "DELETE FROM " + tableName +
                " WHERE idMessage=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, message.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Met à jour un message
     * @param message
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void update(final Message message) throws DataAccessException {
        logger.info("update: " + message.getId());
        String sqlQuery = "UPDATE " + tableName +
                " SET contenu= ?, estEpingle=?, misajourle=?  WHERE idMessage=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setString(1, message.getContent());
            preparedStatement.setBoolean(2, message.getIsPinned());
            preparedStatement.setLong(3, message.getUpdatedDate().getTime());
            preparedStatement.setInt(4, message.getId());
            preparedStatement.executeUpdate(); // executeUpdate c'est pour INSERT,UPDATE,DELETE
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère les messages grâce à une discussion
     * @param discussion
     * @return Une liste de Message
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public List<Message> getMessagesByDiscussion(final Discussion discussion) throws DataAccessException {
        logger.info("getMessagesByDiscussion: " + discussion.getName());
        String sqlQuery = "SELECT * FROM " + tableName +
                " INNER JOIN chatapp.comptes ON message.idcompte = comptes.idcompte" +
                " WHERE idDiscussion=?" +
                " ORDER BY message.creele ASC";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, discussion.getId());
            ResultSet result = preparedStatement.executeQuery();
            List<Message> messages = new ArrayList<>();
            while (result.next()) {
                Message message = MessageMapper.map(result);
                message.setDiscussion(discussion);
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère les messages épinglés grâce à une discussion
     * @param discussion
     * @return Une liste de Message
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public List<Message> getPinnedMessagesByDiscussion(final Discussion discussion) throws DataAccessException {
        logger.info("getPinnedMessagesByDiscussion: " + discussion.getName());
        String sqlQuery = "SELECT * FROM " + tableName +
                " INNER JOIN chatapp.comptes ON message.idcompte = comptes.idcompte" +
                " WHERE idDiscussion=? AND estEpingle=true" +
                " ORDER BY message.creele ASC";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, discussion.getId());
            ResultSet result = preparedStatement.executeQuery();
            List<Message> messages = new ArrayList<>();
            while (result.next()) {
                Message message = MessageMapper.map(result);
                message.setDiscussion(discussion);
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère les messages grâce à une id de Message
     * @param discussion
     * @return Un Message
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public Message getMessageById(final int idMessage, final Discussion discussion) throws DataAccessException {
        logger.info("getMessageById: " + idMessage + " & " + discussion.getName());
        String sqlQuery = "SELECT * FROM " + tableName +
                " INNER JOIN chatapp.comptes ON message.idcompte = comptes.idcompte" +
                " WHERE idMessage=?" +
                " ORDER BY message.creele ASC";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, idMessage);
            ResultSet result = preparedStatement.executeQuery();

            Message message = null;
            if (result.next()) {
                message = MessageMapper.map(result);
                message.setDiscussion(discussion);
            }
            return message;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère les messages dans une Discussion par rapport à une chaine de caractère
     * @param content
     * @param discussion
     * @return Une liste de Message
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public List<Message> findMessagesByDiscussion(final String content, final Discussion discussion) throws DataAccessException {
        logger.info("findMessagesByDiscussion: " + content + " & " + discussion.getName());
        String sqlQuery = "SELECT * FROM " + tableName +
                " INNER JOIN chatapp.comptes ON message.idcompte = comptes.idcompte" +
                " WHERE idDiscussion=? AND contenu LIKE CONCAT('%', ?, '%')" +
                " ORDER BY message.creele DESC";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, discussion.getId());
            preparedStatement.setString(2, content);
            ResultSet result = preparedStatement.executeQuery();
            List<Message> messages = new ArrayList<>();
            while (result.next()) {
                Message message = MessageMapper.map(result);
                message.setDiscussion(discussion);
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }
}
