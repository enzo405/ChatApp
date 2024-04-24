package fr.sio.chat.app.controllers.components;

import fr.sio.chat.app.SceneFactory;
import fr.sio.chat.app.controllers.Controller;
import fr.sio.chat.app.controllers.DiscussionController;
import fr.sio.chat.app.controllers.SceneContext;
import fr.sio.chat.app.events.dispatchers.FriendRequestDispatcher;
import fr.sio.chat.app.events.interfaces.IEvent;
import fr.sio.chat.app.events.interfaces.IEventListener;
import fr.sio.chat.app.events.models.FriendRequestEvent;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.*;
import fr.sio.chat.app.security.SecurityContext;
import fr.sio.chat.app.services.interfaces.*;
import fr.sio.chat.app.websocket.WebSocketClient;
import fr.sio.chat.app.websocket.requests.RequestFriendRequest;
import fr.sio.chat.app.websocket.requests.RequestMemberAddDiscussion;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class CreateDiscussionController extends Controller implements Initializable, IEventListener {
    private static final Logger logger = LoggerFactory.getLogger(CreateDiscussionController.class);
    private final WebSocketClient webSocketClient = WebSocketClient.getInstance();
    private final IDiscussionService discussionService;
    private final IDiscussionTypeService discussionTypeService;
    private final IUserDiscussionService userDiscussionService;
    private final List<User> friends;
    private final DiscussionController discussionController;
    private final List<UserDiscussionController> userDiscussionControllers = new ArrayList<>();
    @FXML
    private AnchorPane root;
    @FXML
    private VBox userChoice;
    @FXML
    private TextField discussionName;
    @FXML
    private ChoiceBox choiceBoxTypeDiscussion;
    @FXML
    private Button createDiscussionButton;
    @FXML
    private Button closeWindow;
    @FXML
    private Label errorMessage;
    @FXML
    private Label errorMessageLoading;

    public CreateDiscussionController(List<User> users, IDiscussionService discussionService, IDiscussionTypeService discussionTypeService, IUserDiscussionService userDiscussionService, DiscussionController discussionController) {
        this.discussionService = discussionService;
        this.userDiscussionService = userDiscussionService;
        this.discussionTypeService = discussionTypeService;
        this.discussionController = discussionController;
        this.friends = users;
    }

    /**
     * Affiche la popup de création de discussion et initialisation des différents éléments
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Button Action Initialisation
        createDiscussionButton.setOnAction(this::createDiscussionPrompt);
        closeWindow.setOnAction(this::closePopup);

        addListeners();

        try {
            fillChoiceBox();
        } catch (DataAccessException ex) {
            errorMessageLoading.setVisible(true);
            errorMessageLoading.setManaged(true);
            errorMessageLoading.setText("Une erreur est survenue lors du chargement");
        }

        if (friends.isEmpty()) {
            userChoice.getChildren().add(new Label("Vous n'avez pas encore d'amis"));
        } else {
            fillUserList();
        }
    }

    /**
     * Handle les events du websocket lors de la réception de demande d'ami
     * @param event trigger par le websocket
     */
    @Override
    public void handle(IEvent event) {
        if (event instanceof FriendRequestEvent) {
            RequestFriendRequest request = ((FriendRequestEvent) event).getRequest();
            boolean containsRequester = friends.contains(request.getRequester());
            boolean containsTarget = friends.contains(request.getTarget());
            if (containsRequester && !request.getIsAccepted()) {
                friends.remove(request.getRequester());
            } else if (!containsTarget && request.getIsAccepted()) {
                friends.add(request.getTarget());
            }
            Platform.runLater(this::fillUserList);
        }
    }

    /**
     * Ajoute les Listeners au Dispatcher (events)
     */
    @Override
    public void addListeners() {
        FriendRequestDispatcher.getInstance().addEventListener(this);
    }

    /**
     * Retire les Listeners du Dispatcher (events)
     */
    @Override
    public void removeListeners() {
        FriendRequestDispatcher.getInstance().removeEventListener(this);
    }

    /**
     * Bouton qui récupère les éléments du formulaire
     * @param event ActionEvent
     */
    @FXML
    private void createDiscussionPrompt(ActionEvent event) {
        // Validation du formulaire
        if (Objects.equals(discussionName.getText(), "") || discussionName.getText() == null) {
            showErrorMessage("Veuillez nommer la discussion");
            event.consume();
            return;
        }

        // La choiceBox contient les Models
        DiscussionType selectedDiscussionType = (DiscussionType) choiceBoxTypeDiscussion.getSelectionModel().getSelectedItem();
        if (selectedDiscussionType == null) {
            showErrorMessage("Vous devez sélectionner un type de discussion");
            event.consume();
            return;
        }
        List<User> selectedUser = getSelectedUser();
        if (selectedUser.isEmpty()) {
            showErrorMessage("Vous devez ajouter au moins un amis");
            event.consume();
            return;
        }
        if (!selectedDiscussionType.getIsGroup() && selectedUser.size() > 1) {
            showErrorMessage("Une discussion privée ne peut avoir que 2 membres.");
            event.consume();
            return;
        }

        try {
            createDiscussion(discussionName.getText(), selectedUser, selectedDiscussionType);
        } catch (DataAccessException ex) {
            logger.error(ex.getMessage(), ex);
            showErrorMessage("Une erreur s'est produite pendant la création de la discussion.");
            event.consume();
        }
    }

    /**
     * Affichage du message d'erreur
     * @param msg message d'erreur à afficher
     */
    private void showErrorMessage(String msg) {
        errorMessage.setVisible(true);
        errorMessage.setManaged(true); // Ajoute le nœud dans le flux
        errorMessage.setText(msg);
    }

    /**
     * Création de la discussion dans la BDD + ajout de celle-ci dans la liste des discussions
     * @param discussionName nom de la discussion
     * @param selectedUser utilisateur à ajouter
     * @param selectedDiscussionType type de discussion
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    private void createDiscussion(String discussionName, List<User> selectedUser, DiscussionType selectedDiscussionType) throws DataAccessException {
        Discussion discussion = new Discussion();
        discussion.setName(discussionName);
        discussion.setDiscussionType(selectedDiscussionType);

        int idDiscussion = discussionService.insert(discussion);
        discussion.setId(idDiscussion);

        List<UserDiscussion> userDiscussions = new ArrayList<>();
        for (User user : selectedUser) {
            userDiscussions.add(new UserDiscussion(discussion, user, false));
        }
        userDiscussions.add(new UserDiscussion(discussion,SecurityContext.getUser(), true)); // Ajoute le créateur de la discussion
        userDiscussionService.insert(userDiscussions); // Ajoute tous les utilisateurs selectionnés, dans la discussion (en owner false par défault)

        // Envoie la requete d'ajout de membres aux membres sélectionnés
        for (User user : selectedUser) {
            List<Integer> receiver = new ArrayList<>();
            receiver.add(user.getId());
            RequestMemberAddDiscussion websocketRequest = new RequestMemberAddDiscussion(user, discussion, receiver);
            webSocketClient.sendRequest(websocketRequest);
        }

        // Mets à jour la liste des discussions du controller
        discussionController.addDiscussion(discussion);
        discussionController.setChatboxAndLoadSidebar(discussion);
        closeWindow.fire();
    }

    /**
     * Récupération des utilisateurs lors du choix des amis à ajouter dans la discussion
     * @return Liste d'utilisateurs sélectionnés
     */
    private List<User> getSelectedUser() {
        List<User> users = new ArrayList<>();
        for (UserDiscussionController userDiscussionController : userDiscussionControllers) {
            if (userDiscussionController.isSelected()) {
                users.add(userDiscussionController.getUser());
                logger.info("getSelectedUser: " + userDiscussionController.getUser().getPseudo() + userDiscussionController.getUser().getId());
            }
        }
        return users;
    }

    /**
     * Fermeture du popup
     */
    private void closePopup(ActionEvent event) {
        root.setVisible(false);
        root.setManaged(false);
        discussionController.setCreationDiscussionPopupActive(false);
    }

    /**
     * Remplissage de la liste des utilisateurs
     */
    private void fillUserList() {
        userChoice.getChildren().clear();
        logger.warn("actualisation");
        for (User friend : friends) {
            // Instanciation du Controller
            UserDiscussionController userDiscussionController = new UserDiscussionController(friend);
            // Création du fxml à l'aide du Controller et du FXMLLoader.load()
            HBox userDiscussionContainer = (HBox) SceneFactory.loadSceneWithController(SceneContext.CHOICE_USER_DISCUSSION_NAME, userDiscussionController);
            userDiscussionControllers.add(userDiscussionController);
            userChoice.getChildren().add(userDiscussionContainer);
        }
    }

    /**
     * Remplissage de la choiceBox permettant de sélectionner le type de discussion
     * @throws DataAccessException erreur de communication avec la base de donnée
     */
    private void fillChoiceBox() throws DataAccessException {
        List<DiscussionType> discussionTypes = discussionTypeService.getAllDiscussionType();

        ObservableList<Object> listDiscussionTypes;
        listDiscussionTypes = FXCollections.observableArrayList(discussionTypes);
        choiceBoxTypeDiscussion.setItems(listDiscussionTypes);
    }
}
