package fr.sio.chat.app.services.interfaces;

import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.InvalidCredentialsException;
import fr.sio.chat.app.exceptions.SessionException;
import fr.sio.chat.app.models.User;

public interface IAuthService extends IService {
    void register(String emailValue, String usernameValue, String passwordValue) throws InvalidCredentialsException, DataAccessException;
    void login(String emailValue, String passwordValue) throws InvalidCredentialsException, SessionException;
    void logout(User user);
    String encodePassword(String passwordValue);
    void validateUsername(String newUsername) throws InvalidCredentialsException, DataAccessException;
    void validateEmail(String newEmail) throws InvalidCredentialsException, DataAccessException;
    void validatePassword(User user, String oldPassword, String newPassword, String passwordConfirmation) throws InvalidCredentialsException;
}
