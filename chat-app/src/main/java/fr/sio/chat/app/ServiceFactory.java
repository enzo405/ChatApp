package fr.sio.chat.app;

import fr.sio.chat.app.dao.*;
import fr.sio.chat.app.dao.interfaces.*;
import fr.sio.chat.app.services.*;
import fr.sio.chat.app.services.interfaces.*;

public class ServiceFactory {
    private static final ServiceFactory instance = new ServiceFactory();
    private final ISessionService sessionService;
    private final IAuthService authService;
    private final IUserService userService;
    private final IFriendService friendService;
    private final IDiscussionService discussionService;
    private final IUserDiscussionService userDiscussionService;
    private final IDiscussionTypeService typeDiscussionService;
    private final IMessageService messageService;

    public ServiceFactory() {
        IFriendDao friendDao = new FriendDao();
        IDiscussionTypeDao discussionTypeDao = new DiscussionTypeDao();
        IDiscussionDao discussionDao = new DiscussionDao();
        IUserDao userDao = new UserDao();
        ISessionDao sessionDao = new SessionDao();
        IUserDiscussionDao userDiscussionDao = new UserDiscussionDao();
        IMessageDao messageDao = new MessageDao();

        sessionService = new SessionService(sessionDao);
        userService = new UserService(userDao);
        authService = new AuthService(sessionService, userService);
        friendService = new FriendService(friendDao);
        userDiscussionService = new UserDiscussionService(userDiscussionDao);
        typeDiscussionService = new DiscussionTypeService(discussionTypeDao);
        discussionService = new DiscussionService(discussionDao,userDiscussionService,typeDiscussionService, friendService);
        messageService = new MessageService(messageDao);
    }

    public static ServiceFactory getInstance() { return instance; }
    public IFriendService getFriendServiceInstance() { return friendService; }
    public IAuthService getAuthServiceInstance() { return authService; }
    public IUserService getUserServiceInstance() { return userService; }
    public ISessionService getSessionServiceInstance() { return sessionService; }
    public IUserDiscussionService getUserDiscussionServiceInstance() { return userDiscussionService; }
    public IDiscussionService getDiscussionServiceInstance() { return discussionService; }
    public IDiscussionTypeService getTypeDiscussionServiceInstance() { return typeDiscussionService; }
    public IMessageService getMessageServiceInstance() {
        return messageService;
    }
}
