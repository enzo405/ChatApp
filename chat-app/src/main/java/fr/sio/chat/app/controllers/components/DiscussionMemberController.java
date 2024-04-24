package fr.sio.chat.app.controllers.components;

import fr.sio.chat.app.controllers.ChatController;
import fr.sio.chat.app.controllers.Controller;
import fr.sio.chat.app.controllers.DiscussionController;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.FriendException;
import fr.sio.chat.app.exceptions.NotAllowedException;
import fr.sio.chat.app.models.Discussion;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.models.UserDiscussion;
import fr.sio.chat.app.security.SecurityContext;
import fr.sio.chat.app.services.interfaces.IDiscussionService;
import fr.sio.chat.app.services.interfaces.IUserDiscussionService;
import fr.sio.chat.app.websocket.WebSocketClient;
import fr.sio.chat.app.websocket.requests.RequestMemberRemoveDiscussion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class DiscussionMemberController extends Controller implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(DiscussionMemberController.class);
    private final WebSocketClient webSocketClient = WebSocketClient.getInstance();
    private final IDiscussionService discussionService;
    private final IUserDiscussionService userDiscussionService;
    private final ChatController chatController;
    private final UserDiscussion member;
    private final User sessionUser = SecurityContext.getUser();

    @FXML
    private VBox root;
    @FXML
    private Label errorMessage;
    @FXML
    private Label labelUsername;
    @FXML
    private ImageView pictureProfile;
    @FXML
    private Button linkToDiscussionBtn;
    @FXML
    private Button deleteButton;

    public DiscussionMemberController(UserDiscussion member, ChatController chatController, IDiscussionService discussionService, IUserDiscussionService userDiscussionService) {
        this.discussionService = discussionService;
        this.userDiscussionService = userDiscussionService;
        this.chatController = chatController;
        this.member = member;
    }

    /**
     * On initialise chaque élément du composant
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Button Action Initialisation
        linkToDiscussionBtn.setOnAction(this::linkToDiscussion);
        deleteButton.setOnAction(this::removeUser);

        labelUsername.setText(member.getUser().getPseudo());
        byte[] userProfilPicture = member.getUser().getProfilePicture();

        if (userProfilPicture != null) {
            pictureProfile.setImage(new Image(new ByteArrayInputStream(userProfilPicture)));
        }

        loadButtonsUI();
    }

    /**
     * Bouton qui supprime l'utilisateur de la discussion
     * Envoie la requête au websocket
     * Supprime l'élément root de l'interface utilisateur
     * @param event Représente l'event de l'action de l'utilisateur
     */
    @FXML
    public void removeUser(ActionEvent event) {
        try {
            userDiscussionService.delete(member);

            // Envoyer la requête au serveur websocket
            RequestMemberRemoveDiscussion websocketRequest = new RequestMemberRemoveDiscussion(member.getUser(), member.getDiscussion(), chatController.getDiscussionReceivers());
            webSocketClient.sendRequest(websocketRequest);

            // Mets à jour la liste des receivers, du nombre de membres de la discussion
            chatController.removeUserFromDiscussion(member);

            if (sessionUser.getId() == member.getUser().getId()) {
                chatController.getDiscussionController().deleteDiscussionFromSidebar(member.getDiscussion());
            }

            removeRootUI();
        } catch (NotAllowedException e) {
            showErrorMessage(e.getMessage());
            event.consume();
        } catch (DataAccessException e) {
            showErrorMessage("Une erreur est survenue");
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
     * @param event Représente l'event de l'action de l'utilisateur
     */
    @FXML
    public void linkToDiscussion(ActionEvent event) {
        if (sessionUser.getId() != member.getUser().getId()) {
            DiscussionController discussionController = chatController.getDiscussionController();
            try {
                Discussion discussion = discussionService.findOrCreateDiscussion(sessionUser, member.getUser());

                discussionController.addDiscussion(discussion);
                discussionController.setChatboxAndLoadSidebar(discussion);
            } catch (FriendException e) {
                logger.warn(e.getMessage());
                showErrorMessage(e.getMessage());
            } catch (DataAccessException e) {
                logger.warn(e.getMessage());
                showErrorMessage("Une erreur est survenue lors de l'ouverture de la discussion");
            }
        }
    }

    /**
     * On enlève le popup de l'interface
     */
    private void removeRootUI() {
        VBox parent = (VBox) root.getParent();
        if (parent != null) {
            parent.getChildren().remove(root);
        } else {
            logger.warn("Le parent de DiscussionMemberController est null!");
        }
    }

    /**
     * Chargement des boutons en fonction du role du membre et de l'utilisateur connecté
     */
    private void loadButtonsUI() {
        boolean isSessionUserOwner = isSessionUserOwner();
        if (sessionUser.getId() == member.getUser().getId() && !isSessionUserOwner) {
            // Quitter une conversation s'il n'est pas owner
            showButton(deleteButton);
        }
        if (isSessionUserOwner && !member.isOwner()) {
            // Retirer un membre de la discussion s'il n'est pas owner
            showButton(deleteButton);
        }
        if (sessionUser.getId() != member.getUser().getId()) {
            // Lancer une discussion avec un membre du groupe
            showButton(linkToDiscussionBtn);
        }
    }

    /**
     * Affichage du bouton
     * @param button bouton à afficher
     */
    private void showButton(Button button) {
        button.setManaged(true);
        button.setVisible(true);
    }

    /**
     * Affichage du message d'erreur
     * @param message contenu du message
     */
    private void showErrorMessage(String message) {
        errorMessage.setText(message);
        errorMessage.setVisible(true);
        errorMessage.setManaged(true);
    }

    /**
     * Méthode pour savoir si l'utilisateur connecté est owner ou pas
     * @return true si l'utilisateur connecté est owner. Sinon false
     */
    private boolean isSessionUserOwner() {
        return chatController.getDiscussionOwners().get(sessionUser.getId()) != null;
    }
}
