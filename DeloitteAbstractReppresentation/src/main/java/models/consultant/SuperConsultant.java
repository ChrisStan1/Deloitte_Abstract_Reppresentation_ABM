package models.consultant;

import models.SimpleFirmModel.Links;
import models.SimpleFirmModel.Messages;
import models.SimpleFirmModel.parameters.Ranking;
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
  public Ranking ranking;

  /****************************************
   * Implementation Of Agent Functions:
   ****************************************/

  // Todo: Ability to select min of agents in each discipline
  // Todo: Implement a function to not randomise (IMPORTANT)
  public Specialization assignAgentSpecialization() {
    return Specialization.values()[new Random().nextInt(Specialization.values().length)];
  }

  /****************************************
   * Implementation Of Agent Actions:
   ****************************************/

  public void registerWithFirmMethod() {
    getLinks(Links.DeloitteConsultantLink.class)
        .send(
            Messages.RegistrationMessage.class,
            (msg, link) -> {
              msg.overlappedProjects = nbAllowedOverlappedProjects;
              msg.specialization = specialization;
              msg.ranking = ranking;
            });
  }

  /****************************************
   * Debugging Features:
   ****************************************/
  @Variable public String dbAgentSpecialization;

  @Variable public String dbAgentStatus;
}
