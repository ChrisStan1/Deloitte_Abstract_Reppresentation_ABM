package models.consultant;

import simudyne.core.abm.Action;

import java.util.Random;

public class JrConsultant extends SuperConsultant implements Consultant {

    //Action used to register Consultants With Deloitte
  public static Action<JrConsultant> registerWithFirm =
      Action.create(JrConsultant.class, SuperConsultant::registerWithFirmMethod);

  @Override
  public void generateAllowedOverlappedProjects() {
    this.nbAllowedOverlappedProjects = new Random().nextInt(3) + 2;
  }
}
