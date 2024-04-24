package fr.sio.websocket.models;

public class DiscussionType {
    private int id;
    private String label;
    private String icon;
    private boolean isGroup;

    public int getId() {
        return id;
    }

    public String getIcon() {
        return icon;
    }

    public String getLabel() {
        return label;
    }

    public boolean getIsGroup() {
        return isGroup;
    }
}
