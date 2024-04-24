package fr.sio.chat.app.models;

import java.sql.Timestamp;

public class Session {

    private int idSession;
    private String token;
    private Timestamp expirationDate;
    private Timestamp createdDate;
    private int idAccount;

    public Session() {}

    public Session(String token, Timestamp expirationDate, Timestamp createdDate, int idAccount) {
        this.token = token;
        this.expirationDate = expirationDate;
        this.createdDate = createdDate;
        this.idAccount = idAccount;
    }

    // region getters
    public int getIdSession() { return idSession; }

    public String getToken() { return token; }

    public Timestamp getExpirationDate() { return expirationDate; }

    public Timestamp getCreatedDate() { return createdDate; }

    public int getIdAccount() { return idAccount; }
    // endregion setters

    // region setters
    public void setIdSession(int idSession) { this.idSession = idSession; }

    public void setToken(String token) { this.token = token; }

    public void setExpirationDate(Timestamp expirationDate) { this.expirationDate = expirationDate; }

    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }

    public void setIdAccount(int idAccount) { this.idAccount = idAccount; }
    // endregion setters
}