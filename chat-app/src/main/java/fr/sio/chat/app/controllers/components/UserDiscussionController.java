package fr.sio.chat.app.controllers.components;

import fr.sio.chat.app.controllers.Controller;
import fr.sio.chat.app.models.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class UserDiscussionController extends Controller implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(UserDiscussionController.class);
    private final User user;
    @FXML
    private HBox root;
    @FXML
    private Label username;
    @FXML
    private ImageView profilePicture;
    @FXML
    private CheckBox checkBox;

    public UserDiscussionController(User user) {
        this.user = user;
    }

    /**
     * Initialisation du controller, de l'interface et des actions
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Button Action Initialisation
        root.onMouseClickedProperty().set(event -> checkBox.setSelected(!isSelected()));

        username.setText(user.getPseudo());
        if (user.getProfilePicture() != null) {
            profilePicture.setImage(new Image(new ByteArrayInputStream(user.getProfilePicture())));
        }
    }

    /**
     * Getter pour récupérer l'état de la checkBox
     * @return boolean etat de la checkbox
     */
    public boolean isSelected() {
        return checkBox.isSelected();
    }

    /**
     * Récupère l'utilisateur du controller
     * @return User lié au controller
     */
    public User getUser() { return user; }
}
