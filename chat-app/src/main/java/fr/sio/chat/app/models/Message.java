package fr.sio.chat.app.models;

import java.sql.Timestamp;

public class Message {

    private int id;
    private String content;
    private Timestamp createdDate;
    private Timestamp updatedDate = null;
    private boolean isPinned = false;
    private Integer idMessageAnswered = 0; // Integer null = 0
    private Discussion discussion;
    private User compte;

    public Message() { }

    public Message(String content, Discussion discussion, User compte) {
        this.content = content;
        this.discussion = discussion;
        this.compte = compte;
    }

    public int getId() { return id; }
    public String getContent() { return content; }
    public Timestamp getCreatedDate() { return createdDate; }
    public Timestamp getUpdatedDate() { return updatedDate; }
    public boolean getIsPinned() { return isPinned; }
    public Integer getIdMessageAnswered() { return idMessageAnswered; }
    public Discussion getDiscussion() { return discussion; }
    public User getCompte() { return compte; }

    public void setId(int id) { this.id = id; }
    public void setContent(String content) { this.content = content; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }
    public void setUpdatedDate(Timestamp updatedDate) { this.updatedDate = updatedDate; }
    public void setIsPinned(boolean isPinned) { this.isPinned = isPinned; }
    public void setIdMessageAnswered(Integer idMessageAnswered) { this.idMessageAnswered = idMessageAnswered; }
    public void setDiscussion(Discussion discussion) { this.discussion = discussion; }
    public void setCompte(User compte) { this.compte = compte; }
}
