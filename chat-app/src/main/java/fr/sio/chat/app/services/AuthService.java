package fr.sio.chat.app.services;

import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.NotFoundException;
import fr.sio.chat.app.exceptions.SessionException;
import fr.sio.chat.app.security.SecurityToken;
import fr.sio.chat.app.security.SessionManager;
import fr.sio.chat.app.services.interfaces.IAuthService;
import fr.sio.chat.app.services.interfaces.ISessionService;
import fr.sio.chat.app.services.interfaces.IUserService;
import fr.sio.chat.app.exceptions.InvalidCredentialsException;
import fr.sio.chat.app.models.Session;
import fr.sio.chat.app.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class AuthService implements IAuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final ISessionService sessionService;
    private final IUserService userService;
    private final SessionManager sessionManager = new SessionManager();
    private final BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();

    public AuthService(ISessionService sessionService, IUserService userService) {
        this.sessionService = sessionService;
        this.userService = userService;
    }

    /**
     * Register un nouvel utilisateur
     * @param emailValue l'email
     * @param usernameValue le nom d'utilisateur 
     * @param passwordValue le mot de passe 
     * @throws InvalidCredentialsException si les informations de connexion sont incorrectes
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void register(String emailValue, String usernameValue, String passwordValue)
        throws InvalidCredentialsException, DataAccessException
    {
        User user = new User(usernameValue, emailValue, encodePassword(passwordValue));

        validateEmail(emailValue);
        validateUsername(usernameValue);
        validatePassword(user, passwordValue, passwordValue, passwordValue);

        userService.insert(user);
    }

    /**
     * Connecte un utilisateur
     * @param emailValue l'email
     * @param passwordValue le mot de passe
     * @throws InvalidCredentialsException si les informations de connexion sont incorrectes
     */
    @Override
    public void login(String emailValue, String passwordValue) throws InvalidCredentialsException, SessionException {
        try {
            User user = userService.getUserByEmail(emailValue);
            if (user == null || !pwdEncoder.matches(passwordValue, user.getPassword())) {
                throw new InvalidCredentialsException("Vos informations de connexion sont incorrectes");
            }
            sessionManager.createSession(user, SecurityToken.generateToken());
        } catch (NotFoundException ex) {
            throw new InvalidCredentialsException("Vos informations de connexion sont incorrectes");
        }
    }

    /**
     * Déconnecte l'utilisateur
     * @param user l'utilisateur
     */
    @Override
    public void logout(User user)
    {
        Session session = sessionService.getSessionByUserId(user);
        if (session != null) {
            sessionManager.expireSession(session);
        } else {
            logger.warn("Aucune session trouvée pour l'utilisateur avec l'ID: " + user.getId());
        }
    }

    /**
     * Encode le mot de passe
     * @param passwordValue le mot de passe
     * @return Un String du mot de passe encodé
     */
    @Override
    public String encodePassword(String passwordValue) {
        return pwdEncoder.encode(passwordValue);
    }

    /**
     * Vérifie si le nom d'utilisateur est valide
     * @param usernameValue le nom d'utilisateur
     * @throws InvalidCredentialsException
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void validateUsername(String usernameValue) throws InvalidCredentialsException, DataAccessException {
        if (usernameValue.length() < 6) {
            throw new InvalidCredentialsException("Le nom d'utilisateur doit faire minimum 6 charactère!");
        }
        if (userService.usernameAlreadyExist(usernameValue)) {
            throw new InvalidCredentialsException("Le nom d'utilisateur " + usernameValue + " existe déjà");
        }
    }

    /**
     * Vérifie si l'email est valide
     * @param email l'email
     * @throws InvalidCredentialsException
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void validateEmail(String email) throws InvalidCredentialsException, DataAccessException {
        if (!email.matches("^(.+)@(\\S+)$")) {
            throw new InvalidCredentialsException("L'email n'est pas valide !");
        }
        if (userService.emailAlreadyExist(email)) {
            throw new InvalidCredentialsException("Une erreur est survenue veuillez réssayer");
        }
    }

    /**
     * Vérifie si le mot de passe est valide
     * @param user l'utilisateur
     * @param oldPassword l'ancien mot de passe
     * @param newPassword le nouveau mot de passe
     * @param passwordConfirmation la confirmation du mot de passe
     * @throws InvalidCredentialsException si le mot de passe n'est pas valide
     */
    @Override
    public void validatePassword(User user, String oldPassword, String newPassword, String passwordConfirmation) throws InvalidCredentialsException {
        if (!newPassword.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–{}:;',?/*~$^+=<>]).{8,20}$")) {
            throw new InvalidCredentialsException("Le mot de passe n'est pas assez sécurisé !");
        } else if (!newPassword.equals(passwordConfirmation)) {
            throw new InvalidCredentialsException("Les deux mots de passe doivent être les mêmes !");
        } else if (!pwdEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Une erreur est survenue lors du changements de mot de passe !");
        }
    }
}
