package fr.sio.chat.app.services;

import fr.sio.chat.app.controllers.SceneContext;
import fr.sio.chat.app.dao.interfaces.IDiscussionTypeDao;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.DiscussionType;
import fr.sio.chat.app.services.interfaces.IDiscussionTypeService;

import java.util.List;

public class DiscussionTypeService implements IDiscussionTypeService {
    private final String DEFAULT_FRIEND_DISCUSSION_TYPE_LABEL = "Private Message";
    private final IDiscussionTypeDao discussionTypeDao;

    public DiscussionTypeService(IDiscussionTypeDao discussionTypeDao) {
        this.discussionTypeDao = discussionTypeDao;
    }

    /**
     * Récupère un type de discussion par son id
     * @param discussionTypeId l'id du type de discussion
     * @return Un TypeDiscussion
     */
    @Override
    public DiscussionType getDiscussionTypeById(int discussionTypeId) {
        try {
            return discussionTypeDao.getDiscussionTypeById(discussionTypeId);
        } catch (DataAccessException ex) {
            return null;
        }
    }

    /**
     * Récupère le type de discussion par défaut
     * @return Un TypeDiscussion
     */
    @Override
    public DiscussionType getDefaultDiscussionType() {
        try {
            DiscussionType discussionType = discussionTypeDao.getDiscussionTypeByName(DEFAULT_FRIEND_DISCUSSION_TYPE_LABEL);
            if (discussionType == null) {
                discussionType = new DiscussionType(DEFAULT_FRIEND_DISCUSSION_TYPE_LABEL, SceneContext.DEFAULT_AVATAR_IMAGE, false);
                discussionTypeDao.insert(discussionType);
            }
            return discussionType;
        } catch (DataAccessException ex) {
            return null;
        }
    }

    /**
     * Récupère tous les types de discussion
     * @return Une liste de TypeDiscussion
     */
    @Override
    public List<DiscussionType> getAllDiscussionType() throws DataAccessException {
        return discussionTypeDao.getAllDiscussionType();
    }
}
