package fr.sio.websocket.models;

public class Discussion {
    private int id;
    private String name;
    private DiscussionType discussionType;

    public DiscussionType getDiscussionType() {
        return discussionType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
