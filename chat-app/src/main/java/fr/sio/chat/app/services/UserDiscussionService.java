package fr.sio.chat.app.services;

import fr.sio.chat.app.dao.interfaces.IUserDiscussionDao;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.NotAllowedException;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.UserDiscussion;
import fr.sio.chat.app.services.interfaces.IUserDiscussionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class UserDiscussionService implements IUserDiscussionService {
    private static final Logger logger = LoggerFactory.getLogger(UserDiscussionService.class);
    private final IUserDiscussionDao userDiscussionDao;

    public UserDiscussionService(IUserDiscussionDao userDiscussionDao) {
        this.userDiscussionDao = userDiscussionDao;
    }

    /**
     * Insère un nouveau UserDiscussion
     * @param userDiscussion
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void insert(UserDiscussion userDiscussion) throws DataAccessException {
        userDiscussionDao.insert(userDiscussion);
    }

    /**
     * Insère une liste de UserDiscussion
     * @param userDiscussions
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void insert(List<UserDiscussion> userDiscussions) throws DataAccessException {
        userDiscussionDao.insert(userDiscussions);
    }

    /**
     * Supprime une UserDiscussion
     * @param userDiscussion
     * @throws NotAllowedException
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void delete(UserDiscussion userDiscussion) throws NotAllowedException, DataAccessException {
        // Empêcher l'owner de quitter la discussion
        if (!userDiscussion.isOwner()) {
            userDiscussionDao.delete(userDiscussion);
        } else {
            throw new NotAllowedException("Le propriétaire de la discussion ne peux pas la quitter.");
        }
    }

    /**
     * Récupère les membres d'une discussion
     * @param discussion
     * @return Une liste de UserDiscussion
     */
    @Override
    public List<UserDiscussion> getDiscussionMembers(Discussion discussion) {
        List<UserDiscussion> discussions = new ArrayList<>();
        if (discussion != null) {
            try {
                discussions.addAll(userDiscussionDao.getByDiscussion(discussion));
            } catch (DataAccessException ignored) {
                // Retourne une liste vide
            }
        }
        return discussions;
    }
}
