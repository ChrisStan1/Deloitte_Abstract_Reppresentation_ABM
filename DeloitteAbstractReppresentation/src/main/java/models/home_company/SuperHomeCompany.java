package models.home_company;

import models.SimpleFirmModel.Messages;
import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Ranking;
import models.SimpleFirmModel.parameters.Specialization;
import models.client_contract.ClientContract;
import models.consultant.JrConsultant;
import models.consultant.SrConsultant;
import models.market.DefaultMarket;
import models.market.Market;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.*;

import static models.SimpleFirmModel.parameters.Ranking.JUNIOR;
import static models.SimpleFirmModel.parameters.Ranking.SENIOR;

public abstract class SuperHomeCompany extends Agent<Globals> {

  /*******************************
   * Setting Up Agent Data members:
   *******************************/

  // Printed:
  @Variable public String name;

  // Hidden:
  public Market market;

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
  public Queue<Long> fiSrLL = new LinkedList<>();
  public Queue<Long> indSrLL = new LinkedList<>();
  public Queue<Long> techSrLL = new LinkedList<>();

  public Queue<Long> fiJrLL = new LinkedList<>();
  public Queue<Long> indJrLL = new LinkedList<>();
  public Queue<Long> techJrLL = new LinkedList<>();

  // Keeping Track of all Consultants Specializations, AvailableSlots & Ranking:
  public HashMap<Long, Specialization> consSpecializationMap = new HashMap<>();
  public HashMap<Long, Integer> consAvailableSlots = new HashMap<>();
  public HashMap<Long, Ranking> consRankingMap = new HashMap<>();

  // Making a hash map to store all agents which are part of 1 key (project ID)
  HashMap<Long, ArrayList<Long>> usedSrAgents = new HashMap<>();
  HashMap<Long, ArrayList<Long>> usedJrAgents = new HashMap<>();

  // Priority Queue Management Functions:
  public Queue<Long> getLLQueue(Specialization specialization, Ranking ranking) {

    switch (specialization) {
      case FINANCE:
        if (ranking == SENIOR) {
          return fiSrLL;
        } else {
          return fiJrLL;
        }
      case INDUSTRIAL:
        if (ranking == SENIOR) {
          return indSrLL;
        } else {
          return indJrLL;
        }
      case TECHNOLOGY:
        if (ranking == SENIOR) {
          return techSrLL;
        } else {
          return techJrLL;
        }
    }
    return null;
  }

  public void assignLLQueue(Specialization specialization, Ranking ranking, Long id) {

    switch (specialization) {
      case FINANCE:
        if (ranking == SENIOR) {
          fiSrLL.add(id);
        } else {
          fiJrLL.add(id);
        }
        break;
      case INDUSTRIAL:
        if (ranking == SENIOR) {
          indSrLL.add(id);
        } else {
          indJrLL.add(id);
        }
        break;
      case TECHNOLOGY:
        if (ranking == SENIOR) {
          techSrLL.add(id);
        } else {
          techJrLL.add(id);
        }
        break;
    }
  }

