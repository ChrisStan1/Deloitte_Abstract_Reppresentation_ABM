package models.consultant;

import simudyne.core.abm.Action;

import java.util.Random;

public class SrConsultant extends SuperConsultant implements Consultant {

  public static Action<SrConsultant> registerWithFirm =
      Action.create(SrConsultant.class, SuperConsultant::registerWithFirmMethod);

  @Override
  public void generateAllowedOverlappedProjects() {
    this.nbAllowedOverlappedProjects = new Random().nextInt(5) + 5;
  }
}
