/**************************
 * SuperHomeCompany
 * Super class for the consulting firm
 * By cas220
 **************************/

package models.home_company;

import models.SimpleFirmModel.Links;
import models.SimpleFirmModel.Messages;
import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Ranking;
import models.SimpleFirmModel.parameters.Specialization;
import models.client_contract.ClientContract;
import models.consultant.JrConsultant;
import models.consultant.SrConsultant;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.*;

import static models.SimpleFirmModel.parameters.Ranking.JUNIOR;
import static models.SimpleFirmModel.parameters.Ranking.SENIOR;

public abstract class SuperHomeCompany extends Agent<Globals> implements HomeCompany {

  /*******************************
   * Setting Up Agent Data members:
   *******************************/

  // Printed:
  @Variable public String name;

  // P&L calculation Variables:
  long currentGrossProfit = 0;
  long currentEBIT = 0;
  long currentNetProfit = 0;

  /*******************************
   * Consultants Information:
   *******************************/

  // Missing Agents:
  @Variable public int missingSrAgents = 0;

  @Variable public int missingJrAgents = 0;
  @Variable public int nbSeniorRequests = 0;
  @Variable public int nbJuniorRequests = 0;

  public Specialization missingJrAgentSpecialization;
  public Specialization missingSrAgentSpecialization;

  // Keeping Track of Available agents in each discipline:
  public Queue<Long> fiSrQueue = new LinkedList<>();
  public Queue<Long> indSrQueue = new LinkedList<>();
  public Queue<Long> techSrQueue = new LinkedList<>();

  public Queue<Long> fiJrQueue = new LinkedList<>();
  public Queue<Long> indJrQueue = new LinkedList<>();
  public Queue<Long> techJrQueue = new LinkedList<>();

  // Keeping Track of all Consultants Specializations, AvailableSlots & Ranking:
  public HashMap<Long, Specialization> consSpecializationMap = new HashMap<>();
  public HashMap<Long, Integer> consAvailableSlots = new HashMap<>();
  public HashMap<Long, Ranking> consRankingMap = new HashMap<>();

  // Making a hash map to store all agents which are part of 1 key (project ID)
  HashMap<Long, ArrayList<Long>> usedConsultingAgents = new HashMap<>();

  // Priority Queue Management Functions:
  protected Queue<Long> getLLQueue(Specialization specialization, Ranking ranking) {

    switch (specialization) {
      case FINANCE:
        if (ranking == SENIOR) {
          return fiSrQueue;
        } else {
          return fiJrQueue;
        }
      case INDUSTRIAL:
        if (ranking == SENIOR) {
          return indSrQueue;
        } else {
          return indJrQueue;
        }
      case TECHNOLOGY:
        if (ranking == SENIOR) {
          return techSrQueue;
        } else {
          return techJrQueue;
        }
    }
    return null;
  }

  protected void assignLLQueue(Specialization specialization, Ranking ranking, Long id) {

    switch (specialization) {
      case FINANCE:
        if (ranking == SENIOR) {
          fiSrQueue.add(id);
        } else {
          fiJrQueue.add(id);
        }
        break;
      case INDUSTRIAL:
        if (ranking == SENIOR) {
          indSrQueue.add(id);
        } else {
          indJrQueue.add(id);
        }
        break;
      case TECHNOLOGY:
        if (ranking == SENIOR) {
          techSrQueue.add(id);
        } else {
          techJrQueue.add(id);
        }
        break;
    }
  }

  // Getter function for the last emitted running contract
  protected ClientContract getLastContract() {
    return runningContracts.get(runningContracts.size() - 1);
  }

  /*******************************
   * Contract Information:
   *******************************/

  // Array List to keep track of Deloitte contracts:
  public ArrayList<ClientContract> runningContracts = new ArrayList<>();

  public ArrayList<ClientContract> toBeRemovedContracts = new ArrayList<>();
  public ArrayList<ClientContract> completedContracts = new ArrayList<>();

  /*******************************
   * Function Implementations:
   *******************************/

  // Method to register HomeCompany with market
  @Override
  public void registerWithMarketMethod() {
    getLinks(Links.DeloitteMarketLink.class)
        .send(
            Messages.MarketRegistrationHomeCompany.class,
            (msg, link) -> {
              msg.ID = getID();
            });
  }

