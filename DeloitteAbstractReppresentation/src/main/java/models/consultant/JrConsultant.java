package models.consultant;

import models.SimpleFirmModel.Messages;
import simudyne.core.abm.Action;

import java.util.Random;

public class JrConsultant extends SuperConsultant {

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

  public static Action<SrConsultant> revenueNsalarySend =
      Action.create(SrConsultant.class, SuperConsultant::revenueSalaryMessage);

  public void generateAllowedOverlappedProjects() {
    this.nbAllowedOverlappedProjects = new Random().nextInt(3) + 2;
  }

  @Override
  public void generateSalary() {
    this.salary =
        ((new Random().nextDouble() * 1000) + getGlobals().JrSalary)
            * 20; // *20 because its a working Month
  }

  @Override
  public void revenueCalculationNewContractGiven() {
    int agentRevenue = getGlobals().JrRevenue;
    revenueCalibrationNewContract(agentRevenue);
  }

  @Override
  void revenueCalculationContractRelease() {
    int agentRevenue = getGlobals().JrRevenue;
    revenueCalibrationContractRelease(agentRevenue);
  }
}
