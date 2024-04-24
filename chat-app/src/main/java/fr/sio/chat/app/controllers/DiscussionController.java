package fr.sio.chat.app.controllers;

import fr.sio.chat.app.SceneFactory;
import fr.sio.chat.app.controllers.components.CreateDiscussionController;
import fr.sio.chat.app.controllers.components.DiscussionSidebarController;
import fr.sio.chat.app.controllers.enums.EFilterDiscussionSidebar;
import fr.sio.chat.app.events.dispatchers.MemberAddDiscussionDispatcher;
import fr.sio.chat.app.events.dispatchers.MemberRemoveDiscussionDispatcher;
import fr.sio.chat.app.events.dispatchers.MessageReceivedDispatcher;
import fr.sio.chat.app.events.interfaces.IEvent;
import fr.sio.chat.app.events.interfaces.IEventListener;
import fr.sio.chat.app.events.models.MemberAddDiscussionEvent;
import fr.sio.chat.app.events.models.MemberRemoveDiscussionEvent;
import fr.sio.chat.app.events.models.MessageReceivedEvent;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.Friend;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.models.UserDiscussion;
import fr.sio.chat.app.security.SecurityContext;
import fr.sio.chat.app.ServiceFactory;
import fr.sio.chat.app.services.interfaces.*;
import fr.sio.chat.app.websocket.requests.RequestMemberAddDiscussion;
import fr.sio.chat.app.websocket.requests.RequestMemberRemoveDiscussion;
import fr.sio.chat.app.websocket.requests.RequestMessageNew;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.*;

public class DiscussionController extends Controller implements Initializable, IEventListener {
    private static final Logger logger = LoggerFactory.getLogger(DiscussionController.class);
    private final IUserDiscussionService userDiscussionService = ServiceFactory.getInstance().getUserDiscussionServiceInstance();
    private final IFriendService friendService = ServiceFactory.getInstance().getFriendServiceInstance();
    private final IDiscussionService discussionService = ServiceFactory.getInstance().getDiscussionServiceInstance();
    private final IDiscussionTypeService discussionTypeService = ServiceFactory.getInstance().getTypeDiscussionServiceInstance();
    private final IMessageService messageService = ServiceFactory.getInstance().getMessageServiceInstance();
    private final User sessionUser = SecurityContext.getUser();
    private ChatController chatController;
    private final Map<Integer, DiscussionSidebarController> discussionSidebarControllers = new HashMap<>();
    private boolean isCreationDiscussionPopupActive = false; // Garder un état pour savoir si le popup est actif ou pas
    private EFilterDiscussionSidebar discussionSidebarState = EFilterDiscussionSidebar.ALL;
    private final List<User> friends = new ArrayList<>();
    private final Map<Integer, Discussion> allDiscussions = new HashMap<>();
    private final Map<Integer, Discussion> privateDiscussions = new HashMap<>();
    private final Map<Integer, Discussion> groupDiscussions = new HashMap<>();
    @FXML
    private ImageView selfIcon;
    @FXML
    private Label selfName;
    @FXML
    private Label selfStatus;
    @FXML
    private VBox containerDiscussions;
    @FXML
    private HBox hboxFilterButtons;
    @FXML
    private GridPane chatContainer;
    @FXML
    private StackPane stackPaneChatContainer;
    @FXML
    private SidebarController sidebarTemplateController;

