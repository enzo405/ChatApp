package fr.sio.chat.app.services;

import fr.sio.chat.app.App;
import fr.sio.chat.app.exceptions.InvalidCredentialsException;
import fr.sio.chat.app.services.interfaces.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Properties;

public class AuthServiceTest {
    private static IAuthService authService;

    @BeforeAll
    public static void setUp() {
        ISessionService sessionService = Mockito.mock(SessionService.class);
        IUserService userService = Mockito.mock(UserService.class);
        authService = new AuthService(sessionService, userService);
    }

    @Test
    public void testRegisterFailed() {
        // Arrange
        String emailValueCorrect = "compte1@gmail.com";
        String usernameValueCorrect = "compte1";
        String passwordValueCorrect = "Chatapp*1";
        String emailValueWrong = "compte1";
        String usernameValueWrong = "compt";
        String passwordValueWrong = "chatapp*1";

        // Act & Assert
        Assertions.assertThrows(InvalidCredentialsException.class, () -> {
            authService.register(emailValueCorrect, usernameValueCorrect, passwordValueWrong);
        });
        Assertions.assertThrows(InvalidCredentialsException.class, () -> {
            authService.register(emailValueWrong, usernameValueCorrect, passwordValueCorrect);
        });
        Assertions.assertThrows(InvalidCredentialsException.class, () -> {
            authService.register(emailValueCorrect, usernameValueWrong, passwordValueCorrect);
        });
    }

    @Test
    public void testRegisterSuccess() {
        // Arrange
        String emailValueCorrect = "compte1@gmail.com";
        String usernameValueCorrect = "compte1";
        String passwordValueCorrect = "Chatapp*1";

        // Act & Assert
        Assertions.assertDoesNotThrow(() -> authService.register(emailValueCorrect, usernameValueCorrect, passwordValueCorrect));
    }


    @Test
    public void testLogin() {
        //Arrange


        //Act


        //Assert


    }


    @Test
    public void testLogout() {
        //Arrange


        //Act


        //Assert


    }
}
