package fr.sio.chat.app.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionException extends Exception {
    private static final Logger logger = LoggerFactory.getLogger(SessionException.class);

    public SessionException(String errorMessage){
        super(errorMessage);
        logger.error(errorMessage);
    }
}
