package fr.sio.chat.app.controllers;

import fr.sio.chat.app.SceneFactory;
import fr.sio.chat.app.exceptions.DataAccessException;
import fr.sio.chat.app.exceptions.InvalidCredentialsException;
import fr.sio.chat.app.security.SecurityContext;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.ServiceFactory;
import fr.sio.chat.app.services.interfaces.IAuthService;
import fr.sio.chat.app.services.interfaces.IUserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingController extends Controller implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(SettingController.class);
    private final IUserService userService = ServiceFactory.getInstance().getUserServiceInstance();
    private final IAuthService authService = ServiceFactory.getInstance().getAuthServiceInstance();
    private final User user = SecurityContext.getUser();
    // region FXML
    @FXML
    private Label labelUsername;
    @FXML
    private TextField textFieldUsername;
    @FXML
    private HBox editUsernameContent;
    @FXML
    private Label usernameMessage;
    @FXML
    private Label labelEmail;
    @FXML
    private HBox editEmailContent;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private Label emailMessage;
    @FXML
    private ImageView avatarField;
    @FXML
    private Label errorMessageAvatar;
    @FXML
    private HBox editPasswordContent;
    @FXML
    private PasswordField passwordFieldPassword;
    @FXML
    private PasswordField passwordFieldPasswordConfirm;
    @FXML
    private PasswordField passwordFieldOldPassword;
    @FXML
    private Label passwordMessage;
    @FXML
    private SidebarController sidebarTemplateController;
    // endregion FXML

    /**
     * On initialise les éléments de la page settings
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sidebarTemplateController.toggleSettingImage();

        updateBoard();
    }

    /**
     * Affichage de l'édition du nom d'utilisateur
     * @param actionEvent  trigger par l'action de l'utilisateur
     */
    @FXML
    public void toggleEditUsername(ActionEvent actionEvent) {
        textFieldUsername.clear();
        usernameMessage.setVisible(false);
        editUsernameContent.setVisible(!editUsernameContent.isVisible());
        editUsernameContent.setManaged(!editUsernameContent.isManaged());
    }

    /**
     * Affichage de l'édition de l'email
     * @param actionEvent  trigger par l'action de l'utilisateur
     */
    @FXML
    public void toggleEditEmail(ActionEvent actionEvent) {
        textFieldEmail.clear();
        emailMessage.setVisible(false);
        editEmailContent.setVisible(!editEmailContent.isVisible());
        editEmailContent.setManaged(!editEmailContent.isManaged());
    }

    /**
     * Affichage de l'édition de mot de passe
     * @param actionEvent  trigger par l'action de l'utilisateur
     */
    @FXML
    public void toggleEditPassword(ActionEvent actionEvent) {
        passwordFieldPassword.clear();
        passwordFieldPasswordConfirm.clear();
        passwordFieldOldPassword.clear();
        passwordMessage.setVisible(false);
        editPasswordContent.setVisible(!editPasswordContent.isVisible());
        editPasswordContent.setManaged(!editPasswordContent.isManaged());
    }

    /**
     * Bouton de sauvegarde du nom d'utilisateur modifié
     * @param actionEvent trigger par l'action de l'utilisateur
     */
    @FXML
    public void saveUsername(ActionEvent actionEvent) {
        usernameMessage.setVisible(false);
        String newUsername = textFieldUsername.getText();

        try {
            authService.validateUsername(newUsername);
            editUsernameContent.setVisible(false);
            user.setPseudo(newUsername);
            userService.update(user);

            editUsernameContent.setManaged(false);
            usernameMessage.setStyle("-fx-text-fill: GREEN");
            usernameMessage.setText("Votre nom d'utilisateur a été modifié.");

            updateBoard();
        } catch (InvalidCredentialsException | DataAccessException ex) {
            usernameMessage.setStyle("-fx-text-fill: RED");
            usernameMessage.setText(ex.getMessage());
        }
        usernameMessage.setVisible(true);
    }

    /**
     * Bouton de sauvegarde de l'email modifié
     * @param actionEvent trigger par l'action de l'utilisateur
     */
    @FXML
    public void saveEmail(ActionEvent actionEvent) {
        emailMessage.setVisible(false);
        String newEmail = textFieldEmail.getText();

        try {
            authService.validateEmail(newEmail);
            editEmailContent.setVisible(false);
            user.setEmail(newEmail);
            userService.update(user);

            editEmailContent.setManaged(false);
            emailMessage.setStyle("-fx-text-fill: GREEN");
            emailMessage.setText("Votre email a été modifié.");

            updateBoard();
        } catch (InvalidCredentialsException | DataAccessException ex) {
            emailMessage.setStyle("-fx-text-fill: RED");
            emailMessage.setText(ex.getMessage());
        }
        emailMessage.setVisible(true);
    }

    /**
     * Bouton de sauvegarde du mot de passe modifié
     * @param actionEvent trigger par l'action de l'utilisateur
     */
    @FXML
    public void savePassword(ActionEvent actionEvent) {
        passwordMessage.setVisible(false);
        String passwordValue = passwordFieldPassword.getText();
        String passwordConfirmValue = passwordFieldPasswordConfirm.getText();
        String oldPasswordValue = passwordFieldOldPassword.getText();

        try {
            authService.validatePassword(user, oldPasswordValue, passwordValue, passwordConfirmValue);
            user.setPassword(authService.encodePassword(passwordValue));
            userService.update(user);

            editPasswordContent.setManaged(false);
            passwordMessage.setStyle("-fx-text-fill: GREEN");
            passwordMessage.setText("Votre mot de passe a été modifié.");

            updateBoard();
        } catch (InvalidCredentialsException | DataAccessException e) {
            passwordMessage.setStyle("-fx-text-fill: RED");
            passwordMessage.setText(e.getMessage());
        }
        passwordMessage.setVisible(true);
    }

    /**
     * Bouton de choix de l'avatar
     * @param actionEvent trigger par l'action de l'utilisateur
     */
    @FXML
    public void buttonChoseAvatar(ActionEvent actionEvent) {
        errorMessageAvatar.setVisible(false);
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Fichiers PNG", "*.png");
        fc.getExtensionFilters().add(extFilter);
        File file = fc.showOpenDialog(SceneFactory.getEventStage(actionEvent));
        if (file != null) {
            try {
                byte[] imageData = readImageData(file);
                Image image = new Image(new ByteArrayInputStream(imageData));
                if (image.getWidth() <= 128 && image.getHeight() <= 128 && file.length() <= 8 * 1024 * 1024) {
                    avatarField.setImage(image);
                    user.setProfilePicture(imageData);
                    userService.update(user);
                } else {
                    errorMessageAvatar.setVisible(true);
                    errorMessageAvatar.setText("L'image doit être inférieure ou égale à 128x128 pixels et 8 MB.");
                }
            } catch (IOException | DataAccessException e) {
                errorMessageAvatar.setVisible(true);
                errorMessageAvatar.setText("Erreur lors de la lecture de l'image : " + e.getMessage());
                actionEvent.consume();
            }
        }
    }

    /**
     * Bouton de déconnexion de l'utilisateur
     * @param actionEvent trigger par l'action de l'utilisateur
     */
    @FXML
    public void logout(ActionEvent actionEvent) {
        authService.logout(user);
        SceneFactory.switchScene(SceneContext.LOGIN_LAYOUTS_NAME, actionEvent);
    }

    /**
     * Actualisation de l'interface
     */
    private void updateBoard() {
        labelEmail.setText(user.getEmail());
        labelUsername.setText(user.getPseudo());
        if (user.getProfilePicture() != null) {
            Image image = new Image(new ByteArrayInputStream(user.getProfilePicture()));
            avatarField.setImage(image);
        }
    }

    /**
     * On lit l'image depuis le fichier
     * @param file fichier à lire
     * @return liste du bytecode de l'image
     * @throws IOException
     */
    private byte[] readImageData(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        }
    }
}
