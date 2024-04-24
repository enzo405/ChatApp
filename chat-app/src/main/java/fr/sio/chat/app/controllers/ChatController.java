package fr.sio.chat.app.controllers;

import fr.sio.chat.app.SceneFactory;
import fr.sio.chat.app.controllers.components.DiscussionInviteFriendController;
import fr.sio.chat.app.controllers.components.DiscussionMemberController;
import fr.sio.chat.app.controllers.components.MessageController;
import fr.sio.chat.app.controllers.components.MessageResearchOrPinnedController;
import fr.sio.chat.app.events.interfaces.IEvent;
import fr.sio.chat.app.events.interfaces.IEventListener;
import fr.sio.chat.app.events.dispatchers.MessageReceivedDispatcher;
import fr.sio.chat.app.events.models.MessageReceivedEvent;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.Message;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.models.UserDiscussion;
import fr.sio.chat.app.security.SecurityContext;
import fr.sio.chat.app.services.interfaces.IDiscussionService;
import fr.sio.chat.app.services.interfaces.IMessageService;
import fr.sio.chat.app.services.interfaces.IUserDiscussionService;
import fr.sio.chat.app.websocket.WebSocketClient;
import fr.sio.chat.app.websocket.requests.RequestMessageNew;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

public class ChatController extends Controller implements IEventListener, Initializable {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);
    private final WebSocketClient webSocketClient = WebSocketClient.getInstance();
    private final IUserDiscussionService userDiscussionService;
    private final IDiscussionService discussionService;
    private final IMessageService messageService;
    private final DiscussionController discussionController;
    private final Map<Integer, MessageController> messageControllers = new HashMap<>();
    private final Discussion discussion;
    private final Map<Integer, UserDiscussion> members = new HashMap<>();
    private final Map<Integer, UserDiscussion> owners = new HashMap<>();
    private final Map<Integer, UserDiscussion> guests = new HashMap<>();
    private final TreeMap<Integer, Message> pinnedMessages = new TreeMap<>();
    private final List<Integer> discussionMembersId = new ArrayList<>();
    private final User sessionUser = SecurityContext.getUser();
    private MessageController answeredMessageController = null;
    private MessageController editingMessageController = null;
    @FXML
    private VBox root;
    @FXML
    private ImageView discussionIcon;
    @FXML
    private Label discussionName;
    @FXML
    private Label status;
    @FXML
    private TextField sendMessageBar;
    @FXML
    private VBox messageContainer;
    @FXML
    private ScrollPane scrollPaneMessageContainer;
    @FXML
    private HBox topMessageBarContainer;
    @FXML
    private Label topMessageBarLabel;
    @FXML
    private Button addMemberTopButton;
    @FXML
    private Button pinTopButton;
    @FXML
    private Button memberListTopButton;
    @FXML
    private TextField showSearchResult;
    @FXML
    private GridPane discussionPopupContainer;
    @FXML
    private BorderPane discussionPopup;
    @FXML
    private Label titleDiscussionPopup;
    @FXML
    private VBox vBoxContentDiscussionPopup;

    public ChatController(Discussion discussion, IUserDiscussionService userDiscussionService, IMessageService messageService, IDiscussionService discussionService, DiscussionController discussionController) {
        this.discussion = discussion;
        this.discussionService = discussionService;
        this.userDiscussionService = userDiscussionService;
        this.messageService = messageService;
        this.discussionController = discussionController;
    }

    /**
     * Initialise le controller et son interface
     * Fait les requêtes pour récupérer les membres, et leur role
     * Vérifie si l'utilisateur est bien dans la discussion
     * Si oui alors la chatbar, message, message épinglé sont initialisé
     * Si non, l'utilisateur vera une discussion vide sans titre
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialise la liste des membres
        for (UserDiscussion userDiscussion : userDiscussionService.getDiscussionMembers(discussion)) {
            members.put(userDiscussion.getUser().getId() ,userDiscussion);
            // Pour ne pas envoyer le message par le websocket à soi-même
            if (userDiscussion.getUser().getId() != sessionUser.getId()) {
                discussionMembersId.add(userDiscussion.getUser().getId());
            }
            if (userDiscussion.isOwner()) {
                owners.put(userDiscussion.getUser().getId() ,userDiscussion);
            } else {
                guests.put(userDiscussion.getUser().getId() ,userDiscussion);
            }
        }
        // Si on accède à la page Discussion sans avoir de discussions
        if (discussion != null) {
            // Double check si l'utilisateur a été kick de la discussion, mais peut encore y accéder
            if (checkDiscussionHasUser(sessionUser)) {
                try {
                    for (Message message : messageService.getPinnedMessagesByDiscussion(discussion)) {
                        pinnedMessages.put(message.getId(), message);
                    }
                } catch (DataAccessException ignored) {
                }

                addListeners();

                loadActionsEvent();
                loadTopButtons();
                Platform.runLater(() -> {
                    try {
                        loadMessageUI();
                    } catch (DataAccessException ignored) {
                        // Si la méthode loadMessageUI fail, l'utilisateur ne pourra pas envoyer de message
                    }
                });
            }
        }
    }

    /**
     * Handle les events du websocket lors de la réception de message
     * @param event trigger par le websocket
     */
    @Override
    public void handle(IEvent event) {
        if (event instanceof MessageReceivedEvent) {
            RequestMessageNew request = ((MessageReceivedEvent) event).getRequest();
            if (discussion.getId() == request.getMessage().getDiscussion().getId()) {
                Platform.runLater(
                    () -> addMessageUI(request.getMessage())
                );
            }
        }
    }

    /**
     * Ajoute les Listeners au Dispatcher (events)
     */
    @Override
    public void addListeners() {
        // Listener Websocket
        MessageReceivedDispatcher.getInstance().addEventListener(this);
    }

    /**
     * Retire les Listeners du Dispatcher (events)
     * pour cette instance et toutes les instances de messages
     */
    @Override
    public void removeListeners() {
        MessageReceivedDispatcher.getInstance().removeEventListener(this);
        for (MessageController messageController : messageControllers.values()) {
            messageController.removeListeners();
        }
    }

    /**
     * Bouton d'envoi du message
     * @param event trigger par l'action de clic et le keyPressed
     */
    @FXML
    public void sendMessage(Event event) {
        if (!checkDiscussionHasUser(sessionUser)) {
            toggleTopMessageBarView(true, "Vous ne faites plus partie de la discussion !", true);
            return;
        };

        // OnKeyPressed & OnAction
        String content = sendMessageBar.getText();
        sendMessageBar.setText("");
        toggleTopMessageBarView(false, null, false);

        try {
            if (!content.isBlank()) {
                if (editingMessageController == null) {
                    // Nouveau message
                    newMessage(content);
                } else {
                    // Modification du message
                    editMessage(content);
                    editingMessageController = null; // Pour que le prochain message envoyé soit considéré comme un nouveau message
                }
            }
        } catch (DataAccessException ex) {
            toggleTopMessageBarView(true, "Une erreur est survenue lors de l'envoie du message !", true);
            event.consume();
        }
    }

    /**
     * Retire l'affichage du popup de réponse au message au-dessus du textarea pour l'envoi du message
     * Set la variable qui vérifie si le message est une modification à null
     * @param event trigger par l'action de l'utilisateur
     */
    @FXML
    public void closeTopMessageBarContainer(ActionEvent event) {
        editingMessageController = null;
        toggleTopMessageBarView(false, "", false);
    }

    /**
     * Permet d'afficher la popup avec le contenu lors du click sur addMemberTopButton, pinTopButton, memberListTopButton
     * @param event trigger par l'action de l'utilisateur
     */
    @FXML
    public void handleDiscussionPopup(Event event) {
        String popupTitle = "";

        if (event.getSource() == addMemberTopButton) {
            popupTitle = "Ajouter de nouveaux membres :";
            populateMemberPopupContent();
        } else if (event.getSource() == pinTopButton) {
            popupTitle = "Messages épinglés :";
            populatePinnedMessagePopupContent();
        } else if (event.getSource() == memberListTopButton) {
            popupTitle = "Listes des membres :";
            populateMemberListPopupContent();
        }

        showPanelSearchResult(!discussionPopupContainer.isVisible(), popupTitle);
    }

    /**
     * Affichage du popup de réponse au message au-dessus du textarea pour l'envoi du message
     * @param messageAnsweredTo message répondu
     */
    public void setMessageAnsweredUI(MessageController messageAnsweredTo) {
        this.editingMessageController = null;
        this.answeredMessageController = messageAnsweredTo;
        sendMessageBar.setText("");
        toggleTopMessageBarView(true, "Répondre à " + messageAnsweredTo.getMessage().getCompte().getPseudo(), false);
    }

    /**
     * Affichage du popup de modification du message au-dessus du textarea pour l'envoi du message
     * @param messageBox controller du message qu'on modifie
     */
    public void setEditingMessageUI(MessageController messageBox) {
        this.editingMessageController = messageBox;
        this.answeredMessageController = null;
        sendMessageBar.setText(messageBox.getMessage().getContent());
        toggleTopMessageBarView(true, "Modifiez votre message", false);
    }

    /**
     * On retire l'utilisateur de la discussion
     * Mets à jour les propriétés qui contiennent le membre
     * Mets à jour l'interface avec le nombre de membres de la discussion groupé
     * @param member membre que l'on veut supprimer de la discussion
     */
    public void removeUserFromDiscussion(UserDiscussion member) {
        if (discussion.getId() == member.getDiscussion().getId()) {
            members.remove(member.getUser().getId());
            guests.remove(member.getUser().getId());

            if (discussion.getDiscussionType().getIsGroup()) {
                updateMembersCountUI(member);
            }
        }
    }

    /**
     * On ajoute l'utilisateur à la discussion
     * Mets à jour les propriétés qui contiennent le membre
     * Mets à jour l'interface avec le nombre de membres de la discussion groupé
     * @param member membre à ajouter dans la discussion
     */
    public void addNewUserToDiscussion(UserDiscussion member) {
        if (discussion.getId() == member.getDiscussion().getId()) {
            members.put(member.getUser().getId(), member);
            guests.put(member.getUser().getId(), member);

            if (discussion.getDiscussionType().getIsGroup()) {
                updateMembersCountUI(member);
            }
        }
    }

    /**
     * Ajout du message épinglé dans l'interface
     * @param message message à ajouter dans la liste
     */
    public void addPinnedMessageUI(Message message) {
        if (!pinnedMessages.containsKey(message.getId())) {
            pinnedMessages.put(message.getId(), message);
        }
    }

    /**
     * Edition du message épinglé dans l'interface
     * @param message message à modifier de la liste
     */
    public void editPinnedMessage(Message message) {
        Message pinnedMessage = pinnedMessages.get(message.getId());
        if (pinnedMessage != null) {
            pinnedMessage.setContent(message.getContent());
        }
    }

    /**
     * On enlève l'épinglage du message
     * @param message message à supprimer de la liste
     */
    public void removePinnedMessageUI(Message message) {
        pinnedMessages.remove(message.getId());
    }

    /**
     * Récupération du DiscussionController
     * @return le discussion controller
     */
    public DiscussionController getDiscussionController() {
        return discussionController;
    }

    /**
     * Récupération des id des membres d'une discussion
     * @return Une liste d'id de membres de discussion
     */
    public List<Integer> getDiscussionReceivers() {
        return discussionMembersId;
    }

    /**
     * Récupère la discussion
     * @return La discussion
     */
    public Discussion getDiscussion() {
        return discussion;
    }

    /**
     * Récupération des owners de discussions
     * @return Un tableau clé, valeur liant l'id de l'owner et le UserDiscussion
     */
    public Map<Integer, UserDiscussion> getDiscussionOwners() {
        return owners;
    }

    /**
     * Affichage du membre à amis que l'on peut ajouter à la discussion
     */
    private void populateMemberPopupContent() {
        vBoxContentDiscussionPopup.getChildren().clear();
        for (User friend : discussionController.getFriends()) {
            if (!discussionMembersId.contains(friend.getId())) {
                vBoxContentDiscussionPopup.getChildren().add(loadAddMemberDiscussionController(friend));
            }
        }
    }

    /**
     * Affichage des messages épinglés
     */
    private void populatePinnedMessagePopupContent() {
        vBoxContentDiscussionPopup.getChildren().clear();
        for (Message pinnedMessage : pinnedMessages.values()) {
            vBoxContentDiscussionPopup.getChildren().add(loadPinnedOrResearchMessageController(pinnedMessage));
        }
    }

    /**
     * Affichage des membres de la discussion
     * En séparant par roles (guests/owner)
     */
    private void populateMemberListPopupContent() {
        vBoxContentDiscussionPopup.getChildren().clear();
        for (UserDiscussion member : owners.values()) {
            vBoxContentDiscussionPopup.getChildren().add(loadDiscussionMemberController(member));
        }
        vBoxContentDiscussionPopup.getChildren().add(createVBoxSeparator(2, "#363A3D"));
        if (!guests.isEmpty()) {
            for (UserDiscussion member : guests.values()) {
                vBoxContentDiscussionPopup.getChildren().add(loadDiscussionMemberController(member));
            }
        }
    }

    /**
     * Affichage des messages que l'on a recherchés
     * Cherche le message avec son contenu et la discussion
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    private void populateSearchResultContent(String researchString) throws DataAccessException {
        vBoxContentDiscussionPopup.getChildren().clear();
        List<Message> messages;
        messages = messageService.findMessagesByDiscussion(researchString, discussion);
        if (messages.isEmpty()) {
            vBoxContentDiscussionPopup.getChildren().add(new Label("Aucun résultat trouvé pour «" + researchString + "»"));
        } else {
            for (Message message : messages) {
                vBoxContentDiscussionPopup.getChildren().add(loadPinnedOrResearchMessageController(message));
            }
        }
    }

    /**
     * On récupère la box permettant d'afficher le membre
     * On charge le controller
     * @param member membre de la discussion à ajouter dans l'instance du controller
     * @return La VBox affichant le membre
     */
    private VBox loadDiscussionMemberController(UserDiscussion member) {
        // Instanciation du controller
        DiscussionMemberController discussionMemberController = new DiscussionMemberController(member, this, discussionService, userDiscussionService);
        // Création du node grâce au FXMLLoader.load()
        return (VBox) SceneFactory.loadSceneWithController(SceneContext.DISCUSSION_MEMBER_BOX_DISCUSSION_NAME, discussionMemberController);
    }

    /**
     * On récupère la box permettant d'afficher le membre à ajouter
     * On charge le controller
     * @param user user de la liste à ajouter dans l'instance du controller
     * @return La VBox affichant le membre à ajouter
     */
    private VBox loadAddMemberDiscussionController(User user) {
        // Instanciation du controller
        DiscussionInviteFriendController discussionMemberController = new DiscussionInviteFriendController(user, this, userDiscussionService);
        // Création du node grâce au FXMLLoader.load()
        return (VBox) SceneFactory.loadSceneWithController(SceneContext.DISCUSSION_INVITE_FRIEND_DISCUSSION_NAME, discussionMemberController);
    }

    /**
     * On récupère la box permettant d'afficher le message épinglé
     * @param message message de la liste à ajouter dans le controller
     * @return La VBox affichant le message épinglé
     */
    private HBox loadPinnedOrResearchMessageController(Message message) {
        // Instanciation du controller
        MessageResearchOrPinnedController messageResearchOrPinnedController = new MessageResearchOrPinnedController(message, messageService, this);
        // Création du node grâce au FXMLLoader.load()
        return (HBox) SceneFactory.loadSceneWithController(SceneContext.MESSAGE_BOX_RESEARCH_OR_PINNED_CHAT_NAME, messageResearchOrPinnedController);
    }

    /**
     * Création du message pour faire l'envoi à la BDD, au websocket et pour l'afficher dans l'UI
     * Si le message répond à un autre, initialise le contoller du message répondu et ajoute à l'interface
     * @param content contenu du nouveau message
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    private void newMessage(String content) throws DataAccessException {
        Message message = new Message(content, discussion, sessionUser);
        Integer messageId;
        if (answeredMessageController != null) {
            message.setIdMessageAnswered(answeredMessageController.getMessage().getId());
            messageId = messageService.insertWithAnsweredMessage(message);
            answeredMessageController = null; // Pour que les prochains messages envoyés ne répondent plus au message
        } else {
            messageId = messageService.insert(message);
        }
        message.setId(messageId);

        addMessageUI(message);

        RequestMessageNew websocketRequest = new RequestMessageNew(message, discussionMembersId);
        webSocketClient.sendRequest(websocketRequest);
    }

    /**
     * Edition du messsage
     * @param content contenu du message
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    private void editMessage(String content) throws DataAccessException{
        editingMessageController.editMessage(content);
    }

    /**
     * On récupère les messages de la discussion et on les affiche
     * Si aucune erreur, on affiche la chatbar et on active l'envoie de message
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    private void loadMessageUI() throws DataAccessException {
        for (Message message : messageService.getMessagesByDiscussion(discussion)) {
            addMessageUI(message);
        }
        // Seulement une fois les message chargé, on peut envoyer des messages
        sendMessageBar.setDisable(false);
        sendMessageBar.setEditable(true);
        sendMessageBar.setPromptText("Envoyer un message dans " + discussion.getName());
    }

    /**
     * Actualisation du nombre de membres dans l'UI de la discussion
     * @param member membre à ajouter / supprimer
     */
    private void updateMembersCountUI(UserDiscussion member) {
        if (discussionMembersId.contains(member.getUser().getId())) {
            discussionMembersId.remove((Integer) member.getUser().getId());
        } else {
            discussionMembersId.add(member.getUser().getId());
        }
        status.setText(members.size() + " membres");
    }

    /**
     * On charge les boutons dans la barre du haut en fontion du type de discussion
     * Initialise le nom et l'image de la discussion
     */
    private void loadTopButtons() {
        discussionIcon.setImage(new Image(discussion.getDiscussionType().getIcon()));
        discussionName.setText(discussion.getName());
        if (discussion.getDiscussionType().getIsGroup()) {
            status.setText(members.size() + " membres");
            memberListTopButton.setVisible(true);
            memberListTopButton.setManaged(true);
            addMemberTopButton.setVisible(true);
            addMemberTopButton.setManaged(true);
        }
    }

    /**
     * On charge les différents events lié à la discussion :
     * <ul>
     * <li>Quand l'utilisateur appui sur ENTRER et quand le focus est sur sendMessageBar => envoie du message</li>
     * <li>Quand l'utilisateur clique sur le textField de la rechercher de message => Affichage du popup</li>
     * <li>Quand l'utilisateur clique dans le vide => ferme la popup</li>
     * <li>Quand l'utilisateur appui sur ENTRER et quand il le focus est sur showSearchResult => Recherche le message dans la discussion</li>
     * </ul>
     */
    private void loadActionsEvent() {
        // Quand l'utilisateur appui sur ENTRER et quand le focus est sur sendMessageBar
        sendMessageBar.onKeyPressedProperty().set(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage(event);
            }
        });
        // Quand l'utilisateur clique sur le showSearchResult
        showSearchResult.focusedProperty().addListener(((observableValue, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                showPanelSearchResult(true, "Résultats :");
                vBoxContentDiscussionPopup.getChildren().clear();
            }
        }));
        // Quand je clique autre part (car le popup se situant dans un stackpane, il est impossible de quitter le focus des popups)
        root.setOnMouseClicked(event -> {
            vBoxContentDiscussionPopup.getChildren().clear();
            discussionPopupContainer.setVisible(false);
        });
        // Quand l'utilisateur appui sur ENTRER et quand il le focus est sur showSearchResult
        showSearchResult.onKeyPressedProperty().set(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                showPanelSearchResult(true, "Résultats :"); // Force l'affichage dans tous les cas
                String researchString = showSearchResult.getText();
                if (researchString != null && !researchString.isBlank()) {
                    try {
                        populateSearchResultContent(researchString.trim());
                    } catch (DataAccessException ex) {
                        vBoxContentDiscussionPopup.getChildren().clear();
                        Label helperMessage = new Label("Une erreur est survenue lors de la recherche");
                        helperMessage.setWrapText(true);
                        vBoxContentDiscussionPopup.getChildren().add(helperMessage);
                    }
                } else {
                    vBoxContentDiscussionPopup.getChildren().clear();
                    Label helperMessage = new Label("Votre recherche doit faire au moins 1 caractères");
                    helperMessage.setWrapText(true);
                    vBoxContentDiscussionPopup.getChildren().add(helperMessage);
                }
            }
        });
    }

    /**
     * Affiche la popup de la discussion dans le coin
     * @param visible affiche ou non la popup
     * @param content contenu à afficher dans le titre du popup
     */
    private void showPanelSearchResult(boolean visible, String content) {
        discussionPopupContainer.setVisible(visible);
        titleDiscussionPopup.setText(content);
    }

    /**
     * Ajoute le message dans l'UI et scroll en bas
     * Instancie le controller
     * @param message message à ajouter
     */
    private void addMessageUI(Message message) {
        MessageController messageController = new MessageController(message, messageService, this);
        HBox messageBox = (HBox) SceneFactory.loadSceneWithController(SceneContext.MESSAGE_BOX_CHAT_NAME, messageController);
        messageControllers.put(message.getId() ,messageController); // Ajoute le controller dans la liste des messagesControllers
        messageContainer.getChildren().add(messageBox);
        scrollToBottom();
    }

    /**
     * On scroll en bas pour afficher ce qui est le plus récent
     */
    private void scrollToBottom() {
        // Pour que le Scrollpane reste en bas de la liste des messages
        messageContainer.heightProperty().addListener(observable -> scrollPaneMessageContainer.setVvalue(1D));
    }

    /**
     * Affiche la popup de réponse / modification au message au-dessus du textarea pour l'envoi du message
     * @param display affiche ou non la popup
     * @param msgLabel contenu à mettre dans le popup → "Réponse à ..." / "Modificiation du message"
     */
    private void toggleTopMessageBarView(boolean display, String msgLabel, boolean isError) {
        topMessageBarContainer.setManaged(display);
        topMessageBarContainer.setVisible(display);
        if (display) {
            topMessageBarLabel.setText(msgLabel);
            if (isError) {
                topMessageBarContainer.setStyle("-fx-background-color: #c43131; -fx-background-radius: 10 10 0 0;");
                topMessageBarLabel.setTextFill(Color.WHITE);
            } else {
                topMessageBarContainer.setStyle("-fx-background-color: #0D0F10; -fx-background-radius: 10 10 0 0;");
                topMessageBarLabel.setTextFill(Paint.valueOf("#9d9d9d"));
            }
        }
    }

    /**
     * Vérifie si l'utilisateur est dans la discussion
     * @param user utilisateur à vérifier
     * @return true si l'utilisateur est dans la discussion, false sinon
     */
    private boolean checkDiscussionHasUser(User user) {
        if (members.get(user.getId()) != null) {
            return true;
        }
        logger.warn("Vous n'avez pas accés à cette discussion");
        discussionController.deleteDiscussionFromSidebar(discussion);
        return false;
    }

    /**
     * Création du séparateur
     * @param height hauteur du separator
     * @param hexColor couleur background du séparator
     * @return VBox contenant le separator
     */
    private VBox createVBoxSeparator(double height, String hexColor) {
        VBox seperatorVBox = new VBox();
        seperatorVBox.setPrefHeight(height);
        seperatorVBox.setStyle("-fx-background-radius: 4; -fx-background-color: " + hexColor);
        return seperatorVBox;
    }
}