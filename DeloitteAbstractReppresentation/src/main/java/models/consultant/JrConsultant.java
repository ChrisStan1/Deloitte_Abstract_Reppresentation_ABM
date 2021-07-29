package models.consultant;

import models.SimpleFirmModel.Messages;
import simudyne.core.abm.Action;
import simudyne.core.abm.Section;

import java.util.Random;

public class JrConsultant extends SuperConsultant implements Consultant {

  // Action used to register Consultants With Deloitte
  public static Action<JrConsultant> registerWithFirm =
      Action.create(JrConsultant.class, SuperConsultant::registerWithFirmMethod);

  public static Action<JrConsultant> consultantRequest =
      Action.create(
          JrConsultant.class,
          a -> {
            a.getMessagesOfType(Messages.ConsultantRequest.class).forEach(a::assignConsultant);
          });

    public static Action<JrConsultant> consultantReleased = Action.create(
            JrConsultant.class,
            a -> {
                a.getMessagesOfType(Messages.ConsultantReleased.class).forEach(a::releaseConsultant);
            });

    public static Action<JrConsultant> consultantQuit =
            Action.create(JrConsultant.class, SuperConsultant::quitConsultant);

    @Override
  public void generateAllowedOverlappedProjects() {
    this.nbAllowedOverlappedProjects = new Random().nextInt(3) + 2;
  }
}
