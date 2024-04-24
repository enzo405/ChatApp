package fr.sio.chat.app.dao;

import fr.sio.chat.app.dao.interfaces.IDiscussionTypeDao;
import fr.sio.chat.app.dao.mapper.DiscussionTypeMapper;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.DiscussionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscussionTypeDao implements IDiscussionTypeDao {
    private static final Logger logger = LoggerFactory.getLogger(DiscussionTypeDao.class);
    private static final String tableName = "chatapp.typediscussion";
    private static final Connection conn = PostgreSQLManager.getInstance().getConnection();

    public DiscussionTypeDao() { }

    public static void initializeTable() {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + tableName + """ 
                (
                    idTypeDiscussion SERIAL PRIMARY KEY,
                    libelle VARCHAR(255),
                    icone VARCHAR(255),
                    estGroupe BOOLEAN
                );""";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sqlCreate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Insère un type de discussion
     * @param discussionType
     * @return L'id de l'élément inséré
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public Integer insert(final DiscussionType discussionType) throws DataAccessException {
        logger.info("insert: " + discussionType.toString());
        String sqlQuery = "INSERT INTO " + tableName +
                "(libelle, icone, estgroupe)" +
                "VALUES (?,?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, discussionType.getLabel());
            preparedStatement.setString(2, discussionType.getIcon());
            preparedStatement.setBoolean(3, discussionType.getIsGroup());
            int rowsAffected = preparedStatement.executeUpdate();
            return DaoUtility.getInsertId(rowsAffected, preparedStatement);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère un type de discussion grâce à un id
     * @param discussionTypeId
     * @return Un TypeDiscussion
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public DiscussionType getDiscussionTypeById(final int discussionTypeId) throws DataAccessException {
        logger.info("getDiscussionTypeById:" + discussionTypeId);
        String sqlQuery = "SELECT * FROM " + tableName + " WHERE idTypeDiscussion=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, discussionTypeId);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                return DiscussionTypeMapper.map(result);
            }
            return null;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère un type de discussion grâce à son nom
     * @param name
     * @return Un TypeDiscussion
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public DiscussionType getDiscussionTypeByName(final String name) throws DataAccessException {
        logger.info("getDiscussionTypeByName: " + name);
        String sqlQuery = "SELECT * FROM " + tableName + " WHERE libelle=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setString(1, name);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                return DiscussionTypeMapper.map(result);
            }
            return null;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère tous les types de discussion
     * @return Une liste de TypeDiscussion
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    public List<DiscussionType> getAllDiscussionType() throws DataAccessException {
        logger.info("getAllDiscussionType");
        String sqlQuery = "SELECT * FROM " + tableName;
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            ResultSet result = preparedStatement.executeQuery();
            List<DiscussionType> discussionTypes = new ArrayList<>();
            while (result.next()) {
                discussionTypes.add(DiscussionTypeMapper.map(result));
            }
            return discussionTypes;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }
}
