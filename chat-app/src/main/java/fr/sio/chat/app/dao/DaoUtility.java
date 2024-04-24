package fr.sio.chat.app.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DaoUtility {
    private static final Logger logger = LoggerFactory.getLogger(DaoUtility.class);

    /**
     * Récupère l'id de l'élément qui a été inséré
     * @param rowsAffected
     * @param preparedStatement
     * @return Un id
     * @throws SQLException
     */
    public static int getInsertId(final int rowsAffected, final PreparedStatement preparedStatement) throws SQLException {
        if (rowsAffected == 1) {
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                logger.error("Échec de la récupération de l'id");
                return -1;
            }
        } else {
            logger.error("Insertion inexistante");
            return -1;
        }
    }
}
