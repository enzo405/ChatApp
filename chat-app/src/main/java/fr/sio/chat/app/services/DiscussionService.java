package fr.sio.chat.app.services;

import fr.sio.chat.app.dao.interfaces.IDiscussionDao;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.FriendException;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.DiscussionType;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.models.UserDiscussion;
import fr.sio.chat.app.services.interfaces.IDiscussionService;
import fr.sio.chat.app.services.interfaces.IDiscussionTypeService;
import fr.sio.chat.app.services.interfaces.IFriendService;
import fr.sio.chat.app.services.interfaces.IUserDiscussionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DiscussionService implements IDiscussionService {
    private static final Logger logger = LoggerFactory.getLogger(DiscussionService.class);
    private final IDiscussionDao discussionDao;
    private final IUserDiscussionService userDiscussionService;
    private final IDiscussionTypeService discussionTypeService;
    private final IFriendService friendService;

    public DiscussionService(IDiscussionDao discussionDao, IUserDiscussionService userDiscussionService, IDiscussionTypeService discussionTypeService, IFriendService friendService) {
        this.discussionDao = discussionDao;
        this.userDiscussionService = userDiscussionService;
        this.discussionTypeService = discussionTypeService;
        this.friendService = friendService;
    }

    /**
     * Insère une nouvelle Discussion
     * @param discussion
     * @return
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public int insert(Discussion discussion) throws DataAccessException {
        return discussionDao.insert(discussion);
    }

    /**
     * Supprime une Discussion
     * @param discussion
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void delete(Discussion discussion) throws DataAccessException {
        discussionDao.delete(discussion);
    }

    /**
     * Met à jour une Discussion
     * @param discussion
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public void update(Discussion discussion) throws DataAccessException {
        discussionDao.update(discussion);
    }

    /**
     * Récupère une Discussion grâce à un id
     * @param id
     * @return
     */
    @Override
    public Discussion getDiscussionById(int id) {
        try {
            return discussionDao.getDiscussionById(id);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    /**
     * Récupère les discussions grâce à un utilisateur et si isGroup est Vrai
     * @param user
     * @return Une liste de Discussion
     */
    @Override
    public List<Discussion> getGroupDiscussionsByUser(User user) {
        try {
            return discussionDao.getDiscussionsByUser(user, true);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    /**
     * Récupère les discussions grâce à un utilisateur
     * @param user
     * @return Une liste de Discussion
     */
    @Override
    public List<Discussion> getDiscussionsByUser(User user) {
        try {
            return discussionDao.getDiscussionsByUser(user);
        } catch (DataAccessException ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Récupère les discussions privées grâce à un utilisateur
     * @param user
     * @return Une liste de Discussion
     */
    @Override
    public List<Discussion> getPrivateDiscussionsByUser(User user) {
        try {
            return discussionDao.getDiscussionsByUser(user, false);
        } catch (DataAccessException ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Récupère une liste de discussions de type non groupe pour deux utilisateurs qui sont amis
     * @param user1 utilisateur 1
     * @param user2 utilisateur 2
     * @return Une liste de Discussion
     */
    private List<Discussion> getPrivateDiscussionsByUsers(User user1, User user2) {
        try {
            return discussionDao.getDiscussionsFriendByUsers(user1, user2);
        } catch (DataAccessException ex) {
            return new ArrayList<>();
        }
    }

    /**
     * Insère une nouvelle discussion privée
     * @param owner l'utilisateur 
     * @param amis l'ami
     * @return Une Discussion
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    private Discussion createPrivateDiscussion(User owner, User amis) throws DataAccessException {
        DiscussionType discussionType = discussionTypeService.getDefaultDiscussionType();
        Discussion discussion = new Discussion(getDefaultDiscussionName(owner,amis), discussionType);
        int idDiscussion = insert(discussion);
        discussion.setId(idDiscussion);
        List<UserDiscussion> userDiscussions = new ArrayList<>();
        userDiscussions.add(new UserDiscussion(discussion, owner, true));
        userDiscussions.add(new UserDiscussion(discussion, amis, true));
        userDiscussionService.insert(userDiscussions);
        return discussion;
    }

    /**
     * Récupère une discussion qui n'est pas un groupe et vérifie si son opposé existe (owner → ami ; ami → owner)
     * @param owner l'utilisateur
     * @param amis l'ami
     * @return Une Discussion
     * @throws FriendException Si les 2 utilisateurs ne sont pas amis
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    @Override
    public Discussion findOrCreateDiscussion(User owner, User amis) throws FriendException, DataAccessException {
        if (!friendService.isFriend(owner, amis)) {
            throw new FriendException("Vous n'êtes pas amis avec " + amis.getPseudo());
        }
        List<Discussion> discussions = getPrivateDiscussionsByUsers(amis, owner);
        Discussion discussion;
        if (!discussions.isEmpty()) {
            discussion = discussions.get(0);
        } else {
            discussion = createPrivateDiscussion(owner, amis);
        }
        return discussion;
    }

    /**
     * Récupère le nom de la discussion par défaut
     * @param owner l'utilisateur
     * @param amis l'ami
     * @return Une String
     */
    private String getDefaultDiscussionName(User owner, User amis) {
        return amis.getPseudo() + ", " + owner.getPseudo();
    }
}