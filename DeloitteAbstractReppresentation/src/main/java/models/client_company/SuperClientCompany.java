package models.client_company;

import models.SimpleFirmModel.Links;
import models.SimpleFirmModel.Messages;
import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Specialization;
import models.client_contract.ContractGenerationStrategy;
import models.client_contract.DefaultContractVisualization;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

public abstract class SuperClientCompany extends Agent<Globals> {

  /*******************************
   * Setting Up Agent parameters:
   ******************************/
  // Printed
  @Variable public String name;

  @Variable public long timeToNextContract; // Why Long?

  // Hidden:
  public Specialization compSpecialization;
  public ContractGenerationStrategy contractGenerationStrategy;

  /****************************************
   * Function Implementations:
   ****************************************/
  public void generateNewContract() {

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


    /****************************************
     * Messages functions:
     ****************************************/
  private void sendContractProposal(
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

  public void isContractAccepted(Messages.ContractProposalResponse msg) {
    if (msg.isAccepted) {
      createNewContractAgent(msg);
    }
    // Todo: Else something...
  }

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
