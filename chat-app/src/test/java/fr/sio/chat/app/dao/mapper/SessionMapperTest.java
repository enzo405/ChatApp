package fr.sio.chat.app.dao.mapper;

import fr.sio.chat.app.models.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SessionMapperTest {

    //region declaration
    final int idSession = 1;
    final int idSession2 = 2;
    final String token = "0df9029493bdf613b119f3c43b14822c1710537510101";
    final String token2 = "bb6f71f0c781704e95c95f0bfd15fb911709755570451";
    final long aLong = 1000000000L;
    final long aLong2 = 1000000L;
    final Timestamp aLongConvertedTimestamp = new Timestamp(70, 0, 12, 14, 46, 40, 0);
    final int idCompte = 1;
    final int idCompte2 = 2;
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
        when(resultSet.getInt("idSession")).thenReturn(idSession);
        when(resultSet.getString("token")).thenReturn(token);
        when(resultSet.getLong("expireLe")).thenReturn(aLong);
        when(resultSet.getLong("creeLe")).thenReturn(aLong);
        when(resultSet.getInt("idCompte")).thenReturn(idCompte);

        //Act
        Session session = SessionMapper.map(resultSet);

        //Assert
        assertEquals(idSession, session.getIdSession());
        assertEquals(token, session.getToken());
        assertEquals(aLongConvertedTimestamp, session.getExpirationDate());
        assertEquals(aLongConvertedTimestamp, session.getCreatedDate());
        assertEquals(idCompte, session.getIdAccount());
    }

    @Test
    public void testMapFailed() throws SQLException {
        //Arrange
        when(resultSet.getInt("idSession")).thenReturn(idSession2);
        when(resultSet.getString("token")).thenReturn(token2);
        when(resultSet.getLong("expireLe")).thenReturn(aLong2);
        when(resultSet.getLong("creeLe")).thenReturn(aLong2);
        when(resultSet.getInt("idCompte")).thenReturn(idCompte2);

        //Act
        Session session = SessionMapper.map(resultSet);

        //Assert
        assertNotEquals(idSession, session.getIdSession());
        assertNotEquals(token, session.getToken());
        assertNotEquals(aLongConvertedTimestamp, session.getExpirationDate());
        assertNotEquals(aLongConvertedTimestamp, session.getCreatedDate());
        assertNotEquals(idCompte, session.getIdAccount());
    }
}
