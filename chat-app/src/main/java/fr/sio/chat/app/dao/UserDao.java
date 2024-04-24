package fr.sio.chat.app.dao;

import fr.sio.chat.app.dao.interfaces.IUserDao;
import fr.sio.chat.app.dao.mapper.UserMapper;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;

public class UserDao implements IUserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);
    private static final String tableName = "chatapp.comptes";
    private static final Connection conn = PostgreSQLManager.getInstance().getConnection();

    public UserDao() { }

    public static void initializeTable() {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + tableName + """ 
                (
                    idCompte SERIAL PRIMARY KEY,
                    pseudo VARCHAR(50),
                    photoDeProfil BYTEA,
                    courriel VARCHAR(255),
                    courrielVerifieLe BIGINT,
                    motDePasse VARCHAR(255),
                    creeLe BIGINT,
                    misAJourLe BIGINT,
                    idStatus INT NOT NULL
                );""";
        // TODO                   FOREIGN KEY(idStatus) REFERENCES Status(idStatus)
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sqlCreate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // region CRUD

    /**
     * Récupère un utilisateur grâce à un id
     * @param id
     * @return Un User
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public User getById(final int id) throws DataAccessException {
        logger.info("getById: " + id);
        String sqlQuery = "SELECT * FROM " + tableName + " WHERE idCompte=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, id);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                return UserMapper.advancedMap(result);
            }
            return null;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère un utilisateur grâce à son pseudo
     * @param usernameValue
     * @return Un User
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public User getByPseudo(final String usernameValue) throws DataAccessException {
        logger.info("getByPseudo:" + usernameValue);
        String sqlQuery = "SELECT * FROM " + tableName + " WHERE pseudo=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setString(1, usernameValue);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                return UserMapper.advancedMap(result);
            }
            return null;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère un utilisateur grâce à son email
     * @param emailValue
     * @return Un Utilisateur
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public User getByEmail(final String emailValue) throws DataAccessException {
        logger.info("getByEmail: " + emailValue);
        String sqlQuery = "SELECT * FROM " + tableName + " WHERE courriel=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setString(1, emailValue);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                return UserMapper.advancedMap(result);
            }
            return null;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Insère un utilisateur
     * @param user
     * @return L'id de l'élément inséré
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public Integer insert(final User user) throws DataAccessException {
        logger.info("insert: " + user.toString());
        String sqlQuery = "INSERT INTO " + tableName +
            "(pseudo,photoDeProfil,courriel,motDePasse,creeLe,idStatus)" +
            "VALUES (?,?,?,?,?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getPseudo());
            preparedStatement.setBytes(2, user.getProfilePicture());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setLong(5, user.getCreatedDate().getTime());
            preparedStatement.setInt(6, user.getId()); // TODO mettre le status quand crée

            int rowsAffected = preparedStatement.executeUpdate();
            return DaoUtility.getInsertId(rowsAffected, preparedStatement);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Met à jour un utilisateur
     * @param user
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void update(final User user) throws DataAccessException {
        logger.info("update: " + user.toString());
        String sqlQuery = "UPDATE " + tableName +
                " SET pseudo=?,photoDeProfil=?,courriel=?,motDePasse=?,idStatus=?, misajourle=?" +
                " WHERE idcompte=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setString(1, user.getPseudo());
            preparedStatement.setBytes(2, user.getProfilePicture());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setInt(5, user.getId()); // TODO mettre le status quand crée
            preparedStatement.setLong(6, user.getUpdatedDate().getTime());
            preparedStatement.setInt(7, user.getId());
            preparedStatement.executeUpdate(); // executeUpdate c'est pour INSERT,UPDATE,DELETE
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }
    // endregion CRUD
}