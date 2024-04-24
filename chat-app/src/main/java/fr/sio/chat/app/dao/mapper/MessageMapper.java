package fr.sio.chat.app.dao.mapper;

import fr.sio.chat.app.models.Message;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class MessageMapper {

    /**
     * Map le message
     * @param result
     * @return Un Message
     * @throws SQLException
     */
    public static Message map(final ResultSet result) throws SQLException {
        Message message = new Message();
        message.setId(result.getInt("idMessage"));
        message.setContent(result.getString("contenu"));
        message.setCreatedDate(new Timestamp(result.getLong("creeLe")));
        message.setUpdatedDate(new Timestamp(result.getLong("misAJourLe")));
        message.setIsPinned(result.getBoolean("estEpingle"));
        message.setIdMessageAnswered(result.getInt("repondA"));
        message.setCompte(UserMapper.map(result));
        return message;
    }

}
