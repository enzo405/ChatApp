package fr.sio.chat.app.dao.mapper;

import fr.sio.chat.app.models.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserMapperTest {

    //region declaration
    final int idCompte = 1;
    final int idCompte2 = 2;
    final String pseudo = "test";
    final String pseudo2 = "test2";
    final byte[] photoDeProfil = new byte[] {0, 1, 2};
    final byte[] photoDeProfil2 = new byte[] {0, 1, 2, 3};
    final String email = "test@test.com";
    final String email2 = "email@email.com";
    final String password = "$2a$10$wM06sMiaamwDYIsE8zKHMePwc030q8DtzGPsQd8d92RZTNt99J59e";
    final String password2 = "$2a$10$wM06sMiaamwDYIsE8zKHMePwc030q8DtzGPsQd8d92RZTNt99T15x";
    final Timestamp verifieLe = new Timestamp(2024, 1, 1, 1, 1, 1, 1);
    final Timestamp verifieLe2 = new Timestamp(2024, 0, 1, 1, 1, 1, 1);
    final long aLong = 1000000000L;
    final long aLong2 = 1000000L;
    final Timestamp aLongConvertedTimestamp = new Timestamp(70, 0, 12, 14, 46, 40, 0);
    static ResultSet resultSet;
    //endregion declaration

    @BeforeAll
    public static void setUp() {
        // Création du mock du ResultSet
        resultSet = mock(ResultSet.class);
    }

    @Test
    public void testMapSuccess() throws SQLException {
        //Arrange
        when(resultSet.getInt("idCompte")).thenReturn(idCompte);
        when(resultSet.getString("pseudo")).thenReturn(pseudo);
        when(resultSet.getBytes("photoDeProfil")).thenReturn(photoDeProfil);

        //Act
        User user = UserMapper.map(resultSet);

        //Assert
        assertEquals(idCompte, user.getId());
        assertEquals(pseudo, user.getPseudo());
        assertEquals(photoDeProfil, user.getProfilePicture());
    }

    @Test
    public void testMapFailed() throws SQLException {
        //Arrange
        when(resultSet.getInt("idCompte")).thenReturn(idCompte2);
        when(resultSet.getString("pseudo")).thenReturn(pseudo2);
        when(resultSet.getBytes("photoDeProfil")).thenReturn(photoDeProfil2);

        //Act
        User user = UserMapper.map(resultSet);

        //Assert
        assertNotEquals(idCompte, user.getId());
        assertNotEquals(pseudo, user.getPseudo());
        assertNotEquals(photoDeProfil, user.getProfilePicture());
    }

    @Test
    public void testAdvancedMapSuccess() throws SQLException {
        //Arrange
        when(resultSet.getInt("idCompte")).thenReturn(idCompte);
        when(resultSet.getString("pseudo")).thenReturn(pseudo);
        when(resultSet.getBytes("photoDeProfil")).thenReturn(photoDeProfil);
        when(resultSet.getString("courriel")).thenReturn(email);
        when(resultSet.getTimestamp("courrielVerifieLe")).thenReturn(verifieLe);
        when(resultSet.getString("motDePasse")).thenReturn(password);
        when(resultSet.getLong("creeLe")).thenReturn(aLong);
        when(resultSet.getLong("misAJourLe")).thenReturn(aLong);

        //Act
        User user = UserMapper.advancedMap(resultSet);

        //Assert
        assertEquals(idCompte, user.getId());
        assertEquals(pseudo, user.getPseudo());
        assertEquals(photoDeProfil, user.getProfilePicture());
        assertEquals(email, user.getEmail());
        assertEquals(verifieLe, user.getEmailVerificationDate());
        assertEquals(password, user.getPassword());
        assertEquals(aLongConvertedTimestamp, user.getCreatedDate());
        assertEquals(aLongConvertedTimestamp, user.getUpdatedDate());
    }

    @Test
    public void testAdvancedMapFailed() throws SQLException {
        //Arrange
        when(resultSet.getInt("idCompte")).thenReturn(idCompte2);
        when(resultSet.getString("pseudo")).thenReturn(pseudo2);
        when(resultSet.getBytes("photoDeProfil")).thenReturn(photoDeProfil2);
        when(resultSet.getString("courriel")).thenReturn(email2);
        when(resultSet.getTimestamp("courrielVerifieLe")).thenReturn(verifieLe2);
        when(resultSet.getString("motDePasse")).thenReturn(password2);
        when(resultSet.getLong("creeLe")).thenReturn(aLong2);
        when(resultSet.getLong("misAJourLe")).thenReturn(aLong2);

        //Act
        User user = UserMapper.advancedMap(resultSet);

        //Assert
        assertNotEquals(idCompte, user.getId());
        assertNotEquals(pseudo, user.getPseudo());
        assertNotEquals(photoDeProfil, user.getProfilePicture());
        assertNotEquals(email, user.getEmail());
        assertNotEquals(verifieLe, user.getEmailVerificationDate());
        assertNotEquals(password, user.getPassword());
        assertNotEquals(aLongConvertedTimestamp, user.getCreatedDate());
        assertNotEquals(aLongConvertedTimestamp, user.getUpdatedDate());
    }
}
