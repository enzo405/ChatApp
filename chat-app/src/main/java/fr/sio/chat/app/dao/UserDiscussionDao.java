package fr.sio.chat.app.dao;

import fr.sio.chat.app.dao.interfaces.IUserDiscussionDao;
import fr.sio.chat.app.dao.mapper.UserDiscussionMapper;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.UserDiscussion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDiscussionDao implements IUserDiscussionDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDiscussionDao.class);
    private static final String tableName = "chatapp.faitpartiede";
    private static final Connection conn = PostgreSQLManager.getInstance().getConnection();

    public UserDiscussionDao() { }

    public static void initializeTable() {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + tableName + """ 
                (
                    idcompte integer not null references chatapp.comptes,
                    iddiscussion integer not null references chatapp.discussion,
                    estproprietaire boolean,
                    primary key (idcompte, iddiscussion)
                );""";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sqlCreate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // region CRUD

    /**
     * Insère un UserDiscussion
     * @param userDiscussion
     * @return L'id de l'élément inséré
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public Integer insert(final UserDiscussion userDiscussion) throws DataAccessException {
        logger.info("insert: " + userDiscussion.getUser().getPseudo() + " & " + userDiscussion.getDiscussion().getName() + " & " + userDiscussion.isOwner());
        String sqlQuery = "INSERT INTO " + tableName +
                "(idcompte, iddiscussion, estproprietaire)" +
                "VALUES (?,?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userDiscussion.getUser().getId());
            preparedStatement.setInt(2, userDiscussion.getDiscussion().getId());
            preparedStatement.setBoolean(3, userDiscussion.isOwner());

            int rowsAffected = preparedStatement.executeUpdate();
            return DaoUtility.getInsertId(rowsAffected, preparedStatement);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Insère une liste de UserDiscussion
     * @param userDiscussions
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void insert(final List<UserDiscussion> userDiscussions) throws DataAccessException {
        logger.info("insert: " + userDiscussions.size());
        String sqlQuery = "INSERT INTO " + tableName +
                "(idcompte, iddiscussion, estproprietaire)" +
                "VALUES (?,?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            for (UserDiscussion userDiscussion : userDiscussions) {
                preparedStatement.setInt(1, userDiscussion.getUser().getId());
                preparedStatement.setInt(2, userDiscussion.getDiscussion().getId());
                preparedStatement.setBoolean(3, userDiscussion.isOwner());
                preparedStatement.addBatch(); // Ajouter la requête à la liste du batch
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Supprime un UserDiscussion
     * @param userDiscussion
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void delete(final UserDiscussion userDiscussion) throws DataAccessException {
        logger.info("delete: " + userDiscussion.getUser().getPseudo() + " & " + userDiscussion.getDiscussion().getName() + " & " + userDiscussion.isOwner());
        String sqlQuery = "DELETE FROM " + tableName +
                " WHERE iddiscussion=? AND idcompte=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, userDiscussion.getDiscussion().getId());
            preparedStatement.setInt(2, userDiscussion.getUser().getId());
            preparedStatement.executeUpdate(); // executeUpdate c'est pour INSERT,UPDATE,DELETE
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère les UserDiscussion grâce à une Discussion
     * @param discussion
     * @return Une liste de UserDiscussion
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public List<UserDiscussion> getByDiscussion(final Discussion discussion) throws DataAccessException {
        logger.info("getDiscussionMember: " + discussion.getName());
        String sqlQuery = "SELECT * FROM " + tableName +
                " INNER JOIN chatapp.discussion ON discussion.iddiscussion = faitpartiede.iddiscussion" +
                " INNER JOIN chatapp.comptes ON comptes.idcompte = faitpartiede.idcompte" +
                " WHERE discussion.iddiscussion=?;";
        List<UserDiscussion> usersDiscussion = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, discussion.getId());
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                usersDiscussion.add(UserDiscussionMapper.map(result, discussion));
            }
            return usersDiscussion;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }
    // endregion CRUD
}
