CREATE TABLE chatapp.PieceJointe(
   idPieceJointe SERIAL PRIMARY KEY,
   UUID VARCHAR(36)
);

CREATE TABLE chatapp.Status(
   idStatus SERIAL PRIMARY KEY,
   libelle VARCHAR(50),
   couleur VARCHAR(7)
);

CREATE TABLE chatapp.TypeGroupe(
   idTypeGroupe SERIAL PRIMARY KEY,
   libelle VARCHAR(50)
);

CREATE TABLE chatapp.StatutDemande(
   idStatutDemande SERIAL PRIMARY KEY,
   libelle VARCHAR(50)
);

CREATE TABLE chatapp.Comptes(
   idCompte SERIAL PRIMARY KEY,
   tag VARCHAR(50),
   pseudo VARCHAR(50),
   photoDeProfil VARCHAR(255),
   courriel VARCHAR(255),
   courrielVerifieLe INT,
   motDePasse VARCHAR(255),
   creeLe INT,
   misAJourLe INT,
   idStatus INT NOT NULL,
   FOREIGN KEY(idStatus) REFERENCES Status(idStatus)
);

CREATE TABLE chatapp.Discussion(
   idDiscussion SERIAL PRIMARY KEY,
   nom VARCHAR(255),
   idTypeGroupe INT NOT NULL,
   FOREIGN KEY(idTypeGroupe) REFERENCES TypeGroupe(idTypeGroupe)
);

CREATE TABLE chatapp.Messages(
   idMessage SERIAL PRIMARY KEY,
   contenu TEXT,
   misAJourLe INT,
   creeLe INT,
   UUID VARCHAR(36),
   idDiscussion INT NOT NULL,
   idCompte INT NOT NULL,
   FOREIGN KEY(idDiscussion) REFERENCES Discussion(idDiscussion),
   FOREIGN KEY(idCompte) REFERENCES Comptes(idCompte)
);

CREATE TABLE chatapp.Sessions(
   idSession SERIAL PRIMARY KEY,
   MAC VARCHAR(255),
   token VARCHAR(255),
   expireLe INT,
   creeLe INT,
   idCompte INT NOT NULL,
   FOREIGN KEY(idCompte) REFERENCES Comptes(idCompte)
);

CREATE TABLE chatapp.EstAmi(
   idRelation SERIAL PRIMARY KEY,
   idStatutDemande INT NOT NULL,
   idCompte1 INT,
   idCompte2 INT,
   FOREIGN KEY(idStatutDemande) REFERENCES StatutDemande(idStatutDemande),
   FOREIGN KEY(idCompte1) REFERENCES Comptes(idCompte),
   FOREIGN KEY(idCompte2) REFERENCES Comptes(idCompte)
);

CREATE TABLE chatapp.FaitPartieDe(
   idCompte INT,
   idDiscussion INT,
   estProprietaire BOOL,
   PRIMARY KEY(idCompte, idDiscussion),
   FOREIGN KEY(idCompte) REFERENCES Comptes(idCompte),
   FOREIGN KEY(idDiscussion) REFERENCES Discussion(idDiscussion)
);

CREATE TABLE chatapp.EstEpingle(
   idDiscussion INT,
   idMessage INT,
   PRIMARY KEY(idDiscussion, idMessage),
   FOREIGN KEY(idDiscussion) REFERENCES Discussion(idDiscussion),
   FOREIGN KEY(idMessage) REFERENCES Messages(idMessage)
);

CREATE TABLE chatapp.AEteCharge(
   idPieceJointe INT,
   idMessage INT,
   PRIMARY KEY(idPieceJointe, idMessage),
   FOREIGN KEY(idPieceJointe) REFERENCES PieceJointe(idPieceJointe),
   FOREIGN KEY(idMessage) REFERENCES Messages(idMessage)
);

INSERT INTO chatapp.TypeGroupe (idTypeGroupe, libelle) VALUES
(1, 'Message Privé'),
(2, 'Groupe');

INSERT INTO chatapp.Status (idStatus, libelle, couleur) VALUES
(1, 'En ligne', '#23a55a'),
(2, 'Occupé', '#43c40'),
(3, 'Absent', '#f0b232'),
(4, 'Invisible', '#7d818c');