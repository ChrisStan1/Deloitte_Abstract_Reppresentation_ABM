package models.home_company;

import models.SimpleFirmModel.Links;
import models.SimpleFirmModel.Messages;
import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Ranking;
import models.SimpleFirmModel.parameters.Specialization;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.HashMap;
import java.util.PriorityQueue;

import static models.SimpleFirmModel.parameters.Ranking.JUNIOR;
import static models.SimpleFirmModel.parameters.Ranking.SENIOR;

public abstract class SuperHomeCompany extends Agent<Globals> {

  /*******************************
   * Setting Up Agent parameters:
   *******************************/
  @Variable public String name;

  /*******************************
   * Consultants Information:
   *******************************/

  // Missing Agents:
  int missingSrAgents = 0;

  int missingJrAgents = 0;

  // Keeping Track of Available agents in each discipline:
  public PriorityQueue<Long> fiSrPQueue = new PriorityQueue<>();
  public PriorityQueue<Long> indSrPQueue = new PriorityQueue<>();
  public PriorityQueue<Long> techSrPQueue = new PriorityQueue<>();

  public PriorityQueue<Long> fiJrPQueue = new PriorityQueue<>();
  public PriorityQueue<Long> indJrPQueue = new PriorityQueue<>();
  public PriorityQueue<Long> techJrPQueue = new PriorityQueue<>();

  // Keeping Track of all Consultants Specializations, AvailableSlots & Ranking:
  public HashMap<Long, Specialization> consSpecializationMap = new HashMap<>();
  public HashMap<Long, Integer> consAvailableSlots = new HashMap<>();
  public HashMap<Long, Ranking> consRankingMap = new HashMap<>();

  // Priority Queue Management Functions:
  public PriorityQueue<Long> getPriorityQueue(Specialization specialization, Ranking ranking) {

    switch (specialization) {
      case FINANCE:
        if (ranking == SENIOR) {
          return fiSrPQueue;
        } else {
          return fiJrPQueue;
        }
      case INDUSTRIAL:
        if (ranking == SENIOR) {
          return indSrPQueue;
        } else {
          return indJrPQueue;
        }
      case TECHNOLOGY:
        if (ranking == SENIOR) {
          return techSrPQueue;
        } else {
          return techJrPQueue;
        }
    }
    return null;
  }

  public void assignPriorityQueue(Specialization specialization, Ranking ranking, Long id) {

    switch (specialization) {
      case FINANCE:
        if (ranking == SENIOR) {
          fiSrPQueue.add(id);
        } else {
          fiJrPQueue.add(id);
        }
      case INDUSTRIAL:
        if (ranking == SENIOR) {
          indSrPQueue.add(id);
        } else {
          indJrPQueue.add(id);
        }
      case TECHNOLOGY:
        if (ranking == SENIOR) {
          techSrPQueue.add(id);
        } else {
          techJrPQueue.add(id);
        }
    }
  }

  /*******************************
   * Function Implementations:
   *******************************/
  public void consultantSetup(Messages.RegistrationMessage msg) {
    consSpecializationMap.put(msg.getSender(), msg.specialization);
    consAvailableSlots.put(msg.getSender(), msg.overlappedProjects);
    consRankingMap.put(msg.getSender(), msg.ranking);
    assignPriorityQueue(msg.specialization, msg.ranking, msg.getSender());
  }

  public void acceptContract(Messages.contractProposal msg) {
    int minNbSrCons = (int) Math.ceil((msg.contSize / getGlobals().nbSrCPerProjectSize) * 0.75);
    int minNbJrCons = (int) Math.ceil((msg.contSize / getGlobals().nbJrCPerProjectSize) * 0.75);

    sendContractProposalResponse(
        availableAgents(msg.contSpecialization, minNbSrCons, minNbJrCons), msg);
  }

  public boolean availableAgents(Specialization specialization, int minNbSrCons, int minNbJrCons) {
    if (getPriorityQueue(specialization, SENIOR).size() < minNbSrCons) {
      getLongAccumulator("DelayedContracts").add(1);
      missingSrAgents++;
      return false;
    }
    if (getPriorityQueue(specialization, JUNIOR).size() < minNbJrCons) {
      getLongAccumulator("DelayedContracts").add(1);
      missingJrAgents++;
      return false;
    }
    return true;
  }

  public void sendContractProposalResponse(boolean isAccepted, Messages.contractProposal from) {
    getLinks(Links.DeloitteClientLink.class)
        .send(
            Messages.contractProposalResponse.class,
            (msg, link) -> {
              msg.isAccepted = isAccepted;

              msg.contId = from.contId;
              msg.contSize = from.contSize;
              msg.contDuration = from.contDuration;
              msg.contSpecialization = from.contSpecialization;
            });
  }
}
