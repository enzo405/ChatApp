package fr.sio.chat.app.controllers.components;

import fr.sio.chat.app.controllers.Controller;
import fr.sio.chat.app.controllers.DiscussionController;
import fr.sio.chat.app.models.Discussion;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class DiscussionSidebarController extends Controller implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(DiscussionSidebarController.class);
    private final Discussion discussion;
    private final DiscussionController discussionController;
    private boolean isActive = false;
    @FXML
    private Button root;
    @FXML
    private HBox discussionContainer;
    @FXML
    private ImageView imageView;
    @FXML
    private VBox contentContainer;
    @FXML
    private Label nameLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label notifLabel;

    public DiscussionSidebarController(Discussion discussion, DiscussionController discussionController) {
        this.discussion = discussion;
        this.discussionController = discussionController;
    }

    /**
     * On initialise la discussion avec l'image et le nom
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Button Action Initialisation
        root.onMouseClickedProperty().set(this::linkToDiscussion);

        // Setting properties and styles
        root.setMaxWidth(Double.MAX_VALUE);

        // Load image
        imageView.setImage(new Image(getClass().getClassLoader().getResourceAsStream(discussion.getDiscussionType().getIcon())));
        nameLabel.setText(discussion.getName());

        updateStyle();
    }

    /**
     * Bouton qui fait un lien vers la discussion
     * @param event
     */
    @FXML
    public void linkToDiscussion(Event event) {
        this.isActive = true;
        discussionController.setChatboxAndLoadSidebar(discussion);

        notifLabel.setText("0");
        notifLabel.setVisible(false);
    }

    /**
     * Affichage le composant
     * @param show
     */
    public void toggleShow(boolean show) {
        root.setManaged(show);
        root.setVisible(show);
    }

    /**
     * Actualisation du style
     */
    public void updateStyle() {
        if (isActive) {
            discussionContainer.setStyle("-fx-background-color: #5d6067; -fx-background-radius: 8;");
        } else {
            discussionContainer.setStyle("-fx-background-color: #323338; -fx-background-radius: 8;");
        }
    }

    /**
     * Vérification de s'il est actif ou non
     * @return l'état de la discussionSidebar
     */
    public boolean isActive() { return this.isActive; }

    /**
     * On est set s'il est actif ou non
     * @param isActive
     */
    public void setActive(boolean isActive) { this.isActive = isActive; }

    /**
     * On récupère la discussion
     * @return retourne la discussion associée au controller
     */
    public Discussion getDiscussion() { return this.discussion; }

    /**
     * Supprime l'element de l'interface
     */
    public void deleteUI() {
        VBox parent = (VBox) root.getParent();
        if (parent != null) {
            parent.getChildren().remove(root);
        } else {
            logger.warn("Le parent de DiscussionSidebarController est null!");
        }
    }

    /**
     * Incrémente la notification et l'affiche
     * Valeur maximum 9
     */
    public void addNotification() {
        int oldValue = Integer.parseInt(notifLabel.getText());
        String newValue;
        if (oldValue >= 9) {
            newValue = "9+";
        } else {
            newValue = Integer.toString(oldValue + 1);
        }
        notifLabel.setText(newValue);
        notifLabel.setVisible(true);
    }
}
