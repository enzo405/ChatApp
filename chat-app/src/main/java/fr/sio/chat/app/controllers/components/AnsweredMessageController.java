package fr.sio.chat.app.controllers.components;

import fr.sio.chat.app.controllers.Controller;
import fr.sio.chat.app.events.dispatchers.MessageDeletedDispatcher;
import fr.sio.chat.app.events.dispatchers.MessageEditedDispatcher;
import fr.sio.chat.app.events.interfaces.IEvent;
import fr.sio.chat.app.events.interfaces.IEventListener;
import fr.sio.chat.app.events.models.MessageDeletedEvent;
import fr.sio.chat.app.events.models.MessageEditedEvent;
import fr.sio.chat.app.models.Message;
import fr.sio.chat.app.websocket.requests.RequestMessageDelete;
import fr.sio.chat.app.websocket.requests.RequestMessageEdit;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class AnsweredMessageController extends Controller implements Initializable, IEventListener {
    private static final Logger logger = LoggerFactory.getLogger(AnsweredMessageController.class);
    private final Message answeredMessage;
    @FXML
    private HBox root;
    @FXML
    private Label answeredMessageLabel;

    public AnsweredMessageController(Message answeredMessage) {
        this.answeredMessage = answeredMessage;
    }

    /**
     * Affiche le message répondu lors de l'initialisation
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        answeredMessageLabel.setText(answeredMessage.getContent());
    }

    /**
     * Reception des events du websocket pour la modification de message et suppression de message
     * @param event Event reçu du websocket
     */
    @Override
    public void handle(IEvent event) {
        if (event instanceof MessageEditedEvent) {
            RequestMessageEdit request = ((MessageEditedEvent) event).getRequest();
            if (request.getMessage().getId() == answeredMessage.getId()) {
                Platform.runLater(
                    () -> editMessageUI(request.getMessage().getContent())
                );
            }
        } else if (event instanceof MessageDeletedEvent) {
            RequestMessageDelete request = ((MessageDeletedEvent) event).getRequest();
            if (request.getMessage().getId() == answeredMessage.getId()) {
                Platform.runLater(this::deleteMessageUI);
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
     * Retire les Listeners du Dispatcher (events)
     */
    @Override
    public void removeListeners() {
        MessageEditedDispatcher.getInstance().removeEventListener(this);
        MessageDeletedDispatcher.getInstance().removeEventListener(this);
    }

    /**
     * Modifie le texte pour afficher le message modifié
     * @param content contenu du message
     */
    private void editMessageUI(String content) {
        answeredMessage.setContent(content);
        answeredMessageLabel.setText(content);
    }

    /**
     * Supprime le message de l'interface
     * Enlève cette instance des listeners du websocket
     */
    private void deleteMessageUI() {
        removeListeners();
        VBox parent = (VBox) root.getParent();
        if (parent != null) {
            parent.getChildren().remove(root);
        } else {
            logger.warn("Le parent de MessageController est null!");
        }
    }
}
