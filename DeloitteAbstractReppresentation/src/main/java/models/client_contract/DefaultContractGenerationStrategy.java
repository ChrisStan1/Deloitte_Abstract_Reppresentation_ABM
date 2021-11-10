/**************************
 * DefaultContractGenerationStrategy
 * Generates the default contracts
 * By cas220
 **************************/

package models.client_contract;

import models.SimpleFirmModel.parameters.Specialization;

import java.util.Random;

public class DefaultContractGenerationStrategy implements ContractGenerationStrategy {

  // Contract ID counter:
  private static long idCounter = 1;

  // Generates new contract size:
  @Override
  public long generateNewContractSize() {
    return (new Random().nextInt(100) + 1) * 250000L;
  }

  // Generates New contract duration: (min 1 month, max 6 moths + contract size)
  @Override
  public long generateNewContractDuration(long size) {
    return new Random().nextInt(5) + 1 + (size / 1000000);
  }

  // Generates new delay between contracts
  @Override
  public long generateNewTimeToNextContract() {
    return new Random().nextInt(5);
  }

  // Generates new contract specialization, 10% chance of a different specialization:
  @Override
  public Specialization generateNewContractSpecialization(Specialization compSpecialization) {
    if (new Random().nextInt(10) >= 1) {
      return compSpecialization;
    } else {
      return Specialization.generateNewRandomSpecialization();
    }
  }

  // Generates a new unique ID for all contracts:
  @Override
  public long generateNewContractId() {
    return idCounter++;
  }
}
