package fr.sio.chat.app.controllers.components;

import fr.sio.chat.app.controllers.Controller;
import fr.sio.chat.app.controllers.HomeController;
import fr.sio.chat.app.controllers.SceneContext;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Friend;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.services.interfaces.IFriendService;
import fr.sio.chat.app.websocket.WebSocketClient;
import fr.sio.chat.app.websocket.requests.RequestFriendRequest;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class FriendRequestController extends Controller implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(FriendRequestController.class);
    private final WebSocketClient webSocketClient = WebSocketClient.getInstance();
    private final HomeController homeController;
    private final IFriendService friendService;
    private final Friend friend;

    @FXML
    private Label errorMessageLabel;
    @FXML
    private ImageView profilePictureFriend;
    @FXML
    private Label nameLabel;
    @FXML
    private Button acceptButton;
    @FXML
    private Button declineButton;

    public FriendRequestController(Friend friend, HomeController homeController, IFriendService friendService) {
        this.friend = friend;
        this.homeController = homeController;
        this.friendService = friendService;
    }

    /**
     * On initialise la demande d'ami avec la photo de profil et le nom
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Button Action Initialisation
        acceptButton.setOnAction(this::acceptFriendRequest);
        declineButton.setOnAction(this::declineFriendRequest);

        User user = friend.getCompte2();
        nameLabel.setText(user.getPseudo());
        Image image;
        if (user.getProfilePicture() != null) {
            image = new Image(new ByteArrayInputStream(user.getProfilePicture()));
        } else {
            image = new Image(SceneContext.DEFAULT_AVATAR_IMAGE);
        }
        profilePictureFriend.setImage(image);
    }

    /**
     * On refuse la demande d'ami
     */
    @FXML
    private void declineFriendRequest(ActionEvent event) {
        try {
            friend.setEstAccepte(false);
            friendService.delete(friend);

            homeController.updateFriendRequestsBoard();
        } catch (DataAccessException e) {
            showError(true, "Un problème est survenue lors de la modification de la demande");
            event.consume();
        }
    }

    /**
     * On accepte la demande d'ami
     * On envoie la requête au websocket
     */
    @FXML
    private void acceptFriendRequest(ActionEvent event) {
        try {
            friend.setEstAccepte(true);
            friendService.update(friend);

            homeController.updateFriendRequestsBoard();
            homeController.updateFriendBoard();

            RequestFriendRequest websocketRequest = new RequestFriendRequest(friend);
            webSocketClient.sendRequest(websocketRequest);
        } catch (DataAccessException e) {
            showError(true, "Un problème est survenue lors de la modification de la demande");
            event.consume();
        }
    }

    /**
     * Affiche l'erreur
     * @param display affiche ou non
     * @param content contenu du message d'erreur à afficher
     */
    private void showError(boolean display, String content) {
        errorMessageLabel.setVisible(display);
        errorMessageLabel.setManaged(display);
        if (display) {
            errorMessageLabel.setText(content);
        }
    }
}
