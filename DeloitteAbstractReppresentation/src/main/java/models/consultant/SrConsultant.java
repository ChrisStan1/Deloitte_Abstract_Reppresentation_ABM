/**************************
 * SrConsultant
 * This definition of a srConsultant
 * By cas220
 **************************/

package models.consultant;

import models.SimpleFirmModel.Messages;
import simudyne.core.abm.Action;

import java.util.Random;

public class SrConsultant extends SuperConsultant {

  /*******************************
   * Action Implementations:
   *******************************/

  public static Action<SrConsultant> registerWithFirm =
      Action.create(SrConsultant.class, SuperConsultant::registerWithFirmMethod);

  public static Action<SrConsultant> consultantRequest =
      Action.create(
          SrConsultant.class,
          a -> {
            a.getMessagesOfType(Messages.ConsultantRequest.class).forEach(a::assignConsultant);
          });

  public static Action<SrConsultant> consultantReleased =
      Action.create(
          SrConsultant.class,
          a -> {
            a.getMessagesOfType(Messages.ConsultantReleased.class).forEach(a::releaseConsultant);
          });

  public static Action<SrConsultant> consultantQuit =
      Action.create(SrConsultant.class, SuperConsultant::quitConsultant);

  public static Action<SrConsultant> revenueNsalarySend =
      Action.create(SrConsultant.class, SuperConsultant::revenueSalaryMessage);

  // Method to generate overlapping projects:
  public void generateAllowedOverlappedProjects() {
    this.nbAllowedOverlappedProjects = new Random().nextInt(10) + 7;
  }

  // Method to generate salary
  @Override
  public void generateSalary() {
    this.salary = getGlobals().SrSalary * 20; // *20 because its a working Month
    // Possible introduction of randomization: ((new Random().nextDouble() * 1000) +
    // getGlobals().SrSalary)

  }

  // Acquiring the revenue rate of senior new contract
  @Override
  public void revenueCalculationNewContractGiven() {
    int agentRevenue = getGlobals().SrRevenue;
    revenueCalibrationNewContract(agentRevenue);
  }

  // Acquiring the revenue rate of senior released form contract
  @Override
  void revenueCalculationContractRelease() {
    int agentRevenue = getGlobals().SrRevenue;
    revenueCalibrationContractRelease(agentRevenue);
  }
}
