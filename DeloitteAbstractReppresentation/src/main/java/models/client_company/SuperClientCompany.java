package models.client_company;

import models.SimpleFirmModel.Links;
import models.SimpleFirmModel.Messages;
import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Specialization;
import models.client_contract.ContractGenerationStrategy;
import models.client_contract.DefaultContractVisualization;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

public abstract class SuperClientCompany extends Agent<Globals> implements ClientCompany {

  /*******************************
   * Setting Up Agent parameters:
   ******************************/
  // Printed
  @Variable public String name;

  @Variable public long timeToNextContract; // Why Long?

  @Variable public int nbSimultaneousContracts;

  // Hidden:
  public Specialization compSpecialization;
  public ContractGenerationStrategy contractGenerationStrategy;

  /****************************************
   * Function Implementations:
   ****************************************/

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

    // Todo: Need to calibrate the nb of simultaneous contracts: (can make separate class always
    // accept or not)
    if (!reachedContractLimit() && timeToNextContract-- <= 0) {
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

  // May vary between different Client company
  protected abstract boolean reachedContractLimit();

  /****************************************
   * Messages functions:
   ****************************************/
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

  @Override
  public void isContractAccepted(Messages.ContractProposalResponse msg) {
    if (msg.isAccepted) {
      createNewContractAgent(msg);
    }
    // Todo: Else something...
  }

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
  /****************************************
   * Debugging Features:
   ****************************************/
  @Variable public String dbCompSpecialization;
}
