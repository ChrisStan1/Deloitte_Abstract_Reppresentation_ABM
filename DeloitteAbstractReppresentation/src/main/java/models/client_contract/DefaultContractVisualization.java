package models.client_contract;

import models.SimpleFirmModel.Links;
import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Specialization;
import simudyne.core.abm.Action;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

public class DefaultContractVisualization extends Agent<Globals> implements ContractVisualization {

  // Printed variables
  @Variable public long contId;
  @Variable public long contSize;
  @Variable public long contDuration;

  /*
  @Variable public int nbSrCons;
  @Variable public int nbJrCons;
   */

  // Hidden Variables
  public Specialization contSpecialization;

  // Debugging Variables
  @Variable public String dbContSpecialization;

  public static Action<DefaultContractVisualization> stepContract =
      Action.create(
          DefaultContractVisualization.class,
          a -> {
            a.contDuration--;
            if (a.contDuration <= 0) {
              a.removeLinks(Links.ContractToClient.class);
              a.stop();
            }
          });
}
