package it.univaq.disim.mwt.teachify.ejb;

import it.univaq.disim.mwt.teachify.business.model.Hour;
import it.univaq.disim.mwt.teachify.business.model.Price;
import it.univaq.disim.mwt.teachify.business.model.Subject;
import it.univaq.disim.mwt.teachify.business.model.TypeOfEducation;

import java.util.List;

import javax.ejb.Remote;
/*Rappresenta l'interfaccia  remota del Session Bean per l'accesso alla Business logic relativa alla gestione dei dati utilizzati per decrivere le caratteristiche dei tutor
 * */
@Remote
public interface EducationManagerRemote {
	//ritorna la lista di tutte le materie
	List<Subject> findAllSubjects() throws ManagerException;
	//crea una materia ritornando l'id generato nella creazione
	Long createSubject(Subject subject) throws ManagerException;
	//cancella una materia
	void deleteSubject(Subject subject) throws ManagerException;
	//aggiorna una materia
	void updateSubject(Subject subject) throws ManagerException;
	//ritorna l'elenco di tutti i livelli scolastici
	List<TypeOfEducation> findAllTypesOfEducation() throws ManagerException;
	//crea un livello scolastico ritornando l'id generato nella creazione
	Long createTypeOfEducation(TypeOfEducation typeOfEducation) throws ManagerException;
	//aggiorna un livello scolastico
	void updateTypeOfEducation(TypeOfEducation typeOfEducation) throws ManagerException;
	//cancella un livello scolastico
	void deleteTypeOfEducation(TypeOfEducation typeOfEducation) throws ManagerException;
	//ritorna l'elenco di tutte le tariffe utilizzabili dai tutor
	List<Price> findAllPrices() throws ManagerException;
	//crea una tariffa ritornando l'id generato nella creazione
	Long createPrice(Price price) throws ManagerException;
	//cancella una tariffa
	void deletePrice(Price price) throws ManagerException;
	//ritorna l'elenco di tutte le ore utilizzabili dai tutor per indicare gli intervalli di disponibilità
	List<Hour> findAllHours() throws ManagerException;
	//crea un orario ritornando l'id generato nella creazione
	Long createHour(Hour hour) throws ManagerException;
	//cancella un orario
	void deleteHour(Hour hour) throws ManagerException;
}
