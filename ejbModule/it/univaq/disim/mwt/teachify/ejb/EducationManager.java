package it.univaq.disim.mwt.teachify.ejb;

import it.univaq.disim.mwt.teachify.business.model.Availability;
import it.univaq.disim.mwt.teachify.business.model.Hour;
import it.univaq.disim.mwt.teachify.business.model.Lesson;
import it.univaq.disim.mwt.teachify.business.model.Price;
import it.univaq.disim.mwt.teachify.business.model.Subject;
import it.univaq.disim.mwt.teachify.business.model.Tutor;
import it.univaq.disim.mwt.teachify.business.model.TypeOfEducation;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.sun.istack.internal.logging.Logger;

/* Implementazione del Session Bean relativo all'interfaccia EducationManagerRemote.
 * La gestione delle transazione è delegata al container: la transazione comincia con l'invocazione del metodo e
 * termina con un commit al termine del metodo stesso
 */

@Stateless
@Remote(EducationManagerRemote.class)
@TransactionManagement(TransactionManagementType.CONTAINER)
public class EducationManager implements EducationManagerRemote {
	@PersistenceContext(unitName = "TEACHIFY")
	private EntityManager manager;

	@Override
	public List<Subject> findAllSubjects() throws ManagerException {
		try {
			return manager.createQuery("SELECT s FROM Subject s", Subject.class).getResultList();
		} catch (Exception e) {
			throw new ManagerException("Errore nella ricerca delle materie", e);
		}
	}

	@Override
	public Long createSubject(Subject subject) throws ManagerException {

		try {
			manager.persist(subject);
			return subject.getId();
		} catch (Exception e) {
			throw new ManagerException("Errore nella creazione della materia", e);
		}
	}
	
	/*
	 * L'eliminazione di una materia comporta l'eliminazione di tutte le lezioni che la contengono
	 */
	@Override
	public void deleteSubject(Subject subject) throws ManagerException {
		try {
			manager.createQuery("DELETE FROM Lesson WHERE subject.id = :id", Lesson.class)
			.setParameter("id", subject.getId())
			.executeUpdate();
			
			manager.remove(manager.find(Subject.class, subject.getId()));

		} catch (Exception e) {
			throw new ManagerException("Errore nell'eliminazine della materia", e);
		}

	}

	@Override
	public void updateSubject(Subject subject) throws ManagerException {
		try {
			manager.merge(subject);
		} catch (Exception e) {
			throw new ManagerException("Errore nell'aggiornamento della materia", e);
		}

	}

	@Override
	public List<TypeOfEducation> findAllTypesOfEducation() throws ManagerException {
		try {
			return manager.createQuery("SELECT t FROM TypeOfEducation t", TypeOfEducation.class).getResultList();
		} catch (Exception e) {
			throw new ManagerException("Errore nella ricerca dei livelli scolastici", e);
		}
	}

	@Override
	public Long createTypeOfEducation(TypeOfEducation typeOfEducation) throws ManagerException {
		try {
			manager.persist(typeOfEducation);
			return typeOfEducation.getId();
		} catch (Exception e) {
			throw new ManagerException("Errore nella creazione del livello scolastico", e);
		}

	}

	@Override
	public void updateTypeOfEducation(TypeOfEducation typeOfEducation) throws ManagerException {
		try {
			manager.merge(typeOfEducation);
		} catch (Exception e) {
			throw new ManagerException("Errore nell'aggiornamento del livello scolastico", e);
		}

	}
	/*
	 * L'eliminazione di un livello scolastico comporta l'eliminazione di tutte le lezioni che contengono tale livello
	 */
	@Override
	public void deleteTypeOfEducation(TypeOfEducation typeOfEducation) throws ManagerException {
		try {
			manager.createQuery("DELETE FROM Lesson WHERE typeOfEducation.id = :id", Lesson.class)
			.setParameter("id", typeOfEducation.getId())
			.executeUpdate();

			manager.remove(manager.find(TypeOfEducation.class, typeOfEducation.getId()));;
			
			
		} catch (Exception e) {
			throw new ManagerException("Errore nell'eliminazine del livello scolastico", e);
		}

	}

	@Override
	public List<Price> findAllPrices() throws ManagerException {
		try {
			return manager.createQuery("SELECT p FROM Price p", Price.class).getResultList();
		} catch (Exception e) {
			throw new ManagerException("Errore nella ricerca delle tariffe", e);
		}
	}

	@Override
	public Long createPrice(Price price) throws ManagerException {
		try {
			manager.persist(price);
			return price.getId();
		} catch (Exception e) {
			throw new ManagerException("Errore nella creazione della tariffa", e);
		}

	}

	/*
	 * La tariffa viene utilizzate nell'oggetto tutor, per cui l'eliminazione può comportare una violazione di
	 * di un constraint. Per evitare che questo accada si è pensato di sostituire la tariffa da eliminare con un'altra tariffa prima di effettuare
	 * la "remove"
	 */
	@Override
	public void deletePrice(Price older) throws ManagerException {
		try {
			
			Price newer = manager.createQuery("SELECT p FROM Price p WHERE p.id != :id", Price.class)
					.setParameter("id",older.getId())
					.setMaxResults(1)
					.getSingleResult();
			
			List<Tutor> tutors = manager.createQuery("SELECT t FROM Tutor t WHERE t.price.id = :id", Tutor.class)
					.setParameter("id", older.getId())
					.getResultList();
			
			for (Tutor tutor : tutors) {
				tutor.setPrice(newer);
			}
			
			manager.remove(manager.find(Price.class, older.getId()));
			
		} catch (Exception e) {
			throw new ManagerException("Errore nell'eliminazine della tariffa", e);
		}

	}

	@Override
	public List<Hour> findAllHours() throws ManagerException {
		try {
			return manager.createQuery("SELECT h FROM Hour h", Hour.class).getResultList();
		} catch (Exception e) {
			throw new ManagerException("Errore nella ricerca degli orari", e);
		}
	}

	@Override
	public Long createHour(Hour hour) throws ManagerException {
		try {
			manager.persist(hour);
			return hour.getId();
		} catch (Exception e) {
			throw new ManagerException("Errore nella creazione dell'orario", e);
		}

	}
	/*
	 * L'eliminazione di un orario comporta l'eliminazione di tutte le disponibilità che contengono tale orario
	 */
	@Override
	public void deleteHour(Hour hour) throws ManagerException {
		try {
			manager.createQuery("DELETE FROM Availability WHERE from.id = :id OR to.id = :id", Availability.class)
			.setParameter("id", hour.getId())
			.executeUpdate();
			
			manager.remove(manager.find(Hour.class, hour.getId()));
		} catch (Exception e) {
			throw new ManagerException("Errore nell'eliminazine dell'orario", e);
		}

	}

}
