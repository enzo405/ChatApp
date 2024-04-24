package fr.sio.chat.app.dao.mapper;

import fr.sio.chat.app.models.Session;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SessionMapper {

    /**
     * Map la Session
     * @param result
     * @return Une Session
     * @throws SQLException
     */
    public static Session map(final ResultSet result) throws SQLException {
        Session session = new Session();
        session.setIdSession(result.getInt("idSession"));
        session.setToken(result.getString("token"));
        session.setExpirationDate(new Timestamp(result.getLong("expireLe")));
        session.setCreatedDate(new Timestamp(result.getLong("creeLe")));
        session.setIdAccount(result.getInt("idCompte"));
        return session;
    }
}
