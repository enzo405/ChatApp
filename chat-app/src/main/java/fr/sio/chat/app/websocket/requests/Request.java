package fr.sio.chat.app.websocket.requests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public abstract class Request {
    private static final Logger logger = LoggerFactory.getLogger(Request.class);
    protected ERequestType type;
    protected List<Integer> receiversId = new ArrayList<>();

    public abstract void dispatch();
}
