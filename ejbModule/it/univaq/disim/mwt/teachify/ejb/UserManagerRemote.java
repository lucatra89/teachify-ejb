package it.univaq.disim.mwt.teachify.ejb;

import java.util.List;

import it.univaq.disim.mwt.teachify.business.model.User;

import javax.ejb.Remote;

//Rappresenta l'interfaccia  remota del Session Bean per l'accesso alla Business logic relativa alla gestione degli utenti
@Remote
public interface UserManagerRemote {
	//Ritorna un utente identificato da una particolare email
	User authenticate(String email) throws ManagerException;
	//Crea un utente ritornando l'id generato nella creazione
	Long createUser( User user) throws ManagerException;
	//Aggiorna un utente
	void updateUser(User user) throws ManagerException;
	//Ritorna un utente identificato da un particolare id
	User findUserById(Long id) throws ManagerException;
	//Assegna i permessi di amministratore ad un utente ritornando l'id dell'utente
	Long createAdmin(User user)  throws ManagerException;
	//Rimuove i permessi di amministratore ad un utente
	void deleteAdmin(User user) throws ManagerException;
	//Ritorna la lista di tutti gli utenti che hanno permessi di amministratore
	List<User> findAllAdmin() throws ManagerException;

}
