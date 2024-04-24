package fr.sio.chat.app.models;

import java.sql.Timestamp;
import java.util.List;

public class User {
    private int id;
    private String pseudo;
    private byte[] profilePicture = null;
    private String email;
    private Timestamp emailVerificationDate = null;
    private String password;
    private Timestamp createdDate;
    private Timestamp updatedDate = null;
//    private Status status;
    private List<User> friends = null;
    private List<Session> sessions;
    private List<Discussion> discussions = null;

    public User() {}
    public User(String pseudo, String email, String password) {
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
    }

    public User(int id, String pseudo, String email, String password, Timestamp createdDate) {
        this.id = id;
        this.pseudo = pseudo;
        this.email = email;
        this.password = password;
        this.createdDate = createdDate;
    }

    // region getters
    public int getId() { return id; }
    public List<User> getFriends() { return friends; }
    public List<Discussion> getDiscussions() { return discussions; }
    public List<Session> getSessions() { return sessions; }
//    public Status getStatus() { return status; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public byte[] getProfilePicture() { return profilePicture; }
    public String getPseudo() { return pseudo; }
    public Timestamp getCreatedDate() { return createdDate; }
    public Timestamp getEmailVerificationDate() { return emailVerificationDate;}
    public Timestamp getUpdatedDate() { return updatedDate; }
    // endregion getters

    // region setters
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }
    public void setDiscussions(List<Discussion> discussions) { this.discussions = discussions; }
    public void setEmail(String email) { this.email = email; }
    public void setEmailVerificationDate(Timestamp emailVerificationDate) { this.emailVerificationDate = emailVerificationDate; }
    public void setFriends(List<User> friends) { this.friends = friends; }
    public void setId(int id) { this.id = id; }
    public void setPassword(String password) { this.password = password; }
    public void setProfilePicture(byte[] profilePicture) { this.profilePicture = profilePicture; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }
    public void setSessions(List<Session> sessions) { this.sessions = sessions; }
//    public void setStatus(Status status) { this.status = status; }
    public void setUpdatedDate(Timestamp updatedDate) { this.updatedDate = updatedDate;}
    // endregion setters

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj.getClass() != this.getClass()) {
            return false;
        }

        final User other = (User) obj;

        return this.getId() == other.getId();
    }

    @Override
    public String toString() {
        return id + ", " + email + ", " + pseudo + ", " + createdDate + ", " + updatedDate;
    }
}
