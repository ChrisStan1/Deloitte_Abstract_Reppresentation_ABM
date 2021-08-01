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
  @Variable public long revenue = 0;
  @Variable public long salary;

  // Hidden:
  public Specialization specialization;
  public Ranking ranking;

  public long basicRevenue;

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

  public void registerWithFirmMethodMessage() {
    getLinks(Links.DeloitteConsultantLink.class)
        .send(
            Messages.Registration.class,
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
    revenueCalculationNewContractGiven();
  }

  public void releaseConsultant(Messages.ConsultantReleased msg) {
    if (msg.contSpecialization == specialization) {
      nbProjectsSameSpec--;
    } else {
      efficiency /= getGlobals().dropInEfficiency;
    }
    nbProjects--;
    revenueCalculationContractRelease();
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
      Ranking ranking,
      long deloitteId) {

    // Consultant parameters
    this.ranking = ranking;
    specialization = newSpecialization;
    generateAllowedOverlappedProjects();
    generateSalary();

    // Linking the agent to everybody else!
    addLink(deloitteId, Links.DeloitteConsultantLink.class);
    consSpecializationMap.forEach(
        (conID, conSpec) -> {
          addLink(conID, Links.ConsultantLink.class);
        });

    // Debugging
    dbAgentSpecialization = specialization.toString();
    dbAgentStatus = ranking.toString();
  }

  abstract void generateAllowedOverlappedProjects();

  abstract void generateSalary();

  /****************************************
   * Function to look at the revenue:
   ****************************************/

  // From:
  // https://www.google.com/search?q=average+working+hours+in+a+month&rlz=1C1CHBF_en-GBGB772GB772&oq=avg+working+hours+in+a+&aqs=chrome.1.69i57j0i10l9.10490j0j7&sourceid=chrome&ie=UTF-8
  // 40 hours per week, 52 weeks per year, and 12 months per year: 40 hours per week x 52 weeks per
  // year / 12 months per year = 173.33 average monthly hours.

  // Partners hour rate = 4000;
  // Directors hour rate = 3500;
  // SrManagers hour rate = 3000;
  // Managers hour rate = 2500;
  // SrConsultant hour rate = 2000;
  // JrConsultant hour rate = 1500;
  // Analyst hour rate = 1200;

  // If working on a job they get the correct hour rate... Working on multiple jobs + 5% hour rate
  // increase;

  // Efficiency affects hour rate by 15%...

  // Each time there is a new job apply these parameters;

  abstract void revenueCalculationNewContractGiven();

  abstract void revenueCalculationContractRelease();

  void revenueCalibrationNewContract(int agentRevenue) {
    if (nbProjects == 1) {
      revenue = (agentRevenue * 30L); // 2500 a day... 2500*30 per month!
      basicRevenue = (agentRevenue * 30L);
    }

    // Bonus on working on multiple projects:
    if (nbProjects > 1) {
      basicRevenue *= 1.05;
    }

    // Effect caused by efficiency:
    revenue = (long) (basicRevenue - (basicRevenue * (0.15 * efficiency)));
  }

  void revenueCalibrationContractRelease(int agentRevenue) {
    // If not not working no revenue;
    if (nbProjects == 0) {
      revenue = (agentRevenue * 30L); // 2500 a day... 2500*30 per month!
      basicRevenue = (agentRevenue * 30L);
    }

    if (nbProjects != 0) {
      basicRevenue /= 1.05;
    }

    // Effect caused by efficiency:
    revenue = (long) (basicRevenue - (basicRevenue * (0.15 * efficiency)));
  }

  public void revenueNsalaryMessage() {
    getLinks(Links.DeloitteConsultantLink.class)
        .send(
            Messages.PandL.class,
            (msg, link) -> {
              msg.revenue = revenue;
              msg.salary = salary;
            });
  }

  /****************************************
   * Debugging Features:
   ****************************************/
  @Variable public String dbAgentSpecialization;

  @Variable public String dbAgentStatus;
}
