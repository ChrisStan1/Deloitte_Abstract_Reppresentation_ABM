package models.client_contract;

import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Specialization;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

public class DefaultContractVisualization extends Agent<Globals> implements ContractVisualization {

  @Variable public long contId;
  @Variable public long contSize;
  @Variable public long contDuration;

  public Specialization contSpecialization;
  @Variable public String dbContSpecialization;

}
