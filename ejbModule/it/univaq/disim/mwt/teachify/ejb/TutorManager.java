package it.univaq.disim.mwt.teachify.ejb;

import it.univaq.disim.mwt.teachify.business.RequestTutors;
import it.univaq.disim.mwt.teachify.business.TutorInfo;
import it.univaq.disim.mwt.teachify.business.TutorInfoFindParams;
import it.univaq.disim.mwt.teachify.business.TutorUpdateGeoLocationParams;
import it.univaq.disim.mwt.teachify.business.model.Availability;
import it.univaq.disim.mwt.teachify.business.model.Contact;
import it.univaq.disim.mwt.teachify.business.model.Feedback;
import it.univaq.disim.mwt.teachify.business.model.Group;
import it.univaq.disim.mwt.teachify.business.model.Lesson;
import it.univaq.disim.mwt.teachify.business.model.Price;
import it.univaq.disim.mwt.teachify.business.model.Request;
import it.univaq.disim.mwt.teachify.business.model.StatusRequest;
import it.univaq.disim.mwt.teachify.business.model.Tutor;
import it.univaq.disim.mwt.teachify.business.model.User;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.jws.soap.SOAPBinding.Use;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/*
 * Implementazione del Session Bean relativo all'interfaccia TutorManagerRemote
 */

@Stateless
@Remote(TutorManagerRemote.class)
public class TutorManager implements TutorManagerRemote {

	@PersistenceContext(unitName = "TEACHIFY")
	private EntityManager manager;
	
	/*
	 * Questo metodo di utility esegue l'aggiornamento dei dati spaziali attraverso
	 * una NamedNativeQuery
	 */
	private void updateTutorGeoLocation(Tutor tutor) {
		manager.createNamedQuery("Tutor.updateGeoLocation")
			.setParameter(TutorUpdateGeoLocationParams.Latitude, tutor.getLocation().getLatitude())
			.setParameter(TutorUpdateGeoLocationParams.Longitude, tutor.getLocation().getLongitude())
			.setParameter(TutorUpdateGeoLocationParams.Id, tutor.getId())
			.executeUpdate();
	}
	
	/*
	 * Questo metodo di utility ritorno il ricalco il nuovo rating del tutor tenendo conto del nuovo feedback inserito
	 */
	
	private float nextFeedbackAvg(Feedback feedback){
		float avg;
		Long tutorId = feedback.getTutor().getId();
		Float current = manager.createQuery("SELECT t.rating FROM Tutor t WHERE t.id = :id ",  Float.class)
				.setParameter("id", tutorId)
				.getSingleResult();

		Long count = manager.createQuery("SELECT COUNT(f) FROM Feedback f WHERE f.tutor.id = :id", Long.class)
				.setParameter("id", tutorId)
				.getSingleResult();
		
		if(count == 0 ){
			avg = feedback.getRating();
		}else{
			avg  = (current * count + feedback.getRating()) / (count + 1);
		}
		
		return avg;
	}

	/*
	 * ritorna la lista di tutorInfo in base ai parametri di ricerca forniti nel pararametro in ingresso utilizzando
	 * una NamedNativeQuery che effettua una query spaziale sui tutors
	 */
	
