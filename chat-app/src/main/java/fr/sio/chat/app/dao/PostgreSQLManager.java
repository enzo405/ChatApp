package fr.sio.chat.app.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLManager {
    private static final Logger logger = LoggerFactory.getLogger(PostgreSQLManager.class);
    private static PostgreSQLManager instance = null;
    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    /**
     * Récupère l'instance PostgreSQLManager
     * @return l'instance
     */
    public static PostgreSQLManager getInstance() {
        if (instance == null) {
            instance = new PostgreSQLManager();
        }
        return instance;
    }

    /**
     * Réalise la connection à la BDD
     */
    public PostgreSQLManager() {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/postgres?currentShema=*******",
                    "admin",
                    "1234");
            logger.info("La connection à été lancé");
        } catch (ClassNotFoundException | SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void initializeAllTables() {
        UserDao.initializeTable();
        SessionDao.initializeTable();
        FriendDao.initializeTable();
        DiscussionTypeDao.initializeTable();
        DiscussionDao.initializeTable();
        UserDiscussionDao.initializeTable();
        MessageDao.initializeTable();
    }
}