    /**
     * Affiche les discussions et le chat lors de l'initialisation
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addListeners();
        sidebarTemplateController.toggleDiscussionsImage();

        selfName.setText(sessionUser.getPseudo());
        if (sessionUser.getProfilePicture() != null) {
            selfIcon.setImage(new Image(new ByteArrayInputStream(sessionUser.getProfilePicture())));
        }

        // Récupère la liste des amis
        for (Friend friend : friendService.getFriendsByUser(sessionUser)) {
            friends.add(friend.getCompte2());
        }

        // Récupère la liste des discussions, discussions amies et discussion groupe
        for (Discussion discussion : discussionService.getDiscussionsByUser(sessionUser)) {
            addDiscussion(discussion);
        }
        updateBoardSidebarAndChat();
    }

    /**
     * Handle les events du websocket lorsque l'on retire / ajoute le membre à une discussion et que l'on reçoit un message
     * @param event trigger par le websocket
     */
    @Override
    public void handle(IEvent event) {
        if (event instanceof MemberRemoveDiscussionEvent) {
            RequestMemberRemoveDiscussion request = ((MemberRemoveDiscussionEvent) event).getRequest();
            Platform.runLater(
                    () -> {
                        UserDiscussion userDiscussion = new UserDiscussion(request.getDiscussion(), request.getUser(), false); // L'owner ne peut pas quitter ou alors mettre à jour le websocket serveur et client
                        if (userDiscussion.getUser().getId() == sessionUser.getId()) {
                            deleteDiscussionFromSidebar(userDiscussion.getDiscussion());
                        }
                        chatController.removeUserFromDiscussion(userDiscussion);
                    }
            );
        } else if (event instanceof MemberAddDiscussionEvent) {
            RequestMemberAddDiscussion request = ((MemberAddDiscussionEvent) event).getRequest();
            Platform.runLater(
                    () -> {
                        UserDiscussion userDiscussion = new UserDiscussion(request.getDiscussion(), request.getUser(), false); // Un nouveau membre ne peut pas être owner
                        if (userDiscussion.getUser().getId() == sessionUser.getId()) {
                            Discussion newDiscussion = userDiscussion.getDiscussion();
                            addDiscussion(newDiscussion);
                        }
                        chatController.addNewUserToDiscussion(userDiscussion);
                    }
            );
        } else if (event instanceof MessageReceivedEvent) {
            RequestMessageNew request = ((MessageReceivedEvent) event).getRequest();
            DiscussionSidebarController discussionSidebarController = discussionSidebarControllers.get(request.getMessage().getDiscussion().getId());
            Platform.runLater(
                () -> {
                    if (!discussionSidebarController.isActive()) {
                        discussionSidebarController.addNotification();
                    }
                }
            );
        }
    }

    /**
     * Ajoute les Listeners au Dispatcher (events)
     */
    @Override
    public void addListeners() {
        // Listener Websocket
        MemberRemoveDiscussionDispatcher.getInstance().addEventListener(this);
        MemberAddDiscussionDispatcher.getInstance().addEventListener(this);
        MessageReceivedDispatcher.getInstance().addEventListener(this);
    }

    /**
     * Retire les Listeners du Dispatcher (events)
     */
    @Override
    public void removeListeners() {
        // décharger le listener de l'instance discussionController
        MemberRemoveDiscussionDispatcher.getInstance().removeEventListener(this);
        MemberAddDiscussionDispatcher.getInstance().removeEventListener(this);
        MessageReceivedDispatcher.getInstance().removeEventListener(this);
        removeListenersChatController();
    }

    /**
     * Bouton de création de la discussion
     * Instanciation du controller
     * Ajout dans l'interface
     * @param actionEvent trigger par l'action de l'utilisateur
     */
    @FXML
    public void createDiscussionPopup(ActionEvent actionEvent) {
        if (!isCreationDiscussionPopupActive) {
            CreateDiscussionController createDiscussionController = new CreateDiscussionController(friends, discussionService, discussionTypeService, userDiscussionService, this);
            AnchorPane createDiscussionPopup = (AnchorPane) SceneFactory.loadSceneWithController(SceneContext.CREATE_DISCUSSION_POPUP_DISCUSSION_NAME, createDiscussionController);
            stackPaneChatContainer.getChildren().add(createDiscussionPopup);
            isCreationDiscussionPopupActive = true;
        } else {
            logger.warn("La popup de création de discussion est déjà active");
        }
    }

    /**
     * Bouton qui affiche les discussions qui sont des discussions de 2 personnes
     * @param actionEvent trigger par l'action de l'utilisateur
     */
    @FXML
    public void showOnlyFriendDiscussions(ActionEvent actionEvent) {
        discussionSidebarState = EFilterDiscussionSidebar.FRIEND;
        if (!privateDiscussions.isEmpty()) {
            Discussion chatDiscussion = privateDiscussions.entrySet().iterator().next().getValue();
            loadDiscussionsSidebar(privateDiscussions, chatDiscussion);
            setChatbox(chatDiscussion);
        }
        updateFiltreButton();
    }