	private List<TutorInfo> findTutorInfos(RequestTutors requestTutors) {
		TypedQuery<TutorInfo> query= manager.createNamedQuery("TutorInfo.find", TutorInfo.class)
				.setParameter(TutorInfoFindParams.Latitude, requestTutors.getLatitude())
				.setParameter(TutorInfoFindParams.Longitude, requestTutors.getLongitude());
		
		if(requestTutors.getSubjectId() == null || requestTutors.getSubjectId() == 0){
			query.setParameter(TutorInfoFindParams.IgnoreSubject, "TRUE");
			query.setParameter(TutorInfoFindParams.Subject, 0);
		}else{
			query.setParameter(TutorInfoFindParams.Subject, requestTutors.getSubjectId());
		}
		
		if(requestTutors.getTypeOfEducationId() == null || requestTutors.getTypeOfEducationId() == 0){
			query.setParameter(TutorInfoFindParams.IgnoreTypeOfEducation, "TRUE");
			query.setParameter(TutorInfoFindParams.TypeOfEducation, 0);
		}else{
			query.setParameter(TutorInfoFindParams.TypeOfEducation, requestTutors.getTypeOfEducationId());
		}
		
		return query.getResultList();
	}
	
	
	/*
	 * La creazione dell'utente comporta il settaggio di alcuni dati di default: price , rating, contact.
	 * Inoltre vengono assegnati i permessi da 'tutor'.
	 */
	@Override
	public Long createTutor(Tutor tutor) throws ManagerException {
		try {
			Price price = manager.createQuery("SELECT p FROM Price p", Price.class)
					.setMaxResults(1)
					.getSingleResult();

			Group group = manager.createQuery("SELECT g FROM Group g WHERE g.name = 'tutor'", Group.class).getSingleResult();
			
			if(tutor.getGroups() == null){
				tutor.setGroups(new HashSet<Group>());
			}
			
			tutor.getGroups().add(group);
			tutor.setRating(5f);
			tutor.setPrice(price);
			tutor.setContact(new Contact(null, tutor.getEmail(), null));
			
			manager.persist(tutor);
			
			updateTutorGeoLocation(tutor);
			
			return tutor.getId();
			
		} catch (Exception e) {
			throw new ManagerException("Errore nella creazione del tutor", e);
		}
	}

	@Override
	public void updateTutorDescription(Tutor tutor) throws ManagerException {
		try {
			manager.find(Tutor.class, tutor.getId()).setDescription(tutor.getDescription());
		} catch (Exception e) {
			throw new ManagerException("Errore nell'aggiornamento del tutor", e);
		}

	}

	@Override
	public void updateTutorLocation(Tutor tutor) throws ManagerException {
		try {
			manager.find(Tutor.class, tutor.getId()).setLocation(tutor.getLocation());
			updateTutorGeoLocation(tutor);
			
		} catch (Exception e) {
			throw new ManagerException("Errore nell'aggiornamento del tutor", e);
		}

	}


	@Override
	public void updateTutorPrice(Tutor tutor) throws ManagerException {
		try {
			manager.find(Tutor.class, tutor.getId())
			.setPrice(tutor.getPrice());
						
		} catch (Exception e) {
			throw new ManagerException("Errore nell'aggiornamento del tutor", e);
		}

	}

	@Override
	public List<TutorInfo> searchTutors(RequestTutors request) throws ManagerException {
		try {
			return findTutorInfos(request);
		} catch (Exception e) {
			throw new ManagerException("Errore nella ricerca dei tutor", e);
		}
	}

	@Override
	public Tutor findTutorByPk(Long id) throws ManagerException {
		try {
			
			return manager.find(Tutor.class, id);
		} catch (Exception e) {
			throw new ManagerException("Errore nella ricerca del tutor", e);
		}
	}

	@Override
	public Long createFeedback(Feedback feedback) throws ManagerException {
		try {
			
			feedback.setCreatedAt(new Date());
			manager.persist(feedback);
			
			manager.find(Tutor.class, feedback.getTutor().getId())
			.setRating(nextFeedbackAvg(feedback));
			
			return feedback.getId();
		} catch (Exception e) {
			throw new ManagerException("Errore nell'aggiornamento del tutor", e);
		}

	}

	@Override
	public List<Long> findAllFeedbackId(Tutor tutor) throws ManagerException {
		try {
			return manager.createQuery("SELECT f.id FROM Feedback f WHERE f.tutor.id = :id", Long.class)
					.setParameter("id", tutor.getId())
					.getResultList();
		} catch (Exception e) {
			throw new ManagerException("Errore nell'aggiornamento del tutor", e);
		}
	}

	@Override
	public Long createRequest(Request request) throws ManagerException {
		try {
			request.setCreatedAt(new Date());
			manager.persist(request);
			return request.getId();
		} catch (Exception e) {
			throw new ManagerException("Errore nell'aggiornamento del tutor", e);
		}

	}

	@Override
	public void updateStatusRequest(Request request) throws ManagerException {
		try {
			manager.find(Request.class, request.getId())
			.setStatus(request.getStatus());
		} catch (Exception e) {
			throw new ManagerException("Errore nell'aggiornamento della richiesta", e);
		}

	}

