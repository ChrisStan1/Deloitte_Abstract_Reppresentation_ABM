package models.client_company;

import models.SimpleFirmModel.Messages;
import models.SimpleFirmModel.parameters.Specialization;

public interface ClientCompany {

  void generateNewContract();

  void sendContractProposal(
      long contId, long contSize, long contDuration, Specialization contSpecialization);

  void isContractAccepted(Messages.ContractProposalResponse msg);

  void createNewContractAgent(Messages.ContractProposalResponse msg);

  void registerWithMarketMethod();

  void contractCompletedMethod(Messages.CompletedContract msg);
  }
