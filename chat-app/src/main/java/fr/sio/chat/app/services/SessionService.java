package fr.sio.chat.app.services;

import fr.sio.chat.app.dao.interfaces.ISessionDao;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Session;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.services.interfaces.ISessionService;
import java.sql.Timestamp;

public class SessionService implements ISessionService {
    private final ISessionDao sessionDao;

    public SessionService(ISessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    /**
     * Insère une nouvelle Session
     * @param user
     * @param token
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void createSession(User user, String token) throws DataAccessException {
        Session session = new Session();
        session.setIdAccount(user.getId());
        session.setToken(token);
        session.setExpirationDate(calculateExpirationTime());
        session.setCreatedDate(new Timestamp(System.currentTimeMillis() / 1000));
        sessionDao.insert(session);
    }

    /**
     * Récupère une session grâce à un id
     * @param user
     * @return Une Session
     */
    @Override
    public Session getSessionByUserId(User user) {
        try {
            return sessionDao.getSessionByUserId(user.getId());
        } catch (DataAccessException ex) {
            return null;
        }
    }

    /**
     * Récupère une session grâce à un token
     * @param token
     * @return Une Session
     */
    @Override
    public Session getSessionByToken(String token) {
        try {
            return sessionDao.getSessionByToken(token);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    /**
     * Met à jour une session pour qu'elle soit expirée
     * @param session
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void expireSession(Session session) throws DataAccessException {
        session.setExpirationDate(new Timestamp(System.currentTimeMillis()));
        sessionDao.expireSession(session);
    }

    /**
     * Vérifie si la session est active
     * @param session
     * @return true si la session est active, false sinon
     */
    @Override
    public boolean isSessionActive(Session session) {
        if (session == null) {
            return false;
        }
        return session.getExpirationDate().getTime() > System.currentTimeMillis();
    }

    /**
     * Calcule une nouvelle date d'expiration
     * @return une date-heure en Timestamp
     */
    private Timestamp calculateExpirationTime() {
        int hoursToExpire = 24;
        long millisecondsToExpire = hoursToExpire * 60 * 60 * 1000;
        long currentTimeInMillis = System.currentTimeMillis();
        long expirationTimeInMillis = currentTimeInMillis + millisecondsToExpire;
        return new Timestamp(expirationTimeInMillis);
    }
}
