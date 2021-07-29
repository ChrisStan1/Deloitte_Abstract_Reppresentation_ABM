package models.consultant;

import models.SimpleFirmModel.Links;
import models.SimpleFirmModel.Messages;
import models.SimpleFirmModel.parameters.Globals;
import models.SimpleFirmModel.parameters.Ranking;
import models.SimpleFirmModel.parameters.Specialization;
import simudyne.core.abm.Agent;
import simudyne.core.annotations.Variable;

import java.util.HashMap;
import java.util.Random;

public abstract class SuperConsultant extends Agent<Globals> {

  /****************************************
   * Agent Characteristics:
   ****************************************/
  // Printed:
  @Variable public int nbAllowedOverlappedProjects;

  @Variable public int nbProjectsSameSpec = 0;
  @Variable public int nbProjects = 0;
  @Variable public double efficiency = 1.0;
  @Variable public int monthsBenched = 0;

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

  public void assignConsultant(Messages.ConsultantRequest msg) {
    if (msg.contSpecialization == specialization) {
      nbProjectsSameSpec++;
    } else {
      efficiency *= getGlobals().dropInEfficiency;
    }
    nbProjects++;
  }

  public void releaseConsultant(Messages.ConsultantReleased msg) {
    if (msg.contSpecialization == specialization) {
      nbProjectsSameSpec--;
    } else {
      efficiency /= getGlobals().dropInEfficiency;
    }
    nbProjects--;
  }

  public void quitConsultant() {
    // Check if the efficiency is to low,
    // if the have been benched for to long,
    // if the as been no jobs for the last 25 days.
    // Salary Value:
    if (efficiency < getGlobals().effQuittingEdge || floatingConsultants()) {
      removeLinks(Links.ConsultantLink.class);
      stop();
    }
  }

  public boolean floatingConsultants() {
    // Keeping track if consultants are in use or not.
    if (nbProjects != 0) {
      monthsBenched = 0;
      return false;
    } else {
      return monthsBenched++ >= getGlobals().nbDaysBenched;
    }
  }

  public void spawnNewConsultant(
      Specialization newSpecialization,
      HashMap<Long, Specialization> consSpecializationMap,
      long deloitteId) {

    specialization = newSpecialization;
    generateAllowedOverlappedProjects();

    // Linking the agent to everybody else!
    addLink(deloitteId, Links.DeloitteClientLink.class);
    consSpecializationMap.forEach(
        (conID, conSpec) -> {
          addLink(conID, Links.ConsultantLink.class);
        });

    // Debugging
    dbAgentSpecialization = specialization.toString();
  }

  abstract void generateAllowedOverlappedProjects();

  /****************************************
   * Debugging Features:
   ****************************************/
  @Variable public String dbAgentSpecialization;

  @Variable public String dbAgentStatus;
}