  // Storing the appropriate consultant to their queue division
  @Override
  public void consultantSetup(Messages.RegistrationConsultant msg) {

    if (!consSpecializationMap.containsKey(msg.getSender())) {
      consSpecializationMap.put(msg.getSender(), msg.specialization);
      consAvailableSlots.put(msg.getSender(), msg.overlappedProjects);
      consRankingMap.put(msg.getSender(), msg.ranking);
      assignLLQueue(msg.specialization, msg.ranking, msg.getSender());
    }
  }

  // Function responsible for accepting/ rejecting contracts.
  // also responsible for initializing the consultant assignment process
  @Override
  public void acceptContract(Messages.ContractProposal msg) {
    int minNbSrCons = (int) Math.ceil((msg.contSize / getGlobals().nbSrCPerProjectSize));
    int minNbJrCons = (int) Math.ceil((msg.contSize / getGlobals().nbJrCPerProjectSize));

    // If available accept contract:
    if (availableAgents(msg.contSpecialization, minNbSrCons, minNbJrCons)) {

      // Keeping Track of Running Contracts:
      runningContracts.add(
          new ClientContract(
              msg.compClient,
              this,
              msg.contSize,
              msg.contDuration,
              msg.contSpecialization,
              msg.contId));

      // Function to assign Consultants:
      assignConsultants(
          minNbSrCons,
          getLLQueue(msg.contSpecialization, SENIOR),
          msg.contSpecialization,
          msg.contId,
          SENIOR);
      assignConsultants(
          minNbJrCons,
          getLLQueue(msg.contSpecialization, JUNIOR),
          msg.contSpecialization,
          msg.contId,
          JUNIOR);

      // Function tell client if contract is accepted or not:
      sendContractProposalResponseMessage(true, getLastContract(), msg);
    }
    // Otherwise return false:
    else {
      sendContractProposalResponseMessage(false, null, msg);
    }
  }

  // Method to check for agent availability for the new contract
  protected boolean availableAgents(Specialization specialization, int nbSrCons, int nbJrCons) {

    int minNbJrCons = (int) Math.ceil(nbJrCons * 0.75);
    int minNbSrCons = (int) Math.ceil(nbSrCons * 0.75);

    // At least 3/4 available same specialization
    boolean isAvailable = true;
    if (getLLQueue(specialization, SENIOR).size() < minNbSrCons
        || sumOfAvailableConsultants(SENIOR) < nbSrCons) {
      missingSrAgents++;
      missingSrAgentSpecialization = specialization;
      isAvailable = false;
    }
    if (getLLQueue(specialization, JUNIOR).size() < minNbJrCons
        || sumOfAvailableConsultants(JUNIOR) < nbJrCons) {
      missingJrAgents++;
      missingJrAgentSpecialization = specialization;
      isAvailable = false;
    }

    if (isAvailable) {
      return true;
    } else {
      getLongAccumulator("DelayedContracts").add(1);
      return false;
    }
  }

  // Check if there are enough available consultant to fill the contract order
  protected int sumOfAvailableConsultants(Ranking ranking) {
    return getLLQueue(Specialization.FINANCE, ranking).size()
        + getLLQueue(Specialization.INDUSTRIAL, ranking).size()
        + getLLQueue(Specialization.TECHNOLOGY, ranking).size();
  }

  // Assigning the designated consultant to the incoming contract
  protected void assignConsultants(
      int nbConsultants,
      Queue<Long> consPQueue,
      Specialization contSpecialization,
      Long contId,
      Ranking ranking) {

    // Remember Assigned IDs
    ArrayList<Long> removedId = new ArrayList<>();

    // Check how many consultants are required:
    int freeSpecializedConsultants = Math.min(consPQueue.size(), nbConsultants);
    int missingConsultants = nbConsultants - consPQueue.size();

    grabBenchedConsultants(
        freeSpecializedConsultants, consPQueue, removedId, contSpecialization, ranking);

    // If all PQueue are empty || there are no more agents to assign, then exit loop.
    if (missingConsultants > 0) {
      assignConsultants(
          missingConsultants,
          availableQueue(ranking, contSpecialization),
          contSpecialization,
          contId,
          ranking);
    }

    // Repopulate Pq;
    for (Long aLong : removedId) {
      if (consAvailableSlots.get(aLong) > 0) {
        consPQueue.add(aLong);
      }
    }

    removedId.addAll(usedConsultingAgents.getOrDefault(contId, new ArrayList<>()));
    usedConsultingAgents.put(contId, removedId);
  }

