package fr.sio.websocket;

import org.glassfish.tyrus.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebsocketServer extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(WebsocketServer.class);
    private static WebsocketServer instance = null;
    private boolean work = true;
    private final int port;
    private final String path;
    private final String hostname;
    private final Class<WebsocketEndpoint> endpoint = WebsocketEndpoint.class;
    private Server server;

    public WebsocketServer(int port, String hostname) {
        this(port, hostname, "/websockets");
    }

    public WebsocketServer(int port, String hostname, String path) {
        this.hostname = hostname;
        this.path = path;
        this.port = port;
        WebsocketServer.instance = this;
        logger.info("Server started on port " + port + " and hostname " + hostname);
    }

    @Override
    public void run() {
        this.server = new Server(this.hostname, this.port, this.path, null, this.endpoint);
        try {
            server.start();
            while (this.work) { }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        } finally {
            stopServer();
        }
    }

    /**
     * Permet de retourner un instance de WebsocketServer
     */
    public static WebsocketServer getInstance() {
        return instance;
    }

    public void stopServer() {
        this.server.stop();
        this.work = false;
        logger.info("Server stopped");
    }
}