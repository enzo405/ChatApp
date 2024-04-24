package fr.sio.chat.app.dao.interfaces;

import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.User;

public interface IUserDao extends IDao<User> {
    User getByPseudo(String pseudo) throws DataAccessException;
    User getByEmail(String emailValue) throws DataAccessException;
    void update(User user) throws DataAccessException;
    User getById(int id) throws DataAccessException;
}
