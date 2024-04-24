package fr.sio.chat.app.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataAccessException extends Exception {
    private static final Logger logger = LoggerFactory.getLogger(DataAccessException.class);

    public DataAccessException(String errorMessage) {
        super(errorMessage);
        logger.error(errorMessage);
    }
}