    /**
     * Bouton qui affiche les discussions qui sont des discussions en groupe
     * @param actionEvent trigger par l'action de l'utilisateur
     */
    @FXML
    public void ShowOnlyGroup(ActionEvent actionEvent) {
        discussionSidebarState = EFilterDiscussionSidebar.GROUP;
        if (!groupDiscussions.isEmpty()) {
            Discussion chatDiscussion = groupDiscussions.entrySet().iterator().next().getValue();
            loadDiscussionsSidebar(groupDiscussions, chatDiscussion);
            setChatbox(chatDiscussion);
        }
        updateFiltreButton();
    }

    /**
     * Bouton qui affiche tous les types de discussion confondus
     * @param actionEvent trigger par l'action de l'utilisateur
     */
    @FXML
    public void ShowAllDiscussion(ActionEvent actionEvent) {
        discussionSidebarState = EFilterDiscussionSidebar.ALL;
        if (!allDiscussions.isEmpty()) {
            Discussion chatDiscussion = allDiscussions.entrySet().iterator().next().getValue();
            loadDiscussionsSidebar(allDiscussions, chatDiscussion);
            setChatbox(chatDiscussion);
        }
        updateFiltreButton();
    }

    /**
     * Récupère les amis
     * @return liste des amis de l'utilisateur
     */
    public List<User> getFriends() {
        return friends;
    }

    /**
     * Suppression d'une discussion de la sidebar, et mets à jour la liste de discussion
     * @param discussion discussion à supprimer
     */
    public void deleteDiscussionFromSidebar(Discussion discussion) {
        DiscussionSidebarController discussionSidebarController = discussionSidebarControllers.get(discussion.getId());
        discussionSidebarController.deleteUI();

        allDiscussions.remove(discussion.getId());
        if (discussion.getDiscussionType().getIsGroup()) {
            groupDiscussions.remove(discussion.getId());
        } else {
            privateDiscussions.remove(discussion.getId());
        }

        // Si la discussion est active
        if (discussion.getId() == chatController.getDiscussion().getId()) {
            updateBoardSidebarAndChat();
        }
    }

    /**
     * Set la chatBox et charge la sidebar
     * Mets à jour les boutons de filtres dans la sidebar
     * @param discussion discussion à charger
     */
    public void setChatboxAndLoadSidebar(Discussion discussion) {
        // Si la discussion cliquée n'est pas dans la liste des discussions filtrée de la sidebar
        if (discussionSidebarState == EFilterDiscussionSidebar.GROUP && !discussion.getDiscussionType().getIsGroup()) {
            discussionSidebarState = EFilterDiscussionSidebar.ALL;
        } else if (discussionSidebarState == EFilterDiscussionSidebar.FRIEND && discussion.getDiscussionType().getIsGroup()) {
            discussionSidebarState = EFilterDiscussionSidebar.ALL;
        }

        switch (discussionSidebarState) {
            case ALL:
                loadDiscussionsSidebar(allDiscussions,discussion);
                break;
            case GROUP:
                loadDiscussionsSidebar(groupDiscussions,discussion);
                break;
            case FRIEND:
                loadDiscussionsSidebar(privateDiscussions,discussion);
                break;
        }
        updateFiltreButton();
        setChatbox(discussion);
    }

    /**
     * Ajoute une discussion dans la sidebar, et dans les propriétés du controller
     * @param newDiscussion nouvelle discussion à mettre dans la sidebar
     */
    public void addDiscussion(Discussion newDiscussion) {
        Map<Integer, Discussion> listDiscussion;
        if (newDiscussion.getDiscussionType().getIsGroup()) listDiscussion = groupDiscussions;
        else listDiscussion = privateDiscussions;

        if (listDiscussion.containsKey(newDiscussion.getId())) {
            logger.warn("La nouvelle discussion est déjà présente dans la liste des discussions");
            return;
        }

        if (newDiscussion.getDiscussionType().getIsGroup()) groupDiscussions.put(newDiscussion.getId(),newDiscussion);
        else privateDiscussions.put(newDiscussion.getId(),newDiscussion);
        allDiscussions.put(newDiscussion.getId(),newDiscussion);
        addDiscussionSidebarController(newDiscussion);
    }

