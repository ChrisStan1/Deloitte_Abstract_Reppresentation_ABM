/**************************
 * JrConsultant
 * This definition of a jrConsultant
 * By cas220
 **************************/

package models.consultant;

import models.SimpleFirmModel.Messages;
import simudyne.core.abm.Action;

import java.util.Random;

public class JrConsultant extends SuperConsultant {

  /*******************************
   * Action Implementations:
   *******************************/

  // Action used to register Consultants With Deloitte
  public static Action<JrConsultant> registerWithFirm =
      Action.create(JrConsultant.class, SuperConsultant::registerWithFirmMethod);

  public static Action<JrConsultant> consultantRequest =
      Action.create(
          JrConsultant.class,
          a -> {
            a.getMessagesOfType(Messages.ConsultantRequest.class).forEach(a::assignConsultant);
          });

  public static Action<JrConsultant> consultantReleased =
      Action.create(
          JrConsultant.class,
          a -> {
            a.getMessagesOfType(Messages.ConsultantReleased.class).forEach(a::releaseConsultant);
          });

  public static Action<JrConsultant> consultantQuit =
      Action.create(JrConsultant.class, SuperConsultant::quitConsultant);

  public static Action<JrConsultant> revenueNsalarySend =
      Action.create(JrConsultant.class, SuperConsultant::revenueSalaryMessage);

  // Method to generate overlapping projects:
  public void generateAllowedOverlappedProjects() {
    this.nbAllowedOverlappedProjects = new Random().nextInt(2) + 1;
  }

  // Method to generate salary
  @Override
  public void generateSalary() {
    this.salary = getGlobals().JrSalary * 20; // *20 because its a working Month
    // Possible introduction of randomization: ((new Random().nextDouble() * 1000) +
    // getGlobals().JrSalary)
  }

  // Acquiring the revenue rate of junior new contract
  @Override
  public void revenueCalculationNewContractGiven() {
    int agentRevenue = getGlobals().JrRevenue;
    revenueCalibrationNewContract(agentRevenue);
  }

  // Acquiring the revenue rate of junior released form contract
  @Override
  void revenueCalculationContractRelease() {
    int agentRevenue = getGlobals().JrRevenue;
    revenueCalibrationContractRelease(agentRevenue);
  }
}
