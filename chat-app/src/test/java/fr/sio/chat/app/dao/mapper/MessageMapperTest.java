package fr.sio.chat.app.dao.mapper;

import fr.sio.chat.app.models.Message;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MessageMapperTest {

    //region declaration
    final int idMessage = 1;
    final int idMessage2 = 2;
    final String contenu = "coucou c'est moi";
    final String contenu2 = "coucou c'est pas moi";
    final long aLong = 1000000000L;
    final long aLong2 = 1000000L;
    final Timestamp aLongConvertedTimestamp = new Timestamp(70, 0, 12, 14, 46, 40, 0);
    final boolean estEpingle = true;
    final boolean estPasEpingle = false;
    final int idMessageRepondu = 1;
    final int idMessageRepondu2 = 2;
    final int idCompte = 1;
    final int idCompte2 = 2;
    final String pseudo = "test";
    final String pseudo2 = "test2";
    final byte[] photoDeProfil = new byte[] {0, 1, 2};
    final byte[] photoDeProfil2 = new byte[] {0, 1, 2, 3};
    static ResultSet resultSet;
    //endregion declaration

    @BeforeAll
    public static void setUp() {
        // Création du mock du ResultSet
        resultSet = mock(ResultSet.class);
    }

    @Test
    public void testAdvancedMapSuccess() throws SQLException {
        //Arrange
        when(resultSet.getInt("idMessage")).thenReturn(idMessage);
        when(resultSet.getString("contenu")).thenReturn(contenu);
        when(resultSet.getLong("creeLe")).thenReturn(aLong);
        when(resultSet.getLong("misAJourLe")).thenReturn(aLong);
        when(resultSet.getBoolean("estEpingle")).thenReturn(estEpingle);
        when(resultSet.getInt("repondA")).thenReturn(idMessageRepondu);
        when(resultSet.getInt("idCompte")).thenReturn(idCompte);
        when(resultSet.getString("pseudo")).thenReturn(pseudo);
        when(resultSet.getBytes("photoDeProfil")).thenReturn(photoDeProfil);

        //Act
        Message message = MessageMapper.map(resultSet);

        //Assert
        assertEquals(idMessage, message.getId());
        assertEquals(contenu, message.getContent());
        assertEquals(aLongConvertedTimestamp, message.getCreatedDate());
        assertEquals(aLongConvertedTimestamp, message.getUpdatedDate());
        assertEquals(estEpingle, message.getIsPinned());
        assertEquals(idMessageRepondu, message.getIdMessageAnswered());
        assertEquals(idCompte, message.getCompte().getId());
        assertEquals(pseudo, message.getCompte().getPseudo());
        assertEquals(photoDeProfil, message.getCompte().getProfilePicture());
    }

    @Test
    public void testAdvancedMapFailed() throws SQLException {
        //Arrange
        when(resultSet.getInt("idMessage")).thenReturn(idMessage2);
        when(resultSet.getString("contenu")).thenReturn(contenu2);
        when(resultSet.getLong("creeLe")).thenReturn(aLong2);
        when(resultSet.getLong("misAJourLe")).thenReturn(aLong2);
        when(resultSet.getBoolean("estEpingle")).thenReturn(estPasEpingle);
        when(resultSet.getInt("repondA")).thenReturn(idMessageRepondu2);
        when(resultSet.getInt("idCompte")).thenReturn(idCompte2);
        when(resultSet.getString("pseudo")).thenReturn(pseudo2);
        when(resultSet.getBytes("photoDeProfil")).thenReturn(photoDeProfil2);

        //Act
        Message message = MessageMapper.map(resultSet);

        //Assert
        assertNotEquals(idMessage, message.getId());
        assertNotEquals(contenu, message.getContent());
        assertNotEquals(aLongConvertedTimestamp, message.getCreatedDate());
        assertNotEquals(aLongConvertedTimestamp, message.getUpdatedDate());
        assertNotEquals(estEpingle, message.getIsPinned());
        assertNotEquals(idMessageRepondu, message.getIdMessageAnswered());
        assertNotEquals(idCompte, message.getCompte().getId());
        assertNotEquals(pseudo, message.getCompte().getPseudo());
        assertNotEquals(photoDeProfil, message.getCompte().getProfilePicture());
    }
}
