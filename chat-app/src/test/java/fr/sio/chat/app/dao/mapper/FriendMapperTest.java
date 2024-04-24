package fr.sio.chat.app.dao.mapper;

import fr.sio.chat.app.models.Friend;
import fr.sio.chat.app.models.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FriendMapperTest {

    //region declaration
    final boolean estAccepte = true;
    final boolean estPasAccepte = false;
    final int idRelation = 1;
    final int idRelation2 = 2;
    static final int idCompte = 1;
    static final int idCompte2 = 2;
    static final String pseudo = "test";
    static final String pseudo2 = "test2";
    static final byte[] photoDeProfil = new byte[] {0, 1, 2};
    static final byte[] photoDeProfil2 = new byte[] {0, 1, 2, 3};
    static final User user = new User();
    static final User user2 = new User();
    static ResultSet resultSet;
    //endregion declaration

    @BeforeAll
    public static void setUp() {
        user.setId(idCompte);
        user.setPseudo(pseudo);
        user.setProfilePicture(photoDeProfil);

        user2.setId(idCompte2);
        user2.setPseudo(pseudo2);
        user2.setProfilePicture(photoDeProfil2);

        // Création du mock du ResultSet
        resultSet = mock(ResultSet.class);
    }

    @Test
    public void testMapSuccess() throws SQLException {
        //Arrange
        when(resultSet.getBoolean("estAccepte")).thenReturn(estAccepte);
        when(resultSet.getInt("idCompte")).thenReturn(idCompte);
        when(resultSet.getString("pseudo")).thenReturn(pseudo);
        when(resultSet.getBytes("photoDeProfil")).thenReturn(photoDeProfil);
        when(resultSet.getInt("idRelation")).thenReturn(idRelation);

        //Act
        Friend friend = FriendMapper.map(resultSet, user);

        //Assert
        assertEquals(estAccepte, friend.getEstAccepte());
        assertEquals(user.getId(), friend.getCompte1().getId());
        assertEquals(user.getPseudo(), friend.getCompte1().getPseudo());
        assertEquals(user.getProfilePicture(), friend.getCompte1().getProfilePicture());
        assertEquals(idCompte, friend.getCompte2().getId());
        assertEquals(pseudo, friend.getCompte2().getPseudo());
        assertEquals(photoDeProfil, friend.getCompte2().getProfilePicture());
        assertEquals(idRelation, friend.getIdRelation());
    }

    @Test
    public void testMapFailed() throws SQLException {
        //Arrange
        when(resultSet.getBoolean("estAccepte")).thenReturn(estPasAccepte);
        when(resultSet.getInt("idCompte")).thenReturn(idCompte2);
        when(resultSet.getString("pseudo")).thenReturn(pseudo2);
        when(resultSet.getBytes("photoDeProfil")).thenReturn(photoDeProfil2);
        when(resultSet.getInt("idRelation")).thenReturn(idRelation2);

        //Act
        Friend friend = FriendMapper.map(resultSet, user2);

        //Assert
        assertNotEquals(estAccepte, friend.getEstAccepte());
        assertNotEquals(user.getId(), friend.getCompte1().getId());
        assertNotEquals(user.getPseudo(), friend.getCompte1().getPseudo());
        assertNotEquals(user.getProfilePicture(), friend.getCompte1().getProfilePicture());
        assertNotEquals(idCompte, friend.getCompte2().getId());
        assertNotEquals(pseudo, friend.getCompte2().getPseudo());
        assertNotEquals(photoDeProfil, friend.getCompte2().getProfilePicture());
        assertNotEquals(idRelation, friend.getIdRelation());
    }
}
