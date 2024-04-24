package fr.sio.chat.app.controllers;

import fr.sio.chat.app.SceneFactory;
import javafx.scene.layout.HBox;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class TitlebarController extends Controller implements Initializable {
    @FXML
    private Button minimizeWindowButton;
    @FXML
    private Button closeWindowButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        closeWindowButton.setOnMouseEntered(event -> closeWindowButton.setStyle("-fx-background-color: red"));
        closeWindowButton.setOnMouseExited(event -> closeWindowButton.setStyle("-fx-background-color: transparent"));

        minimizeWindowButton.setOnMouseEntered(event -> minimizeWindowButton.setStyle("-fx-background-color: #464646"));
        minimizeWindowButton.setOnMouseExited(event -> minimizeWindowButton.setStyle("-fx-background-color: transparent"));
    }

    @FXML
    public void closeWindow(ActionEvent event) {
        Stage stage = SceneFactory.getEventStage(event);
        stage.close();
        Platform.exit(); // Force close the application
    }

    @FXML
    public void minimizeWindow(ActionEvent event) {
        Stage stage = SceneFactory.getEventStage(event);
        stage.setIconified(true); // Minimize the window
    }
}
