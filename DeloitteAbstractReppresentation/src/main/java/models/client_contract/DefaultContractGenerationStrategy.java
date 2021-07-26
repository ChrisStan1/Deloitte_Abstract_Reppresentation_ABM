package models.client_contract;


import models.SimpleFirmModel.parameters.Specialization;

import java.util.Random;

public class DefaultContractGenerationStrategy implements ContractGenerationStrategy {

  // Contract ID counter:
  private static long idCounter = 1;

  @Override
  public long generateNewContractSize() {
    return new Random().nextInt(100) * 100000; // from 100'000 - 100xe^6
  }

  @Override
  public long generateNewContractDuration(long size) {
    return new Random().nextInt(5) + 1 + (size / 1000000);
  }

  @Override
  public long generateNewTimeToNextContract() {
    return new Random().nextInt(5);
  }

  @Override
  public Specialization generateNewContractSpecialization(Specialization compSpecialization) {

    // Todo: Allow the user to select the probability for the specialization inheritance;
    if (new Random().nextInt(10) >= 1) {
      return compSpecialization;
    } else {
      return Specialization.generateNewRandomSpecialization();
    }
  }

  @Override
  public long generateNewContractId() {
    return idCounter++;
  }
}
