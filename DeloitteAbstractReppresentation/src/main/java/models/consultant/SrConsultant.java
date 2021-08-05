package models.consultant;

import models.SimpleFirmModel.Messages;
import simudyne.core.abm.Action;

import java.util.Random;

public class SrConsultant extends SuperConsultant implements Consultant {

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
      Action.create(SrConsultant.class, SuperConsultant::revenueNsalaryMessage);

  @Override
  public void generateAllowedOverlappedProjects() {
    this.nbAllowedOverlappedProjects = new Random().nextInt(5) + 5;
  }

  @Override
  public void generateSalary() {
    this.salary =
        ((new Random().nextDouble() * 1000) + getGlobals().SrSalary)
            * 20; // *20 because its a working Month
  }

  @Override
  public void revenueCalculationNewContractGiven() {
    int agentRevenue = getGlobals().SrRevenue;
    revenueCalibrationNewContract(agentRevenue);
  }

  @Override
  void revenueCalculationContractRelease() {
    int agentRevenue = getGlobals().SrRevenue;
    revenueCalibrationContractRelease(agentRevenue);
  }
}
