package it.univaq.disim.mwt.teachify.ejb;

import it.univaq.disim.mwt.teachify.business.model.Group;
import it.univaq.disim.mwt.teachify.business.model.Tutor;
import it.univaq.disim.mwt.teachify.business.model.User;

import java.util.Collection;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/*
 * Implementazione del Session Bean relativo all'interfaccia UserManagerRemote
 */
@Stateless
@Remote(UserManagerRemote.class)
public class UserManager implements UserManagerRemote {
	@PersistenceContext(unitName="TEACHIFY")
	private EntityManager manager;
	
	@Override
	public User authenticate(String email) throws ManagerException {

		try {
			User user = manager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
					.setParameter("email", email)
					.getSingleResult();
			return user;
		} catch (Exception e) {
			throw new ManagerException("Errore nel reperimento dell'utente", e);
		}
	}

	@Override
	public Long createUser(User user) throws ManagerException {
		try {
			manager.persist(user);
			return user.getId();
		} catch (Exception e) {
			throw new ManagerException("Errore nella creazione dell'utente", e);
		}
		
	}

	@Override
	public void updateUser(User user) throws ManagerException {
		try {
			manager.merge(user);
		} catch (Exception e) {
			throw new ManagerException("Errore nell'aggiornamento dell'utente", e);
		}
		
	}
	

	@Override
	public User findUserById(Long id) throws ManagerException {
		try {
			return manager.find(User.class, id);
		} catch (Exception e) {
			throw new ManagerException("Errore nel reperimento dell'utente", e);
		}
	}

	@Override
	public Long createAdmin(User u) throws ManagerException {
		try {
			Group group = manager.createQuery("SELECT g FROM Group g WHERE g.name = 'admin'", Group.class).getSingleResult();
			User user = manager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
					.setParameter("email", u.getEmail())
					.getSingleResult();
			user.getGroups().add(group);

			return user.getId();
			
			} catch (Exception e) {
			throw new ManagerException("Errore nell'assegnazione dei permessi da amministratore", e);
		}		
	}

	@Override
	public void deleteAdmin(User user) throws ManagerException {
		try {
			Collection<Group> groups = manager.find(User.class, user.getId()).getGroups();
			Group group = null;
			
			for (Group g : groups) {
				if(g.getName().equals("admin")){
					group = g;
				}
			}

			if(group == null){
				groups.remove(group);
			}
		} catch (Exception e) {
			throw new ManagerException("Errore nell'eliminazione dei permessi da amministratore", e);
		}
	}

	@Override
	public List<User> findAllAdmin() throws ManagerException {
		try {
			return manager.createQuery("SELECT u FROM User u WHERE EXISTS  (SELECT g FROM u.groups g WHERE g.name = 'admin')", User.class).getResultList();
		} catch (Exception e) {
			throw new ManagerException("Errore nella ricerca degli amministratori", e);
		}
	}



}
