package models.client_company;

import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Specialization;
import models.client_contract.ContractGenerationStrategy;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

public abstract class SuperClientCompany extends Agent<Globals> {

  /*******************************
   * Setting Up Agent parameters:
   ******************************/
  // Printed
  @Variable public String name;

  // Hidden:
  public Specialization compSpecialization;
  public ContractGenerationStrategy contractGenerationStrategy;

  /****************************************
   * Debugging Features:
   ****************************************/
  @Variable public String dbCompSpecialization;
}
