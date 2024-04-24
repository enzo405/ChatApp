package fr.sio.chat.app;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import fr.sio.chat.app.dao.PostgreSQLManager;
import fr.sio.chat.app.websocket.WebSocketClient;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class App extends Application {
    public static String configFilePath;
    public static Properties properties;
    public static Screen screen;
    private static final boolean MODE_DEV = false;
    private static final String CONFIG_ENV_VAR_NAME = "CHATAPP_CONFIG";
    private static final String CONFIG_FILE_NAME = "/settings.local.properties";
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        logger.info("L'application s'est correctement lancée");
    }

    /**
     * Récupération des propriétés du fichier de configuration
     */
    private boolean loadProperties() {
        // Si la variable d'environnement n'existe pas
        String ConfigEnvVarName = System.getenv(CONFIG_ENV_VAR_NAME);
        if (ConfigEnvVarName == null) {
            // On crée la variable d'environnement par défault
            System.err.println("Veuillez créer la variable d'environnement " + CONFIG_ENV_VAR_NAME
                    + " avec le chemin d'accès que vous souhaitez pour le stockage du fichier de configuration.");
            return false;
        }

        try (InputStream input = new FileInputStream(ConfigEnvVarName + CONFIG_FILE_NAME)) {
            // Si le fichier de configuration existe
            Properties props = new Properties();
            props.load(input);
            properties = props;
            configFilePath = ConfigEnvVarName + CONFIG_FILE_NAME;
            return true;
        } catch (IOException e) {
            // On crée le fichier de configuration
            createConfigFile();
        }
        return false;
    }

    /**
     * Démarrage de l'application
     * @param primaryStage stage de l'application
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Lancement du websocket
        WebSocketClient.getInstance().start();

        // Affectation des propriétés de configuration du projet aux variables static
        boolean isPropertyLoaded = loadProperties();

        if (isPropertyLoaded) {
            screen = Screen.getPrimary();

            // Initialisation des tables en mode dev
            if (MODE_DEV) {
                PostgreSQLManager.initializeAllTables();
            }

            Scene root = SceneFactory.loadMainScene();
            Image appIcon = new Image(getClass().getClassLoader().getResource("static/appIcon.png").toString());
            primaryStage.getIcons().add(appIcon);
            primaryStage.setScene(root);
            primaryStage.setTitle("Chat App");
            primaryStage.initStyle(StageStyle.UNDECORATED);

            // Avoir la page en maximized mais tout en voyant la toolbar windows
            Rectangle2D bounds = screen.getVisualBounds();
            primaryStage.setX(bounds.getMinX());
            primaryStage.setY(bounds.getMinY());
            primaryStage.setWidth(bounds.getWidth());
            primaryStage.setHeight(bounds.getHeight());

            primaryStage.show();
        } else {
            stop();
        }
    }

    /**
     * Arrêt de l'application
     * @throws Exception erreur créée par le stop
     */
    @Override
    public void stop() throws Exception {
        WebSocketClient.getInstance().logout();
        super.stop();
        logger.info("L'application s'est correctement arrêtée");
    }

    /**
     * Création du fichier de configuration
     */
    private void createConfigFile() {
        String ConfigFilePath = System.getenv(CONFIG_ENV_VAR_NAME);
        Properties tempProperty = new Properties();
        tempProperty.setProperty("pref.session", "");

        // Créer le fichier de config
        try {
            FileWriter fileWriter = new FileWriter(ConfigFilePath + CONFIG_FILE_NAME);
            tempProperty.store(fileWriter, "Fichier de configuration de ChatApp");
            logger.info("Le fichier de configuration a été créée, veuillez relancer l'application.");
        } catch (IOException e) {
            System.err.println("Une erreur est survenue lors de la création du fichier de configuration dans le chemin spécifié: " + ConfigFilePath);
        }
    }
}
