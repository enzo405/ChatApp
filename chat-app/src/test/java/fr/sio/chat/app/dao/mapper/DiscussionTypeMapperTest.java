package fr.sio.chat.app.dao.mapper;

import fr.sio.chat.app.models.DiscussionType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiscussionTypeMapperTest {

    //region declaration
    final int idTypeDiscussion = 1;
    final int idTypeDiscussion2 = 2;
    final String libelle = "coucou";
    final String libelle2 = "coucou 2";
    final String icone = "icone";
    final String icone2 = "icone2";
    final boolean estGroupe = true;
    final boolean estPasGroupe = false;
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
        when(resultSet.getInt("idTypeDiscussion")).thenReturn(idTypeDiscussion);
        when(resultSet.getString("icone")).thenReturn(icone);
        when(resultSet.getString("libelle")).thenReturn(libelle);
        when(resultSet.getBoolean("estGroupe")).thenReturn(estGroupe);

        //Act
        DiscussionType discussionType = DiscussionTypeMapper.map(resultSet);

        //Assert
        assertEquals(idTypeDiscussion, discussionType.getId());
        assertEquals(icone, discussionType.getIcon());
        assertEquals(libelle, discussionType.getLabel());
        assertEquals(estGroupe, discussionType.getIsGroup());
    }

    @Test
    public void testMapFailed() throws SQLException {
        //Arrange
        when(resultSet.getInt("idTypeDiscussion")).thenReturn(idTypeDiscussion2);
        when(resultSet.getString("icone")).thenReturn(icone2);
        when(resultSet.getString("libelle")).thenReturn(libelle2);
        when(resultSet.getBoolean("estGroupe")).thenReturn(estPasGroupe);

        //Act
        DiscussionType discussionType = DiscussionTypeMapper.map(resultSet);

        //Assert
        assertNotEquals(idTypeDiscussion, discussionType.getId());
        assertNotEquals(icone, discussionType.getIcon());
        assertNotEquals(libelle, discussionType.getLabel());
        assertNotEquals(estGroupe, discussionType.getIsGroup());
    }
}
