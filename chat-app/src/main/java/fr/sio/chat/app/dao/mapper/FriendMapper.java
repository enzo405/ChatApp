package fr.sio.chat.app.dao.mapper;

import fr.sio.chat.app.models.Friend;
import fr.sio.chat.app.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendMapper {

    /**
     * Map l'ami
     * @param result
     * @param user
     * @return Un Ami
     * @throws SQLException
     */
    public static Friend map(final ResultSet result, final User user) throws SQLException {
        Friend friend = new Friend();
        friend.setEstAccepte(result.getBoolean("estAccepte"));
        friend.setCompte1(user);
        friend.setCompte2(UserMapper.map(result));
        friend.setIdRelation(result.getInt("idRelation"));
        return friend;
    }
}
