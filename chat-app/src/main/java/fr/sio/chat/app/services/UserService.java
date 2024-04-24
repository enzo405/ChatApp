package fr.sio.chat.app.services;

import fr.sio.chat.app.dao.interfaces.IUserDao;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.NotFoundException;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.services.interfaces.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

public class UserService implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final IUserDao userDao;

    public UserService(IUserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Récupère un utilisateur par un id
     * @param id
     * @return Un User
     * @throws NotFoundException
     */
    @Override
    public User getUserById(int id) throws NotFoundException {
        try {
            User user = userDao.getById(id);
            if (user == null) {
                throw new NotFoundException("L'utilisateur avec l'id " + id + " n'a pas été trouvé");
            }
            return user;
        } catch (DataAccessException ex) {
            return null;
        }
    }

    /**
     * Récupère un utilisateur par son email
     * @param email email
     * @return Un User
     * @throws NotFoundException utilisateur avec l'email n'exite pas
     */
    @Override
    public User getUserByEmail(String email) throws NotFoundException {
        try {
            User user = userDao.getByEmail(email);
            if (user == null) {
                throw new NotFoundException("L'utilisateur avec l'email " + email + " n'a pas été trouvé");
            }
            return user;
        } catch (DataAccessException ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Récupère un utilisateur par son nom d'utilisateur
     * @param usernameValue pseudo de l'utilisateur à chercher
     * @return Un User
     * @throws NotFoundException user avec le pseudo n'existe pas
     */
    @Override
    public User getUserByPseudo(String usernameValue) throws NotFoundException {
        try {
            User user = userDao.getByPseudo(usernameValue);
            if (user == null) {
                throw new NotFoundException("L'utilisateur avec le nom " + usernameValue + " n'existe pas");
            }
            return user;
        } catch (DataAccessException ex){
            return null;
        }
    }

    /**
     * Insère un nouvel utilisateur
     * @param user user à insérer
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void insert(User user) throws DataAccessException {
        user.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        userDao.insert(user);
    }

    /**
     * Met à jour un utilisateur
     * @param user Utilisateur à mettre à jour
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void update(User user) throws DataAccessException {
        user.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        userDao.update(user);
    }

    /**
     * Vérifie si un utilisateur existe avec cet email
     * @param emailValue valeur de l'email
     * @return true si il existe, false sinon
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public boolean emailAlreadyExist(String emailValue) throws DataAccessException {
        return userDao.getByEmail(emailValue) != null;
    }

    /**
     * Vérifie si un utilisateur existe avec ce nom d'utilisateur
     * @param usernameValue valeur de l'username
     * @return true si il existe, false sinon
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public boolean usernameAlreadyExist(String usernameValue) throws DataAccessException {
        return userDao.getByPseudo(usernameValue) != null;
    }
}
