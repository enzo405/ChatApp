package fr.sio.chat.app.models;

public class Friend {

    private int idRelation;
    private boolean estAccepte;
    private User compte1;
    private User compte2;

    public Friend() {}

    public Friend(int idRelation ,Boolean estAccepte, User compte1, User compte2) {
        this.idRelation = idRelation;
        this.estAccepte = estAccepte;
        this.compte1 = compte1;
        this.compte2 = compte2;
    }

    // region getters
    public int getIdRelation() { return idRelation; }
    public boolean getEstAccepte() { return estAccepte; }
    public User getCompte1() { return compte1; }
    public User getCompte2() { return compte2; }
    // endregion setters

    // region setters
    public void setIdRelation(int idRelation) { this.idRelation = idRelation; }
    public void setEstAccepte(boolean estAccepte) { this.estAccepte = estAccepte; }
    public void setCompte1(User compte1) { this.compte1 = compte1; }
    public void setCompte2(User compte2) { this.compte2 = compte2; }
    // endregion setters

    @Override
    public String toString() {
        return "Compte 1: " + compte1.getPseudo() + " Compte 2: " + compte2.getPseudo() + " Accepté: " + estAccepte;
    }
}