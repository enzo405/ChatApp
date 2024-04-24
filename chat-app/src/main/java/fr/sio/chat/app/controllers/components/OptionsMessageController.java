package fr.sio.chat.app.controllers.components;

import fr.sio.chat.app.controllers.Controller;
import fr.sio.chat.app.controllers.SceneContext;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.models.Message;
import fr.sio.chat.app.security.SecurityContext;
import fr.sio.chat.app.services.interfaces.IMessageService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class OptionsMessageController extends Controller implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(OptionsMessageController.class);
    private final IMessageService messageService;
    private final Message message;
    private final MessageController messageController;

    @FXML
    private HBox root;
    @FXML
    private Button copyMessageButton;
    @FXML
    private Button deleteMessageButton;
    @FXML
    private Button pinMessageButton;
    @FXML
    private ImageView pinMesageButtonImageView;
    @FXML
    private Button copyLinkButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label errorMessage;

    public OptionsMessageController(IMessageService messageService , MessageController messageController) {
        this.message = messageController.getMessage();
        this.messageController = messageController;
        this.messageService = messageService;
    }

    /**
     * Initialisation des éléments de OptionsMessageController
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // hide buttons if not owner
        if (message.getCompte().getId() == SecurityContext.getUser().getId()) {
            deleteMessageButton.setManaged(true);
            deleteMessageButton.setVisible(true);
        }
        updatePinButton();
    }

    /**
     * Bouton de copie du message dans le presse-papier
     * @param event trigger par l'action de l'utilisateur
     */
    @FXML
    public void copyMessage(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(message.getContent());
        clipboard.setContent(content);
        toggleDisplay();
    }

    /**
     * Bouton de suppression du message
     * @param event trigger par l'action de l'utilisateur
     */
    @FXML
    public void deleteMessage(ActionEvent event) {
        try {
            messageController.deleteMessage();
        } catch (DataAccessException ignore) {
            // Pas besoin d'afficher un message d'erreur, le message ne sera pas supprimé si l'ordre d'éxécution des méthodes reste
        }
    }

    /**
     * Bouton d'épinglage du message
     * @param event trigger par l'action de l'utilisateur
     */
    @FXML
    public void pinMessage(ActionEvent event) {
        if (message.getIsPinned()) { // si le message est déjà épinglé
            message.setIsPinned(false);
            messageController.getChatController().removePinnedMessageUI(message);
        } else {
            message.setIsPinned(true);
            messageController.getChatController().addPinnedMessageUI(message);
        }
        try {
            messageService.update(message);
        } catch (DataAccessException e) {
            showErrorMessage(true, "Une erreur est survenue");
            event.consume();
            return;
        }
        toggleDisplay();
    }

    /**
     * Copie l'id du message
     * @param event trigger par l'action de l'utilisateur
     */
    @FXML
    public void copyLink(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(String.valueOf(message.getId()));
        clipboard.setContent(content);
        toggleDisplay();
    }

    /**
     * Ferme la popup des options messages
     * @param event trigger par l'action de l'utilisateur
     */
    @FXML
    public void closePopup(ActionEvent event) {
        root.setVisible(false);
    }

    /**
     * Bouton toggle qui affiche ou cache les options
     * Mets à jour l'image des messages épinglés quand on affiche le popup
     */
    public void toggleDisplay() {
        // On met à jour l'image pinMessage seulement lors de l'affichage des boutons
        if (!root.isVisible()) {
            updatePinButton(); // on le met ici tant qu'on n'a pas d'event websocket pour les messages épinglés
        }
        root.setVisible(!root.isVisible());
    }

    /**
     * Affiche l'erreur
     * @param show affiche ou non
     * @param content contenu du message d'erreur
     */
    private void showErrorMessage(boolean show, String content) {
        errorMessage.setVisible(show);
        errorMessage.setManaged(show);
        if (show) {
            errorMessage.setText(content);
        }
    }

    /**
     * Mets à jour l'image d'épinglement du message
     * (plus tard sera probablement trigger par le websocket)
     */
    private void updatePinButton() {
        if (message.getIsPinned()) {
            Image unPinImage = new Image(SceneContext.UNPIN_MESSAGE_IMAGE);
            pinMesageButtonImageView.setImage(unPinImage);
        } else {
            Image pinImage = new Image(SceneContext.PIN_MESSAGE_IMAGE);
            pinMesageButtonImageView.setImage(pinImage);
        }
    }
}
