package fr.sio.chat.app.dao;

import fr.sio.chat.app.dao.interfaces.IDiscussionDao;
import fr.sio.chat.app.dao.mapper.DiscussionMapper;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscussionDao implements IDiscussionDao {
    private static final Logger logger = LoggerFactory.getLogger(DiscussionDao.class);
    private static final String tableName = "chatapp.discussion";
    private static final Connection conn = PostgreSQLManager.getInstance().getConnection();

    public DiscussionDao() { }

    public static void initializeTable() {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + tableName + """ 
                (
                    idDiscussion SERIAL PRIMARY KEY,
                    nom VARCHAR(255),
                    idTypeDiscussion INT,
                    FOREIGN KEY(idTypeDiscussion) REFERENCES chatapp.typediscussion(idTypeDiscussion)
                );""";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sqlCreate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Insère une discussion
     * @param discussion
     * @return L'id de l'élément inséré
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public Integer insert(final Discussion discussion) throws DataAccessException {
        logger.info("insert: " + discussion.getName());
        String sqlQuery = "INSERT INTO " + tableName +
                "(nom,idTypeDiscussion)" +
                "VALUES (?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, discussion.getName());
            preparedStatement.setInt(2, discussion.getDiscussionType().getId());
            int rowsAffected = preparedStatement.executeUpdate();
            return DaoUtility.getInsertId(rowsAffected, preparedStatement);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Supprime une discussion
     * @param discussion
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void delete(final Discussion discussion) throws DataAccessException {
        logger.info("delete: " + discussion.getName());
        String sqlQuery = "DELETE FROM " + tableName +
                " WHERE idDiscussion=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, discussion.getId());
            preparedStatement.executeUpdate(); // executeUpdate c'est pour INSERT,UPDATE,DELETE
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Met à jour la discussion
     * @param discussion
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void update(final Discussion discussion) throws DataAccessException {
        logger.info("update: " + discussion.getName());
        String sqlQuery = "UPDATE " + tableName + " SET nom=?,idTypeDiscussion=? WHERE idDiscussion=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setString(1, discussion.getName());
            preparedStatement.setInt(2, discussion.getDiscussionType().getId());
            preparedStatement.setInt(3, discussion.getId());
            preparedStatement.executeUpdate(); // executeUpdate c'est pour INSERT,UPDATE,DELETE
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère une discussion grâce à un id
     * @param id
     * @return Une Discussion
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public Discussion getDiscussionById(final int id) throws DataAccessException {
        logger.info("getDiscussionById: " + id);
        String sqlQuery = "SELECT * FROM " + tableName + " " +
                " INNER JOIN chatapp.typediscussion ON discussion.idtypediscussion = typediscussion.idtypediscussion" +
                " WHERE idDiscussion=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                return DiscussionMapper.mapWithType(result);
            }
            return null;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère une liste de discussions pour un utilisateur
     * @param user
     * @return Une liste de Discussions
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public List<Discussion> getDiscussionsByUser(final User user) throws DataAccessException {
        logger.info("getDiscussionsByUser: " + user.getPseudo());
        String sqlQuery = "SELECT * FROM " + tableName +
                " INNER JOIN chatapp.faitpartiede ON discussion.iddiscussion = faitpartiede.iddiscussion" +
                " INNER JOIN chatapp.typediscussion ON discussion.idtypediscussion = typediscussion.idtypediscussion" +
                " WHERE idcompte=?;";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, user.getId());
            ResultSet result = preparedStatement.executeQuery();
            List<Discussion> discussions = new ArrayList<>();
            while (result.next()) {
                discussions.add(DiscussionMapper.mapWithType(result));
            }
            return discussions;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère une liste de discussions de type groupe pour un utilisateur
     * @param user
     * @param isGroup
     * @return Une liste de Discussions
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public List<Discussion> getDiscussionsByUser(final User user, final boolean isGroup) throws DataAccessException {
        logger.info("getDiscussionsByUser: " + user.getPseudo() + " & " + isGroup);
        String sqlQuery = "SELECT * FROM " + tableName +
                " INNER JOIN chatapp.faitpartiede ON discussion.iddiscussion = faitpartiede.iddiscussion" +
                " INNER JOIN chatapp.typediscussion ON discussion.idtypediscussion = typediscussion.idtypediscussion" +
                " WHERE estgroupe=? AND idcompte=?;";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setBoolean(1, isGroup);
            preparedStatement.setInt(2, user.getId());
            ResultSet result = preparedStatement.executeQuery();
            List<Discussion> discussions = new ArrayList<>();
            while (result.next()) {
                discussions.add(DiscussionMapper.mapWithType(result));
            }
            return discussions;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère une liste de discussions de type non groupe pour deux utilisateurs qui sont amis
     * @param user1
     * @param user2
     * @return Une liste de Discussions
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public List<Discussion> getDiscussionsFriendByUsers(final User user1, final User user2) throws DataAccessException {
        logger.info("getDiscussionsFriendByUsers: " + user1.getPseudo() + " & " + user2.getPseudo());
        String sqlQuery = "SELECT * FROM " + tableName +
                " INNER JOIN chatapp.typediscussion ON discussion.idtypediscussion = typediscussion.idtypediscussion" +
                " WHERE estgroupe=false AND discussion.iddiscussion IN (" +
                " SELECT iddiscussion FROM chatapp.faitpartiede WHERE idcompte=?" +
                " INTERSECT" +
                " SELECT iddiscussion FROM chatapp.faitpartiede WHERE idcompte=?);";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, user1.getId());
            preparedStatement.setInt(2, user2.getId());
            ResultSet result = preparedStatement.executeQuery();
            List<Discussion> discussions = new ArrayList<>();
            while (result.next()) {
                discussions.add(DiscussionMapper.mapWithType(result));
            }
            return discussions;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }
}
