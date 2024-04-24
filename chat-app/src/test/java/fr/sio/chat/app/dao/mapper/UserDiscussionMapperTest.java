package fr.sio.chat.app.dao.mapper;

import fr.sio.chat.app.models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserDiscussionMapperTest {

    //region declaration
    static final int id = 1;
    static final int id2 = 2;
    static final String pseudo = "test";
    static final String pseudo2 = "test2";
    static final byte[] photoDeProfil = new byte[] {0, 1, 2};
    static final byte[] photoDeProfil2 = new byte[] {0, 1, 2, 3};
    static final String discussionName = "test";
    static final String discussionName2 = "test2";
    static final Discussion discussion = new Discussion();
    static final Discussion discussion2 = new Discussion();
    static User user = new User();
    static User user2 = new User();
    final boolean estProprietaire = true;
    final boolean estPasProprietaire = false;
    static ResultSet resultSet;
    //endregion declaration

    @BeforeAll
    public static void setUp() {
        discussion.setId(id);
        discussion.setName(discussionName);

        discussion2.setId(id2);
        discussion2.setName(discussionName2);

        // Création du mock du ResultSet
        resultSet = mock(ResultSet.class);
    }

    @Test
    public void testMapSuccess() throws SQLException {
        //Arrange
        when(resultSet.getBoolean("estproprietaire")).thenReturn(estProprietaire);
        when(resultSet.getInt("idCompte")).thenReturn(id);
        when(resultSet.getString("pseudo")).thenReturn(pseudo);
        when(resultSet.getBytes("photoDeProfil")).thenReturn(photoDeProfil);

        //Act
        UserDiscussion userDiscussion = UserDiscussionMapper.map(resultSet, discussion);

        //Assert
        assertEquals(id, userDiscussion.getUser().getId());
        assertEquals(pseudo, userDiscussion.getUser().getPseudo());
        assertEquals(photoDeProfil, userDiscussion.getUser().getProfilePicture());
        assertEquals(id, userDiscussion.getDiscussion().getId());
        assertEquals(discussionName, userDiscussion.getDiscussion().getName());
        assertEquals(estProprietaire, userDiscussion.isOwner());
    }

    @Test
    public void testMapFailed() throws SQLException {
        //Arrange
        when(resultSet.getBoolean("estproprietaire")).thenReturn(estPasProprietaire);
        when(resultSet.getInt("idCompte")).thenReturn(id2);
        when(resultSet.getString("pseudo")).thenReturn(pseudo2);
        when(resultSet.getBytes("photoDeProfil")).thenReturn(photoDeProfil2);

        //Act
        UserDiscussion userDiscussion = UserDiscussionMapper.map(resultSet, discussion2);

        //Assert
        assertNotEquals(id, userDiscussion.getUser().getId());
        assertNotEquals(pseudo, userDiscussion.getUser().getPseudo());
        assertNotEquals(photoDeProfil, userDiscussion.getUser().getProfilePicture());
        assertNotEquals(id, userDiscussion.getDiscussion().getId());
        assertNotEquals(discussionName, userDiscussion.getDiscussion().getName());
        assertNotEquals(estProprietaire, userDiscussion.isOwner());
    }
}
