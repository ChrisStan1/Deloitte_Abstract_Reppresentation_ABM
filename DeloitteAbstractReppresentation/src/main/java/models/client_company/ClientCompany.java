package models.client_company;

import models.SimpleFirmModel.Messages;
import models.SimpleFirmModel.parameters.Specialization;

public interface ClientCompany {

  void registerWithMarketMethod();

  void generateNewContract();

  void clientCompanyLeve(Messages.MarketClientCompanyQuit msg);

  void sendContractProposal(
      long contId, long contSize, long contDuration, Specialization contSpecialization);

  void isContractAccepted(Messages.ContractProposalResponse msg);

  void createNewContractAgent(Messages.ContractProposalResponse msg);

  void contractCompletedMethod(Messages.CompletedContract msg);
}
