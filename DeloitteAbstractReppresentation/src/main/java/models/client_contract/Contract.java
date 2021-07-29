package models.client_contract;

import models.SimpleFirmModel.parameters.Specialization;

public interface Contract {

  long getSize();

  long getDuration();

  void decreaseDuration();

  boolean isCompleted();

  void setContractID(Long id);

  long getContractID();

  void updateContractTick();

  long getClientCompanyID();

  void addContractVisualization(ContractVisualization visualization);

  void stopContractVisualization();

  Specialization getSpecialization();
}
