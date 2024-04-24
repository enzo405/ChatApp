package fr.sio.chat.app.controllers;

import fr.sio.chat.app.App;
import fr.sio.chat.app.SceneFactory;
import fr.sio.chat.app.controllers.components.FriendRequestController;
import fr.sio.chat.app.controllers.components.UserFriendController;
import fr.sio.chat.app.events.dispatchers.FriendRequestDispatcher;
import fr.sio.chat.app.events.interfaces.IEvent;
import fr.sio.chat.app.events.interfaces.IEventListener;
import fr.sio.chat.app.events.models.FriendRequestEvent;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.FriendException;
import fr.sio.chat.app.exceptions.NotFoundException;
import fr.sio.chat.app.models.Friend;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.security.SecurityContext;
import fr.sio.chat.app.ServiceFactory;
import fr.sio.chat.app.services.interfaces.*;
import fr.sio.chat.app.websocket.WebSocketClient;
import fr.sio.chat.app.websocket.requests.RequestFriendRequest;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;
import java.util.*;

public class HomeController extends Controller implements Initializable, IEventListener {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final WebSocketClient webSocketClient = WebSocketClient.getInstance();
    private final IFriendService friendService = ServiceFactory.getInstance().getFriendServiceInstance();
    private final IUserService userService = ServiceFactory.getInstance().getUserServiceInstance();
    private final IDiscussionService discussionService = ServiceFactory.getInstance().getDiscussionServiceInstance();
    private final int NUM_COLUMN_FRIENDS = (int) Math.floor(App.screen.getVisualBounds().getWidth() / 250) - 1; // 250 = taille de la userFriendBox
    private final User sessionUser = SecurityContext.getUser();

    // region FXML
    @FXML
    private GridPane gridPaneFriends;
    @FXML
    private TextField textFieldAddFriends;
    @FXML
    private VBox vBoxUnreadMessages;
    @FXML
    private VBox vBoxFriendRequests;
    @FXML
    private Button goToFriends;
    @FXML
    private Button goToUnreadMessages;
    @FXML
    private Button goToFriendRequests;
    @FXML
    private HBox titleFriendRequest;
    @FXML
    private Label resultLabel;
    @FXML
    private SidebarController sidebarTemplateController;
    // endregion FXML

    /**
     * Initialise l'interface du controller
     * Fait les requêtes pour récupérer les amis et les demande d'amis de l'utilisateur connecté
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sidebarTemplateController.toggleHomeImage();

        addListeners();

        updateFriendBoard();
        updateFriendRequestsBoard();
        showUnreadMessages();
    }

    @Override
    public void handle(IEvent event) {
        if (event instanceof FriendRequestEvent) {
            Platform.runLater(() -> {
                updateFriendBoard();
                updateFriendRequestsBoard();
            });
        }
    }

    @Override
    public void addListeners() {
        FriendRequestDispatcher.getInstance().addEventListener(this);
    }

    @Override
    public void removeListeners() {
        FriendRequestDispatcher.getInstance().removeEventListener(this);
    }

    /**
     * Bouton d'ajout d'ami
     * Envoie la demande d'amis dans le websocket
     * @param event trigger par l'action de l'utilisateur
     */
    @FXML
    public void addFriend(ActionEvent event) {
        String pseudoValue = textFieldAddFriends.getText();

        try {
            User target = userService.getUserByPseudo(pseudoValue);

            int idRelation = friendService.insert(target);

            Friend friend = new Friend(idRelation, false, sessionUser, target);
            RequestFriendRequest websocketRequest = new RequestFriendRequest(friend);
            webSocketClient.sendRequest(websocketRequest);

            showResult(event, "La demande d'amis a été envoyé");
        } catch (FriendException ex) {
            showResult(event, ex.getMessage());
        } catch (DataAccessException ex) {
            showResult(event, "Une erreur est survenue lors de l'envoie de votre demande d'amis");
        } catch (NotFoundException e) {
            showResult(event, "Le pseudo recherché n'existe pas");
        }

        textFieldAddFriends.setText(""); // Clear le texte
    }

