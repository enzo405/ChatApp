package fr.sio.chat.app.dao;
import fr.sio.chat.app.dao.interfaces.ISessionDao;
import fr.sio.chat.app.dao.mapper.SessionMapper;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class SessionDao implements ISessionDao {
    private static final Logger logger = LoggerFactory.getLogger(SessionDao.class);
    private static final String tableName = "chatapp.sessions";
    private static final Connection conn = PostgreSQLManager.getInstance().getConnection();

    public SessionDao() { }

    public static void initializeTable() {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + tableName + """ 
                (
                    idSession SERIAL PRIMARY KEY,
                    token VARCHAR(255),
                    expireLe BIGINT,
                    creeLe BIGINT,
                    idCompte INT NOT NULL
                );""";
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sqlCreate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Insère une session
     * @param session
     * @return L'id de l'élément inséré
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public Integer insert(final Session session) throws DataAccessException {
        logger.info("insert: " + session.getIdAccount() + " => " + session.getToken());
        String query = "INSERT INTO " + tableName + " (token, expireLe, creeLe, idCompte) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, session.getToken());
            preparedStatement.setLong(2, session.getExpirationDate().getTime());
            preparedStatement.setLong(3, session.getCreatedDate().getTime());
            preparedStatement.setInt(4, session.getIdAccount());

            int rowsAffected = preparedStatement.executeUpdate();
            return DaoUtility.getInsertId(rowsAffected, preparedStatement);
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère la session grâce à l'id de l'utilisateur
     * @param userId
     * @return Une Session
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public Session getSessionByUserId(final int userId) throws DataAccessException {
        logger.info("getSessionByUserId: " + userId);
        String query = "SELECT * FROM " + tableName + " WHERE idCompte = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setInt(1, userId);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                return SessionMapper.map(result);
            }
            return null;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère une Session grâce à un token
     * @param token
     * @return Une Session
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public Session getSessionByToken(final String token) throws DataAccessException {
        logger.info("getSessionByToken: " + token);
        String query = "SELECT * FROM " + tableName + " WHERE token = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setString(1, token);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                return SessionMapper.map(result);
            }
            return null;
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Met à jour la Session pour qu'elle soit expirée
     * @param session
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void expireSession(final Session session) throws DataAccessException {
        logger.info("expireSession: " + session.getToken());
        String query = "UPDATE " + tableName + " SET expireLe = ? WHERE idSession = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setLong(1, session.getExpirationDate().getTime());
            preparedStatement.setInt(2, session.getIdSession());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }
}
