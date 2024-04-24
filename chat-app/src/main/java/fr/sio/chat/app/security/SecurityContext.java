package fr.sio.chat.app.security;

import fr.sio.chat.app.models.User;

public class SecurityContext {
    private static User user;

    /**
     * Récupère l'utilisateur connecté
     * @return l'utilisateur connecté
     */
    public static User getUser() { return user; }

    /**
     * Set l'utilisateur
     * @param user utilisateur connecté
     */
    public static void setUser(User user) { SecurityContext.user = user; }
}
