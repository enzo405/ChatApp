package fr.sio.chat.app.controllers.components;

import fr.sio.chat.app.App;
import fr.sio.chat.app.SceneFactory;
import fr.sio.chat.app.controllers.ChatController;
import fr.sio.chat.app.controllers.Controller;
import fr.sio.chat.app.controllers.SceneContext;
import fr.sio.chat.app.models.Message;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.services.interfaces.IMessageService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MessageResearchOrPinnedController extends Controller implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(MessageResearchOrPinnedController.class);
    private final double MESSAGE_BOX_MAX_WIDTH = App.screen.getVisualBounds().getWidth() * 0.5;
    private final IMessageService messageService;
    private final User user;
    private final Message message;
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
    private Label mainMessageContent;

    public MessageResearchOrPinnedController(Message message, IMessageService messageService, ChatController chatController) {
        this.user = message.getCompte();
        this.messageService = messageService;
        this.message = message;
        this.chatController = chatController;
    }

    /**
     * On initialise chaque élément du composant (photo de profil, le message avec nom, prénom, date du message, contenu du message)
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        byte[] userProfilePicture = user.getProfilePicture();
        if (userProfilePicture != null) {
            profilePicture.setImage(new Image(new ByteArrayInputStream(userProfilePicture)));
        }
        if (message.getIdMessageAnswered() != 0) {
            Message messageAnswered = messageService.getMessageById(message.getIdMessageAnswered(), chatController.getDiscussion());
            addAnsweredMessageUI(messageAnswered);
        }

        usernameLabel.setText(user.getPseudo());
        dateTimeLabel.setText(message.getCreatedDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yy hh:mm:ss")));

        mainMessageContent.setText(message.getContent());
        mainMessageContent.setMaxWidth(MESSAGE_BOX_MAX_WIDTH);
    }

    /**
     * Affiche le message répondu sur l'interface
     * @param messageAnswered message qui correspond au message qui a été répondu
     */
    private void addAnsweredMessageUI(Message messageAnswered) {
        Node messageAnsweredHBox;
        if (messageAnswered != null) {
            AnsweredMessageController answeredMessageController = new AnsweredMessageController(messageAnswered);
            messageAnsweredHBox = SceneFactory.loadSceneWithController(SceneContext.ANSWERED_MESSAGE_CHAT_NAME, answeredMessageController);
        } else {
            messageAnsweredHBox = new Label("Une erreur est survenue pendant le chargement du message répondu");
            messageAnsweredHBox.setStyle("-fx-text-fill: RED; -fx-opacity: 70%");
        }
        containerMessageAndMessageAnsweredVBox.getChildren().add(0,messageAnsweredHBox);
    }
}
