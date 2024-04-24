package fr.sio.chat.app.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotFoundException extends Exception {
    private static final Logger logger = LoggerFactory.getLogger(NotFoundException.class);
    public NotFoundException(String errorMessage){
        super(errorMessage);
        logger.error(errorMessage);
    }
}