    /**
     * Actualisation des amis dans l'interface
     */
    public void updateFriendBoard() {
        List<Friend> friends = friendService.getFriendsByUser(SecurityContext.getUser());

        gridPaneFriends.getChildren().clear();
        gridPaneFriends.getColumnConstraints().clear(); // Supprimer les anciennes contraintes de colonnes
        gridPaneFriends.getRowConstraints().clear(); // Supprimer les anciennes contraintes de lignes

        if (friends.isEmpty()) {
            Label message = new Label("Vous n'avez pas encore d'ami");
            message.setTextFill(Color.WHITE);
            gridPaneFriends.add(message, 0, 0);
            return;
        }

        int rowCount = (int) Math.ceil((double) friends.size() / NUM_COLUMN_FRIENDS); // Calcul du nombre de collone par rapport au nombre d'amis

        // Ajouter les nouvelles contraintes de colonnes en dehors de la boucle
        for (int col = 0; col < NUM_COLUMN_FRIENDS; col++) {
            gridPaneFriends.getColumnConstraints().add(new ColumnConstraints(250));
        }
        // Ajouter les contraintes de lignes en dehors de la boucle
        for (int row = 0; row < rowCount; row++) {
            gridPaneFriends.getRowConstraints().add(new RowConstraints(32));
        }
        // Ajouter les amis dans le GridPane
        for (int col = 0; col < NUM_COLUMN_FRIENDS; col++) {
            for (int row = 0; row < rowCount; row++) {
                int index = row * NUM_COLUMN_FRIENDS + col;
                if (index < friends.size()) {
                    UserFriendController userFriendController = new UserFriendController(friends.get(index), discussionService, friendService, this);
                    VBox userFriendBox = (VBox) SceneFactory.loadSceneWithController(SceneContext.FRIEND_BOX_HOME_NAME, userFriendController);
                    gridPaneFriends.add(userFriendBox, col, row);
                } else {
                    break;
                }
            }
        }
    }

    /**
     * Actualisation des demandes d'ami dans l'interface
     */
    public void updateFriendRequestsBoard() {
        List<Friend> friendRequests = friendService.getFriendRequestsByUser(SecurityContext.getUser());

        vBoxFriendRequests.getChildren().clear();
        vBoxFriendRequests.getChildren().add(titleFriendRequest);

        if (friendRequests.isEmpty()) {
            Label message = new Label("Vous n'avez aucune demande d'ami");
            message.setTextFill(Color.WHITE);
            vBoxFriendRequests.getChildren().add(message);
            return;
        }

        int rowCount = friendRequests.size(); // Calcul du nombre de lignes nécessaires

        for (int row = 0; row < rowCount; row++) {
            if (row < friendRequests.size()) {
                FriendRequestController friendRequestController = new FriendRequestController(friendRequests.get(row), this, friendService);
                VBox friendRequest = (VBox) SceneFactory.loadSceneWithController(SceneContext.FRIEND_REQUEST_HOME_NAME, friendRequestController);
                vBoxFriendRequests.getChildren().add(friendRequest);
            } else {
                break;
            }
        }
    }

    private void showUnreadMessages() {
        // TODO ajouter l'instance de homeController dans le listener de newMessage
        // TODO enregistrer le message dans le cache en locale puis l'affiché
    }

    /**
     * Affichage des messages d'erreur
     * Consomme l'évent
     * @param actionEvent event à consommer
     * @param errorMessage message à afficher
     */
    private void showResult(ActionEvent actionEvent, String errorMessage) {
        resultLabel.setText(errorMessage);
        resultLabel.setTextFill(Color.WHITESMOKE);
        resultLabel.setVisible(true);
        actionEvent.consume();
    }
}