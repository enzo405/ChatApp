package fr.sio.chat.app.websocket;

import fr.sio.chat.app.models.User;
import fr.sio.chat.app.security.SecurityContext;
import fr.sio.chat.app.websocket.requests.Request;
import jakarta.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ClientEndpoint()
public class ClientRequestEndpoint {
    private static final Logger logger = LoggerFactory.getLogger(ClientRequestEndpoint.class);
    private Session session;

    @OnOpen
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        WebSocketClient.getInstance().setSession(session);
        this.session = session;

        User sessionUser = SecurityContext.getUser();
        if (sessionUser != null) {
            WebSocketClient.getInstance().login(sessionUser);
        }
        logger.info("Session ouverte");
    }

    @OnMessage
    public void onMessage(String encodedRequest) {
        Request request = RequestCodec.decode(encodedRequest);
        logger.info(request.toString());
        request.dispatch();
    }

    public Session getSession() {
        return session;
    }
}
