package models.home_company;

import models.SimpleFirmModel.Messages;
import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Specialization;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.HashMap;

public abstract class DefaultHomeCompany extends Agent<Globals> {

  /*******************************
   * Setting Up Agent parameters:
   *******************************/
  @Variable public String name;

  /*******************************
   * Consultants Information:
   *******************************/

  // Keeping Track of all ConsultantsSpecializations & Priority's
  public HashMap<Long, Specialization> consSpecializationMap = new HashMap<>();

  public HashMap<Long, Integer> consPriorityMap = new HashMap<>();

  /*******************************
   * Function Implementations:
   *******************************/
  public void consultantSetup(Messages.RegistrationMessage msg) {
    consSpecializationMap.put(msg.getSender(), msg.specialization);
    consPriorityMap.put(msg.getSender(), msg.overlappedProjects);

    /*
    getPriorityQueue(msg.specialization, msg.isSrConsultant)
        .add(new Pair<Integer, Long>(msg.overlappedProjects, msg.getSender()));
       */
  }
}
