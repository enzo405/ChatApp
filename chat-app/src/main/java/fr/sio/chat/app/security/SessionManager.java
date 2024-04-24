package fr.sio.chat.app.security;

import fr.sio.chat.app.App;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.NotFoundException;
import fr.sio.chat.app.exceptions.SessionException;
import fr.sio.chat.app.models.Session;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.ServiceFactory;
import fr.sio.chat.app.services.interfaces.ISessionService;
import fr.sio.chat.app.services.interfaces.IUserService;
import fr.sio.chat.app.websocket.WebSocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;

public class SessionManager {
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private static final String SESSION_TOKEN_KEY = "pref.session";
    private static final ISessionService sessionService = ServiceFactory.getInstance().getSessionServiceInstance();
    private static final IUserService userService = ServiceFactory.getInstance().getUserServiceInstance();
    public static final int LENGTH_SESSION_TOKEN = 45;

    /**
     * Crée la session en base, envoie la reguête de login au websocket
     * Set l'utilisateur connecté avec l'user de la session
     * @param user utilisateur à lier à la session
     * @param token token à utiliser
     * @throws SessionException Si la session n'a pas pu être créée
     */
    public void createSession(User user, String token) throws SessionException {
        try {
            sessionService.createSession(user, token);
            saveSessionToken(token);
            WebSocketClient.getInstance().login(user);
            SecurityContext.setUser(user);
        } catch (DataAccessException ex) {
            throw new SessionException("La création de la session n'a pas réussie");
        }
    }

    /**
     * Rend la session expirée, envoie la requête de logout
     * Set l'utiilisateur connecté à null
     * @param session session à supprimer
     */
    public void expireSession(Session session) {
        deleteSessionToken();
        WebSocketClient.getInstance().logout();
        SecurityContext.setUser(null);
        try {
            sessionService.expireSession(session);
        } catch (DataAccessException ignored) {
            // Ici le token sera automatiquement supprimé grâce à la date d'expiration du token
        }
    }

    /**
     * Enregistre le token de session dans le fichier de configuration
     * @param token token à sauvegarder dans le fichier de conf
     */
    private void saveSessionToken(String token) {
        logger.info("Session créée " + token);
        App.properties.setProperty(SESSION_TOKEN_KEY, token);
        try (OutputStream outputStream = new FileOutputStream(App.configFilePath)) {
            App.properties.store(outputStream, null);
        } catch (IOException e) {
            logger.error("Erreur lors de l'enregistrement des propriétés", e);
        }
    }

    /**
     * Supprime le token de session dans le fichier de configuration
     */
    private void deleteSessionToken() {
        logger.info("Session détruite");
        App.properties.setProperty(SESSION_TOKEN_KEY, "");
        try (OutputStream outputStream = new FileOutputStream(App.configFilePath)) {
            App.properties.store(outputStream, null);
        } catch (IOException e) {
            logger.error("Erreur lors de l'enregistrement des propriétés", e);
        }
    }

    /**
     * Récupère l'utilisateur de la session en cours
     * @return l'utilisateur connecté
     */
    public static User getSessionUser() {
        if (App.properties.getProperty(SESSION_TOKEN_KEY).length() != LENGTH_SESSION_TOKEN) {
            return null;
        }

        Session session = sessionService.getSessionByToken(App.properties.getProperty(SESSION_TOKEN_KEY));
        if (!sessionService.isSessionActive(session)) {
            return null;
        }

        try {
            User user = userService.getUserById(session.getIdAccount());
            logger.info("L'utilisateur connecté est: " + user.toString());
            return user;
        } catch (NotFoundException ex) {
            return null;
        }
    }
}