  private ClientContract getLastContract() {
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
  public void consultantSetup(Messages.RegistrationMessage msg) {
    consSpecializationMap.put(msg.getSender(), msg.specialization);
    consAvailableSlots.put(msg.getSender(), msg.overlappedProjects);
    consRankingMap.put(msg.getSender(), msg.ranking);
    assignLLQueue(msg.specialization, msg.ranking, msg.getSender());
  }

  public void acceptContract(Messages.ContractProposal msg) {
    int minNbSrCons = (int) Math.ceil((msg.contSize / getGlobals().nbSrCPerProjectSize) * 0.75);
    int minNbJrCons = (int) Math.ceil((msg.contSize / getGlobals().nbJrCPerProjectSize) * 0.75);

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

  public boolean availableAgents(Specialization specialization, int minNbSrCons, int minNbJrCons) {

    // Todo: Check the remaining quarter is available...
    boolean isAvailable = true;
    if (getLLQueue(specialization, SENIOR).size() <= minNbSrCons) {
      missingSrAgents++;
      missingSrAgentSpecialization = specialization;
      isAvailable = false;
    }
    if (getLLQueue(specialization, JUNIOR).size() <= minNbJrCons) {
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

  public void assignConsultants(
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

    // Keep Track of which agent is on which Project
    switch (ranking) {
      case SENIOR:
        usedSrAgents.put(contId, removedId);
        break;
      case JUNIOR:
        usedJrAgents.put(contId, removedId);
    }
  }

  public void grabBenchedConsultants(
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

  public Queue<Long> availableQueue(Ranking ranking, Specialization contSpecialization) {

    if (getLLQueue(contSpecialization.skip(1), ranking).size()
        > getLLQueue(contSpecialization.skip(2), ranking).size()) {
      return getLLQueue(contSpecialization.skip(1), ranking);
    } else {
      return getLLQueue(contSpecialization.skip(2), ranking);
    }
  }

  public void agentRequestsCounter(Ranking ranking) {
    switch (ranking) {
      case SENIOR:
        nbSeniorRequests++;
        break;
      case JUNIOR:
        nbJuniorRequests++;
        break;
    }
  }

  public void stepContract() {
    // Tick trough each contract:
    for (ClientContract contract : runningContracts) {
      contract.updateContractTick();
    }

    runningContracts.removeAll(toBeRemovedContracts);
    toBeRemovedContracts.clear();
  }

  // Contract has Finished:
  public void contractsToBeTerminated(ClientContract clientContract) {
    completedContracts.add(clientContract);
    toBeRemovedContracts.add(clientContract);
  }

  // Release agents
  public void terminateContract() {

    for (ClientContract compContract : completedContracts) {

      // Todo: Add to total Revenue:

      // Stop Visualization
      contractCompleted(compContract.getClientCompanyID());

      ArrayList<Long> agentsFreedSr = usedSrAgents.get(compContract.getContractID());
      ArrayList<Long> agentsFreedJr = usedJrAgents.get(compContract.getContractID());

      releaseConsultants(compContract, agentsFreedSr);
      releaseConsultants(compContract, agentsFreedJr);
    }

    // Remove completed contracts
    completedContracts.clear();
  }

  private void releaseConsultants(ClientContract compContract, ArrayList<Long> agentsFreed) {
    for (long id : agentsFreed) {
      releaseConsultantMessage(id, compContract.getSpecialization());
      updateAgentAvailability(id);
    }
  }

  private void updateAgentAvailability(long id) {

    // Updating AgentAvailability
    consAvailableSlots.put(id, consAvailableSlots.get(id) + 1);

    // If agent is not in Queue, add him back in...
    Specialization agentSpec = consSpecializationMap.get(id);
    Ranking agentRank = consRankingMap.get(id);
    if (!getLLQueue(agentSpec, agentRank).contains(id)) {
      getLLQueue(agentSpec, agentRank).add(id);
    }
  }

  public void hireConsultants() {
    if (missingSrAgents > getGlobals().allowedMissedContracts
        || missingJrAgents > getGlobals().allowedMissedContracts) {
      if (missingSrAgents > 2 && DefaultMarket.getSrEmploymentRate() > new Random().nextDouble()) {
        spawnSrConsultant();
        missingSrAgents = 0;
      }
      if (missingJrAgents > 2 && DefaultMarket.getJrEmploymentRate() > new Random().nextDouble()) {
        spawnJrConsultant();
        missingJrAgents = 0;
      }
    }
  }

  private void spawnJrConsultant() {
    Specialization newSpecialization = missingJrAgentSpecialization;
    spawn(
        JrConsultant.class,
        jr -> {
          jr.spawnNewConsultant(newSpecialization, consSpecializationMap, getID());
        });
  }

  private void spawnSrConsultant() {
    Specialization newSpecialization = missingSrAgentSpecialization;
    spawn(
        SrConsultant.class,
        sr -> {
          sr.spawnNewConsultant(newSpecialization, consSpecializationMap, getID());
        });
  }

  /************************************
   * Sending Messages Implementations:
   ************************************/

  public void requestConsultantMessage(long agentId, Specialization contSpecialization) {
    send(
            Messages.ConsultantRequest.class,
            msg -> {
              msg.contSpecialization = contSpecialization;
            })
        .to(agentId);
  }

  public void sendContractProposalResponseMessage(
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

  public void releaseConsultantMessage(long id, Specialization contSpecialization) {
    send(
            Messages.ConsultantReleased.class,
            msg -> {
              msg.contSpecialization = contSpecialization;
            })
        .to(id);
  }

  public void contractCompleted(long id) {
    send(Messages.CompletedContract.class).to(id);
  }
}
