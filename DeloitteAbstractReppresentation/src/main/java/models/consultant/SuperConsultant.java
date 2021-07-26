package models.consultant;

import models.SimpleFirmModel.parameters.ConsultantStatus;
import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Specialization;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.Random;

public abstract class SuperConsultant extends Agent<Globals> {

  /****************************************
   * Agent Characteristics:
   ****************************************/

  // Printed:
  @Variable public int nbAllowedOverlappedProjects;

  // Hidden:
  public Specialization specialization;
    public ConsultantStatus status;

    /****************************************
   * Implementation Of Agent Functions:
   ****************************************/

  // Todo: This is going to have to change in order to allow the user to specify the size of each
  // department.
  public Specialization assignAgentSpecialization() {
    return Specialization.values()[new Random().nextInt(Specialization.values().length)];
  }

  /****************************************
   * Debugging Features:
   ****************************************/
  // Debugging
  @Variable public String dbAgentSpecialization;
}
