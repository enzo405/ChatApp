package fr.sio.chat.app.models;

public class Discussion {
    private int id;
    private String name;
    private DiscussionType discussionType;
    public Discussion() { }

    public Discussion(String name, DiscussionType discussionType) {
        this.name = name;
        this.discussionType = discussionType;
    }
    // region getters
    public int getId() { return id; }
    public DiscussionType getDiscussionType() { return discussionType; }
    public String getName() { return name; }
    // endregion getters

    // region setters
    public void setId(int id) { this.id = id; }
    public void setDiscussionType(DiscussionType discussionType) { this.discussionType = discussionType; }
    public void setName(String name) { this.name = name; }
    // endregion setters
}
