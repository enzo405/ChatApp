package fr.sio.chat.app.controllers;

import fr.sio.chat.app.SceneFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class SidebarController extends Controller implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(SidebarController.class);

    // region FXML
    @FXML
    private ImageView imageHome;
    @FXML
    private ImageView imageMessages;
    @FXML
    private ImageView imageParameters;
    @FXML
    private Button switchToHomeBtn;
    @FXML
    private Button switchToDiscussionBtn;
    @FXML
    private Button switchToSettingsBtn;
    // endregion FXML

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    /**
     * Bouton permettant de passer à la scène Home
     * @param actionEvent trigger par l'action de l'utilisateur
     */
    @FXML
    public void switchToHome(ActionEvent actionEvent) {
        SceneFactory.switchScene(SceneContext.PAGE_HOME_NAME, actionEvent);
    }

    /**
     * Bouton permettant de passer à la scène des discussions
     * @param actionEvent trigger par l'action de l'utilisateur
     */
    @FXML
    public void switchToDiscussion(ActionEvent actionEvent) {
        SceneFactory.switchScene(SceneContext.DISCUSSION_LAYOUT_DISCUSSION_NAME, actionEvent);
    }

    /**
     * Bouton permettant de passer à la scène des messages
     * @param actionEvent trigger par l'action de l'utilisateur
     */
    @FXML
    public void switchToSettings(ActionEvent actionEvent) {
        SceneFactory.switchScene(SceneContext.SETTINGS_LAYOUTS_NAME, actionEvent);
    }

    /**
     * Affichage du logo actif de la page settings
     */
    public void toggleSettingImage() {
        Image image = new Image(getClass().getResource(SceneContext.SETTING_IMAGE).toString());
        imageParameters.setImage(image);
        switchToSettingsBtn.setStyle(switchToSettingsBtn.getStyle() + "-fx-text-fill: #5d928e");
    }

    /**
     * Affichage du logo actif de la page d'accuel
     */
    public void toggleHomeImage() {
        Image image = new Image(getClass().getResource(SceneContext.HOME_IMAGE).toString());
        imageHome.setImage(image);
        switchToHomeBtn.setStyle(switchToHomeBtn.getStyle() + "-fx-text-fill: #5d928e");
    }

    /**
     * Affichage du logo aftif de la page de discussion
     */
    public void toggleDiscussionsImage() {
        Image image = new Image(getClass().getResource(SceneContext.DISCUSSIONS_IMAGE).toString());
        imageMessages.setImage(image);
        switchToDiscussionBtn.setStyle(switchToDiscussionBtn.getStyle() + "-fx-text-fill: #5d928e");
    }
}
