package models.home_company;

import models.SimpleFirmModel.Messages;
import models.SimpleFirmModel.parameters.Ranking;
import models.SimpleFirmModel.parameters.Specialization;
import models.client_contract.ClientContract;

import java.util.ArrayList;
import java.util.Queue;

public interface HomeCompany {

  Queue<Long> getLLQueue(Specialization specialization, Ranking ranking);

  void assignLLQueue(Specialization specialization, Ranking ranking, Long id);

  ClientContract getLastContract();

  void registerWithMarketMethod();

  void consultantSetup(Messages.RegistrationConsultant msg);

  void acceptContract(Messages.ContractProposal msg);

  boolean availableAgents(Specialization specialization, int minNbSrCons, int minNbJrCons);

  void assignConsultants(
      int nbConsultants,
      Queue<Long> consPQueue,
      Specialization contSpecialization,
      Long contId,
      Ranking ranking);

  void grabBenchedConsultants(
      int nbConsultants,
      Queue<Long> consPQueue,
      ArrayList<Long> removedId,
      Specialization contSpecialization,
      Ranking ranking);

  Queue<Long> availableQueue(Ranking ranking, Specialization contSpecialization);

  void agentRequestsCounter(Ranking ranking);

  void stepContract();

  void contractsToBeTerminated(ClientContract clientContract);

  void terminateContract();

  void releaseConsultants(ClientContract compContract, ArrayList<Long> agentsFreed);

  void updateAgentAvailability(long id);

  void hireConsultants();

  void spawnJrConsultant();

  void spawnSrConsultant();

  void calculatePNLEachConsultant(Messages.PNL PNL);

  void netProfit();

  double getTotalTax(long currentEBIT);

  long getEarningsAfterInterest(long currentEBIT);

  void requestConsultantMessage(long agentId, Specialization contSpecialization);

  void sendContractProposalResponseMessage(
      boolean isAccepted, ClientContract lastContract, Messages.ContractProposal from);

  void releaseConsultantMessage(long id, Specialization contSpecialization);

  void contractCompleted(long clientCompanyID, long contractId);
}
