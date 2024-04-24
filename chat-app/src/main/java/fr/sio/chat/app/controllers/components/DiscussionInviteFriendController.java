package fr.sio.chat.app.controllers.components;

import fr.sio.chat.app.controllers.ChatController;
import fr.sio.chat.app.controllers.Controller;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.models.UserDiscussion;
import fr.sio.chat.app.services.interfaces.IUserDiscussionService;
import fr.sio.chat.app.websocket.WebSocketClient;
import fr.sio.chat.app.websocket.requests.RequestMemberAddDiscussion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class DiscussionInviteFriendController extends Controller implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(DiscussionInviteFriendController.class);
    private final WebSocketClient webSocketClient = WebSocketClient.getInstance();
    private final IUserDiscussionService userDiscussionService;
    private final User user;
    private final ChatController chatController;

    @FXML
    private VBox root;
    @FXML
    private ImageView pictureProfile;
    @FXML
    private Label username;
    @FXML
    private Label errorMessage;

    public DiscussionInviteFriendController(User user, ChatController chatController, IUserDiscussionService userDiscussionService) {
        this.user = user;
        this.chatController = chatController;
        this.userDiscussionService = userDiscussionService;
    }

    /**
     * Initialisation avec le nom de l'ami et sa photo de profil
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        username.setText(user.getPseudo());
        if (user.getProfilePicture() != null) {
            pictureProfile.setImage(new Image(new ByteArrayInputStream(user.getProfilePicture())));
        }
    }

    /**
     * Bouton qui ajoute l'utilisateur dans une nouvelle discussion
     * Et envoie la requete au websocket
     * @param event ActionEvent
     */
    @FXML
    public void addUser(ActionEvent event) {
        UserDiscussion userDiscussion = new UserDiscussion(chatController.getDiscussion(), user, false);
        try {
            userDiscussionService.insert(userDiscussion);
        } catch (DataAccessException ex) {
            showError(true, "Échec d'ajout de l'utilisateur.");
            event.consume();
            return;
        }

        chatController.addNewUserToDiscussion(userDiscussion);

        removeRootUI();

        RequestMemberAddDiscussion websocketRequest = new RequestMemberAddDiscussion(user, chatController.getDiscussion(), chatController.getDiscussionReceivers());
        webSocketClient.sendRequest(websocketRequest);
    }

    /**
     * On enlève le popup de l'interface
     */
    private void removeRootUI() {
        VBox parent = (VBox) root.getParent();
        if (parent != null) {
            parent.getChildren().remove(root);
        } else {
            logger.warn("Le parent de DiscussionInviteFriendController est null!");
        }
    }

    /**
     * Affiche l'erreur au-dessus du contenu
     * @param show si on affiche l'erreur
     * @param content contenu du message d'erreur
     */
    private void showError(boolean show, String content) {
        errorMessage.setVisible(show);
        errorMessage.setManaged(show);
        if (show) {
            errorMessage.setText(content);
        }
    }
}
