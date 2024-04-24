package fr.sio.chat.app.security;

import java.security.SecureRandom;
import java.util.Date;

public class SecurityToken {

    /**
     * Génère le token à partir du timestamp
     * @return Un token en String
     */
    public static String generateToken() {
        StringBuilder tokenBuilder = new StringBuilder();

        // Générer une partie aléatoire du token
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);
        for (byte b : randomBytes) {
            tokenBuilder.append(String.format("%02x", b));
        }

        tokenBuilder.append(new Date().getTime());
        return tokenBuilder.toString();
    }
}
