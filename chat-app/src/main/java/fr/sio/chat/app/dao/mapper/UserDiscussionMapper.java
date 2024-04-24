package fr.sio.chat.app.dao.mapper;

import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.UserDiscussion;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDiscussionMapper {

    /**
     * Map une UserDiscussion
     * @param result
     * @param discussion
     * @return Une UserDiscussion
     * @throws SQLException
     */
    public static UserDiscussion map(final ResultSet result, final Discussion discussion) throws SQLException {
        UserDiscussion userDiscussion = new UserDiscussion();
        userDiscussion.setDiscussion(discussion);
        userDiscussion.setUser(UserMapper.map(result));
        userDiscussion.setIsOwner(result.getBoolean("estproprietaire"));
        return userDiscussion;
    }
}
