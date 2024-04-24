package fr.sio.websocket.models;

public class User {
    private int id;
    private String pseudo;
    private byte[] profilePicture;
    private String email;

    public int getId() {
        return id;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getEmail() {
        return email;
    }
}
