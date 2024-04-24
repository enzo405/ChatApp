package fr.sio.chat.app.controllers;

import fr.sio.chat.app.SceneFactory;
import fr.sio.chat.app.exceptions.InvalidCredentialsException;
import fr.sio.chat.app.ServiceFactory;
import fr.sio.chat.app.exceptions.SessionException;
import fr.sio.chat.app.services.interfaces.IAuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends Controller implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final IAuthService authService = ServiceFactory.getInstance().getAuthServiceInstance();

    // region FXML
    @FXML
    private TextField emailInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Label errorLabel;
    // endregion FXML

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    /**
     * Bouton pour login
     * @param actionEvent  trigger par l'action de l'utilisateur
     */
    @FXML
    public void login(ActionEvent actionEvent) {
        String emailValue = emailInput.getText();
        String passwordValue = passwordInput.getText();

        try {
            authService.login(emailValue, passwordValue);
            SceneFactory.switchScene(SceneContext.PAGE_HOME_NAME, actionEvent);
        } catch (InvalidCredentialsException | SessionException ex) {
            showError(ex.getMessage());
            actionEvent.consume();
        }
    }

    /**
     * Bouton permettant de passer à la scène register
     * @param actionEvent  trigger par l'action de l'utilisateur
     */
    @FXML
    public void switchRegister(ActionEvent actionEvent) {
        SceneFactory.switchScene(SceneContext.REGISTER_LAYOUTS_NAME, actionEvent);
    }

    /**
     * Affichage de l'erreur
     * @param errorMessage message d'erreur à afficher
     */
    private void showError(String errorMessage) {
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
    }
}