  // Selecting available consultants
  protected void grabBenchedConsultants(
      int nbConsultants,
      Queue<Long> consPQueue,
      ArrayList<Long> removedId,
      Specialization contSpecialization,
      Ranking ranking) {

    for (int i = 0; i < nbConsultants; i++) {

      // Keep Track of agents removed from PQ;
      long agentId = Objects.requireNonNull(consPQueue.poll());
      removedId.add(agentId);

      // Updating Agents Availability:
      consAvailableSlots.put(agentId, consAvailableSlots.get(agentId) - 1);

      // Send message to corresponding agent requested:
      requestConsultantMessage(agentId, contSpecialization);

      // Keep Track of called agents:
      agentRequestsCounter(ranking);
    }
  }

  // Method to pic the queue with the most available consulting by specialization.
  protected Queue<Long> availableQueue(Ranking ranking, Specialization contSpecialization) {

    if (getLLQueue(contSpecialization.skip(1), ranking).size()
        > getLLQueue(contSpecialization.skip(2), ranking).size()) {
      return getLLQueue(contSpecialization.skip(1), ranking);
    } else {
      return getLLQueue(contSpecialization.skip(2), ranking);
    }
  }

  // Tracker to see how many of each agent was requested.
  protected void agentRequestsCounter(Ranking ranking) {
    switch (ranking) {
      case SENIOR:
        nbSeniorRequests++;
        break;
      case JUNIOR:
        nbJuniorRequests++;
        break;
    }
  }

  // Function to step through contracts and update them.
  @Override
  public void stepContract() {
    // Tick trough each contract:
    for (ClientContract contract : runningContracts) {
      contract.updateContractTick();
    }

    runningContracts.removeAll(toBeRemovedContracts);
    toBeRemovedContracts.clear();
  }

  // Contract has Finished:
  @Override
  public void contractsToBeTerminated(ClientContract clientContract) {
    completedContracts.add(clientContract);
    toBeRemovedContracts.add(clientContract);
  }

  // Release agents
  @Override
  public void terminateContract() {

    for (ClientContract compContract : completedContracts) {

      // Stop Visualization
      contractCompleted(compContract.getClientCompanyID(), compContract.getContractID());

      ArrayList<Long> freedAgents = usedConsultingAgents.get(compContract.getContractID());

      releaseConsultants(compContract, freedAgents);
    }
  }

  // Function to release consultants form their old contract.
  protected void releaseConsultants(ClientContract compContract, ArrayList<Long> agentsFreed) {
    for (long id : agentsFreed) {
      releaseConsultantMessage(id, compContract.getSpecialization());
      updateAgentAvailability(id);
    }
  }

  // Updating the agents availability within the queue.
  protected void updateAgentAvailability(long id) {

    // Updating AgentAvailability
    consAvailableSlots.put(id, consAvailableSlots.get(id) + 1);

    // If agent is not in Queue, add him back in...
    Specialization agentSpec = consSpecializationMap.get(id);
    Ranking agentRank = consRankingMap.get(id);
    if (!getLLQueue(agentSpec, agentRank).contains(id)) {
      getLLQueue(agentSpec, agentRank).add(id);
    }
  }

  // If there are missing agents registered Spawn new consultants in the desired specialization
  @Override
  public void hireConsultants() {
    if (missingSrAgents > getGlobals().allowedMissedContracts
        || missingJrAgents > getGlobals().allowedMissedContracts) {
      if (missingSrAgents > 5 && getGlobals().srEmploymentMean > new Random().nextDouble()) {
        for (int i = 0; i < (Math.ceil(missingSrAgents * 0.25)); i++) {
          spawnSrConsultant();
        }
        missingSrAgents = 0;
        getGlobals().hasHiredConsultants = true;
      }
      if (missingJrAgents > 5 && getGlobals().jrEmploymentMean > new Random().nextDouble()) {
        for (int i = 0; i < (Math.ceil(missingJrAgents * 0.50)); i++) {
          spawnJrConsultant();
        }
        missingJrAgents = 0;
        getGlobals().hasHiredConsultants = true;
      }
    }
  }

