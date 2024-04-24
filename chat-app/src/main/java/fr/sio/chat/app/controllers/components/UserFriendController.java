package fr.sio.chat.app.controllers.components;

import fr.sio.chat.app.SceneFactory;
import fr.sio.chat.app.controllers.Controller;
import fr.sio.chat.app.controllers.DiscussionController;
import fr.sio.chat.app.controllers.HomeController;
import fr.sio.chat.app.controllers.SceneContext;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.FriendException;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.Friend;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.services.interfaces.IDiscussionService;
import fr.sio.chat.app.services.interfaces.IFriendService;
import fr.sio.chat.app.websocket.WebSocketClient;
import fr.sio.chat.app.websocket.requests.RequestFriendRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class UserFriendController extends Controller implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(DiscussionSidebarController.class);
    private final WebSocketClient webSocketClient = WebSocketClient.getInstance();
    private final Friend friend; // idCompte1 => sessionUser, idCompte2 => amis
    private final IDiscussionService discussionService;
    private final IFriendService friendService;
    private final HomeController homeController;
    @FXML
    private Label errorMessage;
    @FXML
    private Label labelUsername;
    @FXML
    private ImageView pictureProfile;
    @FXML
    private Button linkToDiscussionBtn;
    @FXML
    private Button deleteFriendBtn;

    public UserFriendController(Friend friend, IDiscussionService discussionService, IFriendService friendService, HomeController homeController) {
        this.homeController = homeController;
        this.friend = friend;
        this.discussionService = discussionService;
        this.friendService = friendService;
    }

    /**
     * On initialise chaque élément de UserFriendController
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Button Action Initialisation
        linkToDiscussionBtn.setOnAction(this::linkToDiscussion);
        deleteFriendBtn.setOnAction(this::deleteFriend);

        User user = friend.getCompte2();
        labelUsername.setText(user.getPseudo());
        if (user.getProfilePicture() != null) {
            pictureProfile.setImage(new Image(new ByteArrayInputStream(user.getProfilePicture())));
        }
    }

    /**
     * Bouton de suppression de l'ami
     * @param event trigger par l'action de l'utilisateur
     */
    @FXML
    public void deleteFriend(ActionEvent event) {
        try {
            friend.setEstAccepte(false);
            friendService.delete(friend);

            homeController.updateFriendBoard();

            RequestFriendRequest websocketRequest = new RequestFriendRequest(friend);
            webSocketClient.sendRequest(websocketRequest);
        } catch (DataAccessException ex) {
            showErrorLabel("La suppression de l'amis a échoué");
            event.consume();
        }
    }

    /**
     * Bouton qui fait un lien vers la discussion
     * Cherche la discussion (crée ou alors récupère)
     * Ajoute la discussion dans la sidebar et la selectionne.
     * <p>
     * Erreur possible :
     * <li>L'amis n'existe pas (possible dans des groupes)
     * <li>Probleme avec la base de donnée
     *
     * @param event trigger par l'action de l'utilisateur
     */
    @FXML
    public void linkToDiscussion(ActionEvent event) {
        Scene scene = SceneFactory.getScene(SceneContext.DISCUSSION_LAYOUT_DISCUSSION_NAME);
        Stage stage = SceneFactory.getEventStage(event);
        if (scene.getUserData() instanceof DiscussionController controller) {
            try {
                Discussion discussion = discussionService.findOrCreateDiscussion(friend.getCompte1(), friend.getCompte2());
                controller.addDiscussion(discussion);
                stage.setScene(scene);
                stage.show();
            } catch (FriendException ex) {
                logger.warn(ex.getMessage());
                showErrorLabel(ex.getMessage());
                event.consume();
            } catch (DataAccessException ex) {
                logger.warn(ex.getMessage());
                showErrorLabel("La génération de la discussion n'a pas réussie");
                event.consume();
            }
        } else {
            showErrorLabel("Une erreur est survenue, veuillez redémarrer l'application");
        }
    }

    /**
     * Affichage du message d'erreur
     * @param message contenu du message d'erreur à afficher
     */
    private void showErrorLabel(String message) {
        errorMessage.setVisible(true);
        errorMessage.setManaged(true);
        errorMessage.setText(message);
    }
}
