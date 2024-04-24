package fr.sio.chat.app.controllers;

import fr.sio.chat.app.SceneFactory;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.InvalidCredentialsException;
import fr.sio.chat.app.ServiceFactory;
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

public class RegisterController extends Controller implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);
    private final IAuthService authService = ServiceFactory.getInstance().getAuthServiceInstance();

    // region FXML
    @FXML
    private TextField emailInput;
    @FXML
    private TextField usernameInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Label errorLabel;
    // endregion FXML

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    /**
     * Bouton pour register
     * @param actionEvent trigger par l'action de l'utilisateur
     */
    @FXML
    public void register(ActionEvent actionEvent) {
        String emailValue = emailInput.getText();
        String usernameValue = usernameInput.getText();
        String passwordValue = passwordInput.getText();

        try {
            authService.register(emailValue, usernameValue, passwordValue);
            SceneFactory.switchScene(SceneContext.LOGIN_LAYOUTS_NAME, actionEvent);
        } catch (InvalidCredentialsException ex) {
            showError(ex.getMessage());
            actionEvent.consume();
        } catch (DataAccessException ex) {
            showError("Une erreur est survenue lors de la création de votre compte");
            actionEvent.consume();
        }
    }

    /**
     * Bouton permettant de passer à la scène login
     * @param actionEvent trigger par l'action de l'utilisateur
     */
    @FXML
    public void switchLogin(ActionEvent actionEvent) {
        SceneFactory.switchScene(SceneContext.LOGIN_LAYOUTS_NAME, actionEvent);
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
