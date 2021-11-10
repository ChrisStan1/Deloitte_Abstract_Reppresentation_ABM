/**************************
 * SuperConsultant
 * This is the super class for all consultants
 * By cas220
 **************************/

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

public abstract class SuperConsultant extends Agent<Globals> implements Consultant {

  /****************************************
   * Agent Characteristics:
   ****************************************/
  // Printed:
  @Variable public int nbAllowedOverlappedProjects;
  @Variable public int nbProjectsSameSpec = 0;
  @Variable public int nbProjects = 0;
  @Variable public double efficiency = 1.0;
  @Variable public int monthsBenched = 0;
  @Variable public double utilization = 1;
  @Variable public long revenue = 0;
  @Variable public double salary = 0;

  // Hidden:
  public Specialization specialization;
  public Ranking ranking;
  public int daysAtFirm = 0;
  public long basicRevenue;

  /****************************************
   * Implementation Of Agent Functions:
   ****************************************/

  // Future function; at the moment consultants specializations are randomized.
  public Specialization assignAgentSpecialization() {
    return Specialization.values()[new Random().nextInt(Specialization.values().length)];
  }

  /****************************************
   * Implementation Of Agent Actions:
   ****************************************/

  // Registering each consultant with HomeCompany to be assigned to contracts
  @Override
  public void registerWithFirmMethod() {
    getLinks(Links.DeloitteConsultantLink.class)
        .send(
            Messages.RegistrationConsultant.class,
            (msg, link) -> {
              msg.overlappedProjects = nbAllowedOverlappedProjects;
              msg.specialization = specialization;
              msg.ranking = ranking;
            });
  }

  // Consultant being assigned to a contracts upon request by HomeCompany
  @Override
  public void assignConsultant(Messages.ConsultantRequest msg) {
    if (msg.contSpecialization == specialization) {
      nbProjectsSameSpec++;
    } else {
      efficiency *= getGlobals().dropInEfficiency;
    }
    nbProjects++;
    revenueCalculationNewContractGiven();
  }

  // Consultant completed contract
  @Override
  public void releaseConsultant(Messages.ConsultantReleased msg) {
    if (msg.contSpecialization == specialization) {
      nbProjectsSameSpec--;
    } else {
      efficiency /= getGlobals().dropInEfficiency;
    }
    nbProjects--;
    revenueCalculationContractRelease();
  }

  // Function to check if a Consultant is quitting the HomeCompany
  @Override
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

  // Function utilized to track the Utilization rate and months benched of consultants
  private boolean floatingConsultants() {

    // Keeping Track of The utilization:
    if (nbProjects == 0) {
      monthsBenched++;
    }
    daysAtFirm++;
    // utilization =  (daysAtFirm - monthsBenched) / (double)daysAtFirm;
    utilization =
        (getGlobals().nbDaysBenched - monthsBenched) / (double) getGlobals().nbDaysBenched;

    // Keeping track if consultants are in use or not.
    if (nbProjects != 0) {
      monthsBenched = 0;
      return false;
    } else {
      // Return amount of time floating:
      return monthsBenched >= getGlobals().nbDaysBenched;
    }
  }

  // After a new hire, consultant agents have to be initialized
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

    // Linking the agent to every other agent.
    addLink(deloitteId, Links.DeloitteConsultantLink.class);

    /* (If there is enough computing power uncomment these lines)
    consSpecializationMap.forEach(
        (conID, conSpec) -> {
          addLink(conID, Links.ConsultantLink.class);
        });
     */

    // Debugging
    dbAgentSpecialization = specialization.toString();
    dbAgentStatus = ranking.toString();
  }

  // Abstract function for Number of overlapping projects.
  protected abstract void generateAllowedOverlappedProjects();

  // Abstract function to calculate the monthly salary of each agent.
  protected abstract void generateSalary();

  /****************************************
   * Function to look at the revenue:
   ****************************************/

  // From:
  // https://www.google.com/search?q=average+working+hours+in+a+month&rlz=1C1CHBF_en-GBGB772GB772&oq=avg+working+hours+in+a+&aqs=chrome.1.69i57j0i10l9.10490j0j7&sourceid=chrome&ie=UTF-8
  // 40 hours per week, 52 weeks per year, and 12 months per year: 40 hours per week x 52 weeks per
  // year / 12 months per year = 173.33 average monthly hours.

  // If working on a job they get the correct hour rate... Working on multiple jobs + 5% hour rate
  // increase;

  // Efficiency affects hour rate by 15%...

  // Each time there is a new job apply these parameters;

  // Function to recalibrate the revenue generated by each individual consultant per new project
  // allocated to them
  protected abstract void revenueCalculationNewContractGiven();

  // Recalibrating the revenue every time an agent finishes a contract
  abstract void revenueCalculationContractRelease();

  protected void revenueCalibrationNewContract(int agentRevenue) {
    if (nbProjects == 1) {
      revenue = (agentRevenue * 20L); // AgentRevenue a day... *20 working days per month!
      basicRevenue = (agentRevenue * 20L);
    }

    // Working on multiple projects:
    if (nbProjects > 1) {
      basicRevenue *= 1.05 /* agentRevenue*/;
    }

    // Effect caused by efficiency:
    revenue = (long) (basicRevenue - (basicRevenue * (0.15 * efficiency)));
  }

  protected void revenueCalibrationContractRelease(int agentRevenue) {
    // If not not working no revenue;
    if (nbProjects == 0) {
      revenue = 0;
      basicRevenue = 0;
    }

    if (nbProjects != 0) {
      basicRevenue /= 1.05 /* agentRevenue*/;
    }

    // Effect caused by efficiency:
    revenue = (long) (basicRevenue - (basicRevenue * (0.15 * efficiency)));
  }

  // Message function for HomeCompany to calculate the total revenue and salary.
  @Override
  public void revenueSalaryMessage() {
    getLinks(Links.DeloitteConsultantLink.class)
        .send(
            Messages.PNL.class,
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
