package fr.sio.chat.app.dao.mapper;

import fr.sio.chat.app.models.DiscussionType;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DiscussionTypeMapper {

    /**
     * Map le type de Discussion
     * @param result
     * @return Un TypeDiscussion
     * @throws SQLException
     */
    public static DiscussionType map(final ResultSet result) throws SQLException {
        DiscussionType discussionType = new DiscussionType();
        discussionType.setId(result.getInt("idTypeDiscussion"));
        discussionType.setIcon(result.getString("icone"));
        discussionType.setLabel(result.getString("libelle"));
        discussionType.setIsGroup(result.getBoolean("estGroupe"));
        return discussionType;
    }
}
