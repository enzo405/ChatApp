package fr.sio.chat.app.models;

public class UserDiscussion {
    private Discussion discussion;
    private User user;
    private boolean isOwner;

    public UserDiscussion() { }

    public UserDiscussion(Discussion discussion, User user, boolean isOwner) {
        this.discussion = discussion;
        this.user = user;
        this.isOwner = isOwner;
    }

    // region getters
    public Discussion getDiscussion() {
        return discussion;
    }
    public User getUser() {
        return user;
    }
    public boolean isOwner() { return isOwner; }
    // endregion getters

    // region setters
    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setIsOwner(boolean isOwner) { this.isOwner = isOwner; }
    // endregion setters
}
