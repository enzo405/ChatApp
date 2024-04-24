package fr.sio.chat.app.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FriendException extends Exception {
    private static final Logger logger = LoggerFactory.getLogger(InvalidCredentialsException.class);

    public FriendException(String errorMessage) {
        super(errorMessage);
        logger.error(errorMessage);
    }
}
