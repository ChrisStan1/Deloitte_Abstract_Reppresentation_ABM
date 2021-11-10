/**************************
 * SuperClientCompany
 * Base background functions for all future clients.
 * By cas220
 **************************/
package models.client_company;

import models.SimpleFirmModel.Links;
import models.SimpleFirmModel.Messages;
import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Specialization;
import models.client_contract.ContractGenerationStrategy;
import models.client_contract.DefaultContractVisualization;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.LinkedList;
import java.util.Queue;

public abstract class SuperClientCompany extends Agent<Globals> implements ClientCompany {

  /*******************************
   * Setting Up Agent parameters:
   ******************************/
  // Printed
  @Variable public String name;

  @Variable public long timeToNextContract;

  @Variable public int nbSimultaneousContracts;

  @Variable public boolean isLeaving = false;

  // Hidden:
  public Specialization compSpecialization;
  public ContractGenerationStrategy contractGenerationStrategy;
  public Queue<Long> runningContracts = new LinkedList<>();

  /****************************************
   * Function Implementations:
   ****************************************/

  // Function Designed to store clientCompany information in Market class.
  @Override
  public void registerWithMarketMethod() {
    getLinks(Links.ClientCompanyMarketLink.class)
        .send(
            Messages.MarketRegistrationClientCompany.class,
            (msg, link) -> {
              msg.specialization = compSpecialization;
              msg.ID = getID();
            });
  }

  @Override
  public void generateNewContract() {

    // Can clientCompany generate a new contract
    if (!reachedContractLimit(runningContracts.size()) && timeToNextContract-- <= 0 && !isLeaving) {

      // Setting up a new contract:
      long contractID = contractGenerationStrategy.generateNewContractId();
      long contractSize = contractGenerationStrategy.generateNewContractSize();
      long contractDuration = contractGenerationStrategy.generateNewContractDuration(contractSize);
      Specialization contractSpecialization =
          contractGenerationStrategy.generateNewContractSpecialization(compSpecialization);

      // Time till the next Contract:
      timeToNextContract = contractGenerationStrategy.generateNewTimeToNextContract();

      // Sending Proposition to Deloitte:
      sendContractProposal(contractID, contractSize, contractDuration, contractSpecialization);
    }
  }

  // Number of contracts may different ClientCompany
  protected abstract boolean reachedContractLimit(int size);

  /****************************************
   * Messages functions:
   ****************************************/

  // Sending Contract Proposal and contract details to the HomeCompany
  @Override
  public void sendContractProposal(
      long contId, long contSize, long contDuration, Specialization contSpecialization) {
    getLinks(Links.DeloitteClientLink.class)
        .send(
            Messages.ContractProposal.class,
            (msg, link) -> {
              msg.contId = contId;
              msg.contSize = contSize;
              msg.contDuration = contDuration;
              msg.contSpecialization = contSpecialization;

              msg.compClient = this;
            });
  }

  // Gets notified if contract is accepted, and initializes contract visualization:
  @Override
  public void isContractAccepted(Messages.ContractProposalResponse msg) {
    if (msg.isAccepted) {
      createNewContractAgent(msg);
      runningContracts.add(msg.contId);
    }
  }

  // Function to spawn the ContractVisualization.
  @Override
  public void createNewContractAgent(Messages.ContractProposalResponse msg) {
    spawn(
        DefaultContractVisualization.class,
        a -> {
          a.contId = msg.contId;
          a.contSize = msg.contSize;
          a.contDuration = msg.contDuration;
          a.contSpecialization = msg.contSpecialization;
          a.dbContSpecialization = msg.contSpecialization.toString();
          a.addLink(getID(), Links.ContractToClient.class);
          msg.lastContract.addContractVisualization(a);
        });
  }

  // Contract is complete, remove contract from list, check if Client wasn't to leve
  @Override
  public void contractCompletedMethod(Messages.CompletedContract msg) {
    runningContracts.remove(msg.contID);
    if (isLeaving && runningContracts.size() == 0) {
      stop();
    }
  }

  // Gets message form market if there was a recession, company decides to leve
  @Override
  public void clientCompanyLeve(Messages.MarketClientCompanyQuit msg) {
    isLeaving = true;
  }

  /****************************************
   * Debugging Features:
   ****************************************/
  @Variable public String dbCompSpecialization;
}
