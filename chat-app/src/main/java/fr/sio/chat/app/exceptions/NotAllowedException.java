package fr.sio.chat.app.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotAllowedException extends Exception {
    private static final Logger logger = LoggerFactory.getLogger(NotAllowedException.class);
    public NotAllowedException(String errorMessage){
        super(errorMessage);
        logger.error(errorMessage);
    }
}
