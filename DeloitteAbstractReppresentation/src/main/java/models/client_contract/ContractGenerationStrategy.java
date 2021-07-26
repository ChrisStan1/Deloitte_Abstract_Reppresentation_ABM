package models.client_contract;


import models.SimpleFirmModel.parameters.Specialization;

public interface ContractGenerationStrategy {

  long generateNewContractSize();

  long generateNewContractDuration(long size);

  long generateNewTimeToNextContract();

  long generateNewContractId();

  Specialization generateNewContractSpecialization(Specialization compSpecialization);

}