  // Spawning method for JrConsultants
  @Override
  public void spawnJrConsultant() {
    Specialization newSpecialization = missingJrAgentSpecialization;
    spawn(
        JrConsultant.class,
        jr -> {
          jr.spawnNewConsultant(newSpecialization, consSpecializationMap, JUNIOR, getID());
        });
  }

  //  Spawning methods for SrConsultants
  @Override
  public void spawnSrConsultant() {
    Specialization newSpecialization = missingSrAgentSpecialization;
    spawn(
        SrConsultant.class,
        sr -> {
          sr.spawnNewConsultant(newSpecialization, consSpecializationMap, SENIOR, getID());
        });
  }

  // Profit and loss calculations
  @Override
  public void calculatePNLEachConsultant(Messages.PNL PNL) {
    getLongAccumulator("MonthlyRevenue").add(PNL.revenue);
    getLongAccumulator("MonthlySalary").add((long) PNL.salary);
    getLongAccumulator("MonthlyGrossProfit").add((long) (PNL.revenue - PNL.salary));
    currentGrossProfit += PNL.revenue - PNL.salary;
  }

  // Net profit and EBIT calculations
  @Override
  public void netProfit() {

    // Fixed Costs & Interest & Tax
    currentEBIT = currentGrossProfit - getGlobals().deloitteFixedCosts;
    getLongAccumulator("MonthlyEBIT").add(currentEBIT);

    currentNetProfit = (long) (getEarningsAfterInterest(currentEBIT) - getTotalTax(currentEBIT));
    getLongAccumulator("MonthlyNetProfit").add(currentNetProfit);

    // Looking only at Completed Contracts:
    for (ClientContract contract : completedContracts) {
      getLongAccumulator("MonthlyContractProfit").add(contract.getSize());
    }
    getLongAccumulator("MonthlyContractProfit")
        .add(getLongAccumulator("MonthlyContractProfit").value() - getGlobals().deloitteFixedCosts);

    // Remove completed contracts & Reset Calculations
    completedContracts.clear();
    currentGrossProfit = 0;
    currentEBIT = 0;
    currentNetProfit = 0;
  }

  // Tax Cost Calculation:
  protected double getTotalTax(long currentEBIT) {
    return getEarningsAfterInterest(currentEBIT) * getGlobals().deloitteCorporateTaxRate;
  }

  // Interest Cost Calculation:
  protected long getEarningsAfterInterest(long currentEBIT) {
    return currentEBIT - getGlobals().deloitteInterestCost;
  }

  /************************************
   * Sending Messages Implementations:
   ************************************/
  // Sending message to consultant about the new contract
  protected void requestConsultantMessage(long agentId, Specialization contSpecialization) {
    send(
            Messages.ConsultantRequest.class,
            msg -> {
              msg.contSpecialization = contSpecialization;
            })
        .to(agentId);
  }

  // Replying to ClientCompany if contract was accepted
  protected void sendContractProposalResponseMessage(
      boolean isAccepted, ClientContract lastContract, Messages.ContractProposal from) {
    send(
            Messages.ContractProposalResponse.class,
            msg -> {
              msg.isAccepted = isAccepted;
              msg.contId = from.contId;
              msg.contSize = from.contSize;
              msg.contDuration = from.contDuration;
              msg.contSpecialization = from.contSpecialization;

              msg.lastContract = lastContract;
            })
        .to(from.getSender());
  }

  // Sending message to consultant about being released by old contract
  protected void releaseConsultantMessage(long id, Specialization contSpecialization) {
    send(
            Messages.ConsultantReleased.class,
            msg -> {
              msg.contSpecialization = contSpecialization;
            })
        .to(id);
  }

  // Send message to ClientCompany that the contract is completed
  protected void contractCompleted(long clientCompanyID, long contractId) {
    send(
            Messages.CompletedContract.class,
            msg -> {
              msg.contID = contractId;
            })
        .to(clientCompanyID);
  }
}
