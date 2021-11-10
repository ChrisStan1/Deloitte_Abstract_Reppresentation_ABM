/**************************
 * ClientContract
 * This is the basis for every contract:
 * By cas220
 **************************/

package models.client_contract;

import models.SimpleFirmModel.parameters.Specialization;
import models.client_company.SuperClientCompany;
import models.home_company.SuperHomeCompany;

public class ClientContract implements Contract {

  /****************************************
   * Contract Characteristics:
   ****************************************/

  // Hidden Variables:
  private boolean completed = false;

  private final long size;
  private long duration = 0;
  private long id = -1;

  private final Specialization specialization;
  private SuperClientCompany clientCompany;
  private SuperHomeCompany homeCompany;

  private ContractVisualization visualization;

  // Constructor:
  public ClientContract(
      SuperClientCompany clientCompany,
      SuperHomeCompany homeCompany,
      long size,
      long duration,
      Specialization specialization,
      long contractID) {

    this.clientCompany = clientCompany;
    this.homeCompany = homeCompany;
    this.size = size;
    this.duration = duration;
    this.specialization = specialization;
    this.id = contractID;
  }

  @Override
  public boolean isCompleted() {
    return completed;
  }

  @Override
  public long getSize() {
    return size;
  }

  @Override
  public long getDuration() {
    return duration;
  }

  @Override
  public void decreaseDuration() {
    this.duration--;
  }

  @Override
  public Specialization getSpecialization() {
    return specialization;
  }

  @Override
  public void setContractID(Long id) {
    this.id = id;
  }

  @Override
  public long getContractID() {
    return id;
  }

  // Function responsible for updating the contract variables each month
  @Override
  public void updateContractTick() {

    if (duration <= 0) {
      homeCompany.contractsToBeTerminated(this);
    } else {
      decreaseDuration();
    }
  }

  @Override
  public long getClientCompanyID() {
    return clientCompany.getID();
  }

  @Override
  public void addContractVisualization(ContractVisualization visualization) {
    this.visualization = visualization;
  }

  @Override
  public void stopContractVisualization() {
    visualization.stop();
  }
}
