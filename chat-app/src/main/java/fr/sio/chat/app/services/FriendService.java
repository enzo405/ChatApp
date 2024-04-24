package fr.sio.chat.app.services;

import fr.sio.chat.app.dao.interfaces.IFriendDao;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.FriendException;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.security.SecurityContext;
import fr.sio.chat.app.services.interfaces.IFriendService;
import fr.sio.chat.app.models.Friend;

import java.util.ArrayList;
import java.util.List;

public class FriendService implements IFriendService {
    private final IFriendDao friendDao;

    public FriendService(IFriendDao friendDao) {
        this.friendDao = friendDao;
    }

    /**
     * Insère un nouvel ami
     *
     * @param userFriend
     * @return
     * @throws FriendException
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public int insert(User userFriend) throws FriendException, DataAccessException {
        User sessionUser = SecurityContext.getUser();
        if (isFriend(sessionUser, userFriend)) {
            throw new FriendException("Vous êtes déjà amis avec cette personne");
        }
        if (hasAlreadyRequestedFriend(SecurityContext.getUser(), userFriend)) {
            throw new FriendException("Vous avez déjà demandé en ami cette personne, et elle ne vous à pas encore accepté");
        }
        if (userFriend.getId() == sessionUser.getId()) {
            throw new FriendException("Vous ne pouvez pas être amis avec vous même");
        }
        Friend friend = new Friend();
        friend.setCompte1(sessionUser);
        friend.setCompte2(userFriend);
        friend.setEstAccepte(false);
        return friendDao.insert(friend);
    }

    /**
     * Supprime un ami
     * @param friend
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void delete(Friend friend) throws DataAccessException {
        friendDao.delete(friend);
    }

    /**
     * Met à jour un ami
     * @param friend
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void update(Friend friend) throws DataAccessException {
        friendDao.update(friend);
    }

    /**
     * Récupère les demandes d'ami grâce à un utilisateur
     * @param user
     * @return Une liste d'ami
     */
    @Override
    public List<Friend> getFriendRequestsByUser(User user) {
        try {
            return friendDao.getFriendRequestsByUser(user);
        } catch (DataAccessException ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Récupère les amis grâce à un utilisateur
     * @param user
     * @return Une liste d'ami
     */
    @Override
    public List<Friend> getFriendsByUser(User user) {
        try {
            return friendDao.getFriendsByUser(user);
        } catch (DataAccessException ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Vérifie si 2 utilisateurs sont amis
     * @param user
     * @param friend
     * @return true si ils sont amis, false sinon
     */
    @Override
    public boolean isFriend(User user, User friend) {
        try {
            return friendDao.hasFriendRelation(user, friend, true);
        } catch (DataAccessException ex) {
            return false;
        }
    }

    /**
     * Vérifie si une demande d'ami a déjà été envoyé
     * @param user
     * @param friend
     * @return true si elle a déjà été envoyée, fase sinon
     */
    @Override
    public boolean hasAlreadyRequestedFriend(User user, User friend) {
        try {
            return friendDao.hasFriendRelation(user, friend, false);
        } catch (DataAccessException ex) {
            return false;
        }
    }
}