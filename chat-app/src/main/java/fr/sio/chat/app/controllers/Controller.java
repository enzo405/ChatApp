package fr.sio.chat.app.controllers;

import javafx.scene.Scene;

public abstract class Controller {
    protected Scene scene;

    /**
     * Récupération de la scène
     * @return Une scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * Set la scène
     * @param scene
     */
    public void setScene(Scene scene) {
        this.scene = scene;
    }
}