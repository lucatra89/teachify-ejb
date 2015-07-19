package it.univaq.disim.mwt.teachify.ejb;

import java.util.List;

import it.univaq.disim.mwt.teachify.business.RequestTutors;
import it.univaq.disim.mwt.teachify.business.TutorInfo;
import it.univaq.disim.mwt.teachify.business.model.Availability;
import it.univaq.disim.mwt.teachify.business.model.Feedback;
import it.univaq.disim.mwt.teachify.business.model.Lesson;
import it.univaq.disim.mwt.teachify.business.model.Request;
import it.univaq.disim.mwt.teachify.business.model.StatusRequest;
import it.univaq.disim.mwt.teachify.business.model.Tutor;
import it.univaq.disim.mwt.teachify.business.model.User;

import javax.ejb.Remote;
/*Rappresenta l'interfaccia  remota del Session Bean per l'accesso alla Business logic relativa alla gestione dei tutor
 * */
@Remote
public interface TutorManagerRemote {
	//crea un tutor ritornando l'id generato nella creazione
	public Long createTutor(Tutor tutor) throws ManagerException;
	//aggiorna la descrizione di un tutor
	public void updateTutorDescription(Tutor tutor) throws ManagerException;
	//aggiorna la location di un tutor
	public void updateTutorLocation(Tutor tutor) throws ManagerException;
	//aggiorna i contatti di un tutor
	public void updateTutorContact(Tutor tutor) throws ManagerException;
	//aggiorna la tariffa oraria di un tutor
	public void updateTutorPrice(Tutor tutor) throws ManagerException;
	//ritorna una lista di tutorInfo in base ai parametri di ricerca forniti nel paramentro in ingresso
	public List<TutorInfo> searchTutors(RequestTutors request) throws ManagerException;
	//ritorna un tutor identificato da un id
	public Tutor findTutorByPk(Long id) throws ManagerException;
	//crea un feedback ritornando l'id generato nella creazione
	public Long createFeedback(Feedback feedback) throws ManagerException;
	//ritorna l'elenco degli id dei feedback relativi ad un particolare tutor
	public List<Long> findAllFeedbackId(Tutor tutor) throws ManagerException;
	//ritorna un feedback identificato da un id
	public Feedback findFeedbackById(long id) throws ManagerException;
	//crea un richiesta ritornando l'id generato nella creazione
	public Long createRequest(Request request) throws ManagerException;
	//aggiorna lo stato della rischiesta
	public void updateStatusRequest(Request request) throws ManagerException;
	//ritorna le richieste in attesa di ricevute da un particolare tutor
	public List<Request> findWaitingRequestsByTutor(Tutor tutor) throws ManagerException;
	//ritorna le richieste effettuate da un utente
	public List<Request> findRequestsByUser(User user) throws ManagerException;
	//ritorna lo stato delle richiesta effettuata da un utente verso un tutor
	public StatusRequest statusOfRequest(User user, Tutor tutor) throws ManagerException;
	//crea una lezione ritornando l'id generato nella creazione
	public Long createLesson(Lesson lesson) throws ManagerException;
	//elimina un lezione 
	public void deleteLesson(Lesson lesson) throws ManagerException;
	//crea una disponibilità ritornando l'id generato nella creazione
	public Long createAvailability(Availability availability) throws ManagerException;
	//elimina una disponibilità 
	public void deleteAvailability(Availability availability) throws ManagerException;
}
