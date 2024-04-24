package fr.sio.chat.app.dao.mapper;

import fr.sio.chat.app.models.Discussion;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiscussionMapperTest {

    //region declaration
    final int idDiscussion = 1;
    final int idDiscussion2 = 2;
    final String libelle = "coucou";
    final String libelle2 = "coucou 2";
    final String icone = "icone";
    final String icone2 = "icone2";
    final boolean estGroupe = true;
    final boolean estPasGroupe = false;
    final String nom = "discussion1";
    final String nom2 = "discussion2";
    static ResultSet resultSet;
    //endregion declaration

    @BeforeAll
    public static void setUp() {
        // Création du mock du ResultSet
        resultSet = mock(ResultSet.class);
    }

    @Test
    public void testMapWithTypeSuccess() throws SQLException {
        //Arrange
        when(resultSet.getInt("idDiscussion")).thenReturn(idDiscussion);
        when(resultSet.getString("libelle")).thenReturn(libelle);
        when(resultSet.getString("icone")).thenReturn(icone);
        when(resultSet.getBoolean("estGroupe")).thenReturn(estGroupe);
        when(resultSet.getString("nom")).thenReturn(nom);

        //Act
        Discussion discussionMapper = DiscussionMapper.mapWithType(resultSet);

        //Assert
        assertEquals(idDiscussion, discussionMapper.getId());
        assertEquals(libelle, discussionMapper.getDiscussionType().getLabel());
        assertEquals(icone, discussionMapper.getDiscussionType().getIcon());
        assertEquals(estGroupe, discussionMapper.getDiscussionType().getIsGroup());
        assertEquals(nom, discussionMapper.getName());
    }

    @Test
    public void testMapWithTypeFailed() throws SQLException {
        //Arrange
        when(resultSet.getInt("idDiscussion")).thenReturn(idDiscussion2);
        when(resultSet.getString("libelle")).thenReturn(libelle2);
        when(resultSet.getString("icone")).thenReturn(icone2);
        when(resultSet.getBoolean("estGroupe")).thenReturn(estPasGroupe);
        when(resultSet.getString("nom")).thenReturn(nom2);

        //Act
        Discussion discussion = DiscussionMapper.mapWithType(resultSet);

        //Assert
        assertNotEquals(idDiscussion, discussion.getId());
        assertNotEquals(libelle, discussion.getDiscussionType().getLabel());
        assertNotEquals(icone, discussion.getDiscussionType().getIcon());
        assertNotEquals(estGroupe, discussion.getDiscussionType().getIsGroup());
        assertNotEquals(nom, discussion.getName());
    }
}
