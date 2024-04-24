package fr.sio.websocket.models;

import java.sql.Timestamp;

public class Message {
    private int id;
    private String content;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private boolean isPinned;
    private Integer idMessageAnswered;
    private Discussion discussion;
    private User compte;

    public Integer getIdMessageAnswered() {
        return idMessageAnswered;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    public String getContent() {
        return content;
    }

    public Discussion getDiscussion() {
        return discussion;
    }

    public User getCompte() {
        return compte;
    }

    public int getId() {
        return id;
    }
    
    public boolean getIsPinned() {
        return isPinned;
    }
}