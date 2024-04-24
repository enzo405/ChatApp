package fr.sio.chat.app.models;

public class DiscussionType {
    private int id;
    private String label;
    private String icon;
    private boolean isGroup;

    public DiscussionType() {  }
    public DiscussionType(String label, String icon, boolean isGroup) {
        this.icon = icon;
        this.label = label;
        this.isGroup = isGroup;
    }

    // region getters
    public int getId() {
        return id;
    }
    public String getIcon() {
        return icon;
    }
    public String getLabel() {
        return label;
    }
    public Boolean getIsGroup() { return isGroup; }
    // endregion getters

    // region setters

    public void setId(int id) {
        this.id = id;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    public void setIsGroup(boolean group) { isGroup = group; }
    // endregion setters

    @Override
    public String toString() {
        String userFriendlyBool = getIsGroup() ? "Groupe" : "Privé";
        return userFriendlyBool + ": " + getLabel();
    }
}
