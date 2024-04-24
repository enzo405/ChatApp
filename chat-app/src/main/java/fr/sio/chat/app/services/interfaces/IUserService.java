package fr.sio.chat.app.services.interfaces;

import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.NotFoundException;
import fr.sio.chat.app.models.User;

public interface IUserService extends IService {
    User getUserById(int id) throws NotFoundException;
    User getUserByEmail(String email) throws NotFoundException;
    User getUserByPseudo(String pseudo) throws NotFoundException;
    void insert(User user) throws DataAccessException;
    void update(User user) throws DataAccessException;
    boolean emailAlreadyExist(String emailValue) throws DataAccessException;
    boolean usernameAlreadyExist(String usernameValue) throws DataAccessException;
}
