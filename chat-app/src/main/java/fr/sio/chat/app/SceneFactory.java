package fr.sio.chat.app;

import fr.sio.chat.app.controllers.Controller;
import fr.sio.chat.app.controllers.SceneContext;
import fr.sio.chat.app.events.interfaces.IEventListener;
import fr.sio.chat.app.models.User;
import fr.sio.chat.app.security.SecurityContext;
import fr.sio.chat.app.security.SessionManager;
import fr.sio.chat.app.websocket.WebSocketClient;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;

public class SceneFactory {
    private static final Logger logger = LoggerFactory.getLogger(SceneFactory.class);

    /**
     * Charge la scene principale une scene
     * @return L'élément root de la scene
     */
    public static Scene loadMainScene() {
        User user = SessionManager.getSessionUser();
        if (user != null) {
            SecurityContext.setUser(user);
            return getScene(SceneContext.PAGE_HOME_NAME);
        }
        return getScene(SceneContext.LOGIN_LAYOUTS_NAME);
    }

    /**
     * Charge une scene avec une instance de Controller
     * @param controller le controller instancié à passé dans le fxml
     * @param fxml le chemin du fichier .fxml
     * @return L'élément root de la scene
     */
    public static Node loadSceneWithController(String fxml, Controller controller) { // TODO throws exception
        FXMLLoader fxmlLoader = getFxmlLoader(fxml);
        fxmlLoader.setController(controller);
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupère une scène grâce au chemin du fxml
     * @param fxml
     * @return Une Scene
     */
    public static Scene getScene(String fxml) {
        FXMLLoader fxmlLoader = getFxmlLoader(fxml);
        URL styleSheetURL = App.class.getClassLoader().getResource("static/" + fxml + ".css");
        try {
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(App.class.getClassLoader().getResource("static/css/index.css").toString());
            if (styleSheetURL != null) {
                scene.getStylesheets().add(styleSheetURL.toString());
            }
            scene.setUserData(fxmlLoader.getController());
            return scene;
        } catch (IOException e) {
            logger.error("Une erreur est survenue pendant le chargement de la scene du " + fxml + ".fxml\n", e);
        }
        return null;
    }

    /**
     * Permet le changement de scène
     * @param fxml
     * @param actionEvent
     */
    public static void switchScene(String fxml, ActionEvent actionEvent) {
        // Récupère la scene actuelle
        Scene oldScene = ((Node) actionEvent.getSource()).getScene();

        // Récupère le controller de la scene
        if (oldScene.getUserData() instanceof IEventListener oldController) {
            oldController.removeListeners();
        }

        // Récupère la nouvelle scene et la remplace dans le stage
        Scene scene = getScene(fxml);
        Stage stage = (Stage) oldScene.getWindow();
        stage.setHeight(App.screen.getVisualBounds().getHeight());
        stage.setWidth(App.screen.getVisualBounds().getWidth());
        stage.setScene(scene);
    }

    public static Stage getEventStage(Event event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    /**
     * Récupère le FXMLLoader grâce au chemin du fxml
     * @param fxml
     * @return Un FXMLLoader
     */
    private static FXMLLoader getFxmlLoader(String fxml) {
        return new FXMLLoader(App.class.getClassLoader().getResource("fxml/" + fxml + ".fxml"));
    }
}
