package fr.sio.chat.app.dao.mapper;

import fr.sio.chat.app.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserMapper {

    /**
     * Map partiellement un user
     * @param result
     * @return Un User
     * @throws SQLException
     */
    public static User map(final ResultSet result) throws SQLException {
        User user = new User();
        user.setId(result.getInt("idCompte"));
        user.setPseudo(result.getString("pseudo"));
        user.setProfilePicture(result.getBytes("photoDeProfil"));
        return user;
    }

    /**
     * Map entièrement un User
     * @param result
     * @return Un user
     * @throws SQLException
     */
    public static User advancedMap(final ResultSet result) throws SQLException {
        User user = new User();
        user.setId(result.getInt("idCompte"));
        user.setPseudo(result.getString("pseudo"));
        user.setProfilePicture(result.getBytes("photoDeProfil"));
        user.setEmail(result.getString("courriel"));
        user.setEmailVerificationDate(result.getTimestamp("courrielVerifieLe"));
        user.setPassword(result.getString("motDePasse"));
        user.setCreatedDate(new Timestamp(result.getLong("creeLe")));
        user.setUpdatedDate(new Timestamp(result.getLong("misAJourLe")));
        return user;
    }
}
