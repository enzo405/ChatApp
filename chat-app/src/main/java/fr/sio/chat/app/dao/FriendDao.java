package fr.sio.chat.app.dao;

import fr.sio.chat.app.dao.interfaces.IFriendDao;
import fr.sio.chat.app.dao.mapper.FriendMapper;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Friend;
import fr.sio.chat.app.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendDao implements IFriendDao {
    private static final Logger logger = LoggerFactory.getLogger(FriendDao.class);
    private static final String tableName = "chatapp.estAmi";
    private static final Connection conn = PostgreSQLManager.getInstance().getConnection();

    public FriendDao() { }

    public static void initializeTable() {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS " + tableName + """ 
                (
                    idRelation SERIAL PRIMARY KEY,
                    estAccepte BOOLEAN,
                    idCompte1 INT,
                    idCompte2 INT,
                    FOREIGN KEY(idCompte1) REFERENCES chatapp.comptes(idCompte),
                    FOREIGN KEY(idCompte2) REFERENCES chatapp.comptes(idCompte)
                );
                
                SET search_path = chatapp;
                CREATE OR REPLACE FUNCTION insert_friend()
                RETURNS TRIGGER AS $BODY$
                BEGIN
                    IF NEW.estaccepte = true THEN
                        INSERT INTO chatapp.estami(idcompte1, idcompte2, estaccepte)
                        VALUES (NEW.idcompte2, NEW.idcompte1, true);
                    END IF;
                    RETURN NEW;
                END;
                $BODY$ LANGUAGE plpgsql;
                
                CREATE OR REPLACE FUNCTION delete_friend()
                RETURNS TRIGGER AS $BODY$
                BEGIN
                    DELETE FROM chatapp.estami
                    WHERE idCompte1 = OLD.idCompte2 AND idCompte2 = OLD.idCompte1;
                    RETURN OLD;
                END;
                $BODY$ LANGUAGE plpgsql;
                
                CREATE OR REPLACE TRIGGER insert_friend
                BEFORE UPDATE ON chatapp.estami
                FOR EACH ROW
                EXECUTE FUNCTION insert_friend();
                
                CREATE OR REPLACE TRIGGER delete_friend
                AFTER DELETE ON chatapp.estami
                FOR EACH ROW
                EXECUTE FUNCTION delete_friend();
                
                """;
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sqlCreate);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Insère un ami
     * @param friend
     * @return L'id de l'élément inséré
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public Integer insert(final Friend friend) throws DataAccessException {
        logger.info("insert: " + friend.toString());
        String sqlQuery = "INSERT INTO " + tableName +
                "(estAccepte,idCompte1,idCompte2)" +
                "VALUES (?,?,?)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setBoolean(1, friend.getEstAccepte());
            preparedStatement.setInt(2, friend.getCompte1().getId());
            preparedStatement.setInt(3, friend.getCompte2().getId());

            int rowsAffected = preparedStatement.executeUpdate();
            return DaoUtility.getInsertId(rowsAffected, preparedStatement);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Supprime un ami
     * @param friend
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void delete(final Friend friend) throws DataAccessException {
        logger.info("delete: " + friend.toString());
        String sqlQuery = "DELETE FROM " + tableName +
                " WHERE idRelation=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, friend.getIdRelation());
            preparedStatement.executeUpdate(); // executeUpdate c'est pour INSERT,UPDATE,DELETE
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Met à jour un ami
     * @param friend
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void update(final Friend friend) throws DataAccessException {
        logger.info("update: " + friend.toString());
        String sqlQuery = "UPDATE " + tableName +
                " SET estAccepte=? WHERE idRelation=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setBoolean(1, friend.getEstAccepte());
            preparedStatement.setInt(2, friend.getIdRelation());
            preparedStatement.executeUpdate(); // executeUpdate c'est pour INSERT,UPDATE,DELETE
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère les amis grâce à l'utilisateur
     * @param user
     * @return Une liste d'Amis
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public List<Friend> getFriendsByUser(final User user) throws DataAccessException {
        logger.info("getFriendsByUser: " + user.getPseudo());
        String sqlQuery = "SELECT * FROM " + tableName +
                " INNER JOIN chatapp.comptes ON estAmi.idcompte2 = comptes.idcompte" +
                " WHERE estAccepte=true AND idCompte1=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, user.getId());
            ResultSet result = preparedStatement.executeQuery();
            List<Friend> friends = new ArrayList<>();
            while (result.next()) {
                friends.add(FriendMapper.map(result, user));
            }
            return friends;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Récupère les demandes d'amis grâce à un utilisateur
     * @param user
     * @return Une liste d'Amis
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public List<Friend> getFriendRequestsByUser(final User user) throws DataAccessException {
        logger.info("getFriendRequestsByUser: " + user.getPseudo());
        String sqlQuery = "SELECT * FROM " + tableName +
                " INNER JOIN chatapp.comptes ON estAmi.idcompte1 = comptes.idcompte" +
                " WHERE estAccepte=false AND idCompte2=?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setInt(1, user.getId());
            ResultSet result = preparedStatement.executeQuery();
            List<Friend> friends = new ArrayList<>();
            while (result.next()) {
                friends.add(FriendMapper.map(result, user));
            }
            return friends;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }

    /**
     * Vérifie si la demande d'ami existe
     * @param user
     * @param friend
     * @param estAccepte
     * @return true si il est ami ou false sinon
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public boolean hasFriendRelation(final User user, final User friend, final boolean estAccepte) throws DataAccessException {
        logger.info("hasFriendRelation: " + friend.getId() + " & " + estAccepte);
        String sqlQuery = "SELECT * FROM " + tableName + " WHERE estAccepte=? AND idCompte1 = ? AND idCompte2 = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery);
            preparedStatement.setBoolean(1, estAccepte);
            preparedStatement.setInt(2, user.getId());
            preparedStatement.setInt(3, friend.getId());
            ResultSet result = preparedStatement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessException("Un probleme est survenue lors de la requête");
        }
    }
}