	@Override
	public List<Request> findWaitingRequestsByTutor(Tutor tutor) throws ManagerException {
		try {

			return manager.createQuery("SELECT r FROM Request r WHERE r.tutor.id = :id AND r.status = :status", Request.class)
					.setParameter("id", tutor.getId())
					.setParameter("status", StatusRequest.Waiting)
					.getResultList();
		} catch (Exception e) {
			throw new ManagerException("Errore nella ricerca delle richieste", e);
		}
	}

	@Override
	public List<Request> findRequestsByUser(User user) throws ManagerException {
		try {
			return manager.createQuery("SELECT r FROM Request r WHERE r.user.id = :id", Request.class)
					.setParameter("id", user.getId())
					.getResultList();
		} catch (Exception e) {
			throw new ManagerException("Errore nella ricerca delle richieste", e);
		}
	}

	@Override
	public StatusRequest statusOfRequest(User user, Tutor tutor) throws ManagerException {
		try {
			return manager.createQuery("SELECT r.status FROM Request r WHERE r.user.id = :userId AND r.tutor.id = :tutorId", StatusRequest.class)
					.setParameter("userId", user.getId())
					.setParameter("tutorId", tutor.getId())
					.setMaxResults(1)
					.getSingleResult();
			
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			throw new ManagerException("Errore nella verifica di status della richiesta", e);
		}
	}
	
	/*L'aggiunta della lezione comporta anche l'aggiornamento dell'oggetto parent tutor 
	 */
	@Override
	public Long createLesson(Lesson lesson) throws ManagerException {
		try {
			Tutor tutor = manager.find(Tutor.class, lesson.getTutor().getId());
			tutor.getLessons().add(lesson);
			manager.persist(lesson);
			manager.merge(tutor);
			return lesson.getId();
		} catch (Exception e) {
			throw new ManagerException("Errore nell'aggiunta di una lezione", e);
		}

	}
	/*L'eliminazione della lezione comporta anche l'aggiornamento dell'oggetto parent tutor 
	 */
	@Override
	public void deleteLesson(Lesson l) throws ManagerException {
		try {
			Tutor tutor = manager.find(Tutor.class, l.getTutor().getId());
			Lesson lesson = manager.find(Lesson.class, l.getId());
			manager.remove(lesson);
			tutor.getLessons().remove(lesson);
			manager.merge(tutor);
		
		} catch (Exception e) {
			throw new ManagerException("Errore nell'eliminazione di una lezione", e);
		}

	}
	/*L'aggiunta della disponibilità comporta anche l'aggiornamento dell'oggetto parent tutor 
	 */
	@Override
	public Long createAvailability(Availability availability) throws ManagerException {
		try {
			Tutor tutor = manager.find(Tutor.class, availability.getTutor().getId());
			tutor.getAvailabilities().add(availability);
			manager.persist(availability);
			manager.merge(tutor);
			return availability.getId();
		} catch (Exception e) {
			throw new ManagerException("Errore nell'aggiunta di una disponibilità", e);
		}
	}
	/*L'eliminazione della disponibilità comporta anche l'aggiornamento dell'oggetto parent tutor 
	 */
	@Override
	public void deleteAvailability(Availability a) throws ManagerException {
		try {
			Tutor tutor = manager.find(Tutor.class, a.getTutor().getId());
			Availability availability = manager.find(Availability.class, a.getId());
			manager.remove(availability);
			tutor.getAvailabilities().remove(availability);
			manager.merge(tutor);
		} catch (Exception e) {
			throw new ManagerException("Errore nell'eliminazione di una disponibilità", e);
		}

	}

	@Override
	public void updateTutorContact(Tutor tutor) throws ManagerException {
		try {
			manager.find(Tutor.class,tutor.getId())
			.setContact(tutor.getContact());
			
		} catch (Exception e) {
			throw new ManagerException("Errore nell'aggiornamento del tutor", e);
		}

	}

	@Override
	public Feedback findFeedbackById(long id) throws ManagerException {
		try {
			return manager.find(Feedback.class, id);
		} catch (Exception e) {
			throw new ManagerException("Errore nell'aggiornamento del tutor", e);
		}
	}


}
