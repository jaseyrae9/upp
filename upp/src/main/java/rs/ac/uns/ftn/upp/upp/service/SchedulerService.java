package rs.ac.uns.ftn.upp.upp.service;

import java.util.List;
import java.util.Set;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.impl.persistence.entity.HistoricVariableInstanceEntity;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.upp.upp.exceptions.NotFoundException;
import rs.ac.uns.ftn.upp.upp.model.journal.Edition;
import rs.ac.uns.ftn.upp.upp.model.journal.Journal;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.EditionService;
import rs.ac.uns.ftn.upp.upp.service.entityservice.journal.PaperService;

@Service
public class SchedulerService {

	@Autowired
	private EditionService editionService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private PaperService paperService;

	@Autowired
	private ProcessEngine engine;

	@Scheduled(fixedDelay = 1800000) // nek stoji da proverava na svakih pola sata
	public void checkProcesses() throws NotFoundException {
		System.out.println("proveravamo procese");

		Long number = historyService.createHistoricProcessInstanceQuery().active()
				.processDefinitionKey("ObradaPodnetogTeksta").count();
		List<HistoricProcessInstance> hpi = historyService.createHistoricProcessInstanceQuery().active()
				.processDefinitionKey("ObradaPodnetogTeksta").listPage(0, number.intValue());

		for (HistoricProcessInstance one : hpi) {
			HistoricVariableInstanceEntity variablePaperId = (HistoricVariableInstanceEntity) historyService
					.createHistoricVariableInstanceQuery().processInstanceId(one.getId()).variableName("radId")
					.singleResult();

			HistoricVariableInstanceEntity variable = (HistoricVariableInstanceEntity) historyService
					.createHistoricVariableInstanceQuery().processInstanceId(one.getId())
					.variableName("izabraniCasopisId").singleResult();

			DateTime date = new DateTime(one.getStartTime());
			DateTime nextTime = date.plusMonths(1);

			//DateTime nextTime = date.plusYears(1);
			if (nextTime.isAfterNow()) { // nije prosla godina
				System.out.println("nije proslo godinu dana " + nextTime);
			}

			if (nextTime.isBeforeNow()) { // proslo 10 min
				if (variablePaperId != null) {
					Integer paperId = Integer.parseInt(String.valueOf(variablePaperId.getValue()));
					System.out.println("brise se paper id " + paperId);
					paperService.deletePaperById(paperId);
					engine.getRuntimeService().suspendProcessInstanceById(one.getId());
					engine.getRuntimeService().deleteProcessInstance(one.getId(), "predugo zivi");					
				}
				else {
					engine.getRuntimeService().suspendProcessInstanceById(one.getId());
					engine.getRuntimeService().deleteProcessInstance(one.getId(), "predugo zivi");
				}
			}
			

		}

	}

	

	@Scheduled(fixedDelay = 900000) // na svakih 15 min
	public void checkOrders() {
		System.err.println("usao u scheduler");
		Set<Edition> editions = editionService.findAllByPublished(false); // pronalazi sve koji nisu objavljeni
		if (!editions.isEmpty()) {
			System.err.println("ima onih koji nisu objavljeni");
			for (Edition edition : editions) {
				System.err.println("DEBUG date: " + edition.getDate());
				DateTime date = edition.getDate();
				DateTime next2Month = date.plusMonths(1);
				if (next2Month.isAfterNow()) { // nije proslo 10 min
					System.out.println("nije proslo mesec dana: next2Month " + next2Month);
				}

				if (next2Month.isBeforeNow()) { // proslo 10 min
					System.out.println("proslo je mesec dana " + next2Month);
					edition.setPublished(true);
					editionService.saveEdition(edition);
					Journal journal = edition.getJournal(); // kom casopisu pripada to izdanje
					Edition newEdition = new Edition();
					newEdition.setDate(new DateTime()); // trenutno vreme
					newEdition.setNumber(edition.getNumber() + 1); // sledece izdanje
					newEdition.setJournal(journal); // kom casopisu pripada
					newEdition.setPublished(false); // kad se kreira nije objavljeno
					Edition savedEdition = editionService.saveEdition(newEdition);
					journal.getJournalEditions().add(savedEdition); // dodali to izdanje casopisu

				}
			}
		} else {
			System.out.println("nema neobjavljenih");
		}

	}

}