    /**
     * Set isCreationDiscussionPopupActive
     * @param isActive valeur qui correspond à l'état du popup de création de discussion
     */
    public void setCreationDiscussionPopupActive(boolean isActive) {
        this.isCreationDiscussionPopupActive = isActive;
    }

    /**
     * Actualisation de la sidebar et de la chatbox
     * Si aucune discussion n'est présente, alors on met null pour que l'utilisateur voie une discussion inconnue
     */
    private void updateBoardSidebarAndChat() {
        if (!allDiscussions.isEmpty()) {
            Discussion discussion = allDiscussions.entrySet().iterator().next().getValue();
            setChatboxAndLoadSidebar(discussion);
        } else {
            setChatbox(null);
        }
    }

    /**
     * Ajout de la discussion dans l'UI de la sidebar, charge le controller
     * @param discussion discussion à charger dans la sidebar
     */
    private void addDiscussionSidebarController(Discussion discussion) {
        DiscussionSidebarController discussionSidebarController = new DiscussionSidebarController(discussion, this);
        Button discussionSidebar = (Button) SceneFactory.loadSceneWithController(SceneContext.DISCUSSION_CONTAINER_SIDEBAR_DISCUSSION_NAME, discussionSidebarController);
        discussionSidebarControllers.put(discussion.getId(),discussionSidebarController);
        containerDiscussions.getChildren().add(discussionSidebar);
    }

    /**
     * Charge et affiche les discussions dans la sidebar en fonction de l'État de la discussionSidebar
     * @param discussions liste de discussion (permet de mettres savoir si la discussion est active
     * @param activeDiscussion discussion que l'on veut set comme active dans la sidebar
     */
    private void loadDiscussionsSidebar(Map<Integer, Discussion> discussions, Discussion activeDiscussion) {
        for (DiscussionSidebarController discussionSidebarController : discussionSidebarControllers.values()) {
            Discussion discussion = discussionSidebarController.getDiscussion();

            discussionSidebarController.setActive(discussion.getId() == activeDiscussion.getId());
            discussionSidebarController.updateStyle();
            if (discussions != null) {
                discussionSidebarController.toggleShow(discussions.containsKey(discussion.getId()));
            }
        }
    }

    /**
     * Charge le controller et affiche la chatbox
     * Suppression des listeners de l'ancienne chatbox si présente
     * @param discussion discussion a passé dans l'instance du controller
     */
    private void setChatbox(Discussion discussion) {
        if (this.chatController != null) {
            removeListenersChatController();
        }
        this.chatController = new ChatController(discussion, userDiscussionService, messageService, discussionService, this);
        VBox chatBox = (VBox) SceneFactory.loadSceneWithController(SceneContext.BOX_CHAT_NAME, chatController);
        stackPaneChatContainer.getChildren().clear(); // Supprimer les autres nœuds du StackPane pour pas les empiler
        stackPaneChatContainer.getChildren().add(chatBox);
    }

    /**
     * Décharges les listeners de l'instance de chatController
     */
    private void removeListenersChatController() {
        this.chatController.removeListeners();
    }

    /**
     * Actualisation de l'UI des boutons de filtres
     */
    private void updateFiltreButton() {
        int clickedButtonIndex;
        switch (discussionSidebarState) {
            case GROUP -> clickedButtonIndex = 1;
            case FRIEND -> clickedButtonIndex = 2;
            default -> clickedButtonIndex = 0;
        }
        List<Node> buttons = hboxFilterButtons.getChildren();
        for (int i = 0; i < buttons.size(); i++) {
            Button button = (Button) buttons.get(i);
            if (i == clickedButtonIndex) {
                button.setStyle("-fx-background-color: #322F2F; -fx-background-radius: 25; -fx-text-fill: WHITE;");
            } else {
                button.setStyle("-fx-background-color: transparent; -fx-background-radius: 25; -fx-text-fill: WHITE;");
            }
        }
    }
}
