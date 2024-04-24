package fr.sio.chat.app.dao.mapper;

import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.DiscussionType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscussionMapper {

    /**
     * Map la discussion
     * @param result
     * @return Une Discussion
     * @throws SQLException
     */
    public static Discussion mapWithType(final ResultSet result) throws SQLException {
        Discussion discussion = new Discussion();
        discussion.setId(result.getInt("idDiscussion"));
        DiscussionType discussionType = new DiscussionType(result.getString("libelle"), result.getString("icone"), result.getBoolean("estGroupe"));
        discussion.setDiscussionType(discussionType);
        discussion.setName(result.getString("nom"));
        return discussion;
    }
}
