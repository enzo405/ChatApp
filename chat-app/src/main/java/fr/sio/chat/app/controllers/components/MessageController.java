package fr.sio.chat.app.controllers.components;

import fr.sio.chat.app.App;
import fr.sio.chat.app.SceneFactory;
import fr.sio.chat.app.controllers.ChatController;
import fr.sio.chat.app.controllers.Controller;
import fr.sio.chat.app.controllers.SceneContext;
import fr.sio.chat.app.events.dispatchers.MessageDeletedDispatcher;
import fr.sio.chat.app.events.dispatchers.MessageEditedDispatcher;
import fr.sio.chat.app.events.interfaces.IEvent;
import fr.sio.chat.app.events.interfaces.IEventListener;
import fr.sio.chat.app.events.models.MessageDeletedEvent;
import fr.sio.chat.app.events.models.MessageEditedEvent;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Message;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.security.SecurityContext;
import fr.sio.chat.app.services.interfaces.IMessageService;
import fr.sio.chat.app.websocket.WebSocketClient;
import fr.sio.chat.app.websocket.requests.RequestMessageDelete;
import fr.sio.chat.app.websocket.requests.RequestMessageEdit;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MessageController extends Controller implements Initializable, IEventListener {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final double MESSAGE_BOX_MAX_WIDTH = App.screen.getVisualBounds().getWidth() * 0.5;
    private final WebSocketClient webSocketClient = WebSocketClient.getInstance();
    private final IMessageService messageService;
    private final User user;
    private final Message message;
    private final OptionsMessageController optionsMessageController;
    private final ChatController chatController;
    @FXML
    private HBox root;
    @FXML
    private ImageView profilePicture;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label dateTimeLabel;
    @FXML
    private VBox containerMessageAndMessageAnsweredVBox;
    @FXML
    private Label messageLabel;
    @FXML
    private Button editButton;
    @FXML
    private HBox editedLabelContainer;
    private final Node optionsMessage;

    public MessageController(Message message, IMessageService messageService, ChatController chatController) {
        this.user = message.getCompte();
        this.messageService = messageService;
        this.message = message;
        this.chatController = chatController;
        this.optionsMessageController = new OptionsMessageController(messageService, this);
        optionsMessage = SceneFactory.loadSceneWithController(SceneContext.OPTIONS_MESSAGE_POPUP_CHAT_NAME, optionsMessageController);
    }

    /**
     * On initialise chaque élément du composant
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();

        if (isSessionUserOwner()) {
            editButton.setManaged(true);
            editButton.setVisible(true);
        }
        byte[] userProfilePicture = user.getProfilePicture();
        if (userProfilePicture != null) {
            profilePicture.setImage(new Image(new ByteArrayInputStream(userProfilePicture)));
        }
        if (message.getIdMessageAnswered() != 0) { // 0 => null
            addAnsweredMessageUI(messageService.getMessageById(message.getIdMessageAnswered(), chatController.getDiscussion()));
        }
        if (message.getUpdatedDate() != null && !message.getUpdatedDate().equals(new Timestamp(0))) {
            addEditedLabel();
        }

        usernameLabel.setText(user.getPseudo());
        dateTimeLabel.setText(message.getCreatedDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yy hh:mm:ss")));

        messageLabel.setText(message.getContent());
        messageLabel.setMaxWidth(MESSAGE_BOX_MAX_WIDTH);
        root.getChildren().add(3, optionsMessage); // 3 pour la 3ᵉ position dans le fxml en partant de la gauche
    }

    /**
     * Handle les events du websocket lors de la modification de message et suppression de message
     * @param event event du websocket venant du dispatcher
     */
    @Override
    public void handle(IEvent event) {
        if (event instanceof MessageEditedEvent) {
            RequestMessageEdit request = ((MessageEditedEvent) event).getRequest();
            if (request.getMessage().getId() == message.getId()) {
                Platform.runLater(
                        () -> {
                            String content = request.getMessage().getContent();
                            message.setContent(content);
                            this.editMessagedUI(content);
                        }
                );
            }
        } else if (event instanceof MessageDeletedEvent) {
            RequestMessageDelete request = ((MessageDeletedEvent) event).getRequest();
            if (request.getMessage().getId() == message.getId()) {
                Platform.runLater(
                    this::deleteMessageUI
                );
            }
        }
    }

    /**
     * Ajoute les Listeners au Dispatcher (events)
     */
    @Override
    public void addListeners() {
        MessageEditedDispatcher.getInstance().addEventListener(this);
        MessageDeletedDispatcher.getInstance().addEventListener(this);
    }

    /**
     * Retire les Listeners au Dispatcher (events)
     */
    @Override
    public void removeListeners() {
        MessageEditedDispatcher.getInstance().removeEventListener(this);
        MessageDeletedDispatcher.getInstance().removeEventListener(this);
    }

    /**
     * Bouton de réponse au message, mets à jour l'interface du chat bar
     * @param event trigger par l'action de l'utilisateur
     */
    @FXML
    public void replyToMessage(ActionEvent event) {
        chatController.setMessageAnsweredUI(this);
    }

    /**
     * Bouton d'édition du message, mets à jour l'interface du chat bar
     * @param event trigger par l'action de l'utilisateur
     */
    @FXML
    public void editMessageButton(ActionEvent event) {
        chatController.setEditingMessageUI(this);
    }

    /**
     * Bouton qui affiche les options d'un message
     * @param event trigger par l'action de l'utilisateur
     */
    @FXML
    public void showOptions(ActionEvent event) {
        optionsMessageController.toggleDisplay();
    }

    /**
     * Récupération du message
     * @return Le message lié au controller
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Récupération du chatController
     * @return Le chatController parent
     */
    public ChatController getChatController() {
        return chatController;
    }

    /**
     * Edition du message dans la BDD, dans l'UI et envoi au websocket
     * @param content contenu du message à edit
     * @throws DataAccessException erreur avec la base de donnée
     */
    public void editMessage(final String content) throws DataAccessException{
        message.setContent(content);
        messageService.update(message); // Arrêt de la méthode si l'exception DataAccessException est jetté

        editMessagedUI(message.getContent());

        RequestMessageEdit websocketRequest = new RequestMessageEdit(message, chatController.getDiscussionReceivers());
        webSocketClient.sendRequest(websocketRequest);
    }

    /**
     * Suppression du message dans la BDD, dans l'UI et envoi au websocket
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    public void deleteMessage() throws DataAccessException {
        messageService.delete(message); // Si fail alors la requete websocket n'est pas envoyé et le message n'est pas supprimé dans l'UI

        deleteMessageUI();

        RequestMessageDelete websocketRequest = new RequestMessageDelete(message, chatController.getDiscussionReceivers());
        webSocketClient.sendRequest(websocketRequest);
    }

    /**
     * Modification du message dans l'interface
     * @param content contenu du message à changer dans l'interface
     */
    private void editMessagedUI(String content) {
        chatController.editPinnedMessage(message);

        messageLabel.setText(content);
        addEditedLabel();
    }

    /**
     * Affiche 'modifié' à côté du message
     */
    private void addEditedLabel() {
        editedLabelContainer.setVisible(true);
        editedLabelContainer.setManaged(true);
    }

    /**
     * Suppression du message de l'interface, enlève les listeners de l'EventDispatcher
     * Et mets à jour les messages épinglés
     */
    private void deleteMessageUI() {
        chatController.removePinnedMessageUI(message);

        removeListeners();
        VBox parent = (VBox) root.getParent();
        if (parent != null) {
            parent.getChildren().remove(root);
        } else {
            logger.warn("Le parent de MessageController est null!");
        }
    }

    /**
     * Vérifie si le message est celui de l'utilisateur connecté
     * @return true si c'est le même ou false sinon
     */
    private boolean isSessionUserOwner() {
        return message.getCompte().getId() == SecurityContext.getUser().getId();
    }

    /**
     * Ajoute du message répondu dans la Box du message
     * @param messageAnswered message qui correspond au message qui a été répondu
     */
    private void addAnsweredMessageUI(final Message messageAnswered) {
        Node messageAnsweredHBox;
        if (messageAnswered != null) {
            AnsweredMessageController answeredMessageController = new AnsweredMessageController(messageAnswered);
            messageAnsweredHBox = SceneFactory.loadSceneWithController(SceneContext.ANSWERED_MESSAGE_CHAT_NAME, answeredMessageController);
        } else {
            messageAnsweredHBox = new Label("Une erreur est survenue pendant le chargement du message répondu");
            messageAnsweredHBox.setStyle("-fx-text-fill: #ec4040; -fx-opacity: 100%");
        }
        containerMessageAndMessageAnsweredVBox.getChildren().add(0,messageAnsweredHBox);
    }
}
